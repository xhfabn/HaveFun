package com.wujiawei.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtil {

    //不同项目对应的唯一标识
    private static final String key="wujiawei8848";

    public static String createToken(long id,String mobile){
        DateTime now=DateTime.now();
        DateTime expTime=now.offset(DateField.HOUR,24);
        Map<String,Object> payload=new HashMap<>();
        payload.put(JWTPayload.ISSUED_AT,now);
        payload.put(JWTPayload.EXPIRES_AT,expTime);
        payload.put(JWTPayload.NOT_BEFORE,now);
        payload.put("id",id);
        payload.put("mobile",mobile);
        String token= JWTUtil.createToken(payload,key.getBytes());
        log.info("token:{}",token);
        return token;
    }

    public static boolean validateToken(String token){
        JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
        boolean validate = jwt.validate(0);
        log.info("JWT token校验结果是:{}",validate);
        return validate;
    }

    public static JSONObject getJSONObject(String token) {
        JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        log.info("根据token获取原始内容：{}", payloads);
        return payloads;
    }


}
