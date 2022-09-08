package com.rmxc.change.data;

import com.rmxc.change.util.ReadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class NewDataFormat {


    //转化存储路径
    private static final String storagePath = "./storageFile/";
    //英文月份
    private static final String months = "Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sept|Oct|Nov|Dec";
    //时间划分
    private static final String time = "AM|PM";
    //规整时间格式
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //bcp导出时间格式
    private static final DateTimeFormatter df1 = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM d yyyy h:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df3 = DateTimeFormatter.ofPattern("MMM d yyyy  h:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df4 = DateTimeFormatter.ofPattern("MMM  d yyyy  hh:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df5 = DateTimeFormatter.ofPattern("MMM  d yyyy hh:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df6 = DateTimeFormatter.ofPattern("MMM  d yyyy  h:mm:ss:SSSa", Locale.ENGLISH);


    /**
     * 执行方法
     *
     * @Param directoryPath:数据文件存储路径
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
        List<String> files = readFile.foundFile(directoryPath);
        //获取bcp导出文件名称
        List<String> bcpfiles = files.stream().filter(x -> x.endsWith(".bcp")).collect(Collectors.toList());
        //循环转化
        for (String bcpfile : bcpfiles) {
            changeData(bcpfile, directoryPath, storagePath);
        }
    }


//    public static void main(String[] args) {
//        String a = "Jan  1 2020 10:19:01:766AM|#|";
//        Pattern p = Pattern.compile("(^" + months + ") (.*?)" + time + "$");
//        System.out.println(a.contains("|#|"));
//        System.out.println(a.contains("\\|#\\|"));
//
//    }

    /**
     * 数据转化
     *
     * @Param bcpfile:文件名称
     * @Param storagePath:数据文件存储路径
     * @Param directoryPath:数据文件读取路径
     * @Author lihao
     */
    private void changeData(String bcpfile, String directoryPath, String storagePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(directoryPath + bcpfile)));
            String newFilepath = storagePath + bcpfile.replace("bcp", "txt").replace("dbo.", "");
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newFilepath)));
            String line = "";


            int in = 0;
            System.out.println("------------------------"+bcpfile+"-------------------------");
            while ((line = br.readLine()) != null){
                while (!line.contains("|#|")){
                    System.out.println(line);
                    line= line+br.readLine();
                }
                line = line.replace("|#|","");
                //字段切分
                String[]fields = line.split("\\|~\\|");
                //字段时间格式核验、转换
                List<String> fFields = changeFieldFormat(fields);
                String nLine = String.join("\t", fFields);

                bw.write(nLine + "\n");
                in++;
            }
            System.out.println("--------------" + in + "-------------");
            //关闭连接
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据转化失败：异常为："+ e.getMessage());
        }

    }

    private String unicodeToUtf8(String s) throws UnsupportedEncodingException {
        return new String(s.getBytes("UTF-8"), "UTF-8");
    }

    /**
     * 数据字段类型判别并进行格式调整
     *
     * @Param fields: 行数据字段数组
     * @Return cFields: 格式转换后的字段列表
     * @Author lihao
     */
    private List<String> changeFieldFormat(String[] fields){
        //正则判断时间字段
        Pattern p = Pattern.compile("(^" + months + ") (.*?)" + time + "$");
        List<String> cFields = new ArrayList<>();
        //有序循环字段值
        for (int i =0 ;i<fields.length;i++){
            String field = fields[i];
            Matcher matcher = p.matcher(field);
            if (matcher.find()) {
                cFields.add(changeDateFormat(field));
            }else {
                cFields.add(field);
            }
        }
        return cFields;
    }


    /**
     * 转化时间格式
     *
     * @Param field: 字段值
     * @Return formatData：格式化数值
     * @Author lihao
     */
    private String changeDateFormat(String field) {
            //对时间进行格式化
            String formatData = "";
            //时间格式匹配
            try {
                formatData = LocalDateTime.parse(field, df1).format(df);
            } catch (Exception e) {
                try {
                    formatData = LocalDateTime.parse(field, df2).format(df);
                } catch (Exception e1) {
                    try {
                        formatData = LocalDateTime.parse(field, df3).format(df);
                    }catch (Exception e2){
                        try {
                            formatData = LocalDateTime.parse(field, df4).format(df);
                        }catch (Exception e3){
                            try {
                                formatData = LocalDateTime.parse(field, df5).format(df);
                            }catch (Exception e4){
                                formatData = LocalDateTime.parse(field, df6).format(df);
                            }
                        }
                    }
                }
            }

        return formatData;
    }


}
