package com.wanbao.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wanbao.manage.pojo.ItemDesc;
import com.wanbao.manage.service.ItemDescService;

@Controller
@RequestMapping("/item/desc")
public class ItemDescController {
	@Autowired
	ItemDescService itemDescService;
	
	/**
	 * 根据商品id查询商品描述数据
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}", method=RequestMethod.GET)
	public ResponseEntity<ItemDesc> queryByItemId(@PathVariable("itemId") Long itemId){
		try {
			ItemDesc itemDesc=this.itemDescService.queryById(itemId);
			if(itemDesc==null) {		
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(itemDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	
	
	
}
