package com.mo.controller;

import com.mo.enums.ShortLinkStateEnum;
import com.mo.service.ShortLinkService;
import com.mo.utils.CommonUtil;
import com.mo.vo.ShortLinkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2022/2/21
 */
@Api(tags = "短链模块")
@RestController
@Slf4j
public class LinkApiController {

    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 短链码跳转接口
     *
     * @param shortLinkCode
     * @param request
     * @param response      解析用 301还是302，这边是返回http code是302
     *                      为什么要用 301 跳转而不是 302
     *                      301 是永久重定向，302 是临时重定向
     *                      短地址一经生成就不会变化，所以用 301 是同时对服务器压力也会有一定减少
     *                      但是如果使用了 301，无法统计到短地址被点击的次数
     *                      所以选择302虽然会增加服务器压力，但是有很多数据可以获取进行分析
     */
    @ApiOperation("短链码跳转接口")
    @GetMapping(path = "/{shortLinkCode}")
    public void dispatch(@PathVariable(name = "短链码") String shortLinkCode,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        try {
            log.info("短链码:{}", shortLinkCode);

            //判断短链码是否合规
            if (CommonUtil.isLetterDigit(shortLinkCode)) {
                //查找短链
                ShortLinkVO shortLinkVO = shortLinkService.parseShortLinkCode(shortLinkCode);

                //判断是否过期和可用
                if (isVisitable(shortLinkVO)) {
                    //跳转到源地址
                    response.setHeader("Location", shortLinkVO.getOriginalUrl());
                    //302跳转
                    response.setStatus(HttpStatus.FOUND.value());
                } else {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    return;
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }


    /**
     * 判断短链是否可用
     *
     * @param shortLinkVO
     * @return
     */
    private static boolean isVisitable(ShortLinkVO shortLinkVO) {
        if ((shortLinkVO != null && shortLinkVO.getExpired().getTime() > CommonUtil.getCurrentTimestamp())) {
            if (ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVO.getState())) {
                return true;
            }
        } else if ((shortLinkVO != null && shortLinkVO.getExpired().getTime() == -1)) {
            if (ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVO.getState())) {
                return true;
            }
        }

        return false;
    }


}
