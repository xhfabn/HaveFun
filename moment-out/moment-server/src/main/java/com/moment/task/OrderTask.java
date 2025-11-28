package com.moment.task;

import com.moment.entity.Orders;
import com.moment.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    //处理下单后超时十五分钟未付款的订单
    //每分钟检查一次当前所有的订单中是否有超时的 注意问号是英文状态的
//    @Scheduled(cron = "1/5 * * * * ?")
    @Scheduled(cron = "1 * * * * ?")
    public void processTimeoutOrder(){
        log.info("开始处理超时订单");
        //首先要获得所有的订单 一个是订单的状态，另一个是截止时间
        LocalDateTime time=LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList =orderMapper.getByStatusAndTime(Orders.PENDING_PAYMENT, time);
        if(ordersList!=null&&ordersList.size()>0){
            for (Orders orders : ordersList) {
                //遍历获取每一个订单
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时未处理，已取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    //处理一直处于派送中订单，即用户未点已完成的订单 统一在凌晨一点处理
//    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("开始处理派送中的订单",LocalDateTime.now());
        //大致的数据库的查询语句 select * from users where status=？ 不用管其他，只用在该时间时就可以
        List<Orders> ordersList=orderMapper.getByStatus(Orders.DELIVERY_IN_PROGRESS);
        if(ordersList!=null&&ordersList.size()>0){
            for (Orders orders : ordersList) {
                //遍历获取每一个订单
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }

    }
}
