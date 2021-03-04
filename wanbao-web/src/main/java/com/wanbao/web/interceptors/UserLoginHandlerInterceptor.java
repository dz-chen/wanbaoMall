package com.wanbao.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wanbao.common.utils.CookieUtils;
import com.wanbao.web.service.UserService;
import com.wanbao.web.threadlocal.UserThreadLocal;
import com.wanbao.sso.query.bean.User;

/**
 * 拦截器：仅仅拦截订单相关的请求
 * @author cdz
 *
 */
public class UserLoginHandlerInterceptor implements HandlerInterceptor {

    public static final String COOKIE_NAME = "TT_TOKEN";

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        UserThreadLocal.set(null);              // 由于线程是在线程池中取得的,这样防止得到之前的数据...

        String loginUrl = this.userService.WANBAO_SSO_URL + "/user/login.html";
        String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
        if (StringUtils.isEmpty(token)) {
            // 未登录，跳转到登录页面
            response.sendRedirect(loginUrl);
            return false;
        }

        User user = this.userService.queryByToken(token);
        if (null == user) {
            // 登录超时，跳转到登录页面
            response.sendRedirect(loginUrl);
            return false;
        }

        // 登录成功
        UserThreadLocal.set(user); //将user对象放置到本地线程中，方便在Controller和Service中获取

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        UserThreadLocal.set(null); //清空本地线程中的User对象数据
    }

}
