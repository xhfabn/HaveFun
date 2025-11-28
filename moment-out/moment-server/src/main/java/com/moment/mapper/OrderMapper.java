package com.moment.mapper;

import com.github.pagehelper.Page;
import com.moment.dto.GoodsSalesDTO;
import com.moment.dto.OrdersDTO;
import com.moment.dto.OrdersPageQueryDTO;
import com.moment.entity.Orders;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    @Select("select * from orders where id=#{orderId}")
    Orders selectById(Long orderId);

    Page<Orders> queryPage(OrdersDTO ordersDTO);

    @Delete("delete from orders where id=#{id}")
    void deleteById(Long id);

    Page<Orders> adminQueryPage(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders order by order_time desc ")
    Page<Orders> queryPageAll(OrdersDTO ordersDTO);

    @Select("select count(*) from orders where status=#{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status=#{status} and order_time < #{time}")
    List<Orders> getByStatusAndTime(Integer status,LocalDateTime time);

    @Select("select * from orders where status=#{status}")
    List<Orders> getByStatus(Integer status);

    Double sumByMap(Map map);

    @Select("select count(id) from orders where order_time between #{beginTime} and #{endTime}")
    Integer getByTime(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(id) from orders where status=#{status} and order_time between #{beginTime} and #{endTime}")
    Integer getByTimeAndStatus(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    List<GoodsSalesDTO> getSalesTop(LocalDateTime beginTime, LocalDateTime endTime);

    Integer countByMap(Map map);
}
