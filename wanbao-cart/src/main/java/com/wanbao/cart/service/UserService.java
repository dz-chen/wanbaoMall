package com.wanbao.cart.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wanbao.sso.query.api.UserQueryService;
import com.wanbao.sso.query.bean.User;

/**
 * 注意,此处查询数据不应该直接在redis中查询(虽然也能可以)
 *  => 而应该通过单点登录提供的接口查询
 *  因为在现实开发中redis不只一个,不同团队负责的redis不同(而这里练习的时候所有项目使用同一个redis)
 * @author cdz
 *
 */

@Service
public class UserService {
		
	private UserQueryService userQueryService;
//
//	/*
//	 * 根据token查询用户
//	 * 使用RPC方式/dubbo
//	 */
	public User queryByToken(String token) {
		return this.userQueryService.queryUserByToken(token);
	}

}


















