package com.rmxc.audit.vo;

import lombok.Data;

@Data
public class DealResult {

    //执行sql
    private String sql;
    //sql返回值
    private String value;

}
