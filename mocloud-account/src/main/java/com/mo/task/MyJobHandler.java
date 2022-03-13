package com.mo.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/3/13
 */
@Component
@Slf4j
public class MyJobHandler {

    @XxlJob(value = "testJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> execute(String param) {

        log.info("testJobHandler execute 任务方法触发成功");
        return ReturnT.SUCCESS;
    }

    private void init(){
        log.info("testJobHandler init >>>>>>>>>>>>");
    }

    private void destroy(){
        log.info("testJobHandler destroy >>>>>>>>>>>>");
    }
}
