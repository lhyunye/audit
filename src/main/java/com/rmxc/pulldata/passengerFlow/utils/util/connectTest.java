package com.rmxc.pulldata.passengerFlow.utils.util;

import com.alibaba.fastjson.JSON;
import com.rmxc.pulldata.passengerFlow.apiList.GetSiteDeviceDetailsTotal;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;

import java.util.HashMap;
import java.util.Map;

public class connectTest {
//    private static String apiPath = "/huikeapi/passengerFlowData/V3/getSiteDeviceDetailsTotal";
//

//    public static void main(String[] args) throws Exception {
//        //port01();
//
//       port02();
//
//       // port03();
//      //  port04();
//      //  port05();
//
//
//
////        GetSiteDeviceDetailsTotal getSiteDeviceDetailsTotal = new GetSiteDeviceDetailsTotal();
////        String a = getSiteDeviceDetailsTotal.exec("B806EFE1CA0A6368D61EB675E7326C614", "300");
////        System.out.println(a);
//
//
//    }



    public static void port01() throws Exception {
        //该接口只有Wfjjt
       String apiPath01 = "/huikeapi/passengerFlowData/V3/getWifiProbeByModifyTime";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "St");
        paramsMap.put("siteKey", "10D2AD6495F57AE1A5FD890965D746EE");
        paramsMap.put("modifyTime", "2021-05-19 00:00:00");
        paramsMap.put("interval", "D");
        pulldata(paramsMap,apiPath01);
    }

    public static void port02() throws Exception {

        String apiPath02 = "/huikeapi/passengerFlowData/V3/getStoreAssociationDegreeByDate";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
        paramsMap.put("date", "2021-04");
        pulldata(paramsMap,apiPath02);
    }


    public static void port03() throws Exception {

        String apiPath03 = "/huikeapi/passengerFlowData/V3/getSiteDeviceDetailsTotal";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
        paramsMap.put("siteKey", "B806EFE1CA0A6368D6EB675E7326C614");
        paramsMap.put("deviceStatus", "0");
        paramsMap.put("siteType", "300");
        pulldata(paramsMap,apiPath03);
    }

    public static void port04() throws Exception {

        String apiPath04 = "/huikeapi/passengerFlowData/V3/getSiteDeviceDetails";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
        paramsMap.put("siteKey", "B806EFE1CA0A6368D6EB675E7326C614");
        paramsMap.put("deviceStatus", "0");
        paramsMap.put("siteType", "300");
        paramsMap.put("pageNum", "1");
        paramsMap.put("pageSize", "10");
        pulldata(paramsMap,apiPath04);
    }


    public static void port05() throws Exception {

        String apiPath04 = "/huikeapi/passengerFlowData/V3/getMobilePhoneBrandByDataRange";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
       // paramsMap.put("siteKey", "B806EFE1CA0A6368D6EB675E7326C614");
        paramsMap.put("beginTime", "2021-05-19 00:00:00");
        paramsMap.put("endTime", "2021-05-20 00:00:00");
        pulldata(paramsMap,apiPath04);
    }

    public static void pulldata( Map<String, String> paramsMap,String apiPath) throws Exception {
        String param = JSON.toJSONString(paramsMap);
        String throughNumResult = "";
        if ("St".equals(paramsMap.get("customerId"))){
            throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.STAPPKEY.getValue(), Key.STAPPSECRET.getValue(), param);
        }else {
            throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.JTAPPKEY.getValue(), Key.JTAPPSECRET.getValue(), param);
        }
        System.out.println(throughNumResult);
    }

}
