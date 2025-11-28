package com.wujiawei.member.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
@ComponentScan("com.wujiawei")
@MapperScan("com.wujiawei.*.mapper")
public class MemberApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MemberApplication.class);
        ConfigurableEnvironment env = app.run(args).getEnvironment();
        log.info("启动成功");
    }

}
