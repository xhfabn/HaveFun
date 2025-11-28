package com.wujiawei.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wujiawei.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageReq {


    private String code;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public String getcode() {
        return code;
    }

    public void setcode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DailyTrainQueryReq{");
        sb.append("code='").append(code).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
