package com.wanbao.store.order.service;

import java.util.Date;

import com.wanbao.store.order.bean.TaotaoResult;
import com.wanbao.store.order.dao.IOrder;
import com.wanbao.store.order.pojo.Order;
import com.wanbao.store.order.pojo.PageResult;
import com.wanbao.store.order.pojo.ResultMsg;
import com.wanbao.store.order.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired      
    private IOrder orderDao;        // 如何保证调用的是IOrder的实现类 =>.....-dao.xml配置文件中设置
    //private OrderDAO orderDao;

    
    //    @Autowired
//    private RabbitTemplate rabbitTemplate;

    public TaotaoResult createOrder(String json) {
        Order order = null;
        try {
            order = objectMapper.readValue(json, Order.class);
            // 校验Order对象
            ValidateUtil.validate(order);
        } catch (Exception e) {
            return TaotaoResult.build(400, "请求参数有误!");
        }

        //try {   => 此处不应该捕获异常,否则出错时这个service的方法没有抛出异常,无法进行事务回滚!!!
            // 生成订单ID，规则为：userid+当前时间戳
            String orderId = order.getUserId() + "" + System.currentTimeMillis();
            order.setOrderId(orderId);

            // 设置订单的初始状态为未付款
            order.setStatus(1);

            // 设置订单的创建时间
            order.setCreateTime(new Date());
            order.setUpdateTime(order.getCreateTime());

            // 设置买家评价状态，初始为未评价
            order.setBuyerRate(0);

            // 持久化order对象
            orderDao.createOrder(order);
            
            //发送消息到RabbitMQ
//            Map<String, Object> msg = new HashMap<String, Object>(3);
//            msg.put("userId", order.getUserId());
//            msg.put("orderId", order.getOrderId());
//            List<Long> itemIds = new ArrayList<Long>(order.getOrderItems().size());
//            for (OrderItem orderItem : order.getOrderItems()) {
//                itemIds.add(orderItem.getItemId());
//            }
//            msg.put("itemIds", itemIds);
//            this.rabbitTemplate.convertAndSend(objectMapper.writeValueAsString(msg));
            
            return TaotaoResult.ok(orderId);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //return TaotaoResult.build(400, "保存订单失败!");
    }

    
    
    public Order queryOrderById(String orderId) {
        Order order = orderDao.queryOrderById(orderId);
        return order;
    }

    public PageResult<Order> queryOrderByUserNameAndPage(String buyerNick, int page, int count) {
        return orderDao.queryOrderByUserNameAndPage(buyerNick, page, count);
    }

    public ResultMsg changeOrderStatus(String json) {
        Order order = null;
        try {
            order = objectMapper.readValue(json, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultMsg("400", "请求参数有误!");
        }
        return this.orderDao.changeOrderStatus(order);
    }

}
