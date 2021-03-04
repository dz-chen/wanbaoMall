package com.wanbao.cart.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanbao.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wanbao.cart.service.UserService;
import com.wanbao.cart.threadlocal.UserThreadLocal;
import com.wanbao.common.utils.CookieUtils;
import com.wanbao.sso.query.bean.User;

/**
 * 用户登录拦截器
 * @author cdz
 *
 */
public class UserLoginHandlerInterceptor implements HandlerInterceptor{

	public static final String COOKIE_NAME="TT_TOKEN";
	
	@Autowired
	private UserService userService;
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
		if(StringUtils.isEmpty(token)) {  //未登录,不做任何处理
			return true;
		}
		
		User user=this.userService.queryByToken(token);
		if(user==null) {    			//登录已过期,仍然不做处理
			return true;
		}
		
		//已经登录
		//将user放置到本地线程中,方便Controller和Service中获取
		UserThreadLocal.set(user);
		return true;
	}
	
	

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//(因为tomcat使用的线程池,线程不会被销毁,不清空可能拿到错误数据) 
		//虽然此处不存在这种情况,但这是一种安全意识的体现
		UserThreadLocal.set(null);   //清空本地线程池中的数据
	}

}
