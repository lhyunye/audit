package com.rmxc.audit.schedule;

import com.rmxc.audit.Factory.AuditFactory;
import com.rmxc.audit.Factory.AuditService;
import com.rmxc.audit.util.*;
import com.rmxc.change.data.DataFormat;
import com.rmxc.change.data.NewDataFormat;
import com.rmxc.change.data.WordFormat;
import com.rmxc.change.structure.Converge;
import com.rmxc.change.structure.Convert;
import com.rmxc.checkStatus.Check;
import com.rmxc.pulldata.passengerFlow.PFexec;
import com.rmxc.pulldata.passengerFlow.apiList.GetSiteKeysByCustomerId;
import jline.internal.Log;
import jodd.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 调度分发模块
 * 读取外部文件，获取各策略数据表信息。
 * 分发各数据工厂运行
 */
@Slf4j
public class Dispense {


    /**
     * 数据稽核文件路径
     */
    private static final String path = "./table";
//    private static final String path = "/data/wfj/audit/table";



    public static void main(String[] args) throws Exception {

        //数据稽核
      audit();



        //数据格式转换   数据文件路径
        //changeData(args[0]);

//        changeData("./bcpfile/");

        //建表语句转换 sybase ->> hive        建表语句存储位置,   hive表前缀  . 环境
        //changeSql(args[0],args[1],args[2]);

        //建表语句转换  hive ->> hive   建表语句存储位置
        //changeHiveSql(args[0]);

        //数据接口对接
//        if (!ObjectUtils.isEmpty(args)&&args.length>=1){
//            PFexec.exec(args[0]);
//        }else {
//            PFexec.exec(null);
//        }


        // 编码格式转换   当前文件路径下的文件名称
        //WordFormat.exec(args[0]);

        //开闭工标识获取 门店编码
      //  Check.exec(args[0]);

//        //整理hive历史数据条数
//       SelectHive selectHive = new SelectHive();
//        selectHive.selectHive();

        //数据库查询
//        SelectSybase selectSybase = new SelectSybase();
//        selectSybase.exec(args[0]);//"./select/select"


    }

    /**
     * 数据稽核
     * @author :lihao
     * */
    public static void audit()throws Exception{
        AuditFactory auditFactory = new AuditFactory();
        ToolUtils toolUtils = new ToolUtils();
        List<String> fileName = toolUtils.getFileName(path);
        LocalDateTime start = LocalDateTime.now();
        //选择策略分发
        for (String stype : fileName) {
            AuditService audit = auditFactory.getAudit(stype);
            if (!ObjectUtils.isEmpty(audit)) {
                audit.exec();
            } else {

                Log.error("未找到策略：" + stype);
            }
        }
        LocalDateTime stop = LocalDateTime.now();
        //稽核结果邮件发送
//        Duration between = Duration.between(start, stop);
//        AuditLog.sendResultEmail(between.toMinutes());
    }


    /**
     * 数据格式转换
     * @param :path 数据文件路径
     * @author :lihao
     * */
    public static void changeData(String path){
//        DataFormat dataFormat = new DataFormat();
//        dataFormat.exec(path);

        NewDataFormat newDataFormat = new NewDataFormat();
        newDataFormat.exec(path);
    }




    /**
     * 建表语句转换 sybase ->> hive
     * @Param directoryPath:建表语句存储位置
     * @Param prefix:hive表前缀
     * @Param environment:环境  dev测试  prod 生产
     * @Author lihao
     * */
    public static void changeSql(String directoryPath, String prefix, String environment){
        Convert convert =new Convert();
        convert.exec(directoryPath,prefix,environment);
    }

    /**
     * 建表语句转换 hive ->> hive
     * @Param directoryPath:建表语句存储位置
     * @Author lihao
     * */
    public static void changeHiveSql(String directoryPath){
        Converge converge =new Converge();
        converge.exec(directoryPath);
    }

}
