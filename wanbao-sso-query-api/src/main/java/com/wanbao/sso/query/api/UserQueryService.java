package com.wanbao.sso.query.api;

import com.wanbao.sso.query.bean.User;

/**
 * 服务调用的接口
 * 服务调用者使用这个接口获取结构
 * 服务提供者需要实现这个接口
 */
public interface UserQueryService {

	/**
	 * 根据token查询User对象
	 * @param token
	 * @return
	 */
	public User queryUserByToken(String token);
}
