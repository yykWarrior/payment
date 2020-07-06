package com.pay.controller;

import com.pay.rd.RedissonImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version v1.0
 * @ClassName: RedissonController
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/29 16:26
 */
@RestController
public class RedissonController {
    @Autowired
    private RedissonImpl redisson;

    @RequestMapping("/redisson/get")
    public String getProduct(){
        try {
            redisson.getProduct();
            return "访问成功";
        } catch (Exception e) {
            return "访问失败"+e.getMessage();
        }

    }
}
