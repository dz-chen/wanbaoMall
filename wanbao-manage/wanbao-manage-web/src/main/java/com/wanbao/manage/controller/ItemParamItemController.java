package com.wanbao.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wanbao.manage.pojo.ItemParamItem;
import com.wanbao.manage.service.ItemParamItemService;

@Controller
@RequestMapping("item/param/item")
public class ItemParamItemController {
	private static final Logger LOGGER=LoggerFactory.getLogger(ItemParamItemController.class);
	@Autowired
	private ItemParamItemService itemParamItemService;
	
	/**
	 * 根据商品id => 返回该商品的规格参数
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ResponseEntity<ItemParamItem> queryByItemId(
			@PathVariable("itemId") Long itemId){
		try {
			ItemParamItem record=new ItemParamItem();
			record.setItemId(itemId);
			ItemParamItem itemParamItem=this.itemParamItemService.queryOne(record);
			if(itemParamItem==null) {
				LOGGER.info("查询商品规格参数=> 没有找到. itemId="+itemId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			LOGGER.info("查询商品规格参数=> 成功. itemId="+itemId);
			return ResponseEntity.ok(itemParamItem);
		} catch (Exception e) {
			LOGGER.error("查询商品规格参数=> 失败. itemId="+itemId+",e="+e);
			//e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
}
