package com.wujiawei.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication
@ComponentScan("com.wujiawei")
@MapperScan("com.wujiawei.*.mapper")
@EnableFeignClients("com.wujiawei.batch.feign")
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BatchApplication.class);
        ConfigurableEnvironment env = app.run(args).getEnvironment();
        log.info("batch模块：启动成功");
    }

}
