package com.rmxc.pulldata.passengerFlow.apiList;

import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.pulldata.passengerFlow.utils.CommandUtil;
import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import com.rmxc.pulldata.passengerFlow.utils.util.KeLiuHttpUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiModel {

    public static DateTimeFormatter df_day = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_DAY.getValue());
    public static DateTimeFormatter df_day2 = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_DAY2.getValue());
    public static DateTimeFormatter df_second = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_SECOND.getValue());
    public static DateTimeFormatter df_zero = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_ZERO.getValue());
    public static DateTimeFormatter df_month = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_MONTH.getValue());
    public static DateTimeFormatter df_all = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_ALL.getValue());
    //时间粒度
    public static String interval="D";
    //环境
    private static String env = Key.HIVE_DATASOURCE_NAME_PROD.getValue();

    /**
     * 方法执行入口，主要填写接口入参
     * */
    public abstract  void exec();


    /**
     * api接口调用方法，拉取数据到本地
     * */
    public abstract void pullData(Map<String,String> paramsMap);

    /**
     * 数据load接口，导入到hive中
     * */
    public static void commitHive(String fileName,String tableName,String temporaryTableName,String pt) throws SQLException, IOException {

        if (Strings.isEmpty(pt)){
            commitHiveCustom(fileName,tableName,temporaryTableName,LocalDateTime.now().minusDays(1).format(df_day));
        }else {
            commitHiveCustom(fileName,tableName,temporaryTableName, LocalDate.parse(pt,df_day2).format(df_day));
        }

    }

    /**
     * 数据load接口，导入到hive中 自定义分区值
     * */
    public static void commitHiveCustom(String fileName,String tableName,String temporaryTableName,String partitonValue) throws SQLException, IOException {
        //获取当前路径
        File directory = new File("dataMessage");
        String canonicalPath = directory.getCanonicalPath().replace("\\","/");
        System.out.println(canonicalPath);
        String hdfsFileName = canonicalPath + "/" + fileName + LocalDateTime.now().format(df_day);
        String loaddatatohdfs = String.format(Key.LOAD_LOCAL_DATA_TO_HDFS.getValue(), hdfsFileName, fileName);
        System.out.println(loaddatatohdfs);
        CommandUtil.run(loaddatatohdfs);
//        //生成数据导入sql  hdfs本地-》hive 临时表
        String loaddata = String.format(Key.LOAD_LOCAL_DATA_SQL.getValue(), fileName, temporaryTableName);
//        //执行
        DataBaseConn.ddlsql(env,loaddata);
        //生成数据导入sql hive 临时表-》 hive 正式表
        String format = String.format(Key.LOAD_HIVE_DATA_SQL.getValue(), tableName, partitonValue, temporaryTableName);
        //执行
        DataBaseConn.ddlsql(env,format);
    }

    public static String dopost(Map<String, String> paramsMap,String apiPath,String param) throws Exception{
        String throughNumResult="";
        if ("St".equals(paramsMap.get("customerId"))) {
            throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.STAPPKEY.getValue(), Key.STAPPSECRET.getValue(), param);
        } else {
            throughNumResult = KeLiuHttpUtils.doPost(Key.HOST.getValue(), apiPath, Key.JTAPPKEY.getValue(), Key.JTAPPSECRET.getValue(), param);
        }
        return throughNumResult;
    }

}
