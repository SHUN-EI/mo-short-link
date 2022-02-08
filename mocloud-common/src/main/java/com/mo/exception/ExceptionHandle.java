package com.mo.exception;

import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by mo on 2022/2/8
 */
//@RestControllerAdvice(使用这个注解，方法上面就不用加@ResponseBody)
@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonData handle(Exception e) {

        //是否为自定义异常
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            log.info("[业务异常]{}", e);
            //返回前端的错误信息
            return JsonData.buildCodeAndMsg(bizException.getCode(), bizException.getMsg());

        }else {
            log.info("[系统异常]{}",e);
            return JsonData.buildError("全局异常，未知错误");
        }
    }
}
