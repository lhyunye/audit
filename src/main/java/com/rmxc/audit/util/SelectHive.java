package com.rmxc.audit.util;

import com.rmxc.audit.vo.JdbcResult;
import org.springframework.util.ObjectUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectHive {


    static String  sqlFormat = "select count(1) from %s where pt ='20210331'";



    public void selectHive()throws SQLException, IOException {
        ToolUtils toolUtils = new ToolUtils();
        //获取数据表信息
        List<String> textLine = toolUtils.getTextLine("./table/select");
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./table/a.txt")));
        int n = 1;
        for (String line:textLine){

            n++;
            String sql = String.format(sqlFormat,line);
            JdbcResult jdbcResult = DataBaseConn.queryMsg("hive_prod", sql);
            ResultSet resultSet = jdbcResult.getRs();
            //获取结果
            String count = "";
            if (!ObjectUtils.isEmpty(resultSet)&&resultSet.next()){
                count = resultSet.getString(1);
                bw.write(line+" "+count+"\n");

            }
            System.out.println("----------------------------"+line+"------"+n+"-----"+count+"--------------------");
            DataBaseConn.closeConn(jdbcResult);
        }
        bw.close();

    }



}
