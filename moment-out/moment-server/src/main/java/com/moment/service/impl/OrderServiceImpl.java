package com.moment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.moment.WebSocket.WebSocketServer;
import com.moment.constant.MessageConstant;
import com.moment.context.BaseContext;
import com.moment.dto.*;
import com.moment.entity.*;
import com.moment.exception.AddressBookBusinessException;
import com.moment.exception.OrderBusinessException;
import com.moment.exception.ShoppingCartBusinessException;
import com.moment.mapper.*;
import com.moment.result.PageResult;
import com.moment.service.OrderService;
import com.moment.utils.WeChatPayUtil;
import com.moment.vo.OrderPaymentVO;
import com.moment.vo.OrderStatisticsVO;
import com.moment.vo.OrderSubmitVO;

import com.moment.vo.OrderVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WebSocketServer webSocketServer;

    public OrderSubmitVO orderMake(OrdersSubmitDTO ordersSubmitDTO) {
       //首先要处理异常状态，可能会出现的情况，比如地址为空，或者是购物车为空
        AddressBook byId = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(byId==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //第二种问题是购物车为空
        Long id = BaseContext.getCurrentId();
        ShoppingCart cart=ShoppingCart.builder()
                .id(id)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(cart);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //处理完异常后要考虑向Order表中插入一条数据
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        //对类中未赋值的数据进行赋值
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(orders.PENDING_PAYMENT);
        orders.setUserId(id);
        orders.setConsignee(byId.getConsignee());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(byId.getPhone());
        String address=byId.getProvinceName()+byId.getCityName()+byId.getDistrictName()+byId.getDetail();
        orders.setAddress(address);
        orderMapper.insert(orders);
        //向order_detail表中插入多条数据
        List<OrderDetail> odList=new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            odList.add(orderDetail);
        }
        orderDetailMapper.insert(odList);
        //清空当前的购物车数据
        shoppingCartMapper.deleteAll(id);
        //封装vo返回
        OrderSubmitVO submitVO=OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .build();
        return submitVO;
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        Orders orders = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!orders.getStatus().equals(Orders.PENDING_PAYMENT)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        Integer orderPaidStatus = Orders.PAID;//支付状态，已支付
        Integer orderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单
        LocalDateTime checkOutTime = LocalDateTime.now();//更新支付时间
        orderMapper.updateStatus(orderStatus, orderPaidStatus, checkOutTime, orders.getId());

        Orders updatePayMethod = Orders.builder()
                .id(orders.getId())
                .payMethod(ordersPaymentDTO.getPayMethod())
                .build();
        orderMapper.update(updatePayMethod);

        //通过websocket向客户端浏览器推送消息 type orderId content
        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",orders.getId());
        map.put("content","订单号："+orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public OrderVO queryOneOrder(Long orderId) {
        //先要通过订单id查询订单的信息，封装到orders中去
        Orders orders=orderMapper.selectById(orderId);
        //再要通过用户id查询特定的菜品详情
        if(orders!=null){
            List<OrderDetail> orderDetailList=orderDetailMapper.getById(orders.getId());
            //这样以后，orders中是订单的信息，list中是菜品的详细信息
            OrderVO orderVO=new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            return orderVO;
        }
       throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        OrdersDTO ordersDTO=new OrdersDTO();
        BeanUtils.copyProperties(ordersPageQueryDTO,ordersDTO);
        ordersDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //因为是查询数据库中的数据，所以肯定是用完整的实体类接收
        Page<Orders> page=new Page<>();
//        if(ordersPageQueryDTO.getStatus()==null){
//           ordersPageQueryDTO.setStatus(Orders.UN_PAID);
//        }
        page=orderMapper.queryPage(ordersDTO);

        //将orders从page中取出，进行封装
        List<OrderVO> orderVOList =new ArrayList<>();
        if(page!=null&&page.getTotal()>0){
            for (Orders orders : page) {
                Long id=orders.getId();
                List<OrderDetail> orderDetailList =new ArrayList<>();
                orderDetailList=orderDetailMapper.getById(id);
                OrderVO orderVO=new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(),orderVOList);
    }

    @Override
    public void cancelOrder(Long id) {
        //删除订单，就要删除orders表中的数据，以及ordersDetail表中的数据
        //首先是删除ordersDetail表中的数据
        Orders orders=orderMapper.selectById(id);
        //orders中的id为orderdetail中的orderid
        Long orderId=orders.getId();
        orderDetailMapper.deleteByUserid(orderId);
        orderMapper.deleteById(id);
    }

    @Override
    public void repeteOrder(Long id) {
        //再来一单是将这一单的信息重新放到该购物车中
        //先获得订单的信息
        Orders orders=orderMapper.selectById(id);
        List<OrderDetail> odList=new ArrayList<>();
        odList=orderDetailMapper.getById(id);
        for (OrderDetail orderDetail : odList) {
            ShoppingCart shoppingCart=new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }
    //管理端Order的开发
    @Override
    public PageResult adminOrderScrach(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //最后传出的records是列表
        //还要获取到菜品的信息
        Page<Orders> page=orderMapper.adminQueryPage(ordersPageQueryDTO);
        List<OrderVO> orderVOList =new ArrayList<>();
//        for (Orders orders1 : page) {
//            OrderVO orderVo=new OrderVO();
//            List<OrderDetail> orderDetailList=new ArrayList<>();
//            BeanUtils.copyProperties(orders1,orderDetailList);
//            List<OrderDetail> orderDetails=orderDetailMapper.getById(orders1.getId());
//            orderVo.setOrderDetailList(orderDetails);
//            orderVo.setOrderDishes(o);
//            ordersList.add(orderVo);
//        }
        //todo 这里答案上有不一样的解法
        if(page!=null&&page.getTotal()>0){
            for (Orders orders : page) {
                Long id=orders.getId();
                List<OrderDetail> orderDetailList =new ArrayList<>();
                orderDetailList=orderDetailMapper.getById(id);
                OrderVO orderVO=new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                //这里要将orders 得到名字*数量的格式 定义一个函数
                String orderDishes= getOrderDishesStr(orders);
                orderVO.setOrderDishes(orderDishes);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(),orderVOList);
    }
    //todo 这里函数有stream流的做法
    private String getOrderDishesStr(Orders orders) {
        //要得到的格式类似于 小炒鱼*2
        List<OrderDetail> detailList=orderDetailMapper.getById(orders.getId());
        String orderDish=new String();
        for (OrderDetail detail : detailList) {
            orderDish+=detail.getName()+'*'+detail.getNumber()+';';
        }
        return orderDish;
    }
    @Override
    public OrderStatisticsVO countStaus() {
      Integer toBeConfirmed=orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
      Integer confirmed=orderMapper.countStatus(Orders.CONFIRMED);
      Integer deliveryInProgress=orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
      return new OrderStatisticsVO(toBeConfirmed,confirmed,deliveryInProgress);
    }

    @Override
    public OrderVO selectOrderDetail(Long id) {
        OrderVO orderVO =new OrderVO();
        Orders orders=orderMapper.selectById(id);
        BeanUtils.copyProperties(orders,orderVO);
        List<OrderDetail> orderDetails = orderDetailMapper.getById(id);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        //接单就是改变订单的状态
        Orders orders=orderMapper.selectById(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orders.setId(ordersConfirmDTO.getId());
        orderMapper.update(orders);
    }

    @Override
    public void refuseOrder(OrdersRejectionDTO rejectionDTO) {
        //拒单要考虑多个条件
        //首先要取消的拒单的对象一定是待接单
        Orders orders=orderMapper.selectById(rejectionDTO.getId());
        if(orders.getStatus()!=Orders.TO_BE_CONFIRMED||orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR );
        }
        //拒单要提供拒单的原因
        orders.setRejectionReason(rejectionDTO.getRejectionReason());
        //拒单时若已完成付款，要退款
        if(orders.getPayStatus()==Orders.PAID){
            log.info("申请退款，退款的金额为：{}",orders.getAmount());
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void cancelStatus(OrdersCancelDTO ordersCancelDTO) {
        //取消订单也是要改变订单状态
        Orders orders=orderMapper.selectById(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        //取消订单时商家要给定取消的原因
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        //取消订单时若用户已付款，要退款
        if (orders.getPayStatus()==Orders.PAID){
            log.info("申请退款，退款的金额为：{}",orders.getAmount());
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void deliverOrder(Long id) {
        //派送的订单首先必须是已接单的
        Orders orders=orderMapper.selectById(id);
        if (orders.getStatus()!=Orders.CONFIRMED||orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //派送订单依旧是改变订单的状态
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
       orderMapper.update(orders);
    }

    @Override
    public void completeOrder(Long id) {
        Orders orders=orderMapper.selectById(id);
        //完成的订单必须是派送中的订单
        if (orders.getStatus()!=Orders.DELIVERY_IN_PROGRESS||orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        Orders orders=orderMapper.selectById(id);
        //判断订单是否为空
        if (orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //要弹窗要使用websocket 而websocket中要传入json格式的数据 利用map集合进行封装
        Map map =new HashMap<>();
        //map可以不指定格式
        map.put("type",2);//1表示来单提醒，2表示客户催单
        map.put("orderId",id);//获取订单id
        map.put("content","订单号："+orders.getNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(map));

    }


}
