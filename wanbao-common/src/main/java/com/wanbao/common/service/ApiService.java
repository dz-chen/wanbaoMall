package com.wanbao.common.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanbao.common.httpclient.HttpResult;

/**
 * 自行封装的httpclient的一些方法,方便前台系统调用,减少代码重复
 * @author cdz
 *
 */

@Service  //实现BeanFactoryAware接口是为了能在单例对象中获取多例的一个实例(需要实现setBeanFactory方法)
public class ApiService implements BeanFactoryAware{
	
	//单例下使用多例 => 不应该注入,而是应该通过beanFactory获取多例的对象
//	@Autowired
//	CloseableHttpClient httpclient;
//	
	@Autowired(required=false)
	private RequestConfig requestConfig;   //见配置文件
	
	private BeanFactory beanFactory;
	
	/**
	 * 获取一个httpclient对象(直接从spring容器中获取,而不是注入,可满足多例要求)
	 * @return
	 */
	private CloseableHttpClient getHttpClient() {
		return this.beanFactory.getBean(CloseableHttpClient.class);
	}
	

	/**
	 * GET => 响应为200则返回响应内容,否则返回空
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String doGet(String url) throws ClientProtocolException, IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = this.getHttpClient().execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");                                           
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
	}
		
	/**
	 * 带参数的GET
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String doGet(String url,Map<String,String> params) throws ClientProtocolException, IOException, URISyntaxException {
        URIBuilder builder=new URIBuilder();
        for(Map.Entry<String,String> entry:params.entrySet()) {
        	builder.setParameter(entry.getKey(),entry.getValue());
        }
        return this.doGet(builder.build().toString());
	}
	
	/**
	 * 带有参数的POST请求 => 表单数据
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPost(String url,Map<String,String> params) throws ClientProtocolException, IOException {
		
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        //构造参数表单
        if(null!=params) {
        	List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        	for(Map.Entry<String,String> entry:params.entrySet()) {
        		 parameters.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        	 }
        	// 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);                                                                           
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        
        CloseableHttpResponse response = null;
        
        try {
            // 执行请求
            response = this.getHttpClient().execute(httpPost);
            //返回包括状态码和响应体
            String body;
            if(response.getEntity()==null) body="";           
            else body=EntityUtils.toString(response.getEntity(),"UTF-8");   //toString参数不能为null
            return new HttpResult(response.getStatusLine().getStatusCode(),body);
        } finally {
            if (response != null) {
                response.close();
            }
        }
	}
	
	/**
	 * 带有json数据的 POST请求
	 * @param url
	 * @param json
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPostJson(String url,String json) throws ClientProtocolException, IOException {
		
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        
        if(null!=json) {
        	StringEntity stringEntity=new  StringEntity(json,ContentType.APPLICATION_JSON);                                                                       
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        
        CloseableHttpResponse response = null;
        
        try {
            // 执行请求
            response = this.getHttpClient().execute(httpPost);
            //返回包括状态码和响应体
            String body;
            if(response.getEntity()==null) body="";           
            else body=EntityUtils.toString(response.getEntity(),"UTF-8");   //toString参数不能为null
            return new HttpResult(response.getStatusLine().getStatusCode(),body);
        } finally {
            if (response != null) {
                response.close();
            }
        }
	}

	
	/**
	 * 不带参数的POST请求
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public HttpResult doPost(String url) throws ClientProtocolException, IOException {
		return this.doPost(url,null);
	}

	
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		//spring容器在初始化时会调用此方法,传入beanFactory
		this.beanFactory=beanFactory;
	}
	
		
}








