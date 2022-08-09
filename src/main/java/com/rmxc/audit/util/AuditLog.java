package com.rmxc.audit.util;

import com.rmxc.audit.vo.JdbcResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.tools.ant.taskdefs.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class AuditLog {

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dfl = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String sourceKey = "mysql";


    /**
     * 插入日志
     * @Param dataSource:数据库名称
     * @Param table:数据表名称
     * @Param status:稽核结果
     * @Param loginfo:结果日志
     * @Param mode:稽核种类
     * @Param svalue:源端稽核值
     * @Param dvalue:目的端稽核值
     *
     * @Param type：稽核区间
     * @Author lihao
     */
    public static void addLog(String dataSource,String stable,String dtable,String status,String loginfo,String incr,String model,String svalue,String dvalue,String type,String ssql,String dsql){
        //校验入参
        if (!ToolUtils.checkEmpty(dataSource, stable, status, loginfo)){
            log.error(LogMsg.PARAM_ERROR.getMessage());
        }

        String sql = String.format(SqlModel.addLog, dataSource, stable,dtable, status, loginfo, dfl.format(LocalDateTime.now()), df.format(LocalDateTime.now()),incr,model,svalue,dvalue,type
                ,ssql.replace("'","\"")
                ,dsql.replace("'","\""));
        JdbcResult jdbcResult = DataBaseConn.updateMsg(sourceKey, sql);

        int urs = jdbcResult.getUrs();
        if (1!=urs){
            log.error(LogMsg.LOG_WEITE_ERROR.getMessage());
        }else {
            log.info(LogMsg.LOG_WEITE_SUCCESSS.getMessage());
        }
        //关闭数据库连接
        DataBaseConn.closeConn(jdbcResult);
    }


    /**
     * 稽核结果总结邮件
     * @Param duration:稽核脚本执行时间
     * @Author lihao
     */
    public static void sendResultEmail(long duration)  {
        //校验入参
        if (!ObjectUtils.isEmpty(duration)&&duration<0){
            log.error(LogMsg.PARAM_ERROR.getMessage());
        }
        String nowDate = df.format(LocalDateTime.now());
        String sql = String.format(SqlModel.auditResult, nowDate);
        JdbcResult jdbcResult = DataBaseConn.queryMsg(sourceKey, sql);
        ResultSet rs = jdbcResult.getRs();
        try {
            int columnCount = rs.getMetaData().getColumnCount();
            List<String> rowList = new ArrayList<>();
            //获取全部数据结果
            while (rs.next()){
                String rowString = "";
                for(int i = 1;i<=columnCount;i++){
                    rowString = rowString+"/"+rs.getString(i);
                }
                rowList.add(rowString);
            }
            //分析数据结果
            Map<String,List<String>> factorys = new HashMap<>();
            rowList.stream().forEach(x->{
                String[] split = x.split("/");
                List<String> rows ;
                if (factorys.containsKey(split[4])){
                    rows=factorys.get(split[4]);
                }else {
                   rows = new ArrayList();
                }
                rows.add(split[3]+"/"+split[1]+"."+split[2]);
                factorys.put(split[4],rows);
            });
            //拼接字符串
            String text = String.format(LogMsg.EMAIL_TITLE.getMessage(),nowDate,duration==0?1:duration);
            List<String> trs = new ArrayList<>();
            factorys.forEach((x,y)->{
                List<String> errors = y.stream().filter(f -> f.startsWith("0")).map(m -> {
                    String[] plist = m.split("/");
                    return plist[1].replace("sybase","");
                }).collect(Collectors.toList());
                //拼接每行数据
                String tr = String.format(LogMsg.EMAIL_TABLE.getMessage(), x, y.size(), y.size() - errors.size(), errors.size(), errors.stream().collect(Collectors.joining(",")));
                trs.add(tr);
            });
            text = text+ trs.stream().collect(Collectors.joining(" "))+LogMsg.EMAIL_PS.getMessage()+ LogMsg.EMAIL_ENDING.getMessage();
            SendToMail.SendEmail(nowDate+"日稽核结果",text);
        }catch (Exception e){
            log.error("稽核结果邮件生成报错:"+e.toString());
        }
    }


}
