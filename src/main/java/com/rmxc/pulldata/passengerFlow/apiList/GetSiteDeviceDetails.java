package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import com.rmxc.pulldata.passengerFlow.vo.DeviceDetailsTotal;
import com.rmxc.pulldata.passengerFlow.vo.SiteDeviceDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
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
public class GetSiteDeviceDetails extends ApiModel {

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getSiteDeviceDetails";
    private static String fileName = "SiteDeviceDetail";
    //hive表名
    private static String temporaryTableName = "ods_api_keliu_site_deveice_detail_dd_temporary";
    private static String tableName = "ods_api_keliu_site_deveice_detail_dd";
    //获取当前时间的前一天
    private static String modifyTime = LocalDateTime.now().minusDays(1).format(df_zero);



    /**
     * 默认执行方法
     */
    public void exec() {


    }


    /**
     * 重构执行方法
     */
    public void exec(String siteKey,String siteType,String total) {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
        paramsMap.put("siteKey", siteKey);
        paramsMap.put("deviceStatus", "0");
        paramsMap.put("siteType", siteType);
        paramsMap.put("pageNum", "1");
        paramsMap.put("pageSize", total);
        if (!Strings.isEmpty(total)&&!"0".equals(total)){
            pullData(paramsMap);
        }

    }


    /**
     * @Param paramsMap 接口入参
     * 数据拉取方法
     */
    public void pullData(Map<String, String> paramsMap) {

        String param = JSON.toJSONString(paramsMap);

        try {
            String newFileName = fileName + LocalDateTime.now().format(df_day);
            BufferedWriter bw = WriteTxt.getbw(newFileName);
            //调用getSiteKeysByCustomerId接口，获取接口数据
            String throughNumResult = dopost(paramsMap,apiPath,param);

            //解析转换json数据
            JSONObject jsonObject = JSONObject.parseObject(throughNumResult);
            //判断接口数据是否正常
            if (!ObjectUtils.isEmpty(jsonObject) && jsonObject.containsKey("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                //数据转换
                List<SiteDeviceDetail> dataCollect = data.stream().map(x ->
                        JSONObject.parseObject(x.toString(), SiteDeviceDetail.class)
                ).collect(Collectors.toList());
                //循环写入
                dataCollect.forEach(x->{
                    WriteTxt.writeTxt(newFileName,bw,paramsMap.get("siteKey"),x.getSiteName(),x.getHostName(),x.getDeviceStatus(),x.getDeviceChannelNum(),x.getUseDeviceChannelNum(),
                            x.getCreateTime(),x.getStateTime(),x.getSiteRelation(),paramsMap.get("customerId"));
                });
                bw.close();

            } else {
                log.error("客流系统getSiteDeviceDetailsTotal接口调用反参无数据，入参为：" + param);
            }

        } catch (Exception e) {
            log.error("客流系统getSiteDeviceDetailsTotal接口调用失败，入参为：" + param + ",异常为：" + e.toString());
        }


    }
}
