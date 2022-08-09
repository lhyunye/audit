package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.pulldata.passengerFlow.utils.ReadTxt;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import com.rmxc.pulldata.passengerFlow.utils.util.KeLiuHttpUtils;
import com.rmxc.pulldata.passengerFlow.vo.SiteEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GetSiteKeysByCustomerId extends ApiModel{

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getSiteKeysByCustomerId";
    private static String fileName = "siteEntry";
    private static String temporaryTableName = "ods_api_keliu_site_entry_dd_temporary";
    private static String tableName = "ods_api_keliu_site_entry_dd";


    private static String deviceFileName = "SiteDeviceDetail";
    private static String deviceTemporaryTableName = "ods_api_keliu_site_deveice_detail_dd_temporary";
    private static String deviceTableName = "ods_api_keliu_site_deveice_detail_dd";

    /**
     * 执行方法
     * */
    public void exec(String date)  {
        //删除存储文件
        File file = new File(Key.DATAPATH.getValue()+fileName+LocalDateTime.now().format(df_day));
        if (file.exists()) {
            file.delete();
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "St");
        pullData(paramsMap);
        paramsMap.put("customerId", "Wfjjt");
        pullData(paramsMap);
        try {
            //导入hive
            commitHive(fileName,tableName,temporaryTableName,date);
        }catch (Exception e){
            log.error(tableName+"数据导入异常,请检查hive中该表是否存在");
        }

        try {
            //导入hive
            commitHive(deviceFileName,deviceTableName,deviceTemporaryTableName,date);
        }catch (Exception e){
            log.error(deviceTableName+"数据导入异常,请检查hive中该表是否存在");
        }

    }

    @Override
    public void exec() {

    }

    /**
     *  @Param paramsMap 接口入参
     * 数据拉取方法
     * */
    @Override
    public  void pullData(Map<String,String> paramsMap){
        String param = JSON.toJSONString(paramsMap);
        try {
            String newFileName = fileName + LocalDateTime.now().format(df_day);
            BufferedWriter bw = WriteTxt.getbwnodel(newFileName);
            //调用getSiteKeysByCustomerId接口，获取接口数据
            String throughNumResult = "";
            if ("St".equals(paramsMap.get("customerId"))){
                 throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.STAPPKEY.getValue(), Key.STAPPSECRET.getValue(), param);
            }else {
                 throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.JTAPPKEY.getValue(), Key.JTAPPSECRET.getValue(), param);
            }

            //解析转换json数据
            JSONObject jsonObject = JSONObject.parseObject(throughNumResult);
            //判断接口数据是否正常
            if(!ObjectUtils.isEmpty(jsonObject)&&jsonObject.containsKey("data")){
                JSONArray data = jsonObject.getJSONArray("data");
                //数据转换
                List<SiteEntry> dataCollect = data.stream().map(x ->
                     JSONObject.parseObject(x.toString(), SiteEntry.class)
                ).collect(Collectors.toList());

                //循环写入
                dataCollect.forEach(x->{
                    WriteTxt.writeTxt(newFileName,bw,x.getSiteKey(), x.getSiteName(), x.getType(),paramsMap.get("customerId"));
                    if("Wfjjt".equals(paramsMap.get("customerId"))){
                        try {
                            //获取场所设备总数
                            GetSiteDeviceDetailsTotal gt = new GetSiteDeviceDetailsTotal();
                            String total = gt.exec(x.getSiteKey(), x.getType());
                            //获取场所设备详情
                            GetSiteDeviceDetails gd = new GetSiteDeviceDetails();
                            gd.exec(x.getSiteKey(),x.getType(),total);
                            System.out.println("---------------------------"+total+"---------------------------");
                        }catch (Exception e){
                            log.error(x.getSiteKey()+"场所设备数据获取失败");

                        }
                    }

                });

                bw.close();

            }else {
                log.error("客流系统getSiteKeysByCustomerId接口调用反参无数据，入参为："+param);
            }

        }catch (Exception e){
            log.error("客流系统getSiteKeysByCustomerId接口调用失败，入参为："+param+",异常为："+e.toString());
        }
    }







}
