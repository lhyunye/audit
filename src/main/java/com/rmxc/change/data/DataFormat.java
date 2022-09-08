package com.rmxc.change.data;

import com.rmxc.audit.util.LogMsg;
import com.rmxc.audit.util.ToolUtils;
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
public class DataFormat {


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
            String s = "";
            //抽取第一行数据进行时间字段的查看
            String firstLine = br.readLine();
            if (Strings.isEmpty(firstLine)) {
                return;
            }
            String[] split = firstLine.split("\t");
            //字段数目
            int length = split.length;

            //获取时间字段位置
            List<Integer> dateIndex = getDateFiled(firstLine);

            s = firstLine;
            int in = 0;
            System.out.println("------------------------"+bcpfile+"-------------------------");
            do {
                s = s.replaceAll("  ", " ");
                if (s.endsWith("\t")){
                    s=s+" ";
                }
                String[] splitField = s.split("\t");
                //拼接换行符
                while (splitField.length < length) {
                    String s1 = br.readLine();
                    if (Strings.isEmpty(s1)){
                        //break;
                    }
                    s = s + s1;
                    s = s.replaceAll("  ", " ");
                    splitField = s.split("\t");
                    in++;
                }
                //格式转化
                s=changeDateFormat(dateIndex,s);

                bw.write(s + "\n");
            } while ((s = br.readLine()) != null);
            System.out.println("--------------" + in + "-------------");
            //关闭连接
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据转化失败：异常为：", e.getMessage());
        }

    }

    private String unicodeToUtf8(String s) throws UnsupportedEncodingException {
        return new String(s.getBytes("UTF-8"), "UTF-8");
    }


    /**
     * 获取时间格式字段位置
     *
     * @Param line: 整行数据
     * @Param storagePath:数据文件存储路径
     * @Author lihao
     */
    private List<Integer> getDateFiled(String line) {

        String[] split = line.split("\t");
        List<Integer> indexs = new ArrayList<>();
        Pattern p = Pattern.compile("(^" + months + ")(.*?)" + time + "$");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            Matcher matcher = p.matcher(s);
            if (matcher.find()) {
                indexs.add(i);
            }
        }
        return indexs;
    }

    /**
     * 转化时间格式
     *
     * @Param dateIndex: 时间字段位置
     * @Param splitField:行数据切割
     * @Param rowdata:行数据
     * @Return 行数据
     * @Author lihao
     */
    private String changeDateFormat(List<Integer> dateIndex,String rowdata) {
        rowdata = rowdata.replaceAll("  ", " ");
        String[] splitField = rowdata.split("\t");
        for (Integer i : dateIndex) {

            //对时间进行格式化
            String formatData = "";
            String dateString = splitField[i];
            if (Strings.isEmpty(dateString)) {
                continue;
            }
            //时间格式匹配
            try {
                formatData = LocalDateTime.parse(dateString, df1).format(df);
            } catch (Exception e) {
                if (!dateString.contains("AM") && !dateString.contains("PM")) {
                    continue;
                }
                try {
                    formatData = LocalDateTime.parse(dateString, df2).format(df);
                } catch (Exception e1) {
                    formatData = LocalDateTime.parse(dateString, df3).format(df);
                }
            }

            rowdata = rowdata.replace(dateString, formatData);
        }
        return rowdata;
    }


}
