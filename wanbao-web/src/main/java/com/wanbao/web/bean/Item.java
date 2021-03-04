package com.wanbao.web.bean;

import org.apache.commons.lang3.StringUtils;

public class Item extends com.wanbao.manage.pojo.Item {

    public String[] getImages() {
        return StringUtils.split(super.getImage(), ',');
    }

}
