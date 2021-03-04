package com.wanbao.search.mq.handler;

import com.wanbao.search.bean.Item;
import com.wanbao.search.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemMQHandler {
	@Autowired
	private ItemService itemService;
	@Autowired
	private HttpSolrServer httpSolrServer;

	private static final ObjectMapper MAPPER=new ObjectMapper();
	
	/**
	 * 处理消息:新增、修改、删除的消息。将商品数据同步到solr中
	 * 消息中并没有包含商品的基本数据,需要通过id到后台系统提供的接口中获取
	 * @param msg
	 */
	public void execute(String msg) {
		try {
			JsonNode jsonNode=MAPPER.readTree(msg);
			Long itemId=jsonNode.get("itemId").asLong();
			String type=jsonNode.get("type").asText();
			if(StringUtils.equals(type, "insert") || StringUtils.equals(type, "update")) {
				Item item=this.itemService.quryById(itemId);
				this.httpSolrServer.addBean(item);
				this.httpSolrServer.commit();
			}
			else if(StringUtils.equals(type, "delete")) {
				this.httpSolrServer.deleteById(String.valueOf(itemId));
				this.httpSolrServer.commit();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}













