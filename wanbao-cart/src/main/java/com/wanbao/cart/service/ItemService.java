package com.wanbao.cart.service;

import com.wanbao.cart.bean.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.service.ApiService;


@Service
public class ItemService {
	@Autowired
	private ApiService apiService;
	
	@Value("${WANBAO_MANAGE_URL}")
	private String WANBAO_MANAGE_URL;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();

	/**
	 * 根据商品id向后台查询商品的详细信息
	 * @param itemId
	 * @return
	 */
	public Item quryById(Long itemId) {
		try {
			String url=WANBAO_MANAGE_URL+"/rest/api/item/"+itemId;
			String jsonData=this.apiService.doGet(url);
			if(StringUtils.isNotEmpty(jsonData)) {
				return MAPPER.readValue(jsonData, Item.class);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
