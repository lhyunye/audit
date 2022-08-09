package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import com.rmxc.pulldata.passengerFlow.utils.util.KeLiuHttpUtils;
import com.rmxc.pulldata.passengerFlow.vo.PassengerFlow;
import lombok.extern.slf4j.Slf4j;
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
 * 根据客户id 获取场所客流过店数据
 */
@Slf4j
public class GetDataByRangeDate extends ApiModel {

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getDataByDataRange";
    private static String fileName = "dataModifyTime";
    //hive表名
    private static String temporaryTableName = "ods_api_keliu_data_modify_time_di_temporary";
    private static String tableName = "ods_api_keliu_data_modify_time_di";
    //获取当前时间的前一天
    private static String modifyTimeStart = LocalDateTime.now().minusDays(1).format(df_zero);
    private static String modifyTimeEnd = LocalDateTime.now().minusDays(1).format(df_all);


    /**
     * 默认interval为60，modifyTime为当前时间的前一天
     * 执行方法
     */
    public void exec(String date) {

        //删除存储文件
        File file = new File(Key.DATAPATH.getValue() + fileName + LocalDateTime.now().format(df_day));
        if (file.exists()) {
            file.delete();
        }
        Map<String, String> paramsMap = new HashMap<>();

        if (ObjectUtils.isEmpty(date)){

            paramsMap.put("beginTime", this.modifyTimeStart);
            paramsMap.put("endTime", this.modifyTimeEnd);
        }else {
            paramsMap.put("beginTime", LocalDate.parse(date,df_day2).format(df_zero));
            paramsMap.put("endTime", LocalDate.parse(date,df_day2).format(df_all));
        }

        paramsMap.put("interval", "60");
        String newFileName = fileName + LocalDateTime.now().format(df_day);


        //数据写入
        paramsMap.put("customerId", "St");
        pullData(paramsMap);
        System.out.println(paramsMap);

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
            BufferedWriter bw = WriteTxt.getbwnodel(newFileName);
            //调用getSiteKeysByCustomerId接口，获取接口数据
            String throughNumResult = "";
            if ("St".equals(paramsMap.get("customerId"))) {
                throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.STAPPKEY.getValue(), Key.STAPPSECRET.getValue(), param);
            } else {
                throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.JTAPPKEY.getValue(), Key.JTAPPSECRET.getValue(), param);
            }
            System.out.println(throughNumResult);
            //解析转换json数据
            JSONObject jsonObject = JSONObject.parseObject(throughNumResult);
            System.out.println(jsonObject.toJSONString());
            //判断接口数据是否正常
            if (!ObjectUtils.isEmpty(jsonObject) && jsonObject.containsKey("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                //数据转换
                List<PassengerFlow> dataCollect = data.stream().map(x ->
                        JSONObject.parseObject(x.toString(), PassengerFlow.class)
                ).collect(Collectors.toList());

                //循环写入
                dataCollect.stream().forEach(x -> {
                    WriteTxt.writeTxt(newFileName, bw, x.getSiteKey(), x.getSiteName(), x.getSiteType(), x.getInNum(),
                            x.getOutNum(), x.getThroughNum(), x.getDataTime(), x.getModifyTime(), paramsMap.get("customerId"));
                });
                bw.close();

            } else {

                log.error("客流系统getDataByModifyTime接口调用反参无数据，入参为：" + param);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("客流系统getDataByModifyTime接口调用失败，入参为：" + param + ",异常为：" + e.toString());
        }

    }
}
