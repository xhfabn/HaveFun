package com.wujiawei.business.req;

import com.wujiawei.common.req.PageReq;

public class DailyTrainSeatQueryReq extends PageReq {

    private String trainCode;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DailyTrainSeatQueryReq{");
        sb.append("trainCode='").append(trainCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
