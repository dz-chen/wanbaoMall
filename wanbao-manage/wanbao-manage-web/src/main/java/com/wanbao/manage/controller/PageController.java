package com.wanbao.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 通用的页面跳转逻辑
 * @author cdz
 * 比如:/rest/page/login => 跳转到login.jsp页面
 * 	   /rest/page/login => 跳转到index.jsp页面
 *
 */

@Controller
@RequestMapping("page")
public class PageController {
	
	@RequestMapping(value="{pageName}",method=RequestMethod.GET)
	public String toPage(@PathVariable("pageName") String pageName) {
		return pageName;
	}
}
