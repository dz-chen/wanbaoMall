package com.wanbao.manage.service;

import java.util.List;

import com.wanbao.manage.mapper.ItemParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wanbao.common.bean.EasyUIResult;
import com.wanbao.manage.pojo.ItemParam;

@Service
public class ItemParamService extends BaseService<ItemParam>{
	@Autowired
	ItemParamMapper itemParamMapper;
	// 查询规格参数模板列表
	public EasyUIResult queryItemParamList(Integer page, Integer rows) {
		//设置分页参数
		PageHelper.startPage(page,rows);
		
		Example example=new Example(ItemParam.class);
		//按照创建时间排序
		example.setOrderByClause("created DESC");
		List<ItemParam> items=this.itemParamMapper.selectByExample(example);
		PageInfo<ItemParam> pageInfo=new PageInfo<ItemParam>(items);
		return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
	}
}
