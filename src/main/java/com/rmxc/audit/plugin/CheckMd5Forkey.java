package com.rmxc.audit.plugin;

import com.alibaba.fastjson.JSONObject;
import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.audit.util.SqlModel;
import com.rmxc.audit.vo.JdbcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CheckMd5Forkey {

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 抽样取唯一键值
     * @Param dataSourceName:数据库编码
     * @Param table.txt:数据表名
     * @Param keyCode:唯一键名称
     * @Param suffix:sql后缀
     * @Return keyList:唯一键值列表
     * @Author lihao
     * */
    private List<List<String>> sampleKeyList(String dataSourceName, String table, String keyCode,String suffix)  {
        List<List<String>> keyList=null;
        try {
            //获取唯一键值列
            String queryOneSql = String.format(SqlModel.queryOne, keyCode,table,suffix);
            JdbcResult jdbcResult = DataBaseConn.queryMsg(dataSourceName, queryOneSql);
            ResultSet resultSet =jdbcResult.getRs();
            if (ObjectUtils.isEmpty(resultSet)){
                return null;
            }
            List<String> codes = new ArrayList<>();
            while (resultSet.next()){
                codes.add(resultSet.getString(1));
            }
            //关闭连接
            DataBaseConn.closeConn(jdbcResult);

            //随机抽样
            if(codes.size()>0){
                keyList=randomSampling(codes);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("数据表数据抽样失败，异常信息：",e.toString());
        }
        return keyList;
    }


    /**
     * 抽样方法
     * @Param allList:全列表
     * @Return samplinglist:抽样列表
     * @Author lihao
     * */
    private List<List<String>> randomSampling(List<String> allList) {

        //批次大小
        int batchSize = allList.size() / 5;
        //批次数量
        int batchNum = 1;

        if (batchSize<20){
            batchSize = allList.size();
            batchNum = 1;
        }else if(batchSize>500){
            batchSize = 100;
            batchNum = 5;
        }
        // 为避免一次抽样条数过多导致数据库内存溢出，分批次抽样
        List<List<String>> samplinglist =new ArrayList<>();
        for (int n=0;n<batchNum;n++){
            int[] arraysNorepeat = getArraysNorepeat(0, allList.size() - 1, batchSize);
            List<String> samplings =new ArrayList<>();
            for (int i :arraysNorepeat){
                samplings.add(allList.get(i));
            }
            allList.removeAll(samplinglist);

            samplinglist.add(samplings);
        }

        return samplinglist;
    }

    /**
     * 获取不重复的随机数组
     * @Param min 随机数范围最小值
     * @Param max 随机数范围最大值
     * @Param amount  随机数个数
     * @Return 传入的随机数个数长度的数组
     * 因为随机数不可重复，所以最大值减去最小值不能小于随机数个数
     * @Author lihao
     */
    private int[] getArraysNorepeat(int min,int max,int amount){
        if((max - min) < amount){
            throw new RuntimeException("参数异常：随机个数大于随机范围");
        }
        int arr[] = new int[amount];
        int ran,i=0;
        boolean repeat;//记录是否存在重复
        while(i < arr.length){
            repeat = false;
            ran = (int) (Math.random()*(max-min)+min);
            for (int j = 0; j < arr.length; j++) {
                if( arr[j] == ran){
                    repeat = true;
                    break;
                }
            }
            if(!repeat){
                arr[i] = ran;
                i++;
            }
        }
        return arr;
    }


    /**
     * 抽值加密
     * @Param dataSourceName:数据库编码
     * @Param table.txt:数据表名
     * @Param keyCode:唯一键名称
     * @Param keyList:唯一键值列表
     * @Return md5Str:md5加密值
     * @Author lihao
     * */
    private String changeMsgForMd5(String dataSourceName,String table,String keyCode,List<String> keyList)  {
        String md5Str="";

        try {
            if (ObjectUtils.isEmpty(keyList)){
                return null;
            }
            //拼接查询条件
            String criteria = "where "+keyCode+ " in ('" +keyList.stream().collect(Collectors.joining("','"))+"') order by "+keyCode;
            //根据数据库切换查询模板
            String model = SqlModel.queryAllMsgByCase;
            if (dataSourceName.contains("sybase")){
                model = SqlModel.queryAllMsgByCaseS;
            }
            //获取唯一键值列
            String queryOneSql = String.format(model, table,criteria);
            JdbcResult jdbcResult = DataBaseConn.queryMsg(dataSourceName, queryOneSql);
            ResultSet resultSet =jdbcResult.getRs();
            if (ObjectUtils.isEmpty(resultSet)){
                return "";
            }
            List<String> msgs = new ArrayList<>();
            int columnCount = resultSet.getMetaData().getColumnCount();
            //日志写入
            String dirMsg ="./"+df.format(LocalDateTime.now()) +"log/";
            File dir = new File(dirMsg);
            if (!dir.exists()){
                dir.mkdir();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(dirMsg + dataSourceName+"_"+table+".txt", true));
            while (resultSet.next()){
                String rowString = "";
                for(int i = 1;i<=columnCount;i++){
                    rowString = rowString+" "+resultSet.getString(i);
                }
                rowString=rowString.replaceAll("\\.0+\\s",".0");
                writer.write(rowString+"\r\n");//写入文件
                msgs.add(rowString);
            }

            //转换字符串
            String json = JSONObject.toJSONString(msgs);
            //MD5加密
            md5Str = DigestUtils.md5DigestAsHex(json.getBytes());
            writer.write("MD5加密值为："+md5Str);
            writer.flush();//清空缓冲区数据
            writer.close();//关闭读写流
            //关闭连接
            DataBaseConn.closeConn(jdbcResult);
        }catch (IOException e){
            log.error("数据日志写入失败，异常信息：",e.toString());
        }catch (Exception e){
            log.error("数据表数据抽样失败，异常信息：",e.toString());
        }
        return md5Str;
    }



    /**
     * 数据表内容稽核
     * @Param syBaseDataSource:sybase数据库编码
     * @Param hiveDataSource:hive数据库编码
     * @Param syBaseTable：sybase数据表
     * @Param hiveTable：hive数据表
     * @Param keyCode：主键编码
     * @Param suffix：sql后缀
     * @Return boolean ：稽核结果
     * @Author lihao
     * */
    public Boolean checkMd5(String syBaseDataSource, String hiveDataSource, String syBaseTable, String hiveTable, String keyCode,String suffix){
        //抽样抽取主键
        List<List<String>> keyList = sampleKeyList(syBaseDataSource, syBaseTable, keyCode,suffix);
        String smd5 = "";
        String hmd5 = "";
        //拉取数据md5加密计算
        for (List<String> keys:keyList){
             smd5 = smd5 + changeMsgForMd5(syBaseDataSource, syBaseTable, keyCode, keys);
             hmd5 = hmd5 + changeMsgForMd5(hiveDataSource, hiveTable, keyCode, keys);
        }
        return (!Strings.isEmpty(smd5)&&smd5.equals(hmd5));
    }
}
