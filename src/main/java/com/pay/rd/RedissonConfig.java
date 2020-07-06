package com.pay.rd;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.connection.ConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @version v1.0
 * @ClassName: GetRedisson
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/29 16:12
 */
@Component
public class RedissonConfig {

    @Bean
    public Redisson getRedisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0);
        config.useSingleServer().setPassword("123456");
        //config.useClusterServers().addNodeAddress("redis://127.0.0.1:6379");
        //config.useClusterServers().setPassword("123456");
        return (Redisson) Redisson.create(config);
    }
}
