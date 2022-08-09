package com.rmxc.audit.util;

import org.slf4j.Logger;

public class SqlModel {

    public static final String queryCount = "select count(*) from %s";

    public static final String queryCountByCase = "select count(*) from %s %s";


    public static final String sumHive = " sum(cast(nvl(%s,0) as decimal(38,%s)))";
    public static final String sumSybase = " isnull(%s,0)";

    public static final String querySybaseSumByCase = "select sum(%s) from %s %s";

    public static final String queryHiveSumByCase = "select %s from %s %s";

    public static final String queryHiveString = "concat_ws('',%s) as keycode";

    public static final String querySybaseString = "(%s) as keycode";

    public static final String queryOne = "select %s from %s";

    public static final String queryStringEnd = " order by keycode";

    public static final String queryOneByCase = "select %s from %s %s";

    public static final String queryAllMsgByCase = "select * from %s %s";

    public static final String queryAllMsgByCaseS = "select *,STR_REPLACE( CONVERT(VARCHAR,dateadd(dd,-1,getdate()),111),'/',null) AS dt from %s %s";

    public static final String addLog = "INSERT INTO audit_log (`data_source`, `s_table_name`, `d_table_name`,`status`, `log`, `insert_time`, `date_time`,`incr`,`model`,`s_value`,`d_value`,`type`,`s_sql`,`d_sql`) VALUES ('%s','%s','%s', '%s', '%s', '%s', '%s', '%s','%s', '%s', '%s', '%s','%s', '%s')";

    public static final String auditResult = "select data_source,s_table_name,status,model from audit_log where date_time = '%s' ";
}
