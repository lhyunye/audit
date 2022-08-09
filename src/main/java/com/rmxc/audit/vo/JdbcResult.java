package com.rmxc.audit.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Data
@Slf4j
public class JdbcResult {

    //数据库连接
    private Connection conn;

    //执行语句
    private Statement stmt;

    //查询结果
    private ResultSet rs;

    //更新结果  1：成功  0：失败
    private int urs;

}
