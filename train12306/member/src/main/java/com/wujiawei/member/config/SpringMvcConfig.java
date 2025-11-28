package com.wujiawei.member.config;

import com.wujiawei.common.interceptor.LogInterceptor;
import com.wujiawei.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    private LogInterceptor logInterceptor;

    @Resource
    private MemberInterceptor memberInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
        registry.addInterceptor(memberInterceptor)
        .addPathPatterns("/**")
                .excludePathPatterns(
                        "/member/member/send-code",
                        "/member/member/login");
    }
}
