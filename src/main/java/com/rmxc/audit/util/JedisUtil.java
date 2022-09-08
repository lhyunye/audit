package com.rmxc.audit.util;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class JedisUtil {

    //获取redis 链接
    public static Jedis getRedis(){


        //链接Redis
        Jedis jedis = new Jedis("127.0.0.1");

        return jedis;
    }


    //获取redis 链接
    public static JedisCluster getRedisCluster(){

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMinIdle(2);
        config.setMaxIdle(500);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("10.6.2.92", 6385));
        nodes.add(new HostAndPort("10.6.2.93", 6385));
//        nodes.add(new HostAndPort("10.6.2.168", 6385));
        JedisCluster jedisCluster = new JedisCluster(nodes,10000,10000,100,"CMs6385",config);



        return jedisCluster;
    }

//    public static void main(String[] args) throws InterruptedException {
//        JedisCluster redisCluster = getRedisCluster();
//        redisCluster.set("1","1");
//        System.out.println(redisCluster.get("1"));
//        Thread.sleep(3000);
//        redisCluster.close();
//
//    }
}
