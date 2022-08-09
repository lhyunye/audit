package com.rmxc.pulldata.passengerFlow.utils.enums;

import lombok.Getter;

import javax.ws.rs.GET;

@Getter
public enum Key {

    HIVE_DATASOURCE_NAME_DEV("hive_dev"),
    HIVE_DATASOURCE_NAME_PROD("hive_prod"),
    LOAD_LOCAL_DATA_TO_HDFS("hdfs dfs -put -f %s /pro-data/kl/%s "),
    LOAD_LOCAL_DATA_SQL("LOAD DATA  INPATH '/pro-data/kl/%s' OVERWRITE  INTO TABLE %s"),
    LOAD_HIVE_DATA_SQL("insert overwrite table %s partition(pt = '%s') select * from %s"),
    DATE_PATTERN_DAY("yyyyMMdd"),
    DATE_PATTERN_DAY2("yyyy-MM-dd"),
    DATE_PATTERN_SECOND("yyyy-MM-dd HH:mm:ss"),
    DATE_PATTERN_ZERO("yyyy-MM-dd 00:00:00"),
    DATE_PATTERN_MONTH("yyyy-MM"),
    DATE_PATTERN_ALL("yyyy-MM-dd 23:59:59"),
    DATAPATH("./dataMessage/"),
    SQLPATH("./src/main/java/com/rmxc/pulldata/passengerFlow/creatSql/"),
    STAPPKEY("203902378"),
    STAPPSECRET("eYJozzM5lGtzNli7bpuyNTTptHtSe94R"),
    JTAPPKEY("24908745"),
    JTAPPSECRET("2aj2u3wfflww8x8qgqqt8pyfiw5s3t1v"),

    HOST("api.winneryun.com");


    /**
     * 配置信息
     */
    private String value;

    /**/


     Key( String value) {
        this.value = value;
    }
}
