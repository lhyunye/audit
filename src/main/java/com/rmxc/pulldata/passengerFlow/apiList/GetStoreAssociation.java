package com.rmxc.pulldata.passengerFlow.apiList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import com.rmxc.pulldata.passengerFlow.vo.SiteDeviceDetail;
import com.rmxc.pulldata.passengerFlow.vo.StoreAssociation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.io.BufferedWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口返回，通过月份获取店铺关联度数据
 */
@Slf4j
public class GetStoreAssociation extends ApiModel {

    private static String apiPath = "/huikeapi/passengerFlowData/V3/getStoreAssociationDegreeByDate";
    private static String fileName = "StoreAssociation";
    //hive表名
    private static String temporaryTableName = "ods_api_keliu_store_association_di_temporary";
    private static String tableName = "ods_api_keliu_store_association_di";
    //获取当前月的前一月
    private static String modifyTime = LocalDateTime.now().minusMonths(1).format(df_month);




    /**
     * 重构执行方法
     */
    public void exec(String date) {


        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("customerId", "Wfjjt");
        if (!Strings.isEmpty(date)){
          modifyTime = LocalDate.parse(date,df_day2).format(df_month);
        }
        paramsMap.put("date", modifyTime);
        pullData(paramsMap);
        try {
            //导入hive
            commitHiveCustom(fileName,tableName,temporaryTableName,modifyTime.replace("-",""));
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
            String throughNumResult = dopost(paramsMap,apiPath,param);

            //解析转换json数据
            JSONObject jsonObject = JSONObject.parseObject(throughNumResult);
            //判断接口数据是否正常
            if (!ObjectUtils.isEmpty(jsonObject) && jsonObject.containsKey("data")) {
                JSONArray data = jsonObject.getJSONArray("data");
                //数据转换
                List<StoreAssociation> dataCollect = data.stream().map(x ->
                        JSONObject.parseObject(x.toString(), StoreAssociation.class)
                ).collect(Collectors.toList());
                //循环写入
                dataCollect.forEach(x->{
                    WriteTxt.writeTxt(newFileName,bw,x.getSource(),x.getSourceName(),x.getTarget(),x.getTargetName(),x.getLabel(),
                            x.getDate(),paramsMap.get("customerId"));
                });
                bw.close();

            } else {
                log.error("客流系统getStoreAssociationDegreeByDate接口调用反参无数据，入参为：" + param);
            }

        } catch (Exception e) {
            log.error("客流系统getStoreAssociationDegreeByDate接口调用失败，入参为：" + param + ",异常为：" + e.toString());
        }


    }
}
