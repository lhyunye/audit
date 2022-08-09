package com.rmxc.pulldata.passengerFlow.vo;

import lombok.Data;

@Data
public class StoreAssociation {

    private String source;            //店铺编码

    private String sourceName;            //店铺名称

    private String target;        //目标店铺编码

    private String targetName;    //目标店铺名称

    private String label;       //标签

    private String date;          //时间

}
