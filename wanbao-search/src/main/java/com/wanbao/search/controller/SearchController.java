package com.wanbao.search.controller;

import java.io.UnsupportedEncodingException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wanbao.search.bean.SearchResult;
import com.wanbao.search.service.ItemSearchService;

@Controller
@RequestMapping("/")
public class SearchController {
	@Autowired
	private ItemSearchService itemSearchService;
	
	public static final Integer ROWS=32;


	/**
	 * 跳转到搜索页面,并执行搜索
	 * @param keyWords
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public ModelAndView search(@RequestParam("q") String keyWords,
			@RequestParam(value="page",defaultValue="1") Integer page) {   //page代表要获取第几页
		ModelAndView mv=new ModelAndView("search");                 //跳转到search.jsp页面


		//解决keyWords是中文时的乱码问题
		try {
			keyWords=new String(keyWords.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			keyWords="";
		}
		
		//搜索关键字
		mv.addObject("query",keyWords);
		
		//搜索结果数据
		SearchResult searchResult=null;
		try {
			// 通过solor查询
			searchResult=this.itemSearchService.search(keyWords,page,ROWS);   //从solr中查询
		} catch (SolrServerException e) {
			e.printStackTrace();
			searchResult=new SearchResult(0L,null);
		}
		mv.addObject("itemList", searchResult.getList());
		 
		//页数(第几页)
		mv.addObject("page", page);
		
		//总页数
		int total=searchResult.getTotal().intValue();
		int pages=total%ROWS==0 ? total/ROWS : total/ROWS+1;
		mv.addObject("pages", pages);
		
		return mv;
	}
}
