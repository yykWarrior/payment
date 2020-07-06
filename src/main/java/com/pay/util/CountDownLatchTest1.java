package com.pay.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version v1.0
 * @ClassName: CountDownLatchTest1
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/3 10:38
 */
public class CountDownLatchTest1 implements Runnable{
    final AtomicInteger number = new AtomicInteger();
    volatile boolean bol = false;

    @Override
    public void run() {
        //System.out.println(number.getAndIncrement());
        synchronized (this) {
            try {
                if (!bol) {
                    //System.out.println(bol);
                    bol = true;
                    getProduct();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }




    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1000);
        for (int i=0;i<500;i++) {
            fixedThreadPool.execute((Runnable) () -> {

                String uuid = UUID.randomUUID().toString();
                String key="m";
                JedisShardInfo shardInfo = new JedisShardInfo("redis://localhost:6379");//这里是连接的本地地址和端口
                shardInfo.setPassword("123456");//这里是密码
                Jedis jedis = new Jedis(shardInfo);
                jedis.connect();
                jedis.watch(key);
                String s = jedis.get(key);
                //System.out.println(s);
                int i1 = Integer.parseInt(jedis.get(key));
                if(s==null){
                    System.out.println("do not start");
                }else if(i1 <=0){
                    System.out.println(i1);
                    //System.out.println(s);
                    System.out.println("fail");
                }else if(jedis.sismember("user",uuid)){
                    System.out.println("you have get a product,do not get seconder");
                }else {
                    Transaction multi = jedis.multi();
                    multi.decr(key);
                    multi.sadd("user", uuid);
                    List<Object> exec = multi.exec();
                    if (exec == null || exec.size() == 0) {
                        System.out.println("秒杀失败，稍后重试");
                    }else {
                        System.out.println(i1);
                        System.out.println("成功秒杀");
                    }
                }

            });

        }
    }



    public String getProduct(){
        String uuid = UUID.randomUUID().toString();
        String key="card";
        JedisShardInfo shardInfo = new JedisShardInfo("redis://localhost:6379/9");//这里是连接的本地地址和端口
        shardInfo.setPassword("123456");//这里是密码
        Jedis jedis = new Jedis(shardInfo);
        jedis.connect();

        jedis.watch(key);
        Set<String> keys = jedis.keys("*");

        Set<String> k1 = jedis.smembers("k1");
        //System.out.println(k1.);
        String card = jedis.get(key);
        System.out.println(card);
        if(card==null){
            System.out.println("do not start");
            jedis.set(key,"100");
            return "fail";
        }
        int i = Integer.parseInt(card);
        if(i<=0){
            System.out.println("product is over");
            return "fail";
        }
        if(jedis.sismember("user",uuid)){
            System.out.println("you have get a product,do not get seconder");
        }

        Transaction multi = jedis.multi();
        multi.decr(card);
        multi.sadd("user",uuid);
        List<Object> exec = multi.exec();
        if(exec == null || exec.size() == 0) {
            System.out.println("秒杀失败，稍后重试");
        }
        System.out.println("成功秒杀");
        return "success";
    }
}