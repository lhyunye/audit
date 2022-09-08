package com.rmxc.audit.Factory;

import com.rmxc.audit.service.CountServer;
import com.rmxc.audit.service.KeyServer;
import com.rmxc.audit.service.StringServer;
import com.rmxc.audit.service.SumServer;
import org.springframework.util.ObjectUtils;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AuditFactory {

    private static final AuditFactory FACTORY = new AuditFactory();
    /**
     * 策略类集合
     */
    private Map<String,Class<? extends AuditService>>  services = new HashMap<>();


    public AuditFactory() {
        services.put("key", KeyServer.class);
        services.put("count", CountServer.class);
        services.put("sum", SumServer.class);
        services.put("string", StringServer.class);


    }

    /**
     * 获取对应值的策略类
     * @Param name 文件名称
     * @Return Price
     * @throws Exception classNotFound
     */
    public AuditService getAudit(String name) throws Exception{
        Class<? extends AuditService> aClass = services.get(name);
        if (!ObjectUtils.isEmpty(aClass)){
            return aClass.newInstance();
        }
        return null;
    }

    static AuditFactory getInstance() {
        return FACTORY;
    }

}
