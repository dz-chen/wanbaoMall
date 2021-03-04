package com.wanbao.manage.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_item_desc")
public class ItemDesc extends BasePojo{
    
    @Id//对应tb_item中的id 
    // desc表中没有主键(id), 此处通过@Id注解,将itemId认为是主键,方便使用通用Mapper根据Id查询
    private Long itemId;
    
    private String itemDesc;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }
    
    

}
