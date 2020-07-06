package com.pay.controller;

import com.pay.limit.RedisLimiter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version v1.0
 * @ClassName: RedisController
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/28 9:27
 */
@RestController
public class RedisController {
    int sum=100;
    int n=0;

    @RedisLimiter(10)
    @RequestMapping("test/avoid")
    public void aVoid(){
        System.out.println("购买产品成功");
        /*if(sum>0) {
            sum--;
            n++;
            System.out.println("第" + n + "个请求,还剩产品" + sum + "个");
        }*/
    }
}
