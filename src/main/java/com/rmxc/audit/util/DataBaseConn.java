package com.rmxc.audit.util;

import com.rmxc.audit.vo.JdbcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class DataBaseConn {

    private static String userName = "spring.%s.username";
    private static String password = "spring.%s.password";
    private static String url = "spring.%s.url";
    private static String driver = "spring.%s.driver-class-name";


    /**
     * 获取数据库连接
     * @Param dataSourceName:数据库名称
     * @Return conn:数据库连接
     * @Author lihao
     */
    public static Connection getConn(String dataSourceName){
        Connection conn =null;
        try {
            //获取数据库连接信息
            String userNameValue = String.format(userName, dataSourceName);
            String passwordValue = String.format(password, dataSourceName);
            String urlValue = String.format(url, dataSourceName);
            String driverValue = String.format(driver, dataSourceName);
            Properties properties = ToolUtils.readPropertiesFile("application.properties");
            //数据库连接
            Class.forName(properties.getProperty(driverValue)).newInstance();
            Properties sysProps = System.getProperties();
            sysProps.put("user", properties.getProperty(userNameValue)); // 设置数据库访问用户名
            sysProps.put("password", properties.getProperty(passwordValue)); // 密码
            conn = DriverManager.getConnection(properties.getProperty(urlValue), sysProps);//连接url

        }catch (Exception e){
            log.error("数据库连接异常："+e.toString());
        }
       return conn;
    }


    /**
     * 关闭数据库连接
     * @Param jr:数据库连接
     * @Author lihao
     */
    public static void closeConn(JdbcResult jr) {
        try {
            if (!ObjectUtils.isEmpty(jr)){
                if (!ObjectUtils.isEmpty(jr.getRs())){
                    jr.getRs().close();
                }
                jr.getStmt().close();
                jr.getConn().close();
            }
        }catch (Exception e){
            log.error("数据库连接关闭异常："+e.toString());
        }

    }
    /**
     * 选择数据库执行查询sql
     * @Param dataSourceName:数据库编码
     * @Param sql:执行sql
     * @Return rs:执行结果
     * @Author lihao
     * */
    public static JdbcResult queryMsg(String dataSourceName, String sql) {
        JdbcResult jr = new JdbcResult();
        try {
            //校验参数
            if (!ToolUtils.checkEmpty(dataSourceName,sql)) {
                return null;
            }
           //获取数据库连接
            Connection conn = getConn(dataSourceName);
            if(!ToolUtils.checkEmpty(conn)){
                return null;
            }
            Statement stmt = conn.createStatement();
            //执行sql
            ResultSet rs = stmt.executeQuery(sql);
            jr.setConn(conn);
            jr.setStmt(stmt);
            jr.setRs(rs);
            log.info("执行sql:"+sql);
        } catch (Exception e) {
            log.error("执行sql异常："+e.toString());
        }

        return jr;
    }


    /**
     * 选择数据库执行ddl sql
     * @Param dataSourceName:数据库编码
     * @Param sql:执行sql
     * @Return rs:执行结果
     * @Author lihao
     * */
    public static Boolean ddlsql(String dataSourceName, String sql) {
        boolean execute = false;
        try {
            //校验参数
            if (!ToolUtils.checkEmpty(dataSourceName,sql)) {
                return null;
            }
            //获取数据库连接
            Connection conn = getConn(dataSourceName);
            if(!ToolUtils.checkEmpty(conn)){
                return null;
            }
            Statement stmt = conn.createStatement();
            //执行sql
            execute = stmt.execute(sql);
            conn.close();

            log.info("执行sql:"+sql);
        } catch (Exception e) {
            log.error("执行sql异常："+e.toString());
        }

        return execute;
    }

    /**
     * 选择数据库执行更新sql
     * @Param dataSourceName:数据库编码
     * @Param sql:执行sql
     * @Return rs:执行结果
     * @Author lihao
     * */
    public static JdbcResult updateMsg(String dataSourceName, String sql) {
        JdbcResult jr = new JdbcResult();
        try {
            //校验参数
            if (!ToolUtils.checkEmpty(dataSourceName,sql)) {
                return null;
            }
            //获取数据库连接
            Connection conn = getConn(dataSourceName);
            if(!ToolUtils.checkEmpty(conn)){
                return null;
            }
            Statement stmt = conn.createStatement();
            //执行sql
            int rs = stmt.executeUpdate(sql);
            jr.setConn(conn);
            jr.setStmt(stmt);
            jr.setUrs(rs);
            log.info("执行sql:"+sql);
        } catch (Exception e) {
            log.error("执行sql异常："+e.toString());
        }

        return jr;
    }

}
