package com.mo.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.mo.enums.EventMessageTypeEnum;
import com.mo.feign.ProductFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.TrafficManager;
import com.mo.model.EventMessage;
import com.mo.model.LoginUserDTO;
import com.mo.model.TrafficDO;
import com.mo.request.TrafficPageRequest;
import com.mo.service.TrafficService;
import com.mo.utils.JsonData;
import com.mo.utils.JsonUtil;
import com.mo.vo.ProductVO;
import com.mo.vo.TrafficVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

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

    /**
     * 过期流量包处理-物理删除过期流量包
     *
     * @return
     */
    @Override
    public Boolean deleteExpireTraffic() {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        int rows = trafficManager.deleteExpireTraffic(loginUserDTO.getAccountNo());
        log.info("删除过期流量包行数：rows={}",rows);

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
