package com.wujiawei.gateway.config;

import cn.hutool.http.HttpStatus;

import com.wujiawei.gateway.config.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class LoginMemberFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path=exchange.getRequest().getURI().getPath();

        if(path.equals("/admin")
        || path.equals("/member/member/login")
        || path.equals("/member/member/send-code")){
            log.info("不需要登录验证");
            return chain.filter(exchange);
        }
        log.info("需要登录验证");
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if(token==null||token.equals("")){
            log.info("token为空");
            exchange.getResponse().setRawStatusCode(HttpStatus.HTTP_UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        boolean validated = JwtUtil.validateToken(token);
        if(validated){
            return chain.filter(exchange);
        }else{
            log.info("token无效");
            exchange.getResponse().setRawStatusCode(HttpStatus.HTTP_UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
