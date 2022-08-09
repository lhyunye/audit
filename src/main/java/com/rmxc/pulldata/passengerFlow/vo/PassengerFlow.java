package com.rmxc.pulldata.passengerFlow.vo;

import lombok.Data;

@Data
public class PassengerFlow {


    //场所名称
    private String siteName;

    //数据时间
    private String dataTime;

    //加密后场所的Key
    private String siteKey;

    //场所类型
    private String siteType;

    //进客流量
    private String inNum;

    //出客流量
    private String outNum;

    //更新时间
    private String modifyTime;

    //过店客流
    private String throughNum;

}
