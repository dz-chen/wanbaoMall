package com.wanbao.sso.query.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.service.RedisService;
import com.wanbao.sso.query.api.UserQueryService;
import com.wanbao.sso.query.bean.User;


@Service
public class UserQueryServiceImpl implements UserQueryService{

	@Autowired
	private RedisService redisService;
	
	private static final Integer REDIS_TIME=60*30;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	
	/**
	 * 在redis中,根据token查询用户信息
	 * @param token
	 * @return
	 */
	@Override
	public User queryUserByToken(String token) {
		
		String key="TOKEN_"+token;
		String jsonData=this.redisService.get(key);
		if(StringUtils.isEmpty(jsonData)) {       //登录超时,redis中已经没有用户数据
			return null;
		}
		
		//重新设置token的生存时间！！！ => 中级程序员必备!!!
		this.redisService.expire(key,REDIS_TIME);
		
		try {                                     //查到已登录用户信息,反序列化
			return MAPPER.readValue(jsonData, User.class);
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
