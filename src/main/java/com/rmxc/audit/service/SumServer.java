package com.rmxc.audit.service;

import com.rmxc.audit.Factory.AuditService;
import com.rmxc.audit.plugin.CheckCount;
import com.rmxc.audit.plugin.CheckSum;
import com.rmxc.audit.util.*;
import com.rmxc.audit.vo.CheckResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.concurrent.ExecutorService;



@Slf4j
public class SumServer extends AuditService {

    //稽核流程类型    数据文件内容：  数据源名称  数据表名称  hive数据表名称  稽核方式（all/指定日期） 增量字段 sum字段 精度
    private final String name = "sum";

    //稽核流程插件
    CheckSum cs = new CheckSum();


    @Override
    public void exec() throws InterruptedException {
        ToolUtils toolUtils = new ToolUtils();
        //获取数据表信息
        List<String> textLine = toolUtils.getTextLine("./table/" + name);
        JedisCluster redis = JedisUtil.getRedisCluster();
        redis.del(PublicValue.REDIS_TASK_LIST_NAME_SUM.getValue());
        //稽核准备
        for (String line : textLine) {
            redis.rpush(PublicValue.REDIS_TASK_LIST_NAME_SUM.getValue(), line);

        }

        ExecutorService fixedThreadPool = ToolUtils.getThreadPool(Integer.parseInt(PublicValue.THREAD_TOOL_NUM.getValue()));
        while (redis.exists(PublicValue.REDIS_TASK_LIST_NAME_SUM.getValue())) {

            fixedThreadPool.execute(new Runnable() {
                public void run() {
                    try {
                        JedisCluster redis1 = JedisUtil.getRedisCluster();
                        String line = redis1.lpop(PublicValue.REDIS_TASK_LIST_NAME_SUM.getValue());
                        if (Strings.isEmpty(line)){
                            return;
                        }
                        System.out.println(line);
                        String[] words = line.split(" +");
                        if (words.length == 8) {
                            //数据稽核
                            checkStream(words[0], words[1], words[2],words[3],words[4],words[5],words[6],words[7]);
                        } else {
                            log.error("数据格式异常，运维人员验证" + name + "文件对应数据：" + line);
                        }
                        redis1.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Thread.sleep(1200);
        }
        redis.close();
        ToolUtils.closeThreadPool(fixedThreadPool);

    }

    /**
     * 数据稽核流程
     *
     * @Param syBaseDataSource:sybase数据库编码
     * @Param syBaseTable：sybase数据表
     * @Param hiveTable：hive数据表
     * @Param type：同步类型  all(全表)  append(增量)  yyyy-MM-dd (自定义时间稽核)  yyyy-MM-dd&yyyy-MM-dd(指定日期段)
     * @Param incr：增量字段
     * @Param sum：sum字段
     * @Param precision：精度
     * @Param dstatus：全量和增量识别标志  di 增量   dd 全量
     * @Return
     * @Author lihao
     */
    private void checkStream(String syBaseDataSource, String syBaseTable, String hiveTable ,String type,String incr,String sum,String precision,String dstatus) {
        String hiveDataSource = ToolUtils.getHiveSource();
        String sybaseSuffix = ToolUtils.getSybaseSuffix(type,incr);
        String hiveSuffix = ToolUtils.getHiveSuffix(hiveTable,type,incr,dstatus);
        //sum稽核
        CheckResult checkResult = cs.checkSum(syBaseDataSource, hiveDataSource, syBaseTable, hiveTable,sybaseSuffix,hiveSuffix,sum,precision);
        // 日志记录
        String logInfo = checkResult.getLog();
        String status = "1";
        if (Strings.isEmpty(logInfo)) {
            logInfo = syBaseTable + " sum稽核成功，"+sum+"字段之和（"+type+"）为：" + checkResult.getSvalue() + "";
            log.info(logInfo);
        } else {
            //SUM稽核失败
            logInfo = syBaseTable + LogMsg.SUM_AUDIT_ERROR.getMessage()+logInfo;
            status = "0";
            log.error(logInfo);
        }
        //写入日志
        AuditLog.addLog(syBaseDataSource, syBaseTable,hiveTable, status, logInfo,incr, name,checkResult.getSvalue(),checkResult.getDvalue(),type,checkResult.getSsql(),checkResult.getDsql());
    }

}
