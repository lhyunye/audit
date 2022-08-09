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
public class CheckNumber {

    //转化存储路径
    private static final String storagePath = "./createsql/";



    /**
     * 执行方法
     *
     * @Param directoryPath:建表语句存储位置
     * @Param prefix:hive表前缀
     * @Param environment:环境  dev测试  prod 生产
     * @Author lihao
     */
    public void exec(String directoryPath, String environment) throws IOException {
        //创建存储路径
        File dir = new File(storagePath);
        if (dir.exists()) {
            ReadFile.deleteDirectory(storagePath);
        }
        dir.mkdir();

        ReadFile readFile = new ReadFile();
        List<String> files = readFile.foundFile(directoryPath);
        //获取数据建表语句文件名称
        List<String> sqlfiles = files.stream().filter(x -> x.endsWith(".txt")).collect(Collectors.toList());
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(storagePath+"resoult.txt")));
        //循环转化
        for (String sqlfile : sqlfiles) {
            changesql(sqlfile, bw, environment, directoryPath, storagePath);
        }
        bw.close();
    }


    /**
     * 建表语句转换
     *
     * @Param sqlfile:sybase数据表建表语句文件路径
     * @Param prefix:hive表前缀
     * @Param environment:hive数据库环境
     * @Param storagePath:建表语句存储路径
     * @Param directoryPath:建表语句读取路径
     * @Author lihao
     */
    private void changesql(String sqlfile, BufferedWriter bw,String environment, String directoryPath, String storagePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(directoryPath + sqlfile)));
            String newFilepath = storagePath + sqlfile;

            String s = "";
            StringBuffer buffer = new StringBuffer();
            while ((s = br.readLine()) != null) {
                buffer.append(s.trim());
            }
            String sybasesql = buffer.toString();
            String fieldBody = getFieldBody(sybasesql);
            //table CUST_XS_MX (
            Pattern compile = Pattern.compile("table(\\s)+([A-Z]|[a-z]|_|\\d)*");
            Matcher matcher = compile.matcher(sybasesql);
            //获得表名并进行sql转换
            if (matcher.find()) {
                String tableName =  matcher.group().replace("table", "").trim().toLowerCase(Locale.ROOT);
                bw.write(tableName+"                   "+sqlfile);
                bw.write("\n");
                bw.write(fieldBody);
                bw.write("\n");

            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据转化失败：异常为：", e.getMessage());
        }


    }

    /**
     * 获取字段配置部分，进行格式转化
     *
     * @Param sybasesql: sybase建表语句
     * @Return fieldBody:hive建表语句字段部分
     * @Author lihao
     */
    public String getFieldBody(String sybasesql) throws Exception {
        //截取sql 字段部分
        Pattern p = Pattern.compile("\\(.*\\)");
        Matcher matcher = p.matcher(sybasesql);
        String fieldBody = "";
        if (matcher.find()) {
            String body = matcher.group();
            body = body.replace("(", " ").replace(")", " ");
            body = body.replaceAll("\\d,\\d", "");
            String[] split = body.split(",");
            StringBuffer fields = new StringBuffer();
            //迭代字段类型转换   sybase -> hive
            for (String line : split) {
                if (line.contains("CONSTRAINT")) {
                    break;
                }
                if (line.contains("numeric")||line.contains("decimal")){
                    fields.append(getFieldMsg(line));
                    fields.append("/");
                }

            }
            fieldBody = fields.toString().replace(" ","");

        }
        return fieldBody;
    }

    /**
     * 字段格式转化
     *
     * @Param line: sybase 中的一个字段所有属性
     * @Return result:hive 字段的属性
     * @Author lihao
     */
    public static String getFieldMsg(String line) {
        Pattern fieldPattern = Pattern.compile("([A-Z]|[a-z]|_|\\d).*?(\\s)");
        Matcher fieldMatcher = fieldPattern.matcher(line);
        String result = "";
        if (fieldMatcher.find()) {
            String fieldName = fieldMatcher.group();
            if (fieldMatcher.find()) {
                String type = fieldMatcher.group().split("\\(")[0];
                String fieldType = matchFieldType(type.trim());
               result = fieldName;
            }

        }
        return result;
    }
    /**
     * sybase ->hive 类型映射
     *
     * @Param type: sybase 中的字段类型
     * @Return fieldType:hive 字段的类型
     * @Author lihao
     */
    public static String matchFieldType(String type) {

        String fieldType = "";
        switch (type) {
            case "datetime":
                fieldType = "string";
                break;
            case "varchar":
                fieldType = "string";
                break;
            case "char":
                fieldType = "string";
                break;
            case "numeric":
                fieldType = "double";
                break;
            case "number":
                fieldType = "double";
                break;
            case "int":
                fieldType = "int";
                break;
            default:
                fieldType = "string";
                break;
        }
        return fieldType;
    }
}
