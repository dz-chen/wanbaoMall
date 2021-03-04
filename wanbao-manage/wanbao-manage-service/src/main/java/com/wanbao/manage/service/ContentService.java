package com.wanbao.manage.service;

import java.util.List;

import com.wanbao.manage.mapper.ContentMapper;
import com.wanbao.manage.pojo.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wanbao.common.bean.EasyUIResult;

@Service
public class ContentService extends BaseService<Content>{
	@Autowired
	private ContentMapper contentMapper;

	public EasyUIResult queryListByCatgoryId(Long categoryId, Integer page, Integer rows) {
		PageHelper.startPage(page,rows);
		
		//此处没有使用通用mapper,而是采用原始的mybatis形式进行练习 (当然使用通用mapper也行)
		List<Content> list=this.contentMapper.queryContentList(categoryId);
		PageInfo<Content> pageInfo=new PageInfo<Content>(list);
		return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
	}

}
