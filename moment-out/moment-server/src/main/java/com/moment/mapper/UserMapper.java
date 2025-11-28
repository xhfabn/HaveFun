package com.moment.mapper;

import com.moment.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openid}")
    User selectUserById(String openid);


    void insertUserById(User user);

    @Select("select * from user where id=#{id}")
    User getById(Long userId);

    @Select("select count(id) from user where create_time < #{beginTime}")
    Integer getByOrderTime(LocalDateTime beginTime);

    @Select("select count(id) from user where create_time between #{beginTime} and #{endTime}")
    Integer getNewByOrderTime(LocalDateTime beginTime, LocalDateTime endTime);

    Integer countByMap(Map map);
}
