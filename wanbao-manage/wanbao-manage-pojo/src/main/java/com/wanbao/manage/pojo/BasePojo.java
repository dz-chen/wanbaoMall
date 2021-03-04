package com.wanbao.manage.pojo;

import java.util.Date;


// 通用的数据 
public abstract class BasePojo {
    
    private Date created;
    private Date updated;
    
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    

}
