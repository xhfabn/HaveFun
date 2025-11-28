package com.moment.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
public class UserLoginWxDTO implements Serializable {

    private String code;

}
