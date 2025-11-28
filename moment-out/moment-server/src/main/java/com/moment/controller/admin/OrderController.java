package com.moment.controller.admin;

import com.moment.dto.OrdersCancelDTO;
import com.moment.dto.OrdersConfirmDTO;
import com.moment.dto.OrdersPageQueryDTO;
import com.moment.dto.OrdersRejectionDTO;
import com.moment.result.PageResult;
import com.moment.result.Result;
import com.moment.service.OrderService;
import com.moment.vo.OrderStatisticsVO;
import com.moment.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Tag(name = "管理端订单接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @Operation(summary = "订单搜索")
    public Result<PageResult> orderScrach(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索：{}",ordersPageQueryDTO);
        PageResult pageResult=orderService.adminOrderScrach(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/statistics")
    @Operation(summary = "各个状态的订单数量统计")
    public Result<OrderStatisticsVO> orderStatusNum(){
        log.info("开始统计各个状态的订单数量");
        OrderStatisticsVO orderStatisticsVO=orderService.countStaus();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> selectOrderDetail(@PathVariable Long id){
        log.info("开始查询订单详情：{}",id);
        OrderVO orderVO=orderService.selectOrderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    @Operation(summary = "接单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("开始接单：{}",ordersConfirmDTO);
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @Operation(summary = "拒单")
    public Result refuseOrder(@RequestBody OrdersRejectionDTO rejectionDTO){
        log.info("开始拒单：{}",rejectionDTO);
        orderService.refuseOrder(rejectionDTO);
        return Result.success();
    }
    @PutMapping("/cancel")
    @Operation(summary = "取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("开始取消订单：{}",ordersCancelDTO);
        orderService.cancelStatus(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @Operation(summary = "派送订单")
    public Result deliverOrder(@PathVariable Long id){
        log.info("开始取消订单：{}",id);
        orderService.deliverOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "完成订单")
    public Result completeOrder(@PathVariable Long id){
        log.info("完成订单：{}",id);
        orderService.completeOrder(id);
        return Result.success();
    }
}
