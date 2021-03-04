package com.wanbao.manage.service;

import java.util.Date;

import com.wanbao.manage.mapper.ItemParamItemMapper;
import com.wanbao.manage.pojo.ItemParamItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;

@Service
public class ItemParamItemService extends BaseService<ItemParamItem>{
	@Autowired
	private ItemParamItemMapper itemParamItemMaper;
	
	
	
	public Integer updateItemParamItem(Long itemId,String itemParams) {
		//更新数据
		ItemParamItem itemParamItem=new ItemParamItem();
		itemParamItem.setParamData(itemParams);
		itemParamItem.setUpdated(new Date());
		
		//更新的条件(更新"itemId"字段等于itemId的数据行)
		Example example=new Example(ItemParamItem.class);
		example.createCriteria().andEqualTo("itemId", itemId);
		return this.itemParamItemMaper.updateByExampleSelective(itemParamItem, example);
	}

}
