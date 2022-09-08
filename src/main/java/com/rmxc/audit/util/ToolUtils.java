package com.rmxc.audit.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ToolUtils {
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");

    //触发器汇聚表。最新数据和历史删除数据都存在，需过滤掉删除数据
    private static String[] triList={"db_inv_main_converge","db_inv_com_converge","db_inv_pay2_converge","db_jxc_xl_day_converge","db_jxc_com_curm_day_converge","db_jxc_gz_curm_day_converge","db_str_com_day_converge","db_commod_converge","db_rpt_zb_inv_xstj_converge","md_inv_main_converge","md_inv_com_converge","md_inv_pay2_converge","md_jxc_xl_day_converge","md_jxc_com_curm_day_converge","md_jxc_gz_curm_day_converge","md_str_com_day_converge","md_commod_converge","md_rpt_zb_inv_xstj_converge"};


    /**
     * 获取线程池
     *
     * @Param num:线程池数量
     * @Return fixedThreadPool:线程池
     * @Author lihao
     */
    public static ExecutorService getThreadPool(int num) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num);
        return fixedThreadPool;
    }


    /**
     * 关闭线程池
     *
     * @Param fixedThreadPool:线程池数量
     * @Author lihao
     */
    public static void closeThreadPool(ExecutorService fixedThreadPool) {
        // 第一步：使新任务无法提交
        fixedThreadPool.shutdown();
        try {
            // 第二步：等待未完成任务结束  默认为5小时
            if(!fixedThreadPool.awaitTermination(1, TimeUnit.HOURS)) {
                // 第三步：取消当前执行的任务
                fixedThreadPool.shutdownNow();
                // 第四步：等待任务取消的响应
                if(!fixedThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Thread pool did not terminate");
                }
            }
        } catch(InterruptedException ie) {
            // 第五步：出现异常后，重新取消当前执行的任务
            fixedThreadPool.shutdownNow();
            Thread.currentThread().interrupt(); // 设置本线程中断状态
        }
    }




    /**
     * 获取文件内的每行数据
     *
     * @Param path:文件路径
     * @Return textLine:文件数据内容
     * @Author lihao
     */
    public List<String> getTextLine(String path) {
        List<String> textLine = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            List<String> sqlList = new ArrayList<>();
            textLine = reader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            log.error("读取文件内容异常："+e.toString());
        }
        return textLine;
    }

    /**
     * 获取对应的Hive数据库名
     *
     * @Return hiveSource: hive数据库名称
     * @Author lihao
     */
    public static String getHiveSource() {
        String hiveSource = "hive_prod";

        return hiveSource;
    }

    /**
     * 获取对应的Hive数据表名
     *
     * @Param tableName:数据源名称
     * @Param tableName:数据源名称
     * @Return hiveTable: hive数据表名称
     * @Author lihao
     */
    public static String getHiveTable(String dataSource,String tableName) {
        String hiveTable = "";
        if (!Strings.isEmpty(tableName)&&!Strings.isEmpty(dataSource)&&dataSource.contains("@")) {
            String[] split = dataSource.split("@");
            hiveTable = (split.length>1?split[1]+"_":"")+tableName.toLowerCase(Locale.ROOT);
        }
        return hiveTable;
    }

    /**
     * 获取路径下的文件名称
     *
     * @Param path:文件路径
     * @Return hiveTable: hive数据表名称
     * @Author lihao
     */
    public List<String> getFileName(String path) {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }
        List<String> nameList = new ArrayList<>();
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (!fs.isDirectory()) {
                nameList.add(fs.getName());
            }
        }
        return nameList;
    }


    /**
     * 判断参数列表是否为空
     *
     * @Param param 可变参数
     * @Return boolean: 是否含有空值 true : 不含   false:含有
     * @Author lihao
     */
    public static boolean checkEmpty(Object... param) {
        long count = Arrays.stream(param).map(x -> { boolean empty = ObjectUtils.isEmpty(x);return empty; }).filter(x -> x).count();
        if (count == 0) {
            return true;
        }
        return false;

    }


    /**
     * 通过配置文件名读取内容
     * @Param fileName:配置文件名称
     * @Return props:配置内容
     * @Author lihao
     */
    public static Properties readPropertiesFile(String fileName) {
        try {
            Resource resource = new ClassPathResource(fileName);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            return props;
        } catch (Exception e) {
            log.error("————读取配置文件：" + fileName + "出现异常，读取失败————");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取sybase数据拼接后缀
     * @Param type:类型
     * @Param incr: 数据
     * @Return suffix:sql 后缀
     * @Author lihao
     */
    public static String getSybaseSuffix(String type,String incr) {
        if ("alldd".equals(type)){
            return "";
        }
        String suffix = dealSuffix(type,incr,"sybase");
        return suffix;
    }

    /**
     * 获取hive数据拼接后缀
     * @Param type:类型
     * @Param incr:字段
     * @Param status: 增量和全量识别标志
     * @Return suffix:sql 后缀
     * @Author lihao
     */
    public static String getHiveSuffix(String type,String incr,String status) {

        String suffix = "";
        if("di".equals(status)){
            suffix =dealSuffix(type,incr,"hive");
            suffix = suffix+" and copt = "+df1.format(LocalDateTime.now().minusDays(1))+"";
        }else {
            if("alldd".equals(type)){
                suffix = "where pt = "+df1.format(LocalDateTime.now().minusDays(1))+"";
            }else {
                suffix = suffix+" and pt = "+df1.format(LocalDateTime.now().minusDays(1))+"";
            }
        }
        return suffix;
    }

    /**
     * 获取hive数据拼接后缀
     * @Param type:类型
     * @Param incr:字段
     * @Param status: 增量和全量识别标志
     * @Return suffix:sql 后缀
     * @Author lihao
     */
    public static String getHiveSuffix(String tableName,String type,String incr,String status) {

        String suffix = "";
        suffix =dealSuffix(type,incr,"hive");
        if("di".equals(status.trim())){
            if (tableName.contains("_converge")){
                List<String> useStatus = Arrays.stream(triList).filter(x -> tableName.contains(x)).collect(Collectors.toList());
                if(useStatus.size()>0){
                    suffix = suffix +" and (operation_type = 1 or operation_type = 'A') ";
                }
                suffix = suffix+" and copt = "+df1.format(LocalDateTime.now().minusDays(1))+"";
            }
        }else {
            if("alldd".equals(type)){
                suffix = "where pt = "+df1.format(LocalDateTime.now().minusDays(1))+"";
            }else {
                suffix = suffix+" and pt = "+df1.format(LocalDateTime.now().minusDays(1))+"";
            }
        }
        return suffix;
    }


    public static String dealSuffix(String type,String incr,String status){
        String suffix = "";
        DateTimeFormatter dateTimeFormatter=df;
        //针对部分视图表hive中未保存增量时间字段
        if (incr.contains("/")){
            if("hive".equals(status)){
                incr = incr.split("/")[1];
                dateTimeFormatter = df1;
            }else {
                incr = incr.split("/")[0];
            }
        }
        suffix = getSwitchGE(type,incr,dateTimeFormatter);
        return suffix;

    }


    public static String getSwitchGE(String type,String incr,DateTimeFormatter df){
        String suffix = "";
        System.out.println("-----------------------------------------------------");
        System.out.println(type);
        switch (type){
            case "all":
                suffix = " where "+incr+" >= '2019-01-01' and "+incr+" <= '"+df.format(LocalDateTime.now().minusDays(1))+"'" ;
                break;
            case "append":
                suffix = " where "+incr+" >= '"+LocalDate.now().minusDays(1).format(df)+"'and "+incr+" < '"+LocalDate.now().format(df)+"'" ;
                break;
            default:
                if(type.contains("-&")){
                    String[] times = type.split("&");
                    suffix = " where "+incr+" < '"+LocalDate.parse(times[1]).plusDays(1).format(df)+"'" ;
                }else if(type.contains("&-")){
                String[] times = type.split("&");
                suffix = " where "+incr+" > '"+LocalDate.parse(times[0]).format(df)+"'" ;
                }else if(type.contains("&")){
                    String[] times = type.split("&");
                    if(type.contains("date")){// date-100&date-70
                        suffix = " where "+incr+" >= '"+LocalDate.now().minusDays(Integer.parseInt(times[0].split("-")[1])).format(df)+"'and "+incr+" < '"+LocalDate.now().minusDays(Integer.parseInt(times[1].split("-")[1])).format(df)+"'" ;
                    }else {
                        suffix = " where "+incr+" >= '"+LocalDate.parse(times[0]).format(df)+"'and "+incr+" < '"+LocalDate.parse(times[1]).plusDays(1).format(df)+"'" ;
                    }
                }else {
                    suffix = " where "+incr+" >= '"+LocalDate.parse(type).format(df)+"'and "+incr+" < '"+LocalDate.parse(type).plusDays(1).format(df)+"'" ;
                }
                break;
        }
        return suffix;
    }



}
