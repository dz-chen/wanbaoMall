package com.wanbao.manage.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wanbao.manage.pojo.Item;
import com.wanbao.manage.service.ItemService;

@Controller
@RequestMapping("api/item")
public class ApiItemController {
	private static final Logger LOGGER=LoggerFactory.getLogger(ApiItemController.class);
	@Autowired
	private ItemService itemService;
	
	/**
	 * 根据商品id查询商品数据
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="{itemId}",method=RequestMethod.GET)
	public ResponseEntity<Item> queryByid(@PathVariable("itemId") Long itemId){
		try {
			Item item=this.itemService.queryById(itemId);
			if(null==item) {
				LOGGER.info("前台向后台查询商品信息 =>结果为空,itemId="+itemId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			LOGGER.info("前台向后台查询商品信息 =>成功,itemId="+itemId);
			return ResponseEntity.ok(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("前台向后台查询商品信息 =>服务器内部错误,itemId="+itemId);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
