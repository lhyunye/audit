package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import com.rmxc.pulldata.passengerFlow.utils.util.KeLiuHttpUtils;
import com.rmxc.pulldata.passengerFlow.vo.MobilePhoneBrand;
import com.rmxc.pulldata.passengerFlow.vo.PassengerFlow;
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
 * 根据客户id 获取手机终端品牌数据
 */
@Slf4j
public class GetMobilePhoneBrandByDataRange extends ApiModel {

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getMobilePhoneBrandByDataRange";
    private static String fileName = "dataModifyTime";
    //hive表名
    private static String temporaryTableName = "ods_api_keliu_mobile_phone_brand_di_temporary";
    private static String tableName = "ods_api_keliu_mobile_phone_brand_di";
    //获取当前时间的前一天
    private static String beginTime = LocalDateTime.now().minusDays(1).format(df_day2);
    private static String endTime = LocalDateTime.now().format(df_day2);


    /**
     * 执行方法
     */
    public void exec(String date) {

        //删除存储文件
        File file = new File(Key.DATAPATH.getValue() + fileName + LocalDateTime.now().format(df_day));
        if (file.exists()) {
            file.delete();
        }
        Map<String, String> paramsMap = new HashMap<>();

        if (Strings.isEmpty(date)){
            paramsMap.put("beginTime", beginTime);
            paramsMap.put("endTime", endTime);
        }else {
            paramsMap.put("beginTime", date);
            paramsMap.put("endTime", LocalDate.parse(date,df_day2).plusDays(1).format(df_day2));
        }

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
                List<MobilePhoneBrand> dataCollect = data.stream().map(x ->
                        JSONObject.parseObject(x.toString(), MobilePhoneBrand.class)
                ).collect(Collectors.toList());

                //循环写入
                dataCollect.forEach(x -> {
                    WriteTxt.writeTxt(newFileName, bw, x.getSiteName(),x.getSiteKey(),x.getDateTime(),x.getModifyTime(),x.getSony(),x.getNokia(),x.getMotorola(),x.getXiaomi(),x.getZte(),x.getLenovo(),
                            x.getZte(),x.getIphone(),x.getSamsung(),x.getHuawei(),x.getMicrosoft(),x.getUnknown(),x.getOthers(),x.getMeizu(),x.getOppo(),x.getCoolpad(),x.getVivo(),x.getHisense(),x.getLeshi(),
                            x.getTcl(),x.getGionee(), paramsMap.get("customerId"));
                });
                bw.close();

            } else {
                log.error("客流系统getMobilePhoneBrandByDataRange接口调用反参无数据，入参为：" + param);
            }

        } catch (Exception e) {
            log.error("客流系统getMobilePhoneBrandByDataRange接口调用失败，入参为：" + param + ",异常为：" + e.toString());
        }

    }
}
