package com.wujiawei.common.controller;

import com.wujiawei.common.exception.BusinessException;
import com.wujiawei.common.resp.CommonResp;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        CommonResp commonResp = new CommonResp();
        log.error("系统出现异常,请联系管理员", e);
        commonResp.setSuccess(false);
        commonResp.setMessage("系统出现异常,请联系管理员");
        return commonResp;
    }


    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp exceptionHandler(HttpServletRequest req, BusinessException e) throws Exception {
        CommonResp commonResp = new CommonResp();
        log.error("业务异常:{}", e.getE().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getE().getDesc());
        return commonResp;
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp exceptionHandler(HttpServletRequest req, BindException e) throws Exception {
        CommonResp commonResp = new CommonResp();
        log.error("校验异常:{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }


}
