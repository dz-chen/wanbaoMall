package com.wanbao.common.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemCatResult {

	@JsonProperty("data")   //指定这个对象(itemCats)转换为json时的名称=>data
	private List<ItemCatData> itemCats = new ArrayList<ItemCatData>();

	public List<ItemCatData> getItemCats() {
		return itemCats;
	}

	public void setItemCats(List<ItemCatData> itemCats) {
		this.itemCats = itemCats;
	}

}
