package com.wanbao.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wanbao.common.bean.EasyUIResult;
import com.wanbao.manage.pojo.Item;
import com.wanbao.manage.service.ItemService;

@Controller
@RequestMapping("item")    //请求的路径同样是/rest/item,但是不同的请求类型GET、POST、PUT得到不同的响应 => restful
public class ItemController {
	//获取日志对象
	private static final Logger LOGGER=LoggerFactory.getLogger(ItemController.class);
	
	
	@Autowired
	private ItemService itemService;
	
//	@Autowired
//	private ItemDescService itemDescService;
	
	/**
	 * 新增商品
	 * @param item
	 * @param desc
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> sveItem(Item item, @RequestParam("desc") String desc,
			@RequestParam("itemParams") String itemParams){     
		try {
			
			/* 下面代码存在的问题 => 对同一个商品的保存两个service完成,不是在同一个事务中!! */
			
//			//商品表保存  初始化上传表单时没有的字段
//			item.setStatus(1);
//			item.setId(null);    //出于安全考虑,强制设置id为null,通过数据库自增获得
//			this.itemService.save(item);
//			
//			//商品描述保存
//			ItemDesc itemDesc=new ItemDesc();
//			itemDesc.setItemId(item.getId());
//			itemDesc.setItemDesc(desc);
//			this.itemDescService.save(itemDesc);
			
			/*比较坑,每个这类判断都是返回false*/
//			if(LOGGER.isDebugEnabled()) {
//				LOGGER.debug("新增商品,item={},desc={}",item,desc);
//			}
			
		
			LOGGER.debug("新增商品,item={},desc={}",item,desc);

			// 用一个service完成保存,从而是一个事务!
			Boolean success=this.itemService.saveItem(item,desc,itemParams);
			if(!success) {
				LOGGER.info("新增商品失败 item={}",item);
				//保存失败
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();	
			}
			LOGGER.info("新增商品成功,itemId={}",item.getId());
			
			return ResponseEntity.status(HttpStatus.CREATED).build();
			
		} catch (Exception e) {
			LOGGER.error("新增商品失败,item="+item+"desc="+desc,e);
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	
	/**
	 * 查询商品列表  :/rest/item
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryItemList(
			@RequestParam(value="page",defaultValue="1")Integer page,
			@RequestParam(value="rows",defaultValue="30") Integer rows){
		
		//EasyUIResult easyUIResult;
		try {
			EasyUIResult easyUIResult = itemService.queryItemList(page,rows);
			return ResponseEntity.ok(easyUIResult);
		} catch (Exception e) {
			LOGGER.error("查询商品失败,page="+page+"rows="+rows,e);
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 商品更新/编辑
	 * @param item
	 * @param desc
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Void> updateItem(Item item, @RequestParam("desc") String desc,
			@RequestParam("itemParams") String itemParams){     
		try {
			
			LOGGER.info("编辑商品,item={},desc={}",item,desc);
			//保存商品
			Boolean success=this.itemService.updateItem(item,desc,itemParams);
			if(!success) {
				LOGGER.info("编辑商品失败 item={}",item);
				//保存失败
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();	
			}
			LOGGER.info("编辑商品成功,itemId={}",item.getId());
			
			//204
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			
		} catch (Exception e) {
			LOGGER.error("编辑商品失败,item="+item+"desc="+desc,e);
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	
	
	
}



















