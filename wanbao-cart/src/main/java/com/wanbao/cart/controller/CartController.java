package com.wanbao.cart.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanbao.cart.threadlocal.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wanbao.cart.pojo.Cart;
import com.wanbao.cart.service.CartCookieService;
import com.wanbao.cart.service.CartService;
import com.wanbao.sso.query.bean.User;

@RequestMapping("cart")
@Controller
public class CartController {
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private CartCookieService cartCookieService;


	/**
	 * 查询购物车列表
	 * 返回购物车列表页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="list",method=RequestMethod.GET)
	public ModelAndView cartList(HttpServletRequest request) {
		ModelAndView mv=new ModelAndView("cart");
		User user= UserThreadLocal.get();
		List<Cart> cartList=null;
		if(user==null) {           //未登录 => 将传过来的cookie中的购物车信息放入页面
			try {
				cartList=this.cartCookieService.queryCartList(request);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {                     //已登录,查询tb_cart中的购物车信息放入页面
			cartList=this.cartService.queryCartList();
		}
		mv.addObject("cartList",cartList);
		return mv;
	}
	
	
	/**
	 * 加入商品到购物车,然后跳转到购物车页面
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public String addItemToCart(@PathVariable("itemId") Long itemId,
			HttpServletRequest request, HttpServletResponse response) {
		// 注:cart的登陆拦截器中不管登陆与否,最终都放行=> 都能进入这里...

		User user=UserThreadLocal.get();
		if(user==null) {      //未登录或者登陆已过期 => 仍要将商品数据保存到cookie返回给前端
			//request、response用于操作cookie
			try {
				this.cartCookieService.addItemToCart(itemId,request,response);
				System.out.println("用户未登录......");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		else                  //已登录,购物车信息写入数据库
		{          
			this.cartService.addItemToCart(itemId);
		}
		
		//重定向到购物车列表页面 (redirect:代表重定向)
		return "redirect:/cart/list.html";
	}
	
	
	/**
	 * 更新购物车中商品数量
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping(value="update/num/{itemId}/{num}",method=RequestMethod.POST)
	public ResponseEntity<Void> updateNum(
			@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num,
			HttpServletRequest request, HttpServletResponse response){
		User user=UserThreadLocal.get();
		if(null==user) {            //未登录,直接在cookie中更新
			try {
				this.cartCookieService.updateNum(itemId,num,request,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {                      //已登录,更新数据库
			this.cartService.updateNum(itemId,num);
		}
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	
	/**
	 * 删除购物车中的商品
	 * @param itemId
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping(value="delete/{itemId}",method=RequestMethod.GET)
	public String delete(@PathVariable("itemId") Long itemId,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		User user=UserThreadLocal.get();
		if(user==null) {              //未登录
			this.cartCookieService.delete(itemId,request,response);
		}
		else {                       //已登录
			this.cartService.delete(itemId);
		}
		
		// 重定向到购物车页面
		return "redirect:/cart/list.html";
	}

}






