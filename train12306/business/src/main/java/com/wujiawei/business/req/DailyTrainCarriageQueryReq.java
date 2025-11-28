package com.wujiawei.business.req;

import com.wujiawei.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainCarriageQueryReq extends PageReq {

    private String tranCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DailyTrainCarriageQueryReq{");
        sb.append("tranCode='").append(tranCode).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
