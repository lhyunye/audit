package com.rmxc.audit.util;

import com.alibaba.fastjson.JSONObject;
import com.rmxc.audit.vo.JdbcResult;
import com.rmxc.change.data.DataFormat;
import com.sun.org.apache.regexp.internal.RE;
import com.youzan.cloud.open.sdk.common.constant.OAuthEnum;
import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Auth;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v1_0_0.api.*;
import com.youzan.cloud.open.sdk.gen.v1_0_0.model.*;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanUsersWeixinFollowersInfoSearch;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanUsersWeixinFollowersInfoSearchParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanUsersWeixinFollowersInfoSearchResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanTradeGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanTradesSoldGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradeGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradeGetResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;

import org.apache.tools.ant.taskdefs.SendEmail;
import com.youzan.cloud.open.sdk.core.client.auth.Token;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class test {

    public static void getTradesSold(Token token) throws ParseException, SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();


        YouzanTradesSoldGet youzanTradesSoldGet = new YouzanTradesSoldGet();

        //创建参数对象
        YouzanTradesSoldGetParams youzanTradesSoldGetParams = new YouzanTradesSoldGetParams();
        //设置参数
        youzanTradesSoldGetParams.setPageNo(1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String data = "2021-06-05 00:00:00";

        Date parse = simpleDateFormat.parse(data);

        youzanTradesSoldGetParams.setStartCreated(parse);

        youzanTradesSoldGet.setAPIParams(youzanTradesSoldGetParams);

        YouzanTradesSoldGetResult result = yzClient.invoke(youzanTradesSoldGet,  token, YouzanTradesSoldGetResult.class);

        System.out.println(JSONObject.toJSON(result).toString());


    }

    public static void getTrade(Token token) throws ParseException, SDKException {

        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();

        YouzanTradeGet youzanTradeGet = new YouzanTradeGet();
        YouzanTradeGetParams youzanTradeGetParams = new YouzanTradeGetParams();
        youzanTradeGetParams.setTid("E20210607144144028904131");
        youzanTradeGet.setAPIParams(youzanTradeGetParams);

        YouzanTradeGetResult result = yzClient.invoke(youzanTradeGet, token, YouzanTradeGetResult.class);
        System.out.println(JSONObject.toJSON(result).toString());
    }

    public static void getOrderPromotion(Token token) throws SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();

        YouzanTradeOpenPcOrderPromotion youzanTradeOpenPcOrderPromotion = new YouzanTradeOpenPcOrderPromotion();
        //创建参数对象,并设置参数
        YouzanTradeOpenPcOrderPromotionParams youzanTradeOpenPcOrderPromotionParams = new YouzanTradeOpenPcOrderPromotionParams();
        youzanTradeOpenPcOrderPromotionParams.setPostagePromotion(true);
        youzanTradeOpenPcOrderPromotionParams.setGoodsLevelPromotion(true);
        youzanTradeOpenPcOrderPromotionParams.setOrderSharePromotion(true);
        youzanTradeOpenPcOrderPromotionParams.setTid("E20210607144144028904131");
        youzanTradeOpenPcOrderPromotion.setAPIParams(youzanTradeOpenPcOrderPromotionParams);

        YouzanTradeOpenPcOrderPromotionResult result = yzClient.invoke(youzanTradeOpenPcOrderPromotion, token, YouzanTradeOpenPcOrderPromotionResult.class);
        System.out.println(JSONObject.toJSON(result).toString());
    }

    public static void getOrderBatch(Token token) throws SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();

        YouzanLogisticsOrderBatchQuery youzanLogisticsOrderBatchQuery = new YouzanLogisticsOrderBatchQuery();
        //创建参数对象,并设置参数
        YouzanLogisticsOrderBatchQueryParams youzanLogisticsOrderBatchQueryParams = new YouzanLogisticsOrderBatchQueryParams();
        List<YouzanLogisticsOrderBatchQueryParams.YouzanLogisticsOrderBatchQueryParamsOrderdetailparams> orderDetailParams = new ArrayList<>();
        YouzanLogisticsOrderBatchQueryParams.YouzanLogisticsOrderBatchQueryParamsOrderdetailparams detail = new YouzanLogisticsOrderBatchQueryParams.YouzanLogisticsOrderBatchQueryParamsOrderdetailparams();
        detail.setOrderId("E20210607144144028904131");
        orderDetailParams.add(detail);
        youzanLogisticsOrderBatchQueryParams.setOrderDetailParams(orderDetailParams);
        youzanLogisticsOrderBatchQuery.setAPIParams(youzanLogisticsOrderBatchQueryParams);

        YouzanLogisticsOrderBatchQueryResult result = yzClient.invoke(youzanLogisticsOrderBatchQuery, token, YouzanLogisticsOrderBatchQueryResult.class);
        System.out.println(JSONObject.toJSON(result).toString());
    }

    public static void getStandardData(Token token) throws SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();
        YouzanDatacenterCloudTeamStandardData youzanDatacenterCloudTeamStandardData = new YouzanDatacenterCloudTeamStandardData();
        //创建参数对象,并设置参数
        YouzanDatacenterCloudTeamStandardDataParams youzanDatacenterCloudTeamStandardDataParams = new YouzanDatacenterCloudTeamStandardDataParams();
        youzanDatacenterCloudTeamStandardDataParams.setEndDay("2021-07-01");
        youzanDatacenterCloudTeamStandardDataParams.setStartDay("2021-07-01");
        List<Long> kdt_list = new ArrayList<>();
        kdt_list.add(Long.getLong("44561513"));
        youzanDatacenterCloudTeamStandardDataParams.setKdtList(kdt_list);
        youzanDatacenterCloudTeamStandardData.setAPIParams(youzanDatacenterCloudTeamStandardDataParams);
        YouzanDatacenterCloudTeamStandardDataResult result = yzClient.invoke(youzanDatacenterCloudTeamStandardData, token, YouzanDatacenterCloudTeamStandardDataResult.class);
        System.out.println(JSONObject.toJSON(result).toString());
    }

    public static void getStandardData2(Token token) throws SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();
        YouzanDatacenterDatastandardTeam youzanDatacenterDatastandardTeam = new YouzanDatacenterDatastandardTeam();
        //创建参数对象,并设置参数
        YouzanDatacenterDatastandardTeamParams youzanDatacenterDatastandardTeamParams = new YouzanDatacenterDatastandardTeamParams();
        youzanDatacenterDatastandardTeamParams.setEndDay(20210701);
        youzanDatacenterDatastandardTeamParams.setDateType(1);
        youzanDatacenterDatastandardTeamParams.setStartDay(20210701);
        List<Long> kdt_list = new ArrayList<>();
        kdt_list.add(Long.parseLong("45728876"));
        youzanDatacenterDatastandardTeamParams.setKdtList(kdt_list);
        youzanDatacenterDatastandardTeamParams.setShopRole(1);

        youzanDatacenterDatastandardTeam.setAPIParams(youzanDatacenterDatastandardTeamParams);

        YouzanDatacenterDatastandardTeamResult result = yzClient.invoke(youzanDatacenterDatastandardTeam, token, YouzanDatacenterDatastandardTeamResult.class);
        System.out.println(JSONObject.toJSON(result).toString());
    }


    public static void getFlowoverviewQuery(Token token) throws SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();

        YouzanBigdataTeamFlowoverviewQuery youzanBigdataTeamFlowoverviewQuery = new YouzanBigdataTeamFlowoverviewQuery();
        //创建参数对象,并设置参数
        YouzanBigdataTeamFlowoverviewQueryParams youzanBigdataTeamFlowoverviewQueryParams = new YouzanBigdataTeamFlowoverviewQueryParams();
