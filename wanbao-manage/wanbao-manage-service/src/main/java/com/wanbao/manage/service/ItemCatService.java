package com.wanbao.manage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanbao.manage.pojo.ItemCat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.bean.ItemCatData;
import com.wanbao.common.bean.ItemCatResult;
import com.wanbao.common.service.RedisService;

@Service
public class ItemCatService extends BaseService<ItemCat>{
	
	// 使用泛型注入,以下都不需要
//	@Autowired
//	private ItemCatMapper itemCatMapper;
	
	
//	@Override     //spring4.x 泛型注入,不再需要通过getMapper获取mapper
	// 注:getMapper方法的生命来自BaseService,它是抽象类,所以在这里实现,以获取需要的mapper
//	public Mapper<ItemCat> getMapper(){
//		return this.itemCatMapper;
//	}
//	

	//继承BaseService后就不再需要使用单独的查询,直接使用基类中通用的查询即可
	// 对应的Controller同样进行修改!!!
//	public List<ItemCat> queryItemCatListByParentId(long pid) {
//		// TODO Auto-generated method stub
//		
//		ItemCat record=new ItemCat();
//		record.setParentId(pid);          //查询参数
//		return this.itemCatMapper.select(record);
//	}

	@Autowired
	private RedisService redisService;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	private static final String REDIS_KEY="WANBAO_MANAGE_ITEM_CAT_API";          //规则:项目名_模块名_业务名
	
	private static final Integer REDIS_TIME=60*60*24*30*3;
	/**
	 * 全部查询，并且生成类目的树状结构
	 * @return
	 */
	public ItemCatResult queryAllToTree() {
		ItemCatResult result = new ItemCatResult();
		
		// 捕获整个redis操作的异常,避免发生异常导致程序退出无法获取数据
		try {
			//先从redis缓存中查询,如果命中就返回，没有命中继续执行
			String cacheData=this.redisService.get(REDIS_KEY);
			if(StringUtils.isNotEmpty(cacheData)) {          //命中
					return MAPPER.readValue(cacheData, ItemCatResult.class);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// 如果上面代码在redis中没有找到,再执行下面的代码在关系数据库中查找
		
		// 全部查出，并且在内存中生成树形结构
		List<ItemCat> cats = super.queryAll();
		
		// 转为map存储，key为父节点ID，value为数据集合
		Map<Long, List<ItemCat>> itemCatMap = new HashMap<Long, List<ItemCat>>();
		for (ItemCat itemCat : cats) {
			if(!itemCatMap.containsKey(itemCat.getParentId())){
				itemCatMap.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			itemCatMap.get(itemCat.getParentId()).add(itemCat);
		}
		
		// 封装一级对象
		List<ItemCat> itemCatList1 = itemCatMap.get(0L);
		for (ItemCat itemCat : itemCatList1) {
			ItemCatData itemCatData = new ItemCatData();
			itemCatData.setUrl("/products/" + itemCat.getId() + ".html");
			itemCatData.setName("<a href='"+itemCatData.getUrl()+"'>"+itemCat.getName()+"</a>");
			result.getItemCats().add(itemCatData);
			if(!itemCat.getIsParent()){
				continue;
			}
			
			// 封装二级对象
			List<ItemCat> itemCatList2 = itemCatMap.get(itemCat.getId());
			List<ItemCatData> itemCatData2 = new ArrayList<ItemCatData>();
			itemCatData.setItems(itemCatData2);
			for (ItemCat itemCat2 : itemCatList2) {
				ItemCatData id2 = new ItemCatData();
				id2.setName(itemCat2.getName());
				id2.setUrl("/products/" + itemCat2.getId() + ".html");
				itemCatData2.add(id2);
				if(itemCat2.getIsParent()){
					// 封装三级对象
					List<ItemCat> itemCatList3 = itemCatMap.get(itemCat2.getId());
					List<String> itemCatData3 = new ArrayList<String>();
					id2.setItems(itemCatData3);
					for (ItemCat itemCat3 : itemCatList3) {
						itemCatData3.add("/products/" + itemCat3.getId() + ".html|"+itemCat3.getName());
					}
				}
			}
			if(result.getItemCats().size() >= 14){
				break;
			}
		}
		
		//将数据库的查询结果写入到redis缓存中
		try {
			this.redisService.set(REDIS_KEY, MAPPER.writeValueAsString(result),REDIS_TIME );
		} catch (Exception e) {
		 	e.printStackTrace();
		}
		return result;
	}
	
	
}
