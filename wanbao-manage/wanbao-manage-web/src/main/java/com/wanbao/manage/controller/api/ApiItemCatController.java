package com.wanbao.manage.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.bean.ItemCatResult;
import com.wanbao.manage.service.ItemCatService;

@Controller
@RequestMapping("api/item/cat")
public class ApiItemCatController {
	private static final Logger LOGGER=LoggerFactory.getLogger(ApiItemCatController.class);
	@Autowired
	private ItemCatService itemCatService;
		
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	/**
	 * 对外提供接口服务,查询所有商品类目数据 => 返回类目树!!!
	 * 主界面左侧的商品类目栏会使用此树
	 * 支持jsonp的写法更高级见 => day04 视频24
	 * 参数callback => 目的是解决跨域问题!
	 * 注:由于这部分数据很少更改,应该使用redis缓存,queryAllToTree现在缓存中找,没找到再到关系数据库中找
	 */
	@RequestMapping(method=RequestMethod.GET,produces="text/plain; charset=UTF-8")   // produces解决乱码问题
	public ResponseEntity<String> queryItemCatList(
			@RequestParam(value="callback",required=false) String callback){
		try {
			ItemCatResult itemCatResult=this.itemCatService.queryAllToTree();
			if(itemCatResult==null) {
				LOGGER.info("查询类目树 => 没有找到");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			String json=MAPPER.writeValueAsString(itemCatResult);   //对象转为json数据
			if(StringUtils.isEmpty(callback)) {		// 如果没有回调
				return ResponseEntity.ok(json);
			}
			
			LOGGER.info("查询类目树 => 成功！");
			return ResponseEntity.ok(callback+"("+json+");");      //添加跨域支持
		} catch (Exception e) {
			LOGGER.info("查询类目树 => 发生错误!!!");
			//e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
