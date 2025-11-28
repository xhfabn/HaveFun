package com.wujiawei.business.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication
@ComponentScan("com.wujiawei")
@MapperScan("com.wujiawei.*.mapper")
public class BusinessApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        ConfigurableEnvironment env = app.run(args).getEnvironment();
        log.info("启动成功");
    }

}
