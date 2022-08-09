package com.rmxc.pulldata.passengerFlow;

import com.rmxc.pulldata.passengerFlow.apiList.*;
import com.rmxc.pulldata.passengerFlow.utils.CommandUtil;

public class PFexec {

    public static void exec(String date){
        //根据客户id获取场所编码  及场所设备信息
        GetSiteKeysByCustomerId getSiteKeysByCustomerId = new GetSiteKeysByCustomerId();
        getSiteKeysByCustomerId.exec(date);
        //根据客户id 获取场所客流过店数据--获取最新数据
        GetDataByModifyTime getDataByModifyTime = new GetDataByModifyTime();
        getDataByModifyTime.exec(date);
        //根据客户id 获取场所客流过店数据--获取历史数据
//        GetDataByRangeDate getDataByRangeDate = new GetDataByRangeDate();
//        getDataByRangeDate.exec(date);
//        //根据客户id 获取手机终端品牌数据
        GetMobilePhoneBrandByDataRange getMobilePhoneBrandByDataRange = new GetMobilePhoneBrandByDataRange();
        getMobilePhoneBrandByDataRange.exec(date);
        //根据时间戳获取场所探针数据
        GetWifiProbeByModifyTime getWifiProbeByModifyTime = new GetWifiProbeByModifyTime();
        getWifiProbeByModifyTime.exec(date);
        //通过月份获取店铺关联度数据。
        GetStoreAssociation getStoreAssociation = new GetStoreAssociation();
        getStoreAssociation.exec(date);


    }

}
