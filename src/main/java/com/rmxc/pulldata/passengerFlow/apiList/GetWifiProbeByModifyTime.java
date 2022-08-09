package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import com.rmxc.pulldata.passengerFlow.utils.util.KeLiuHttpUtils;
import com.rmxc.pulldata.passengerFlow.vo.PassengerFlow;
import com.rmxc.pulldata.passengerFlow.vo.WifiProbe;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据时间戳获取场所探针数据
 */
@Slf4j
public class GetWifiProbeByModifyTime extends ApiModel {

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getWifiProbeByModifyTime";
    private static String fileName = "WifiProbeModifyTime";
    //hive表名
    private static String temporaryTableName = "ods_api_keliu_wifi_probe_di_temporary";
    private static String tableName = "ods_api_keliu_wifi_probe_di";
    //获取当前时间的前一天
    private static String modifyTime = LocalDateTime.now().minusDays(1).format(df_zero);


    /**
     * 默认interval为D，modifyTime为当前时间的前一天
     * 执行方法
     */
    public void exec(String date) {

        //删除存储文件
        File file = new File(Key.DATAPATH.getValue() + fileName + LocalDateTime.now().format(df_day));
        if (file.exists()) {
            file.delete();
        }
        Map<String, String> paramsMap = new HashMap<>();

        if(!Strings.isEmpty(date)){
            modifyTime = LocalDate.parse(date,df_day2).format(df_zero);
        }

        paramsMap.put("modifyTime", modifyTime);
        paramsMap.put("interval", "60");
        paramsMap.put("customerId", "Wfjjt");
        pullData(paramsMap);
        System.out.println(paramsMap);

        try {
            //导入hive
            commitHive(fileName,tableName,temporaryTableName,date);
        }catch (Exception e){
            log.error(tableName+"数据导入异常,请检查hive中该表是否存在");
        }

    }


    @Override
    public void exec() {

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
            String throughNumResult = "";
            if ("St".equals(paramsMap.get("customerId"))) {
                throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.STAPPKEY.getValue(), Key.STAPPSECRET.getValue(), param);
            } else {
                throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.JTAPPKEY.getValue(), Key.JTAPPSECRET.getValue(), param);
            }

            //解析转换json数据
            JSONObject jsonObject = JSONObject.parseObject(throughNumResult);
            //判断接口数据是否正常
            if (!ObjectUtils.isEmpty(jsonObject) && jsonObject.containsKey("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                //数据转换
                List<WifiProbe> dataCollect = data.stream().map(x ->
                        JSONObject.parseObject(x.toString(), WifiProbe.class)
                ).collect(Collectors.toList());

                //循环写入
                dataCollect.stream().filter(x -> x.getModifyTime().contains(modifyTime.split(" ")[0])).forEach(x -> {
                    WriteTxt.writeTxt(newFileName, bw, x.getSiteName(),x.getSiteKey(),x.getSiteType(),x.getCustomerNum(),x.getOldCustomer(),
                            x.getReturnStoreRates(),x.getFirstArrNum(),x.getInCount(),x.getPassNum(),x.getAvgWanderTime(),x.getDataTime(),x.getModifyTime(), paramsMap.get("customerId"));
                });

                bw.close();

            } else {
                log.error("客流系统getWifiProbeByModifyTime接口调用反参无数据，入参为：" + param);
            }

        } catch (Exception e) {
            log.error("客流系统getWifiProbeByModifyTime接口调用失败，入参为：" + param + ",异常为：" + e.toString());
        }

    }
}
