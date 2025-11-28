package com.moment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moment.constant.MessageConstant;
import com.moment.constant.StatusConstant;
import com.moment.dto.UserLoginDTO;
import com.moment.dto.UserLoginWxDTO;
import com.moment.entity.Employee;
import com.moment.entity.User;
import com.moment.exception.AccountLockedException;
import com.moment.exception.AccountNotFoundException;
import com.moment.exception.LoginFailedException;
import com.moment.exception.PasswordErrorException;
import com.moment.mapper.EmployeeMapper;
import com.moment.mapper.UserMapper;
import com.moment.properties.WeChatProperties;
import com.moment.service.UserService;
import com.moment.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    public User wxLogin(UserLoginWxDTO userLoginWxDTO) {
        //        调用微信接口服务，获取当前微信用户的openid
        String openid = getOpenid(userLoginWxDTO);
//        判断是否登录失败
        if(openid==null){
            throw new  LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
//        判断是否是新用户
        User user=userMapper.selectUserById(openid);
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                            .build();
//            用动态语句来插入，因为后面要用到它的主键值
            userMapper.insertUserById(user);
        }
        return user;
    }

    @Override
    public User userLogin(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);
        User user=new User();
        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的密码进行md5加密处理

        password= DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        user.setId(employee.getId());
        user.setName(employee.getName());
        //3、返回实体对象
        return user;

    }

    private String getOpenid(UserLoginWxDTO userLoginWxDTO) {
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code", userLoginWxDTO.getCode());
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
