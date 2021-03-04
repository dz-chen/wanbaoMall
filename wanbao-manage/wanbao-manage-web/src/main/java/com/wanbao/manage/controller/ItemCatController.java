package com.wanbao.manage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wanbao.manage.pojo.ItemCat;
import com.wanbao.manage.service.ItemCatService;

@Controller
@RequestMapping("item/cat")
public class ItemCatController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemCatController.class);
	@Autowired
	private ItemCatService itemCatService;
	
	/**
	 * 查询商品类目列表
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ItemCat>> queryItemCatListByParentId(
			@RequestParam(value="id",defaultValue="0") Long pid){
		try {
			
			// => 继承了BaseService后,使用BaseService中通用的方法
			ItemCat record=new ItemCat();
			record.setParentId(pid);
			List<ItemCat> list=this.itemCatService.queryListByWhere(record);
			
			
			//资源没找到 => 返回404
			if(list==null || list.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(list);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	
	/**
	 * 根据商品的类目id(最终叶子节点id)返回该类目对应的名称
	 * @param id
	 * @return
	 */     //produces防止返回的中文出现乱码
	@RequestMapping(value="/getCatName",method=RequestMethod.GET,produces = {"text/html;charset=UTF-8"} )
	public ResponseEntity<String> queryCatNameByCatId(
			@RequestParam("cid") Long id){
		try {
			// 查询了多余的数据,有点冗余
			ItemCat itemCat=itemCatService.queryById(id);
			
			//没找到,404
			if(itemCat==null) {
				LOGGER.info("没有找到对应编号的类别名称,id="+id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			String catName=itemCat.getName();
			LOGGER.info("查找类别名称成功,id="+id+",name="+catName);
			return ResponseEntity.ok(catName);
		} catch (Exception e) {
			LOGGER.error("根据类别编号查找类别名称发生错误,id="+id+",e="+e);
			//e.printStackTrace();
		}
		
		//发生错误 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
