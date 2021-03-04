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
import org.springframework.web.bind.annotation.RequestParam;

import com.wanbao.common.bean.EasyUIResult;
import com.wanbao.manage.pojo.ItemParam;
import com.wanbao.manage.service.ItemParamService;

@Controller
@RequestMapping("item/param")
public class ItemParamController {
	private static final Logger LOGGER=LoggerFactory.getLogger(ItemParamController.class);
	
	@Autowired
	private ItemParamService itemParamService;
	
	/**
	 * 根据类目编号返回 => 该类目对应的规格参数模板
	 * @return
	 */
	@RequestMapping(value="{itemCatId}",method=RequestMethod.GET)  //{itemCat}代表url中最后一个是传入的参数!!!                                                                                         
	public ResponseEntity<ItemParam> queryByItemCatId(
			@PathVariable("itemCatId") Long itemCatId){
		try {
			//根据模板条件查询(不能根据id查询,因为此表的id不是item_cat_id !!!)
			ItemParam record=new ItemParam();
			record.setItemCatId(itemCatId);
			ItemParam itemParam=this.itemParamService.queryOne(record);  //查询与record中数据搭配的记录!
			if(itemParam==null) {
				//没找到,404
				LOGGER.info("没有找到类目参数模板,itemCatId="+itemCatId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			LOGGER.info("找到类目参数模板,itemCatId="+itemCatId);
			return ResponseEntity.ok(itemParam);
		} catch (Exception e) {
			LOGGER.error("查找类目参数模板出错,itemCatId="+itemCatId+",e="+e);
			//e.printStackTrace();
		}
		
		LOGGER.error("查找类目参数模板,发生服务器内部错误,itemCatId="+itemCatId);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 新增规格参数模板
	 * @param itemCatId
	 * @param paramData
	 * @return
	 */
	@RequestMapping(value="{itemCatId}",method=RequestMethod.POST)
	public ResponseEntity<Void> saveParamItem(
			@PathVariable("itemCatId") Long itemCatId,
			@RequestParam("paramData") String paramData){
		try {
			ItemParam itemParam=new ItemParam();
			itemParam.setId(null);
			itemParam.setItemCatId(itemCatId);
			itemParam.setParamData(paramData);
			this.itemParamService.save(itemParam);
			LOGGER.info("保存规格参数模板成功,itemCatId="+itemCatId+",itemParam="+itemParam);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			LOGGER.info("保存规格参数模板失败,itemCatId="+itemCatId+",e="+e);
			//e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		
	}
	
	/**
	 * 查询产品模板规格参数模板列表
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryItemParamList(
			@RequestParam(value="page",defaultValue="1")Integer page,
			@RequestParam(value="rows",defaultValue="30") Integer rows){
		
		EasyUIResult easyUIResult;
		try {
			easyUIResult = itemParamService.queryItemParamList(page,rows);
			LOGGER.info("查询规格参数成功,page="+page+",rows="+rows);
			return ResponseEntity.ok(easyUIResult);
		} catch (Exception e) {
			LOGGER.error("查询规格参数失败,page="+page+"rows="+rows,e);
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
	}
	
		
	
}
