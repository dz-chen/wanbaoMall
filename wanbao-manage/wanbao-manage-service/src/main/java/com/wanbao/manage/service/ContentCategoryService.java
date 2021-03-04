package com.wanbao.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wanbao.manage.pojo.ContentCategory;

@Service
public class ContentCategoryService extends BaseService<ContentCategory>{

	/**
	 * 保存广告类目
	 * @param contentCategory
	 */
	public void saveContentCatogery(ContentCategory contentCategory) {
		//两个数据库操作,注意写到一个事务中!!
		contentCategory.setId(null);
		contentCategory.setIsParent(false);
		contentCategory.setSortOrder(1);
		contentCategory.setStatus(1);
		super.save(contentCategory);
		
		//判断该节点的父节点的isParent是否为true,不是则需要修改为true
		ContentCategory parent=super.queryById(contentCategory.getParentId());
		if(!parent.getIsParent()) {
			parent.setIsParent(true);
			super.update(parent);
		}
		
		
	}

	/**
	 * 删除广告类目(同时删除所有子类目,并且修改父节点的isParent属性)
	 * @param contentCategory
	 */
	public void deleteAll(ContentCategory contentCategory) {
		List<Object> ids=new ArrayList<Object>();
		ids.add(contentCategory.getId());
		
		//1.递归查找该节点下所有子节点id(所有要删除的节点id存储在ids中)
		this.findAllSubNode(ids, contentCategory.getId());
		super.deleteByIds(ids,ContentCategory.class,"id");
		
		//2.判断该节点是否还有兄弟节点,如果没有,修改父节点的isParent为false
		ContentCategory record=new ContentCategory();
		record.setParentId(contentCategory.getParentId());
		//查询所有父节点与contentCategory相同的节点(即兄弟节点)
		List<ContentCategory> list=super.queryListByWhere(record);   
		if(list==null || list.isEmpty()) {
			ContentCategory parent=new ContentCategory();
			parent.setId(contentCategory.getParentId());
			parent.setIsParent(false);
			super.updateSelective(parent);
		}
	}
	
	
	/**
	 * 工具方法 => 查找一个类目下面所有子节点
	 * @param ids
	 * @param pid
	 */
	private void findAllSubNode(List<Object> ids,Long pid) {
		ContentCategory record=new ContentCategory();
		record.setParentId(pid);
		List<ContentCategory> list=super.queryListByWhere(record);   //查询与record中数据匹配的所有数据
		for(ContentCategory contentCategory:list) {
			ids.add(contentCategory.getId());
			//平淡段该节点是否为父节点,如果是,继续查找其子节点
			if(contentCategory.getIsParent()) {
				findAllSubNode(ids,contentCategory.getId());
			}
		}
	}

}














