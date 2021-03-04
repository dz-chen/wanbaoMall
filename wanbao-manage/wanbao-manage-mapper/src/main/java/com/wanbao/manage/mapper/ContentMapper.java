package com.wanbao.manage.mapper;

import java.util.List;

import com.github.abel533.mapper.Mapper;
import com.wanbao.manage.pojo.Content;

public interface ContentMapper extends Mapper<Content>{
	
	/**
	 * 根据categoryId查询内容列表,并且按照更新时间倒序排序
	 *  =>此处是原始的mybatis写法 ,没有使用通用mapper(当然用通用mapper也行)
	 * @param categoryId
	 * @return
	 */
	public List<Content> queryContentList(Long categoryId);
	
	
}
