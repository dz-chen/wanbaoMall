package com.wanbao.sso.service;


import java.io.IOException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.service.RedisService;
import com.wanbao.sso.mapper.UserMapper;
import com.wanbao.sso.pojo.User;

@Service
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RedisService redisService;
	
	@Value("${TOKEN_COOKIE_NAME}")
	private String TOKEN_COOKIE_NAME;
	
	@Value("${TOKEN_REIDS_PREFIX}")
	private String TOKEN_REIDS_PREFIX;
	
	public static final ObjectMapper MAPPER=new ObjectMapper();
	
	private static final Integer REDIS_TIME=60*30;
	
	//检查相关数据是否已经存在
	public Boolean check(String param, Integer type) {
		User record=new User();
		switch(type) {
		case 1:
			record.setUsername(param);  // 用户名
			break;
		case 2:
			record.setPhone(param);    // 电话
			break;
		case 3:
			record.setEmail(param);    //邮箱
			break;
		default:
			return null;    //参数有误
		}
		return this.userMapper.selectOne(record)==null;
	}

	/**
	 * 注册
	 * @param user
	 * @return
	 */
	public Boolean doRegister(User user) {
		//初始化处理
		user.setId(null);
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		//加密处理,MD5加密 => 不过密文可以被暴力破解
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		return this.userMapper.insert(user)==1;
	}

	
	/**
	 * 登录前检查该用户是否已经登录 => 根据token查询redis
	 */
	public Boolean checkLogin(HttpServletRequest request) {
		Cookie[] cookies=request.getCookies();
		String token="";
		for(Cookie cookie:cookies) {
			if(cookie.getName().equals(this.TOKEN_COOKIE_NAME)) {
				token=cookie.getValue();
				break;
			}
		}
		String key=this.TOKEN_REIDS_PREFIX+"_"+token;
		String result=this.redisService.get(key);
		if(result==null || result.equals("")) return false;    //未登录
		return true;                                           //已登录
	}
	
	/**
	 * 登陆,登录成功后将信息缓存到redis中,然后生成并返回token
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public String doLogin(String username, String password) throws Exception {
		//方法1：同时查询用户名也密码,能找到则可以登录
		//方法2：根据用户名查找密码，比较返回的密码与用处输入的密码
		// => 推荐方法2,只有一个查询条件,速度更快
		
		User record=new User();
		record.setUsername(username);
		User user=this.userMapper.selectOne(record);
		//用户不存在
		if(user==null) {
			return null;
		}
		
		//密码错误
		if(!StringUtils.equals(DigestUtils.md5Hex(password), user.getPassword())) {
			return null;
		}
		
		//登录成功,将用户信息保存到redis中(不能使用session)
		//序列化时没有写入password字段(见pojo中JsonIgnore注解)
		String token=DigestUtils.md5Hex(username+System.currentTimeMillis());  //生成token
		this.redisService.set("TOKEN_"+token, MAPPER.writeValueAsString(user), REDIS_TIME);
		return token;
	}

	/**
	 * 在redis中,根据token查询用户信息
	 * @param token
	 * @return
	 */
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
