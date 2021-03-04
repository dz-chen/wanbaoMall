package com.wanbao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.wanbao.web.service.IndexService;

@RequestMapping("index")
@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 首页
     * 注:广告数据是从后台系统查询所得,并非直接来自数据库
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        // 首页大广告
        String indexAD1 = this.indexService.queryIndexAD1();
        mv.addObject("indexAD1", indexAD1);     // 传递给页面的数据
        // 首页小广告
        String indexAD2 = this.indexService.queryIndexAD2();
        mv.addObject("indexAD2", indexAD2);
        return mv;
    }

}
