package com.rmxc.pulldata.passengerFlow.vo;

import lombok.Data;

@Data
public class SiteDeviceDetail {

    private String siteName;            //场所名称

    private String hostName;            //设备名称

    private String deviceStatus;        //设备状态（1在线2离线）

    private String deviceChannelNum;    //设备路数

    private String useDeviceChannelNum; //已用设备路数

    private String createTime;          //创建时间

    private String stateTime;           //设备最后状态时间

    private String siteRelation;        //设备通道关联场所

}