//        youzanBigdataTeamFlowoverviewQueryParams.setNodeKdtId(8888);
//        youzanBigdataTeamFlowoverviewQueryParams.setDateType(1);
//        youzanBigdataTeamFlowoverviewQueryParams.setChannelType(weapp);
        youzanBigdataTeamFlowoverviewQueryParams.setCurrentDay(20201022);
        youzanBigdataTeamFlowoverviewQuery.setAPIParams(youzanBigdataTeamFlowoverviewQueryParams);

        YouzanBigdataTeamFlowoverviewQueryResult result = yzClient.invoke(youzanBigdataTeamFlowoverviewQuery, token, YouzanBigdataTeamFlowoverviewQueryResult.class);
    }


    public static void getFollowersInfo(Token token) throws SDKException {
        //初始化有赞客户端
        DefaultYZClient yzClient = new DefaultYZClient();
        YouzanUsersWeixinFollowersInfoSearch youzanUsersWeixinFollowersInfoSearch = new YouzanUsersWeixinFollowersInfoSearch();
//创建参数对象,并设置参数
        YouzanUsersWeixinFollowersInfoSearchParams youzanUsersWeixinFollowersInfoSearchParams = new YouzanUsersWeixinFollowersInfoSearchParams();
        youzanUsersWeixinFollowersInfoSearchParams.setStartFollow("2021-06-19 00:00:00");
        youzanUsersWeixinFollowersInfoSearchParams.setEndFollow("2021-06-19 23:00:00");
        youzanUsersWeixinFollowersInfoSearchParams.setPageNo(1);
        youzanUsersWeixinFollowersInfoSearchParams.setPageSize(10);
        youzanUsersWeixinFollowersInfoSearch.setAPIParams(youzanUsersWeixinFollowersInfoSearchParams);

        YouzanUsersWeixinFollowersInfoSearchResult result = yzClient.invoke(youzanUsersWeixinFollowersInfoSearch, token, YouzanUsersWeixinFollowersInfoSearchResult.class);
        System.out.println(JSONObject.toJSON(result).toString());

    }


    public static void main(String[] args) throws SQLException, MessagingException, SDKException, ParseException {



        String a = "14013139464160543,3226067691815744,543807342650656,19215281648.43,12561418477.38,13381309493.55,9846650.30,18503500346.75";

        String[] split = a.split(",");
        List<BigDecimal> values = new ArrayList<>();
        for(String value:split){
            BigDecimal bigDecimal = new BigDecimal(value);
            values.add(bigDecimal);

        }
        BigDecimal bigss=BigDecimal.ZERO;
        for(BigDecimal b :values){
            bigss=bigss.add(b);
        }
        String sum = bigss.setScale(Integer.parseInt("2"),BigDecimal.ROUND_UP).toString();

        System.out.println(sum);


//        Token token = new Token("542dc937584358012747912aff01829");

        //getTradesSold(token);
        //getTrade(token);
        //getOrderPromotion(token);
//        getOrderBatch(token);
//       getStandardData(token);
//        System.out.println(new Date().getTime());
//       // getStandardData2(token);
       // getFollowersInfo(token);

    }
}
