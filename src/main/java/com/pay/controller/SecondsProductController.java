package com.pay.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

/**
 * @version v1.0
 * @ClassName: SecondsProduct
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/3 10:15
 */
@RestController
public class SecondsProductController {

    @RequestMapping("redis/getProduct")
    public String getProduct(){
        String uuid = UUID.randomUUID().toString();
        String key="card";
        Jedis jedis = new Jedis();

        jedis.watch(key);
        String card = jedis.get(key);
        if(card==null){
            System.out.println("do not start");
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
        return "success";
        }
}
