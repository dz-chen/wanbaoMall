package com.wanbao.sso.query.controller;

import com.wanbao.sso.query.api.UserQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.sso.query.api.UserQueryService;
import com.wanbao.sso.query.bean.User;

@RequestMapping("user")
@Controller
public class UserController {

    @Autowired
    private UserQueryService userQueryService;

    private static final ObjectMapper MAPPER=new ObjectMapper();
    
	/**
	 * 根据token查询已登录用户信息(注意添加跨域支持)  
	 * 从原来的单点登录系统中改造而来
	 * @param token
	 * @return
	 */
	@RequestMapping(value="{token}",method=RequestMethod.GET)
	public ResponseEntity<Object> queryUserByToken(@PathVariable("token") String token,
			@RequestParam(value="callback",required=false) String callback){
		try {
			User user=this.userQueryService.queryUserByToken(token);
			if(user==null) {      //资源不存在(登录过期)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			String jsonData=this.MAPPER.writeValueAsString(user);
			if(StringUtils.isEmpty(callback)) {          //没有跨域,直接返回对象(springMVC会处理对象,变成json字符串给前端)
				return ResponseEntity.ok((Object)user);
			}
			else {
				return ResponseEntity.ok((Object)(callback+"("+jsonData+")"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

}
