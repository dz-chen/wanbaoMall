package com.wanbao.web.service;

import com.wanbao.web.bean.Order;
import com.wanbao.web.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.httpclient.HttpResult;
import com.wanbao.common.service.ApiService;
import com.wanbao.sso.query.bean.User;

@Service
public class OrderService {

    @Autowired
    private ApiService apiService;

    @Value("${WANBAO_ORDER_URL}")
    private String WANBAO_ORDER_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 提交订单到订单系统(order.com/order/create)
     * 
     * @param order
     * @return
     */
    public String submit(Order order) {
        String url = WANBAO_ORDER_URL + "/order/create";

//        // 填充当前登录用户的信息  => 这部分目前改为直接在controller中实现 --2021.03.02
//        User user = UserThreadLocal.get();
//        order.setUserId(user.getId());
//        order.setBuyerNick(user.getUsername());

        try {
            // 这里是前台系统,它需要通过httpclient(apiService)向订单系统提交订单
            String json = MAPPER.writeValueAsString(order);
            HttpResult httpResult = this.apiService.doPostJson(url, json);
            if (httpResult.getCode().intValue() == 200) {
                String body = httpResult.getBody();
                JsonNode jsonNode = MAPPER.readTree(body);
                if (jsonNode.get("status").asInt() == 200) {
                    // 提交成功
                    return jsonNode.get("data").asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Order queryByOrderId(String orderId) {
        String url = WANBAO_ORDER_URL + "/order/query/" + orderId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isNotEmpty(jsonData)) {
                return MAPPER.readValue(jsonData, Order.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
