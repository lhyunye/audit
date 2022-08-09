package com.rmxc.audit.service;

import com.rmxc.audit.Factory.AuditService;
import com.rmxc.audit.plugin.CheckCount;
import com.rmxc.audit.plugin.CheckMd5Forkey;
import com.rmxc.audit.plugin.CheckString;
import com.rmxc.audit.util.AuditLog;
import com.rmxc.audit.util.LogMsg;
import com.rmxc.audit.util.ToolUtils;
import com.rmxc.audit.vo.CheckResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Slf4j
public class StringServer extends AuditService {

    //稽核流程类型    数据文件内容：  数据源名称  数据表名称  hive数据表名称  稽核方式（all/指定日期） 增量字段 check字段字段 主键 全量和增量识别标志
    private final String name = "string";

    //稽核流程插件

    //抽样md5加密稽核（须有唯一值）
    private static CheckString cmk = new CheckString();


    @Override
    public void exec() {
        ToolUtils toolUtils = new ToolUtils();
        //获取数据表信息
        List<String> textLine = toolUtils.getTextLine("./table/" + name);
        //稽核准备
        for (String line : textLine) {
            String[] words = line.split(" +");
            if (words.length == 7) {
                //数据稽核
                checkStream(words[0], words[1], words[2],words[3],words[4],words[5],words[6]);
            } else {
                log.error("数据格式异常，运维人员验证" + name + "文件对应数据：" + line);
            }
        }
    }

    /**
     * 数据稽核流程
     *
     * @Param syBaseDataSource:sybase数据库编码
     * @Param syBaseTable：sybase数据表
     * @Param hiveTable：hive数据表
     * @Param type：同步类型  all(全表)  append(增量)  yyyy-MM-dd (自定义时间稽核)  yyyy-MM-dd&yyyy-MM-dd(指定日期段)
     * @Param incr：增量字段
     * @Param check：string字段
     * @Param dstatus：全量和增量识别标志  di 增量   dd 全量
     * @Return
     * @Author lihao
     */
    private void checkStream(String syBaseDataSource, String syBaseTable, String hiveTable ,String type,String incr,String check,String dstatus) {
        String hiveDataSource = ToolUtils.getHiveSource();
        String ssuffix = ToolUtils.getSybaseSuffix(type,incr);
        String hsuffix = ToolUtils.getHiveSuffix(hiveTable,type,incr,dstatus);

        String status = "1";

        CheckResult checkResult = cmk.checkString(syBaseDataSource, hiveDataSource, syBaseTable, hiveTable, ssuffix, hsuffix, check);
        // 日志记录
        String logInfo = checkResult.getLog();
        if (Strings.isEmpty(logInfo)){
            logInfo = syBaseTable + " String稽核成功，"+check+"字段列（"+type+"）MD5值为：" + checkResult.getSvalue() + "";
            log.info(logInfo);
        } else {
            //String稽核失败
            logInfo = syBaseTable + LogMsg.STRING_AUDIT_ERROR.getMessage()+logInfo;
            status = "0";
            log.error(logInfo);
        }

        //写入日志
        AuditLog.addLog(syBaseDataSource, syBaseTable,hiveTable, status, logInfo,incr, name,checkResult.getSvalue(),checkResult.getDvalue(),type,checkResult.getSsql(),checkResult.getDsql());
    }

}
