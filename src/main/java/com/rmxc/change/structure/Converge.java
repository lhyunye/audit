package com.rmxc.change.structure;

import com.rmxc.change.util.ReadFile;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Slf4j
public class Converge {


    //转化存储路径
    private static final String storagePath = "./convergesql/";

    private static final String hivesqlModel = "" +
            "CREATE TABLE IF NOT EXISTS `%s`(  \n" +
            "%s\n" +
            ",`pt` string)                                   \n" +
            " COMMENT ''                                   \n" +
            " PARTITIONED BY (                                   \n" +
            "   `copt` string)                                     \n" +
            " ROW FORMAT SERDE \n" +
            "  'org.apache.hadoop.hive.ql.io.orc.OrcSerde' \n" +
            " STORED AS orc ;\n" +
            "\n" +
            "insert overwrite table %s partition(copt='${bizdate}') select * from %s;" +
            "\n" +
            "ALTER TABLE %s DROP IF EXISTS PARTITION (copt='${bizdate-11}');" ;


    /**
     * 执行方法
     *
     * @Param directoryPath:建表语句存储位置
     * @Author lihao
     */
    public void exec(String directoryPath) {
        //创建存储路径
        File dir = new File(storagePath);
        if (dir.exists()) {
            ReadFile.deleteDirectory(storagePath);
        }
        dir.mkdir();

        ReadFile readFile = new ReadFile();
        //获取数据建表语句文件名称
        List<String> files = readFile.foundFile(directoryPath);

        //循环转化
        for (String sqlfile : files) {
            changesql(sqlfile, directoryPath, storagePath);
        }

    }

    /**
     * 建表语句转换
     * hive -> hive
     * @Param sqlfile:oldhive表名
     * @Param storagePath:建表语句存储路径
     * @Param directoryPath:建表语句读取路径
     * @Author lihao
     */
    private void changesql(String sqlfile, String directoryPath, String storagePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(directoryPath + sqlfile)));
            String newFilepath = storagePath + sqlfile;
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFilepath)));
            String s = "";
            StringBuffer buffer = new StringBuffer();
            while ((s = br.readLine()) != null) {
                buffer.append(s.trim());
            }
            String oldsql = buffer.toString();
            //去除换行符
            oldsql=oldsql.replaceAll("\n","").replaceAll("\\|","");
            //格式转换
            String formatsql = formatsql(sqlfile, oldsql);
            bw.write(formatsql);
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据转化失败：异常为：", e.getMessage());
        }
    }

    /**
     * 拼接sql
     * @Param sqlfile:oldhive表名
     * @Param oldsql:原始hive sql
     * @Return convergesql:转换后sql
     * @Author lihao
     */
    private String formatsql(String sqlfile, String oldsql) {
        Pattern p= Pattern.compile("\\((.*?)\\)");
        Matcher matcher = p.matcher(oldsql);
        String convergesql = "";
        if (matcher.find()) {
            String group = matcher.group();
            String field=group.replace("(","").replace(")","");
            String newhiveName = sqlfile+"_CONVERGE";
            convergesql=String.format(hivesqlModel,newhiveName,field,newhiveName,sqlfile,newhiveName);
        }else {
            log.error("数据表{}建表语句异常",sqlfile);
        }
        return convergesql;
    }
}
