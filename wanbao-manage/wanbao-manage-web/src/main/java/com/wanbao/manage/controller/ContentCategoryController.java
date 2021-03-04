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

import com.wanbao.manage.pojo.ContentCategory;
import com.wanbao.manage.service.ContentCategoryService;

@Controller
@RequestMapping("content/category")
public class ContentCategoryController {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ContentCategoryController.class);
	
	@Autowired
	private ContentCategoryService contentCategoryService;
	/**
	 * 根据父节点id查询内容分类列表
	 * @param pid
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ContentCategory>> querylistByPatrentId(
			@RequestParam(value="id",defaultValue="0") Long pid){
		try {
			ContentCategory record=new ContentCategory();
			record.setParentId(pid);
			List<ContentCategory> list=this.contentCategoryService.queryListByWhere(record);
			if(list==null || list.isEmpty()) {
				LOGGER.info("查询内容分类列表 => 没有找到!,parentId="+pid);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			LOGGER.info("查询内容分类列表 => 成功,parentId="+pid);
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			LOGGER.info("查询内容分类列表 => 发生异常,parentId="+pid);
			//e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	/**
	 * 新增节点
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<ContentCategory> saveContentCatogery(ContentCategory contentCategory){
		try {
			//注意看service中的代码,既要保存新增内容，又要修改父节点 => 写到一个事务中
			this.contentCategoryService.saveContentCatogery(contentCategory);
			return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);	
	}
	
	/**
	 * 重命名内容节点
	 * @param id
	 * @param name
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT)    //PUT、DELETE注意web.xml配置文件中需要添加过滤器
	public ResponseEntity<Void> rename(@RequestParam(value="id") Long id, @RequestParam(value="name") String name){
		try {
			//前端传递的数据时json对象,后端不使用对象接受,将两个数据分开接收 => 更安全,防止对象被修改,传入更多的数据
			ContentCategory category=new ContentCategory();
			category.setId(id);
			category.setName(name);
			this.contentCategoryService.updateSelective(category);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 删除节点,包含它的所有子节点,同时可能修改其父节点的isParent属性
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping(method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(ContentCategory contentCategory) {
		try {
			this.contentCategoryService.deleteAll(contentCategory);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		
		
	}
	

}






















