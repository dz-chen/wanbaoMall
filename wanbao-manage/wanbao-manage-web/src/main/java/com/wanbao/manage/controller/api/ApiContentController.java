package com.wanbao.manage.controller.api;

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
import com.wanbao.manage.service.ContentService;

@Controller
@RequestMapping("api/content")
public class ApiContentController {
	private static final Logger LOGGER=LoggerFactory.getLogger(ApiContentController.class);
	@Autowired
	private ContentService contentService;
	
	/**
	 * 根据类目id查找具体内容列表 => 对外提供的内容列表查询服务
	 * @param categoryId
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryListByCatgoryId(
			@RequestParam(value="categoryId")Long categoryId,
			@RequestParam(value="page",defaultValue="1") Integer page,
			@RequestParam(value="rows",defaultValue="30") Integer rows){
		
		try {
			EasyUIResult easyUIResult=this.contentService.queryListByCatgoryId(categoryId, page, rows);
			LOGGER.info("根据类目id查找具体广告内容=>成功, categoryId="+categoryId);
			return ResponseEntity.ok(easyUIResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("根据类目id查找具体广告内容=>服务器内部错误, categoryId="+categoryId);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	
}













