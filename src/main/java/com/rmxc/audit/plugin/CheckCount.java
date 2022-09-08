package com.rmxc.audit.plugin;

import com.alibaba.fastjson.JSONObject;
import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.audit.util.SqlModel;
import com.rmxc.audit.vo.CheckResult;
import com.rmxc.audit.vo.DealResult;
import com.rmxc.audit.vo.JdbcResult;
import jodd.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CheckCount {


    /**
     * 查询数据表行数
     * @Param dataSourceName:数据库编码
     * @Param table:数据表名
     * @Param suffix:sql后缀,可自定义
     * @Return count:数据表行数
     * @Author lihao
     * */
    private DealResult selectCount(String dataSourceName, String table, String suffix)  {

        String count ="0";
        DealResult dealResult = new DealResult();
        String sql = "";
        try {
            sql = String.format(SqlModel.queryCountByCase, table,suffix);
            System.out.println(sql);
            JdbcResult jdbcResult = DataBaseConn.queryMsg(dataSourceName, sql);
            ResultSet resultSet = jdbcResult.getRs();
            if (!ObjectUtils.isEmpty(resultSet)&&resultSet.next()){
                count = resultSet.getObject(1).toString();
                //关闭连接
               DataBaseConn.closeConn(jdbcResult);
            }
        }catch (Exception e){
            log.error("数据表行数查询失败，异常信息：",e.toString());
        }
        dealResult.setSql(sql);
        dealResult.setValue(count);
        return dealResult;
    }



    /**
     * 数据表行数稽核
     * @Param syBaseDataSource:sybase数据库编码
     * @Param hiveDataSource:hive数据库编码
     * @Param syBaseTable：sybase数据表
     * @Param hiveTable：hive数据表
     * @Author lihao
     * */
    public CheckResult checkCount(String syBaseDataSource, String hiveDataSource, String syBaseTable, String hiveTable,String ssuffix,String hsuffix)  {
        DealResult sdeal = selectCount(syBaseDataSource, syBaseTable, ssuffix);
        DealResult ddeal =selectCount(hiveDataSource, hiveTable,hsuffix);
        CheckResult checkResult = new CheckResult();
        checkResult.setDsql(ddeal.getSql());
        checkResult.setDvalue(ddeal.getValue());
        checkResult.setSsql(sdeal.getSql());
        checkResult.setSvalue(sdeal.getValue());
        if (Strings.isEmpty(sdeal.getValue()) || !sdeal.getValue().equals(ddeal.getValue())) {
            //异常二字为判断条件，不可修改
            checkResult.setLog("异常：sybase数据库条数为："+sdeal.getValue()+",hive数据库条数为："+ddeal.getValue());
        }
        return checkResult;
    }



}
