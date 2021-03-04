package com.wanbao.web.service;

import java.util.List;

import com.wanbao.web.bean.Cart;
import com.wanbao.web.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.service.ApiService;
import com.wanbao.sso.query.bean.User;

@Service
public class CartService {

    @Autowired
    private ApiService apiService;

    @Value("${WANBAO_CART_URL}")
    private String WANBAO_CART_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<Cart> queryCartList() {
        try {
            User user = UserThreadLocal.get();
            String url = WANBAO_CART_URL + "/service/api/cart/" + user.getId();
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isNotEmpty(jsonData)) {
                return MAPPER.readValue(jsonData,
                        MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
