package com.rmxc.audit.vo;

import lombok.Data;

@Data
public class CheckResult {
    //源端稽核值
    private String svalue;

    //目的端稽核值
    private String dvalue;

    //源端sql
    private String ssql;

    //目的端sql
    private String dsql;

    //执行日志
    private String log;

}
