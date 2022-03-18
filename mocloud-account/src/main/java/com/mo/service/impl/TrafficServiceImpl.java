package com.mo.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.mo.constant.CacheKey;
import com.mo.constant.TimeConstant;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.EventMessageTypeEnum;
import com.mo.exception.BizException;
import com.mo.feign.ProductFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.TrafficManager;
import com.mo.model.EventMessage;
import com.mo.model.LoginUserDTO;
import com.mo.model.TrafficDO;
import com.mo.request.TrafficPageRequest;
import com.mo.request.TrafficUseRequest;
import com.mo.service.TrafficService;
import com.mo.utils.JsonData;
import com.mo.utils.JsonUtil;
import com.mo.utils.TimeUtil;
import com.mo.vo.ProductVO;
import com.mo.vo.TrafficUseVO;
import com.mo.vo.TrafficVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/3/10
 */
@Service
@Slf4j
public class TrafficServiceImpl implements TrafficService {

    @Autowired
    private TrafficManager trafficManager;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 流量包使用(扣减)
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData reduce(TrafficUseRequest request) {
        Long accountNo = request.getAccountNo();
        //处理流量包，筛选出未更新流量包，和选定当次使用的流量包
        TrafficUseVO trafficUseVO = processTrafficList(accountNo);

        log.info("今天可用总次数:{},当前使用流量包:{}", trafficUseVO.getDayTotalLeftTimes(), trafficUseVO.getCurrentTrafficDO());
        if (trafficUseVO.getCurrentTrafficDO() == null) {
            return JsonData.buildResult(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }

        log.info("待更新流量包列表:{}", trafficUseVO.getUnUpdatedTrafficIds());
        if (trafficUseVO.getUnUpdatedTrafficIds().size() > 0) {
            //批量更新今日的流量包
            trafficManager.batchUpdateUsedTimes(accountNo, trafficUseVO.getUnUpdatedTrafficIds());
        }

        //先更新完流量包,再扣减当次使用的流量包
        Integer rows = trafficManager.addDayUsedTimes(accountNo, trafficUseVO.getCurrentTrafficDO().getId(), 1);

        if (rows != 1) {
            throw new BizException(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }

        //在redis中设置总流量包次数，创建短链时，短链服务递减即可
        //若有新增的流量包，则删除这个key

        //先获取当天剩余的秒数,用于流量包过期配置
        long leftSecondsOneDay = TimeUtil.getRemainSecondsOneDay(new Date());

        //缓存key
        String trafficDayTotalTimesKey = String.format(CacheKey.TRAFFIC_DAY_TOTAL_KEY, accountNo);

        //保存总流量包次数到redis，用于高并发下短链服务扣减流量包
        //减少1是减去此次已使用的的流量包次数
        redisTemplate.opsForValue().setIfAbsent(trafficDayTotalTimesKey,
                trafficUseVO.getDayTotalLeftTimes() - 1,
                leftSecondsOneDay, TimeUnit.SECONDS);

        return JsonData.buildSuccess(trafficUseVO);
    }

    /**
     * 处理流量包，筛选出未更新流量包，和选定当次使用的流量包
     *
     * @param accountNo
     * @return
     */
    private TrafficUseVO processTrafficList(Long accountNo) {

        //全部流量包
        List<TrafficDO> trafficDOList = trafficManager.selectAvailableTraffics(accountNo);
        if (trafficDOList == null || trafficDOList.size() == 0) {
            throw new BizException(BizCodeEnum.TRAFFIC_EXCEPTION);
        }

        //天剩余可用总次数 = 总次数 - 已用次数
        Integer dayTotalLeftTimes = 0;

        //当前使用的流量包
        TrafficDO currentTrafficDO = null;

        //记录没过期，但是今天没更新的流量包id-列表
        List<Long> unUpdatedTrafficIds = new ArrayList<>();

        //获取当前的日期
        String todayStr = TimeUtil.format(new Date(), TimeConstant.DATE_YYYY_MM_DD);

        for (TrafficDO trafficDO : trafficDOList) {

            //获取流量包的更新时间
            String trafficUpdateDate = TimeUtil.format(trafficDO.getUpdateTime(), TimeConstant.DATE_YYYY_MM_DD);
            if (todayStr.equalsIgnoreCase(trafficUpdateDate)) {
                //流量包已经被更新
                //获取该流量包可用次数 = 每天限制总次数-每天已使用次数 (可叠加)
                int dayLeftTimes = trafficDO.getDayLimit() - trafficDO.getDayUsed();
                dayTotalLeftTimes = dayTotalLeftTimes + dayLeftTimes;

                //选定当次使用的流量包
                if (dayLeftTimes > 0 && currentTrafficDO == null) {
                    currentTrafficDO = trafficDO;
                }
            } else {
                //流量包未更新
                //未更新的流量包的可用次数 = 每天限制总次数 (可叠加)
                dayTotalLeftTimes = dayTotalLeftTimes + trafficDO.getDayLimit();

                //记录没过期，但是今天没更新的流量包id-列表
                unUpdatedTrafficIds.add(trafficDO.getId());

                //选定当次使用的流量包
                if (currentTrafficDO == null) {
                    currentTrafficDO = trafficDO;
                }
            }
        }

        TrafficUseVO trafficUseVO = new TrafficUseVO(dayTotalLeftTimes, currentTrafficDO, unUpdatedTrafficIds);

        return trafficUseVO;

    }

    /**
     * 过期流量包处理-物理删除过期流量包
     *
     * @return
     */
    @Override
    public Boolean deleteExpireTraffic() {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        int rows = trafficManager.deleteExpireTraffic(loginUserDTO.getAccountNo());
        log.info("删除过期流量包行数：rows={}", rows);

        return true;
    }

    /**
     * 查找某个流量包详情
     *
     * @param trafficId
     * @return
     */
    @Override
    public TrafficVO detail(Long trafficId) {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        trafficManager.findByIdAndAccountNo(trafficId, loginUserDTO.getAccountNo());
        return null;
    }

    /**
     * 分页查询流量包列表
     * 不可用的需要去归档数据查
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageTrafficList(TrafficPageRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        request.setAccountNo(loginUserDTO.getAccountNo());

        Map<String, Object> resultMap = trafficManager.pageTrafficList(request);

        return resultMap;
    }

    /**
     * 处理 MQ队列里面的流量包相关消息
     *
     * @param eventMessage
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void processTrafficMessage(EventMessage eventMessage) {

        String messageType = eventMessage.getEventMessageType();
        Long accountNo = eventMessage.getAccountNo();

        if (EventMessageTypeEnum.ORDER_PAY.name().equalsIgnoreCase(messageType)) {
            //订单已支付,新增发放流量包
            String content = eventMessage.getContent();
            Map<String, Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);

            //还原订单商品信息
            String outTradeNo = (String) orderInfoMap.get("outTradeNo");
            Integer buyNum = (Integer) orderInfoMap.get("buyNum");

            //商品快照,转换为商品对象
            String productStr = (String) orderInfoMap.get("product");
            ProductVO productVO = JsonUtil.json2Obj(productStr, ProductVO.class);
            log.info("商品信息:{}", productVO);

            //流量包有效期
            LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(productVO.getValidDay());
            Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());

            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    //每天限制多少条，短链，可叠加
                    .dayLimit(productVO.getDayTimes() * buyNum)
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo(outTradeNo)
                    .expiredDate(date)
                    .build();

            //保存
            Integer rows = trafficManager.add(trafficDO);
            log.info("消费消息新增流量包:rows={},trafficDO={}", rows, trafficDO);

            //新增流量包，删除redis中缓存的key
            String trafficDayTotalTimesKey = String.format(CacheKey.TRAFFIC_DAY_TOTAL_KEY, accountNo);
            redisTemplate.delete(trafficDayTotalTimesKey);

        } else if (EventMessageTypeEnum.TRAFFIC_FREE_INIT.name().equalsIgnoreCase(messageType)) {
            //新用户注册,发放免费流量包
            Long productId = Long.valueOf(eventMessage.getBizId());

            JsonData jsonData = productFeignService.detail(productId);

            ProductVO productVO = jsonData.getData(new TypeReference<ProductVO>() {
            });
            //构建流量包对象
            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    .dayLimit(productVO.getDayTimes())
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo("free_init")
                    .expiredDate(new Date())
                    .build();

            trafficManager.add(trafficDO);
        }
    }
}
