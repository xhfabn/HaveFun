package com.wujiawei.business.req;

import com.wujiawei.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainStationQueryReq extends PageReq {

    private String trainCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public String gettrainCode() {
        return trainCode;
    }

    public void settrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
