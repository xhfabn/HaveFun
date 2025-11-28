package com.moment.service;

import com.moment.dto.*;
import com.moment.result.PageResult;
import com.moment.vo.OrderPaymentVO;
import com.moment.vo.OrderStatisticsVO;
import com.moment.vo.OrderSubmitVO;
import com.moment.vo.OrderVO;


public interface OrderService {


    OrderSubmitVO orderMake(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO queryOneOrder(Long orderId);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    void cancelOrder(Long id);

    void repeteOrder(Long id);

    PageResult adminOrderScrach(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO countStaus();

    OrderVO selectOrderDetail(Long id);

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void refuseOrder(OrdersRejectionDTO rejectionDTO);

    void cancelStatus(OrdersCancelDTO ordersCancelDTO);

    void deliverOrder(Long id);

    void completeOrder(Long id);

    void reminder(Long id);
}
