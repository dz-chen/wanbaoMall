package com.wanbao.common.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 商品种类(一级类目、二级类目、三级类目)
 */
public class ItemCatData  {
	
	@JsonProperty("u") // 序列化成json数据时为 u
	private String url;		// 通过点击此url可访问此类目商品
	
	@JsonProperty("n")
	private String name;	// 类目名称
	
	@JsonProperty("i")
	private List<?> items;  // 包含的子类目

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<?> getItems() {
		return items;
	}

	public void setItems(List<?> items) {
		this.items = items;
	}
	
	

}
