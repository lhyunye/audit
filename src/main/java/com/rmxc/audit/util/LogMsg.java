package com.rmxc.audit.util;

import lombok.Data;
import lombok.Getter;

@Getter
public enum LogMsg {

    EMAIL_TITLE("<h2>运维人员亲启：</h2><br/>&nbsp;&nbsp;&nbsp;&nbsp;%s日的数据稽核已完成，共耗时%s分钟。其中各策略稽核结果如下：<br/>&nbsp;&nbsp;&nbsp;<table width=\"300\" border=\"1\" cellspacing=\"0\" cellpadding=\"10\" style=\"text-align: center;\"><tr><td>策略</td><td>总数</td><td>成功</td><td>失败</td><td>失败表</td></tr>"),
    EMAIL_TABLE(" <tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>"),
    EMAIL_PS(" </table> <br> ps(各稽核策略详情):<br> 1、count:数据行数稽核<br>2、sum:数据指定字段求和稽核"),
    EMAIL_ENDING(" <br><br><br><br><br><br><br><br><br><br><br><br><br><span style= \"text-align:center\">声明:此电子邮件包含来自王府井股份有限公司的信息，而且是机密的或者专用的信息。<br>这些信息是供以上列出的收件人使用的。<br>如果是误收邮件请通知发件人。<br></span>"),
    MD5_AUDIT_SUCCESS(" MD5稽核结束：数据一致性校验通过"),
    MD5_AUDIT_ERROR(" MD5稽核结束：数据一致性校验未通过"),
    ROW_AUDIT_ERROR(" 行数稽核失败，运维人员查看原因,"),
    SUM_AUDIT_ERROR(" SUM稽核失败，运维人员查看原因,"),
    STRING_AUDIT_ERROR(" STRING稽核失败，运维人员查看原因,"),
    LOG_WEITE_ERROR( "日志写入失败"),
    LOG_WEITE_SUCCESSS( "日志写入成功"),
    PARAM_ERROR( "入参格式异常");

    /**
     * 信息
     */
    private String message;


    LogMsg(String message) {
        this.message = message;
    }
}
