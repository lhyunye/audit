package com.rmxc.audit.plugin;

import com.alibaba.fastjson.JSONObject;
import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.audit.util.SqlModel;
import com.rmxc.audit.vo.CheckResult;
import com.rmxc.audit.vo.DealResult;
import com.rmxc.audit.vo.JdbcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CheckString {




    /**
     * 查询数据表字段之和
     * @Param dataSourceName:数据库编码
     * @Param table:数据表名
     * @Param check:核查字段
     * @Param suffix:sql后缀,可自定义
     * @Author lihao
     * */
    private DealResult selectString(String dataSourceName,String table,String check ,String suffix)  {

        StringBuffer message = new StringBuffer();
        String md5Str = "";
        DealResult dealResult = new DealResult();
        try {
            //拼接sql
            String[] split = check.split("/");
            String checkformat = "" ;
            if("hive_prod".equals(dataSourceName)){
                checkformat = String.format(SqlModel.queryHiveString, Arrays.stream(split).collect(Collectors.joining(",")));
            }else {
                checkformat = String.format(SqlModel.querySybaseString, Arrays.stream(split).collect(Collectors.joining("||")));

            }
            String sql = String.format(SqlModel.queryOneByCase, checkformat,table,suffix+SqlModel.queryStringEnd);

            JdbcResult jdbcResult = DataBaseConn.queryMsg(dataSourceName, sql);
            ResultSet resultSet = jdbcResult.getRs();
            List<String> codes = new ArrayList<>();
            //获取结果
            while (resultSet.next()){
                codes.add(resultSet.getString(1));
            }
            resultSet.close();
            String sodeString = JSONObject.toJSONString(codes);
            sodeString = sodeString.replaceAll("\r|\n|\\s", "");
            //MD5加密
            md5Str = DigestUtils.md5DigestAsHex(sodeString.getBytes());
            dealResult.setValue(md5Str);
            dealResult.setSql(sql);
        }catch (Exception e){
            log.error("数据表行数查询失败，异常信息：",e.toString());
        }
        return dealResult;
    }



    /**
     * 数据表sum稽核
     * @Param syBaseDataSource:sybase数据库编码
     * @Param hiveDataSource:hive数据库编码
     * @Param syBaseTable：sybase数据表
     * @Param hiveTable：hive数据表
     * @Param ssuffix：sybase数据查询后缀
     * @Param hsuffix：hive数据查询后缀
     * @Param check：String字段
     * @Author lihao
     * */
    public CheckResult checkString(String syBaseDataSource, String hiveDataSource, String syBaseTable, String hiveTable,String ssuffix,String hsuffix,String check)  {
        DealResult sdeal = selectString(syBaseDataSource, syBaseTable, check, ssuffix);
        DealResult ddeal = selectString(hiveDataSource, hiveTable,check,hsuffix);
        CheckResult checkResult = new CheckResult();
        checkResult.setSvalue(sdeal.getValue());
        checkResult.setDvalue(sdeal.getValue());
        checkResult.setSsql(sdeal.getSql());
        checkResult.setDsql(ddeal.getSql());
        if (Strings.isEmpty(sdeal.getValue()) || !sdeal.getValue().equals(ddeal.getValue())) {
            checkResult.setLog("异常：sybase "+check+"数据列Md5加密值为："+sdeal.getValue()+",hive "+check+"数据列Md5加密值为："+ddeal.getValue());
        }
        //异常二字为判断条件，不可修改
        return checkResult;
    }



}
