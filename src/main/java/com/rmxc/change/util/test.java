package com.rmxc.change.util;


import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.RowsQueryEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.rmxc.audit.util.JedisUtil;
import com.rmxc.audit.util.PublicValue;
import com.rmxc.change.structure.CheckNumber;
import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.rmxc.audit.util.PublicValue.REDIS_TASK_LIST_NAME_COUNT;

public class test {

//    public static void main(String[] args) throws IOException {
//
//
//        JedisCluster redis = JedisUtil.getRedisCluster();
//        redis.del(REDIS_TASK_LIST_NAME_COUNT.getValue());
//
//            redis.rpush(PublicValue.REDIS_TASK_LIST_NAME_COUNT.getValue(), "");
//
//        //redis.close();
//
//
//
////
////        BufferedWriter bw = WriteTxt.getbw("./a.txt");
////
////       // String[] a = {"2021-06-23","2021-06-24","2021-06-25","2021-06-26","2021-06-27","2021-06-28","2021-06-29","2021-06-30"};
////        String[] a = {"2021-04-01","2021-04-02","2021-04-03","2021-04-04","2021-04-05","2021-04-06","2021-04-07","2021-04-08","2021-04-09","2021-04-10","2021-04-11","2021-04-12","2021-04-13","2021-04-14","2021-04-15","2021-04-16","2021-04-17","2021-04-18","2021-04-19","2021-04-20","2021-04-21","2021-04-22","2021-04-23","2021-04-24","2021-04-25","2021-04-26","2021-04-27","2021-04-28","2021-04-29","2021-04-30","2021-05-01","2021-05-02","2021-05-03","2021-05-04","2021-05-05","2021-05-06","2021-05-07","2021-05-08","2021-05-09","2021-05-10","2021-05-11","2021-05-12","2021-05-13","2021-05-14","2021-05-15","2021-05-16","2021-05-17","2021-05-18","2021-05-19","2021-05-20","2021-05-21","2021-05-22","2021-05-23","2021-05-24","2021-05-25","2021-05-26","2021-05-27","2021-05-28","2021-05-29","2021-05-30","2021-05-31","2021-06-01","2021-06-02","2021-06-03","2021-06-04","2021-06-05","2021-06-06","2021-06-07","2021-06-08","2021-06-09","2021-06-10","2021-06-11","2021-06-12","2021-06-13","2021-06-14","2021-06-15","2021-06-16","2021-06-17","2021-06-18","2021-06-19","2021-06-20","2021-06-21","2021-06-22","2021-06-23","2021-06-24","2021-06-25","2021-06-26","2021-06-27","2021-06-28","2021-06-29","2021-06-30"};
////        String model = "%s  JXC_XL_DAY %s  %s RQ XXTAX/ZRJCHSJE/ZRJCBHSJE/BRJHHSJECF/BRJHHSJEDF/BRJHBHSJECF/BRJHBHSJEDF/BRJHHSTZ/BRJHBHSTZ/BRYGHSJECF/BRYGHSJEDF/BRYGBHSJECF/BRYGBHSJEDF/BRXSHSJECF/BRXSHSJEDF/BRXSBHSJECF/BRXSBHSJEDF/BRXSHSTZ/BRXSBHSTZ/BRXSLSJE/BRXSHYLSJE/BRXSZKSC/BRXSZKGYS/BRXSHYZK/BRXSYHZK/BRXSSYSY/BRPDHSJECF/BRPDHSJEDF/BRPDBHSJECF/BRPDBHSJEDF/BRSYHSJECF/BRSYHSJEDF/BRSYBHSJECF/BRSYBHSJEDF/BRSYHSTZ/BRSYBHSTZ/BRPFHSJEJF/BRPFHSJEDF/BRPFBHSJEJF/BRPFBHSJEDF/BRPFHSJE/BRPFBHSJE/BRPFHSZK/BRPFBHSZK/BRPFHSTZ/BRPFBHSTZ/BRRMB/BRXYK/BRZP/BRLQ/BRQT/BRSRJXSE/BRSRXXSE/BRJCHSJE/BRJCBHSJE/KCHSJE1/KCBHSJE1/KCHSJE2/KCBHSJE2/KCHSJE3/KCBHSJE3 no di";
////        String[] resource = {"JZ","DL","TY","LY","BT","LS","ZH","XA","YC","DS","XA2","CQ2","LY2","XN3","BT2","XN2","CS2","CD2","CD","XN","KM","SA","GZ","CS"};
////        String[] tablename = {"ods_city_jz_1_di_mcht_db_jxc_xl_day_converge","ods_city_dl_1_di_mcht_md_jxc_xl_day_converge","ods_city_ty_1_di_mcht_db_jxc_xl_day_converge","ods_city_ly_1_di_mcht_db_jxc_xl_day_converge","ods_city_bt_1_di_mcht_db_jxc_xl_day_converge","ods_city_ls_1_di_mcht_db_jxc_xl_day_converge","ods_city_zh_1_di_mcht_db_jxc_xl_day_converge","ods_city_xa_1_di_mcht_db_jxc_xl_day_converge","ods_city_yc_1_di_mcht_db_jxc_xl_day_converge","ods_city_ds_1_di_mcht_db_jxc_xl_day_converge","ods_city_xa_2_di_mcht_db_jxc_xl_day_converge","ods_city_cq_2_di_mcht_db_jxc_xl_day_converge","ods_city_ly_2_di_mcht_db_jxc_xl_day_converge","ods_city_xn_3_di_mcht_db_jxc_xl_day_converge","ods_city_bt_2_di_mcht_db_jxc_xl_day_converge","ods_city_xn_2_di_mcht_db_jxc_xl_day_converge","ods_city_cs_2_di_mcht_db_jxc_xl_day_converge","ods_city_cd_2_di_mcht_db_jxc_xl_day_converge","ods_city_cd_1_di_mcht_db_jxc_xl_day_converge","ods_city_xn_1_di_mcht_db_jxc_xl_day_converge","ods_city_km_1_di_mcht_db_jxc_xl_day_converge","ods_city_sa_1_di_mcht_md_jxc_xl_day_converge","ods_city_gz_1_di_mcht_db_jxc_xl_day_converge","ods_city_cs_1_di_mcht_db_jxc_xl_day_converge"};
////
////
////        for (int i = 0;i<resource.length;i++){
////            WriteTxt.writeTxt("newFileName",bw, String.format("#%s  JXC_XL_DAY",  resource[i]));
////            for (String time :a){
////                String format = String.format(model, resource[i],tablename[i],time);
////                WriteTxt.writeTxt("newFileName",bw,format);
////            }
////        }
////
////
////
////        bw.close();
//
////        File file = new File("D:\\UserData\\Desktop\\fsdownload\\log.000007");
////        EventDeserializer eventDeserializer = new EventDeserializer();
////        eventDeserializer.setCompatibilityMode(
////                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
////                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
////        );
////        BinaryLogFileReader reader = new BinaryLogFileReader(file, eventDeserializer);
////        try {
////            for (Event event; (event = reader.readEvent()) != null; ) {
////                EventData data = event.getData();
////                System.out.println(event.toString());
////                if (data != null && data.getClass().isAssignableFrom(RowsQueryEventData.class)) {
////                    RowsQueryEventData dmlData = (RowsQueryEventData) data;
////                    //System.out.println(dmlData.getQuery());
////                }
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        } finally {
////            reader.close();
////        }
//
////        String a = "Jan  1 2019  9:14:14:546AM\t\tI\t6006\t1756280.000000\t0006665\t9\t355034\t80110002\t401109101105103\t0356\t1.000000\t13.900000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t1.000000\t0.000000\t0.000000\t0.000000\t0.000000\t13.900000\t1.390000\t13.900000\t2.090000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t020046\t超市\t1.390000\t0.000000\t60061756280\t1\t41031\t41031\t";
////        String b = "Jan  1 2019  9:15:18:313AM\t9900005859929\tI\t6006\t1756281.000000\t0116983\t0\t361802\t80102001\t403101103101101\t0037\t1.480000\t21.980000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t-0.010000\t0.000000\t0.010000\t32.530000\t3.252960\t32.520000\t1.130000\t32.520000\t0.000000\t0.000000\t0.000000\t0.000000\t0.000000\t020046\t超市\t3.252960\t0.000000\t60061756281\t1\t41031\t\tTEL";
////        a = a.replaceAll("  ", " ");
////        b = b.replaceAll("  ", " ");
////        String[] split = b.split("\t");
////        String[] split1 = a.split("\t");
////        System.out.println(split1.length);
////        System.out.println(split.length);
////        if (a.endsWith("\t")){
////            a=a+" ";
////            String[] split2 = a.split("\t");
////            System.out.println(split2.length);
////        }
//
//
////        CheckNumber checkNumber = new CheckNumber();
////        checkNumber.exec("./bcpfile/","dev");
//    }


}
