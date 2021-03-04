package com.wanbao.cart.service;

import java.util.Date;
import java.util.List;

import com.wanbao.cart.bean.Item;
import com.wanbao.cart.threadlocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.wanbao.cart.mapper.CartMapper;
import com.wanbao.cart.pojo.Cart;
import com.wanbao.sso.query.bean.User;

@Service
public class CartService {
	
	@Autowired
	CartMapper cartMapper;
	
	@Autowired
	ItemService itemService;
	/**
	 * 添加商品到购物车
	 * 逻辑:判断加入的商品在原有的购物车中是否存在
	 *  => 如果存在则数量相加; 否则直接写入
	 * @param itemId
	 */
	public void addItemToCart(Long itemId) {
		User user= UserThreadLocal.get();
		Cart record=new Cart();
		record.setItemId(itemId);
		record.setUserId(user.getId());
		Cart cart=this.cartMapper.selectOne(record);
		if(cart==null) {      //购物车中没有该商品数据,写入数据库
			cart=new Cart();
			cart.setUserId(user.getId());
			cart.setCreated(new Date());
			cart.setUpdated(cart.getCreated());
			//购物车中商品的基本数据需要通过后台系统查询
			Item item=this.itemService.quryById(itemId);
			cart.setItemId(itemId);
			cart.setItemTitle(item.getTitle());
			cart.setItemPrice(item.getPrice());
			cart.setItemImage(StringUtils.split(item.getImage(),',')[0]);
			cart.setNum(1);   //TODO  => 应该改为前台的数量
			 // 保存到数据库
            this.cartMapper.insert(cart);
		}
		else {                 //购物车中有数据,添加商品数量
			//TODO 暂时默认加1, 实际上需要获取	前台的商品数量
			cart.setNum(cart.getNum()+1);
			cart.setUpdated(new Date());
			this.cartMapper.updateByPrimaryKey(cart);
		}
	}
	
	
	/**
	 * 查询购物车列表
	 * @return
	 */
	public List<Cart> queryCartList() {
		Example example=new Example(Cart.class);
		//设置排序条件
		example.setOrderByClause("created DESC");
		//设置查询条件
		example.createCriteria().andEqualTo("userId", UserThreadLocal.get().getId());
		return this.cartMapper.selectByExample(example);
	}
	
	/**
	 * 根据用户id查询购物车列表
	 * @param itemId
	 * @return
	 */
	public List<Cart> queryCartList(Long itemId){
		Example example=new Example(Cart.class);
		//设置排序条件
		example.setOrderByClause("created DESC");
		//设置查询条件
		example.createCriteria().andEqualTo("userId", itemId);
		return this.cartMapper.selectByExample(example);
	}
	
	
	
	/**
	 * 更新购物车中商品的数量
	 * @param itemId
	 * @param num
	 */
	public void updateNum(Long itemId, Integer num) {
		//更新的数据
		Cart record=new Cart();
		record.setNum(num);
		record.setUpdated(new Date());
		
		//更新的条件
		Example example=new Example(Cart.class);
		example.createCriteria().andEqualTo("itemId", itemId)
				.andEqualTo("userId", UserThreadLocal.get().getId());

		this.cartMapper.updateByExampleSelective(record, example);
	}

	
	
	/**
	 * 删除购物车中的某个商品
	 * @param itemId
	 */
	public void delete(Long itemId) {
		Cart record=new Cart();
		record.setItemId(itemId);
		record.setUserId(UserThreadLocal.get().getId());
		this.cartMapper.delete(record);
	}
}











