package com.wanbao.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanbao.web.interceptors.UserLoginHandlerInterceptor;
import com.wanbao.web.service.UserService;
import com.wanbao.web.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.wanbao.web.bean.Cart;
import com.wanbao.web.bean.Item;
import com.wanbao.web.bean.Order;
import com.wanbao.web.service.CartService;
import com.wanbao.web.service.ItemService;
import com.wanbao.web.service.OrderService;
import com.wanbao.sso.query.bean.User;

@RequestMapping("order")
@Controller
public class OrderController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    /**
     * 订单确认页
     * 
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
        ModelAndView mv = new ModelAndView("order");
        Item item = this.itemService.queryById(itemId);
        mv.addObject("item", item);
        return mv;
    }

    /**
     * 基于购物车下单（购物车中全部商品下单）
     * 
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView toCartOrder() {
        ModelAndView mv = new ModelAndView("order-cart");
        List<Cart> carts = this.cartService.queryCartList();
        mv.addObject("carts", carts);
        return mv;
    }

    /**
     * 提交订单
     * 进入到这个方法,说明用户已经登录(通过了springMVC拦截器UserLoginHandlerInterceptor)
     * @param order
     * @return
     */
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submit(Order order
                        /*,@CookieValue(UserLoginHandlerInterceptor.COOKIE_NAME) String token*/) {
        Map<String, Object> result = new HashMap<String, Object>();

        // 根据token向SSO系统查询该登录用户的信息,然后填写信息到订单中
//        User user=this.userService.queryByToken(token);
//        order.setUserId(user.getId());
//        order.setBuyerNick(user.getUsername());

        // 由于拦截器中已经查询了SSO,这里再次查询没有必要
        // => 在拦截器中将SSO结果放ThreadLocal中,这里直接在ThreadLocal查询即可
        User user = UserThreadLocal.get();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());

        // 向order系统提交订单
        String orderId = this.orderService.submit(order);
        if (StringUtils.isEmpty(orderId)) {
            // 订单提交失败
            result.put("status", 500);
        } else {
            // 订单提交成功
            result.put("status", 200);
            result.put("data", orderId);
        }
        return result;
    }

    /**
     * 跳转到订单提交成功页面
     * @param orderId
     * @return
     */
    @RequestMapping(value = "success", method = RequestMethod.GET)
    public ModelAndView success(@RequestParam("id") String orderId) {
        ModelAndView mv = new ModelAndView("success");
        // 订单数据
        Order order = this.orderService.queryByOrderId(orderId);
        mv.addObject("order", order);
        // 送货时间，当前时间向后推2天,格式：08月18日
        mv.addObject("date", new DateTime().plusDays(2).toString("MM月dd日"));
        return mv;
    }

}
