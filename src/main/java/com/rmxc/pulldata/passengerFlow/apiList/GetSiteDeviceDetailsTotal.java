package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.pulldata.passengerFlow.vo.DeviceDetailsTotal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口返回，modifyTime到当前时间15分钟前的数据
 * interval    表示DataTime的粒度，
 * D天、60小时
 */
@Slf4j
public class GetSiteDeviceDetailsTotal extends ApiModel {

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getSiteDeviceDetailsTotal";


    private String total = "0";


    /**
     * 默认执行方法
     */
    public void exec() {


    }


    /**
     * 重构执行方法
     */
    public String exec(String siteKey,String siteType) {


        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
        paramsMap.put("siteKey", siteKey);
        paramsMap.put("deviceStatus", "0");
        paramsMap.put("siteType", siteType);
        pullData(paramsMap);
        return total;

    }


    /**
     * @Param paramsMap 接口入参
     * 数据拉取方法
     */
    public void pullData(Map<String, String> paramsMap) {

        String param = JSON.toJSONString(paramsMap);

        try {
            //调用getSiteKeysByCustomerId接口，获取接口数据
            String throughNumResult = dopost(paramsMap,apiPath,param);

            //解析转换json数据
            JSONObject jsonObject = JSONObject.parseObject(throughNumResult);
            //判断接口数据是否正常
            if (!ObjectUtils.isEmpty(jsonObject) && jsonObject.containsKey("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                //数据转换
                List<DeviceDetailsTotal> dataCollect = data.stream().map(x ->
                        JSONObject.parseObject(x.toString(), DeviceDetailsTotal.class)
                ).collect(Collectors.toList());
                //获取场所设备总记录数据
                if(dataCollect.size()>0){
                    total=dataCollect.get(0).getPageCount();
                }

            } else {
                log.error("客流系统getSiteDeviceDetailsTotal接口调用反参无数据，入参为：" + param);
            }

        } catch (Exception e) {
            log.error("客流系统getSiteDeviceDetailsTotal接口调用失败，入参为：" + param + ",异常为：" + e.toString());
        }


    }
}
