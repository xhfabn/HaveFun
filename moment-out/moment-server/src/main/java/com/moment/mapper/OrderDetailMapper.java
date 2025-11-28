package com.moment.mapper;

import com.moment.entity.OrderDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {


    void insert(List<OrderDetail> odList);

    @Select("select * from order_detail where order_id=#{id}")
    List<OrderDetail> getById(Long id);

    @Delete("delete from order_detail where order_id=#{orderId}")
    void deleteByUserid(Long orderId);
}
