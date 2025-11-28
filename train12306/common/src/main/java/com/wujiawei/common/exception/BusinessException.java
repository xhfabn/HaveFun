package com.wujiawei.common.exception;

public class BusinessException extends RuntimeException {
    private BusinessExceptionEnum e;
    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setE(BusinessExceptionEnum e) {
        this.e = e;
    }
    //重写打印报错的信息 正常会循环调用导致 打印多次的信息
    // 这里只是返回本层实现打印单层的日志
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
