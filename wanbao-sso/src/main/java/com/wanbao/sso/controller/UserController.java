package com.wanbao.sso.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wanbao.common.utils.CookieUtils;
import com.wanbao.sso.pojo.User;
import com.wanbao.sso.service.UserService;

/**
 * 登录用户查询改用dubbo服务,
 * sso目前只提供登录、注册接口
 */
@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private static final String COOKIE_NAME="TT_TOKEN";
	
	private static final Logger LOGGER=LoggerFactory.getLogger(UserController.class);	
	
	/**
	 * 跳转到注册页面
	 * @return
	 */
	@RequestMapping(value="register",method=RequestMethod.GET)
	public String register() {
		return "register";
	}
	
	/**
	 * 跳转到登陆页面
	 * @return
	 */
	@RequestMapping(value="login",method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	/**
	 * 检测数据是否可用
	 * param是要检测的内容(张三)
	 * type是检测的类型(username)
	 * @param param
	 * @param type
	 * @return
	 */
	@RequestMapping(value="{param}/{type}",method=RequestMethod.GET)
	public ResponseEntity<Boolean> check(@PathVariable("param") String param,
			@PathVariable("type") Integer type){
		try {
			Boolean flag=this.userService.check(param,type);
			if(flag==null) {   //参数有误
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);	 // 400
			}
			// 为了配合前端校验逻辑,做出妥协取反
			return ResponseEntity.ok(!flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value="doRegister",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> doRegister(@Valid User user,BindingResult bindingResult){  //@Valid注解进行校验
		Map<String,Object> result=new HashMap<String,Object>();
		// @Valid User user,BindingResult bindingResult
		// => 在springMVC中就进行了数据校验(格式是否正确等...)

		//如果没有通过校验(在springMVC中进行的数据校验)
		if(bindingResult.hasErrors()) {
			result.put("status", 400);             //=> 这个status是业务状态码,而不是报文中的状态码
			//获取错误信息
			List<String> msgs=new ArrayList<String>();
			List<ObjectError> allErrors=bindingResult.getAllErrors();
			for(ObjectError objectError:allErrors) {
				String msg=objectError.getDefaultMessage();
				msgs.add(msg);
			}
			result.put("data", "参数有误"+StringUtils.join(msgs,"|"));
			return result;
		}
	
		try {
			Boolean success=this.userService.doRegister(user);
			if(success) {
				result.put("status", 200);
			}else {
				result.put("status", 500);
				result.put("data", "haha~~~");
			}
		} catch (Exception e) {
			result.put("status", 500);
			result.put("data", "haha~~~");
			//e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 登录
	 * @param user
	 * @return
	 */
	@RequestMapping(value="doLogin",method=RequestMethod.POST)  // 从 => /service/*进入
	@ResponseBody
	public Map<String,Object> doLogin(
			User user,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> result=new HashMap<String,Object>();
		try {

			// 1.检查是否已经登录(根据token取检查redis中是否有缓存,如果有缓存,则已经登录)
			if(this.userService.checkLogin(request)) {             //已经登录
				result.put("status",429);                          //业务状态码,不是响应状态码!!
				LOGGER.info("用户重复登录, username="+user.getUsername());
				return result;
			}

			// 2.如果尚未登录,进行登录(登录信息写入redis缓存,并返回token)
			String token=this.userService.doLogin(user.getUsername(), user.getPassword());
			//登陆失败
			if(StringUtils.isEmpty(token)) {
				result.put("status",500);
				return result;
			}
			
			//登陆成功,保存token到返回给前端的cookie中
			result.put("status",200);
			CookieUtils.setCookie(request, response,COOKIE_NAME , token);
			
		} catch (Exception e) {
			result.put("status",500);
			e.printStackTrace();
			
		}
		return result;
	}
	
	/**
	 * 根据token查询已登录用户信息(注意添加跨域支持)
	 * => 已弃用,改用dubbo,见wanbao-sso-query-service
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/query/{token}",method=RequestMethod.GET)
	public ResponseEntity<Object> queryUserByToken(@PathVariable("token") String token,
			@RequestParam(value="callback",required=false) String callback){
//		try {
//			User user=this.userService.queryUserByToken(token);
//			if(user==null) {      //资源不存在(登录过期)
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//			}
//			String jsonData=UserService.MAPPER.writeValueAsString(user);
//			if(StringUtils.isEmpty(callback)) {          //没有跨域,直接返回对象(springMVC会处理对象,变成json字符串给前端)
//				return ResponseEntity.ok((Object)user);
//			}
//			else {
//				return ResponseEntity.ok((Object)(callback+"("+jsonData+")"));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

		User user=this.userService.queryUserByToken(token);
		LOGGER.info("sso.wanbao.com不再提供查询用户信息服务!");
		
		if(StringUtils.isEmpty(callback)) {          //没有跨域,直接返回对象(springMVC会处理对象,变成json字符串给前端)
			return ResponseEntity.ok((Object)user);
		}
		else {
			String jsonData=null;
			try {
				jsonData = UserService.MAPPER.writeValueAsString(user);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return ResponseEntity.ok((Object)(callback+"("+jsonData+")"));
		}
	}
	
}












