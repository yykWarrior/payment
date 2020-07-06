package com.pay.rd;

import com.alibaba.fastjson.JSON;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static jodd.util.ThreadUtil.sleep;

/**
 * @version v1.0
 * @ClassName: RedissonImpl
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/29 16:17
 */
@Service
public class RedissonImpl {
    @Autowired
    private RedissonConfig redissonConfig;
    @Autowired
    private RedisTemplate redisTemplate;

     Jedis jedis=new Jedis("127.0.0.1",6379);

    public Jedis getJedis() {
        jedis.auth("123456");
        return jedis;
    }

    public void getProduct(){
        RLock redis_lock = null;
        try {
            Redisson redisson = redissonConfig.getRedisson();
            //加锁
            redis_lock = redisson.getLock("redis_lock");
            redis_lock.lock(5, TimeUnit.SECONDS);
            //redis_lock.lock();
            //业务逻辑
            //获取当前剩余优惠卷库存
            String k100 = getJedis().get("k100");
            int integer = Integer.parseInt(k100);
            sleep(40);
          /*  Set keys = redisTemplate.keys("*");
            System.out.println(keys);
            Object k100 = redisTemplate.opsForValue().get("k100");
            Integer integer = JSON.parseObject(k100.toString(), Integer.class);*/
            if(integer>0){
                System.out.println("抢到优惠卷");
                //优惠卷减一
               // Integer integer1 = integer--;
                getJedis().decr("k100");
                //redisTemplate.opsForValue().set("k100",integer1);
            }else{
                System.out.println("抢优惠卷失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //释放锁
            redis_lock.unlock();
        }

    }
}
