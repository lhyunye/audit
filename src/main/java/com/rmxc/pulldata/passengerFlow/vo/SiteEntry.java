package com.rmxc.pulldata.passengerFlow.vo;

import lombok.Data;

@Data
public class SiteEntry {

    //加密后key
    private String siteKey;

    //场所名称
    private String siteName;

    //场所类型  集团:100 广场(连锁店铺):300 区域:400 楼层:500 店铺:600 通道:700
    private String type;



}
