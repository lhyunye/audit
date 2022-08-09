package com.rmxc.audit.util;

import lombok.Getter;

@Getter
public enum PublicValue {

    THREAD_TOOL_NUM("5"),
    //每日稽核采用1  每月稽核采用2  自定义稽核采用3
    REDIS_TASK_LIST_NAME_COUNT("audit_task_list_count3"),
    REDIS_TASK_LIST_NAME_SUM("audit_task_list_sum3"),
    REDIS_TASK_LIST_NAME_STRING("audit_task_list_string3");
    /**
     * 配置信息
     */
    private String value;

    /**/


    PublicValue( String value) {
        this.value = value;
    }
}
