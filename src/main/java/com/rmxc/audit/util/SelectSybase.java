package com.rmxc.audit.util;

import com.rmxc.audit.vo.JdbcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class SelectSybase {


    public void exec(String path) throws SQLException {
        ToolUtils toolUtils = new ToolUtils();
        //获取数据表信息
        List<String> textLine = toolUtils.getTextLine(path);
        //稽核准备
        for (String line : textLine) {
            String[] words = line.split("\\|");
            if (words.length == 2) {
                //数据稽核
                querySql(words[0], words[1]);
            } else {
                log.error("数据格式异常" + line);
            }
        }


    }


    public  void querySql(String source,String sql) throws SQLException {

        if (Strings.isEmpty(source)||Strings.isEmpty(sql)){
            System.out.println("请输入数据源或查询sql  格式为:数据源同sql之间制表符分隔");
        }
        sql = sql.replace(";","");

        JdbcResult jdbcResult = DataBaseConn.queryMsg(source, sql);
        ResultSet resultSet = jdbcResult.getRs();
        System.out.println("数据源为"+source+"执行sql为："+sql);
        System.out.println("----------------------------------------------------------");
        String head = sql.split("from")[0].replace("select", "").replace("SELECT", "");
        System.out.println(head);
        while (!ObjectUtils.isEmpty(resultSet)&&resultSet.next()){
            List<String> values = new ArrayList<>();
            int i = 1;
            while (true){
                try {
                    String value = resultSet.getObject(i).toString();
                    i++;
                    values.add(value);
                }catch (Exception e){
                    break;
                }
            }
            System.out.print("|");
            values.stream().forEach(x-> {System.out.print(x+" | ");});
            System.out.println("");

        }
        //关闭连接
        DataBaseConn.closeConn(jdbcResult);

    }
}
