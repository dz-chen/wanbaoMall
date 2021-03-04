package com.wanbao.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wanbao.common.bean.EasyUIResult;
import com.wanbao.common.service.ApiService;
import com.wanbao.manage.mapper.ItemMapper;
import com.wanbao.manage.pojo.Item;
import com.wanbao.manage.pojo.ItemDesc;
import com.wanbao.manage.pojo.ItemParamItem;


@Service
public class ItemService extends BaseService<Item>{
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ItemDescService itemDescService;
	@Autowired
	private ItemParamItemService itemParamItemService;
	@Autowired
	private ApiService apiService;
	
	@Value("${WANBAO_WEB_URL}")
	private String WANBAO_WEB_URL;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ItemService.class);
	
	//service 与 service嵌套(相当于两个事务嵌套) => spring默认是一个事务
	public Boolean saveItem(Item item, String desc,String itemParams) {
		//商品表保存  初始化上传表单时没有的字段
		item.setStatus(1);
		item.setId(null);    //出于安全考虑,强制设置id为null,通过数据库自增获得
		Integer count1=super.save(item);
		
		//商品描述保存数据
		ItemDesc itemDesc=new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		Integer count2=this.itemDescService.save(itemDesc);
		
		//保存商品规格参数数据
		ItemParamItem itemParamItem=new ItemParamItem();
		itemParamItem.setItemId(item.getId());
		itemParamItem.setParamData(itemParams);
		Integer count3=this.itemParamItemService.save(itemParamItem);
		
		//发送消息
		sendMsg(item.getId(),"insert"); 
		
		return count1.intValue()==1 &&  count2.intValue()==1 && count3.intValue()==1 ;
	}
	
	// 查询商品
	public EasyUIResult queryItemList(Integer page, Integer rows) {
		//设置分页参数
		PageHelper.startPage(page,rows);
		
		Example example=new Example(Item.class);
		//按照创建时间排序
		example.setOrderByClause("created DESC");
		List<Item> items=this.itemMapper.selectByExample(example);
		PageInfo<Item> pageInfo=new PageInfo<Item>(items);
		return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
	}

	
	
	// 更新/编辑商品 => 注意写到一个事务中！！！
	public Boolean updateItem(Item item, String desc, String itemParams) {
		// 更新商品信息
		Integer count1=super.updateSelective(item);
		
		//更新商品描述信息
		ItemDesc itemDesc=new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		Integer count2=this.itemDescService.updateSelective(itemDesc);
		
		//更新商品规格参数
		/*updateSelective是根据主键更新,itemParamItem并没有设置主键(参考对应poo) => 需要单独写更新函数*/
//		ItemParamItem itemParamItem=new ItemParamItem();
//		itemParamItem.setItemId(item.getId());
//		itemParamItem.setParamData(itemParams);
//		Integer count3=this.itemParamItemService.updateSelective(itemParamItem);
		Integer count3=this.itemParamItemService.updateItemParamItem(item.getId(),itemParams);
		
		//通知其他系统该商品已经更新
		//通常有事务的service中不能捕获异常； 不过此处调用api已经处于数据库操作之后,
		//如果事务已经完成,对api的调用出现错误应该尽量不影响原来的事务 => 捕获异常
//		try {
//			String url=WANBAO_WEB_URL+"/item/cache/"+item.getId()+".html";
//			this.apiService.doPost(url);
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
		
		// => 上面的更新其他系统缓存方式导致系统间耦合较大, 改为使用消息队列
		sendMsg(item.getId(),"update"); 
		
		//return count1.intValue()==1 &&  count2.intValue()==1 && count3.intValue()==1;
		// 对于导入的京东数据,由于没有规格参数,导致count3=0, 此处忽略这种情况！
		return count1.intValue()==1 ||  count2.intValue()==1 || count3.intValue()==1;
	}
	
	
	
	/**
	 *  自定义的发送消息方法(消息队列)
	 * @param itemId
	 * @param type
	 */
	private void sendMsg(Long itemId, String type) {
		try {
			Map<String,Object> msg=new HashMap<String,Object>();
			msg.put("itemId",itemId);
			msg.put("type",type);
			msg.put("date",System.currentTimeMillis());
			this.rabbitTemplate.convertAndSend("item."+type, MAPPER.writeValueAsString(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		
}
