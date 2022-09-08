package com.rmxc.audit.util;

import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TaskExec implements Runnable {






    private String taskListName = "audit_task_list";

    @Override
    public void run() {

        JedisCluster redis = JedisUtil.getRedisCluster();
        if (redis.exists(taskListName)){
            String lpop = redis.lpop(taskListName);

        }


    }
    public static DateTimeFormatter df_day2 = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_DAY2.getValue());
    public static DateTimeFormatter df_zero = DateTimeFormatter.ofPattern(Key.DATE_PATTERN_ZERO.getValue());
    //规整时间格式
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //bcp导出时间格式
    private static final DateTimeFormatter df1 = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df2 = DateTimeFormatter.ofPattern("MMM d yyyy h:mm:ss:SSSa", Locale.ENGLISH);
    private static final DateTimeFormatter df3 = DateTimeFormatter.ofPattern("MMM d yyyy  h:mm:ss:SSSa", Locale.ENGLISH);

    public static void main(String[] args) {
String a = "111.11";
        System.out.println(ObjectUtils.isEmpty(a)?"":a.split("\\.")[0]);
//        String a = "sid,record_sid,member_id,amount,point,point_base,point_prom,growth";
//
//
//        String[] split = a.split(",");
//        String model1 = "sum(%s)";
//        List<String> types = new ArrayList<>();
//        List<String> types1 = new ArrayList<>();
//        String model2 = "sum(cast(nvl(%s,0) as decimal(38,2)))";
//        for (String type :split){
//            String format = String.format(model1, type);
//            String format2 = String.format(model2, type);
//            types.add(format);
//            types1.add(format2);
//        }
//
//
//        String collect = types.stream().collect(Collectors.joining(","));
//        String collect2 = types1.stream().collect(Collectors.joining(","));
//        System.out.println(collect);
//        System.out.println(collect2);


//        String a = "date-40";
//        if (a.contains("date-")){
//            System.out.println(a);
//        }
//        String formatData= "";
//        String dateString ="Jul 6 2020 5:08:54:456PM";
//        formatData = LocalDateTime.parse(dateString, df2).format(df);
//
//        System.out.println(formatData);


//        Jedis redis =JedisUtil.getRedis();
//        redis.rpush("111","111");
//
//        while (redis.exists("111")){
//            System.out.println(redis.lpop("111"));
//        }
//
//        System.out.println(redis.get("111"));

//        String date = "2021-06-15";
//        LocalDate parse = LocalDate.parse(date,df_day2);
//        String format = parse.format(df_zero);
//        System.out.println(format);


//        BigDecimal b = new BigDecimal("259456465346546546546468845.2518468348");
//        BigDecimal b1 = new BigDecimal("0E-10");
//        b= b.add(b1);
//        System.out.println(b.toString());
//        String model = " \"%s\": {       \n" +
//                "          \"type\": \"%s\"     \n" +
//                "                  },\n";
        //String test = "store_code      string,store_name    string,seqno      double,syjh      string,fphm      double,rqsj      keyword,yyyh      string,com_code      string,com_name     string,commod_sphh  string,commod_spec  string,splb      string,gz      string,gz_name string,sl      double,zhhsjj      double,lsj      double,jg      double,zke      double,zre      double,lszkfd      double,hyzk      double,hyzkfd      double,yhzk      double,yhzkfd      double,yhzk1      double,yhzkfd1      double,yhzk2      double,yhzkfd2      double,yhzk3      double,yhzkfd3      double,sysy      double,ysyjh      string,yfphm      double,flag      string,dzxl      string,gys      string,gys_name string,pp      string,yhseqno      double,yjhx      string,yjhxzkfd      double,vip_kh      string,vip_zke      double,vip_zkfd      double,fs_rqsj      string,wk_rq      string,ks      double,th_type      string,line_no      double,bz1      string,bz2      string,bz3      double,bz4      double,sqkh      string,hdxmh      string,mjjs      double,mjs      double,ismjjt      string,jcjf      double,bzjf      double,cxjf      double,bz5      string,bz6      string,bz7      string,bz8      double,bz9      double,bz10      double,bz11      double,bz12      double,bz13      double,klm      string,counter      string,ybillno      string,yrowno      int,spucode      string,skucode      string,sapcode      string,padbillno      string,orderid      string,hl      double,lb      string,ztpp      string,dt      string,operation_type      int,ext_rowid      string,ext_seq      string,pt      string,ds      string";
      //  String test = "store_code      string,store_name    string,seqno         double,syjh      string,fphm      double,rqsj      string,djlb      string,flag      string,ysje      double,sjfk      double,zl      double,zke      double,zre      double,hyzke      double,yhzke      double,sysy      double,syyh      string,bc       string,yyyh      string,gz       string,hykh      string,dyfp      string,tsbz      string,sqgh      string,sqkh      double,rmb      double,usd      double,usdhl      double,hkd      double,hkdhl      double,xjzp      double,zzzp      double,xyk      double,lq      double,mzk      double,ick      double,sz      double,pay11      double,hl11      double,pay12      double,hl12      double,pay13      double,hl13      double,pay14      double,hl14      double,pay15      double,hl15      double,vip_kh      string,vip_zke      double,vip_zkfd      double,zzke      double,zzre      double,fs_rqsj      string,wk_rq      string,hyklb      string,orderid      string,channel      string,dt         string,operation_type      int,ext_rowid      string,ext_seq        string,pt      string,ds      string";
       // String test ="store_code      string,store_name      string,seqno     double,syjh     string,fphm     double,syyh     string,rqsj     string,paycode     string, pay_name     string,mxcode     string, mx_name     string,zpno       string,xm         string,sfz        string,bank       string,je         double,hl         double,fs_rqsj    string,wk_rq      string,line_no    double,dt         string,operation_type     int,ext_rowid     string,ext_seq     string,pt     string,ds     string";
       // String test = "store_code       string,store_name       string,seqno           string,ddbh           string,rowno           double,com_code           string,commod_sphh       string,commod_spec       string,xstm           string,splb           string,name           string,unit           string,bzhl           double,cd           string,sl           double,jxtax           double,hsjj           double,hsjjje           double,bhsjj           double,bhsjjje           double,lsj           double,lsje           double,mll           double,shsl           double,shhsje           double,shbhsje           double,shlsje           double,jysl           double,jyhsje           double,jybhsje           double,jylsje           double,scrq           string,bzqts           double,blqts           double,kcsl           double,kchsjjje           double,jbrq           string,jbkcsl           double,memo           string,ztl           double,pzzxl           double,szxl           double,weight           double,sphh           string,buyunit           string,buyjj           double,buybzhl           double,bz           string,pt           string,ds           string";
        //String test = "store_code       string,store_name       string,seqno       string,handno       string,djbh       string,djlb       string,type       string,gys       string,gys_name  string,gz       string,gz_name   string,jyfs       string,store       string,htbh       string,zqqx       double,xdrq       string,dhrq       string,jhrq       string,cgy       string,cgy_name   string,lry       string,lry_name  string,lrrq       string,shr       string,shr_name  string,shrq       string,qxr       string,qxr_name  string,qxrq       string,shbz       string,yf       double,bxf       double,qt       double,qt1       double,fkzj       double,ck       string,fktj       string,operusr1       string,operrq1       string,operusr2       string,operrq2       string,operusr3       string,operrq3       string,operusr4       string,operrq4       string,pt       string,ds        string";
        //String test = "store_code      string,store_name      string,seqno           string,rowno           double,com_code        string,xstm           string,splb           string,name           string,unit           string,bzhl           double,cd           string,yssl           double,sssl           double,pici           string,zpsl           double,jxtax           double,hsjj           double,hsjjje           double,bhsjj           double,bhsjjje           double,lsj           double,lsje           double,kcsl           double,kchsjjje           double,mll           double,scrq           string,bzqts           double,blqts           double,jbrq           string,jbkcsl           double,kl            double,hyj           double,memo           string,weight           double,sphh           string,buyunit           string,buyjj           double,buybzhl           double,pt              string,ds              string";
//        String test = "store_code       string,store_name       string,seqno         string,djbh         string,djlb         string,store         string,gys         string,gys_name      string,gz         string,gz_name      string,jyfs         string,htbh         string,ddbh         string,fphm         string,zqqx         double,xdrq         string,dhrq         string,jhrq         string,ysrq         string,cgy         string,cgy_name      string,lry         string,lry_name      string,lrrq         string,shr         string,shr_name      string,shrq         string,qxr         string, qxr_name     string,qxrq         string,shbz         string,yf         double,bxf         double,qt         double,qt1         double,fkzj         double,yseqno         string,ydjbh         string,dhpici         double,exec1         string,ck         string,operusr1         string,operrq1         string,operusr2         string,operrq2         string,operusr3         string,operrq3         string,operusr4         string,operrq4         string,pt              string,ds              string";
//        String[] split = test.split(",");
//        String a = "";
//        for (String w :split){
//            String[] split1 = w.split(" +");
//            String type = split1[1].replace("string","text");
//            String format = String.format(model, split1[0], type);
//            a=a+format;
//        }
//        System.out.println(a);
    }


}
