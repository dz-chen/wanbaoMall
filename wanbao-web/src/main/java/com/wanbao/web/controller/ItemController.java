package com.wanbao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.wanbao.manage.pojo.ItemDesc;
import com.wanbao.web.bean.Item;
import com.wanbao.web.service.ItemService;

@RequestMapping("item")
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 显示商品详情页
     * 注意:商品详情页的前端显示的类别始终为:家用电器 > 大 家 电 > 平板电视 > 长虹（CHANGHONG） > 长虹LED42538ES
     *     这部分可以自己完善修改.....
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ModelAndView itemDetail(@PathVariable("itemId") Long itemId) {
        ModelAndView mv = new ModelAndView("item");
        // 设置模型数据 => 先在前台系统的redis缓存中查找,没有找到再通过api从后台系统查找
        Item item = this.itemService.queryById(itemId);
        mv.addObject("item", item);

        // 商品描述数据
        ItemDesc itemDesc = this.itemService.queryDescByItemId(itemId);
        mv.addObject("itemDesc", itemDesc);

        // 商品规格参数数据
        String itemParam = this.itemService.queryItemParamItemByItemId(itemId);
        mv.addObject("itemParam", itemParam);
        return mv;
    }

}
