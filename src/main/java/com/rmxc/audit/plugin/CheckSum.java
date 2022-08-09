package com.rmxc.audit.plugin;

import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.audit.util.SqlModel;
import com.rmxc.audit.vo.CheckResult;
import com.rmxc.audit.vo.DealResult;
import com.rmxc.audit.vo.JdbcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CheckSum {




    /**
     * 查询数据表字段之和
     * @Param dataSourceName:数据库编码
     * @Param table:数据表名
     * @Param suffix:sql后缀,可自定义
     * @Return precision:精度
     * @Author lihao
     * */
    private DealResult selectSum(String dataSourceName,String table,String sum ,String suffix,String precision)  {
        String count ="";
        DealResult dealResult = new DealResult();
        try {
            String sql = "";
            String[] split = sum.split("/");
            //拼接sql
            //根据有无精度标准确认是何种类型的sql model
            if (ObjectUtils.isEmpty(precision)){
                String collect = Arrays.stream(split).map(x->String.format(SqlModel.sumSybase,x)).collect(Collectors.joining("+"));
                sql = String.format(SqlModel.querySybaseSumByCase, collect,table,suffix);
            }else {
                String collect = Arrays.stream(split).map(x -> String.format(SqlModel.sumHive, x,precision)).collect(Collectors.joining(","));
                sql = String.format(SqlModel.queryHiveSumByCase, collect,table,suffix);
            }
            System.out.println(sql);
            dealResult.setSql(sql);
            JdbcResult jdbcResult = DataBaseConn.queryMsg(dataSourceName, sql);
            ResultSet resultSet = jdbcResult.getRs();

            if (ObjectUtils.isEmpty(precision)){
                //获取结果
                if (!ObjectUtils.isEmpty(resultSet)&&resultSet.next()){
                    count = resultSet.getString(1);

                }
            }else {
                if (!ObjectUtils.isEmpty(resultSet)&&resultSet.next()){
                    int i = 1;
                    count = resultSet.getString(1);
                    List<BigDecimal> values = new ArrayList<>();
                    while (true){
                        try {
                            String value = resultSet.getString(i);
                            double v = Double.parseDouble(value);
                            BigDecimal bigDecimal = new BigDecimal(value);
                            values.add(bigDecimal);
                            i++;
                        }catch (Exception e){
                            break;
                        }
                    }
                    BigDecimal bigss=BigDecimal.ZERO;

                    for(BigDecimal b :values){
                        bigss=bigss.add(b);
                    }
                    count = bigss.setScale(Integer.parseInt(precision),BigDecimal.ROUND_UP).toString();
                }
                      }
            //关闭连接
            DataBaseConn.closeConn(jdbcResult);
            if (ObjectUtils.isEmpty(count)){
                count= "0";
            }
            dealResult.setValue(count);
        }catch (Exception e){
            log.error("数据表行数查询失败，异常信息：",e.toString());
            dealResult.setValue("0");
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
     * @Param sum：sum字段
     * @Param precision：精度
     * @Author lihao
     * */
    public CheckResult checkSum(String syBaseDataSource, String hiveDataSource, String syBaseTable, String hiveTable,String ssuffix,String hsuffix,String sum,String precision)  {
        CheckResult checkResult = new CheckResult();
        DealResult sdeal = selectSum(syBaseDataSource, syBaseTable, sum, ssuffix, null);
        //无设置精度时，以sybase中查询出来的精度为准
        String sSumNumber = sdeal.getValue();
        if ("no".equals(precision)){
            if (!Strings.isEmpty(sSumNumber)&&sSumNumber.contains(".")){
                precision=String.valueOf(sSumNumber.split("\\.")[1].length());
            }else {
                precision= "0";
            }
        }
        DealResult ddeal = selectSum(hiveDataSource, hiveTable,sum,hsuffix,precision);
        checkResult.setSsql(sdeal.getSql());
        checkResult.setDsql(ddeal.getSql());
        checkResult.setSvalue(sdeal.getValue());
        checkResult.setDvalue(ddeal.getValue());

        if (Strings.isEmpty(sSumNumber) || !sSumNumber.equals(ddeal.getValue())) {
            //异常二字为判断条件，不可修改
            checkResult.setLog( "异常：sybase "+sum+"数据之和为："+sSumNumber+",hive "+sum+"数据之和为："+ddeal.getValue());
        }
        return checkResult;
    }



}
