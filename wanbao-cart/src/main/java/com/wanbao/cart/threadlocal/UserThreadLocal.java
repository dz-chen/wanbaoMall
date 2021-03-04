package com.wanbao.cart.threadlocal;

import com.wanbao.sso.query.bean.User;


public class UserThreadLocal {
	
	private static final ThreadLocal<User> LOCAL=new ThreadLocal<User>();
	
	//将构造方法私有化,从而避免在其他类中被实例化
	private UserThreadLocal() {
		
	}
	
	public static void set(User user) {
		LOCAL.set(user);
	}
	
	public static User get() {
		return LOCAL.get();
	}
}
