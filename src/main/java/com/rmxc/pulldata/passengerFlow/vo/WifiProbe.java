package com.rmxc.pulldata.passengerFlow.vo;

import lombok.Data;

@Data
public class WifiProbe {
    private String siteName;  //场所名称

    private String siteKey;  //加密后的场所

    private String siteType;  //场所类型

    private String customerNum;  //顾客数

    private String oldCustomer;  //老顾客数

    private String returnStoreRates;  //返点率

    private String firstArrNum;  //首次到店人数

    private String inCount;  //客流人次

    private String passNum; //过店设备数

    private String avgWanderTime; //平均游逛时间

    private String dataTime;  //数据时间

    private String modifyTime; //更新时间

}
