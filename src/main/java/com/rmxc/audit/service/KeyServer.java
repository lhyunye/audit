package com.rmxc.audit.service;

import com.rmxc.audit.Factory.AuditService;
import com.rmxc.audit.plugin.CheckCount;
import com.rmxc.audit.plugin.CheckMd5Forkey;
import com.rmxc.audit.util.AuditLog;
import com.rmxc.audit.util.LogMsg;
import com.rmxc.audit.util.ToolUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Slf4j
public class KeyServer extends AuditService {

    ///废弃

    //稽核流程类型    数据文件内容：  数据库名称  数据表名称  主键  同步方式
    private final String name = "key";

    //稽核流程插件
    //同步数据行数稽核
    private static CheckCount cc = new CheckCount();
    //抽样md5加密稽核（须有唯一值）
    private static CheckMd5Forkey cmk = new CheckMd5Forkey();


    @Override
    public void exec() {
        ToolUtils toolUtils = new ToolUtils();
        //获取数据表信息
        List<String> textLine = toolUtils.getTextLine("./table/" + name);
        //稽核准备
        for (String line : textLine) {
            String[] words = line.split(" +");
            if (words.length == 5) {
                //数据稽核
                checkStream(words[0], words[1], words[2],words[3],words[4],words[5]);
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
     * @Param keyCode：主键编码
     * @Param type：同步类型  all(全表同步)  append(增量同步)  yyyy-MM-dd (自定义时间稽核)
     * @Param iner: 数据增量判别字段
     * @Param dstatus：全量和增量识别标志  di 增量   dd 全量
     * @Return
     * @Author lihao
     */
    private void checkStream(String syBaseDataSource, String syBaseTable, String keyCode,String type,String incr,String dstatus) {
        String hiveDataSource = ToolUtils.getHiveSource();
        String hiveTable = ToolUtils.getHiveTable(syBaseDataSource,syBaseTable);
        String ssuffix = ToolUtils.getSybaseSuffix(type,incr);
        String hsuffix = ToolUtils.getHiveSuffix(type,incr,dstatus);
        //行数稽核
        String sCount = "";//cc.checkCount(syBaseDataSource, hiveDataSource, syBaseTable, hiveTable,ssuffix,hsuffix);
        // 日志记录
        String logInfo = "";
        String status = "1";
        if (!Strings.isEmpty(sCount)&&!sCount.contains("异常")) {
            log.info(syBaseTable + " 行数稽核成功，共：" + sCount + "条数据，进行md5加密稽核");
            //抽样内容稽核
            Boolean result = cmk.checkMd5(syBaseDataSource, hiveDataSource, syBaseTable, hiveTable, keyCode,ssuffix);
            if (result) {
                //稽核成功
                logInfo = syBaseTable + LogMsg.MD5_AUDIT_SUCCESS.getMessage();
                log.info(logInfo);
            } else {
                //稽核失败
                logInfo = syBaseTable + LogMsg.MD5_AUDIT_ERROR.getMessage();
                status = "0";
                log.error(logInfo);
            }
        } else {
            //行数稽核失败
            logInfo = syBaseTable + LogMsg.ROW_AUDIT_ERROR.getMessage()+sCount;
            status = "0";
            sCount = "";
            log.error(logInfo);
        }
        //写入日志
       // AuditLog.addLog(syBaseDataSource, syBaseTable, status, logInfo, name,sCount,type);
    }

}
