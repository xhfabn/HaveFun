package com.moment.service;

import com.moment.dto.UserLoginDTO;
import com.moment.dto.UserLoginWxDTO;
import com.moment.entity.Employee;
import com.moment.entity.User;


public interface UserService {

    User wxLogin(UserLoginWxDTO userLoginDTO);

    User userLogin(UserLoginDTO userLoginDTO);

}
