package com.rmxc.checkStatus;

import com.rmxc.audit.util.DataBaseConn;
import com.rmxc.audit.vo.JdbcResult;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import org.springframework.util.ObjectUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
//根据输入的门店查看 开闭工标识
public class Check {


    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    public static void exec(String stroeCode ) throws SQLException, IOException {

        JdbcResult jdbcResult = DataBaseConn.queryMsg(stroeCode, "SELECT * FROM HT_ERRORS WHERE RQ >= '"+ LocalDateTime.now().format(df) +"' AND PROCNAME = 'everydaydo_end'");
        ResultSet resultSet = jdbcResult.getRs();
        List<String> codes = new ArrayList<>();
        if (ObjectUtils.isEmpty(resultSet)){
            return;
        }
        //获取返回值
        while (resultSet.next()){
            codes.add(resultSet.getString(1));
        }
        if (codes.size()>0){
            //获取最新开闭工完成时间
            String time = codes.get(codes.size() - 1);
            String date = time.split(" ")[0].replace("-","");
            String newFileName = stroeCode  + date + ".txt";
            //记录
            BufferedWriter bw = WriteTxt.getbw(newFileName);
            WriteTxt.writeTxt(newFileName,bw,time);
            bw.close();
        }

    }



}
