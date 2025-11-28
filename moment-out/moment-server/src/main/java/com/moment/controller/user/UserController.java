package com.moment.controller.user;

import com.moment.constant.JwtClaimsConstant;
import com.moment.dto.UserLoginDTO;
import com.moment.dto.UserLoginWxDTO;
import com.moment.entity.Employee;
import com.moment.entity.User;
import com.moment.properties.JwtProperties;
import com.moment.result.Result;
import com.moment.service.UserService;
import com.moment.utils.JwtUtil;
import com.moment.vo.EmployeeLoginVO;
import com.moment.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Tag(name = "用户相关的接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;
// 微信登录的接口
//    @PostMapping("/login")
//    @Operation(summary = "用户登录")
//    public Result<UserLoginVO> userLogin(@RequestBody UserLoginWxDTO userLoginWxDTO){
//        log.info("用户的登录信息为：{}", userLoginWxDTO.getCode());
//        User user= userService.wxLogin(userLoginWxDTO);
//        Map<String,Object> claims=new HashMap<>();
//        claims.put(JwtClaimsConstant.USER_ID,user.getId());
//        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
//        UserLoginVO userLoginVO=UserLoginVO.builder()
//                .id(user.getId())
//                .openid(user.getOpenid())
//                .token(token)
//                .build();
//        return Result.success(userLoginVO);
//    }


// 正常登录的接口
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户的登录信息为：{}",userLoginDTO.getUsername());
        User user = userService.userLogin(userLoginDTO);
        Map<String,Object> claims=new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }
}
