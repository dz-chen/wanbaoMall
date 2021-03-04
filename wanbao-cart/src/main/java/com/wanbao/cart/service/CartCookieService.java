package com.wanbao.cart.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanbao.cart.bean.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.cart.pojo.Cart;
import com.wanbao.common.utils.CookieUtils;

@Service
public class CartCookieService {
	@Autowired
	private ItemService itemService;
	
	private static final String COOKIE_NAME="TT_CART";
	
	private static final Integer COOKIE_TIME=60*60*24*30*12;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	/**
	 * 添加商品到购物车 
	 * 逻辑：判断加入的商品在原有购物车中是否存在,如果存在则数量相加,如果不存在则写入
	 * 只是此处是从cookie中读写信息, cookie会返回给客户端
	 * @param itemId
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
		// 读取cookie中的购物车信息
		String jsonData=CookieUtils.getCookieValue(request, COOKIE_NAME,true);
		List<Cart> carts=null;
		if(StringUtils.isEmpty(jsonData)) {            //cookie中没有购物车信息
			carts=new ArrayList<Cart>();
		}
		else {                                         //cookie中有购物车信息
			//反序列化 => 成List类型
			carts=MAPPER.readValue(jsonData,
					MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
		}
		
		// 添加商品信息到cookie中  或者修改cookie  
		Cart cart=null;
		for(Cart c:carts) {
			if(c.getItemId().longValue()==itemId.longValue()) {  //要添加的商品已经在购物车中
				cart=c; break;
			}
		}
		
		if(cart==null) {     //购物车中不存在要添加的商品/cookie中无鼓购物车信息 => 直接写入cookie
			cart=new Cart();
			cart.setCreated(new Date());
			cart.setUpdated(cart.getCreated());
			//购物车中商品的基本数据需要通过后台系统查询
			Item item=this.itemService.quryById(itemId);
			cart.setItemId(itemId);
			cart.setItemTitle(item.getTitle());
			cart.setItemPrice(item.getPrice());
			cart.setItemImage(StringUtils.split(item.getImage(),",")[0]);
			cart.setNum(1);   //TODO  => 应该改为前台的数量
			//加入购物车列表中
			carts.add(cart);
		}
		else {                          //购物车中存在要添加的商品 => 数量+1
			cart.setNum(cart.getNum()+1);
			cart.setUpdated(new Date());
		}
		
		//将购物车列表数据写入cookie
		CookieUtils.setCookie(request, response, COOKIE_NAME,
				MAPPER.writeValueAsString(carts),COOKIE_TIME,true);
	}

	
	/**
	 * 从cookie中查询购物车列表
	 * @param request
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public List<Cart> queryCartList(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		String jsonData=CookieUtils.getCookieValue(request, COOKIE_NAME,true);
		List<Cart> carts=null;
		if(StringUtils.isEmpty(jsonData)) {
			carts=new ArrayList<Cart>();
		}
		else {
			// 反序列化成数组
			carts=MAPPER.readValue(jsonData,
					MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
		}
		return carts;
	}

	
	/**
	 * 在cookie中更新商品数量
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void updateNum(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Cart> carts=this.queryCartList(request);
		Cart cart=null;
		for(Cart c:carts) {
			if(c.getItemId().longValue()==itemId.longValue()) {
				cart=c; break;
			}
		}
		if(cart!=null) {
			cart.setNum(num);
			cart.setUpdated(new Date());
		}
		else {
			//参数非法
			return;
		}
		
		//将购物车列表数据写入cookie
		CookieUtils.setCookie(request, response, COOKIE_NAME,
				MAPPER.writeValueAsString(carts),COOKIE_TIME,true);
	}

	
	
	/***
	 * 删除cookie中的商品数据
	 * @param itemId
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void delete(Long itemId, HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
		List<Cart> carts=this.queryCartList(request);
		Cart cart=null;
		for(Cart c:carts) {
			if(c.getItemId().longValue()==itemId.longValue()) {
				cart=c;
				carts.remove(c); break;
			}
		}
		if(cart==null) {       //参数非法
			return;
		}
		
		//将购物车列表数据写入cookie
		CookieUtils.setCookie(request, response, COOKIE_NAME,
				MAPPER.writeValueAsString(carts),COOKIE_TIME,true);
	}
	
	
}











