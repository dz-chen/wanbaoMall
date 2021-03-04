package com.wanbao.web.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wanbao.sso.query.api.UserQueryService;
import com.wanbao.sso.query.bean.User;

@Service
public class UserService {

    @Autowired
    private ApiService apiService;

    @Value("${WANBAO_SSO_URL}")
    public String WANBAO_SSO_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Autowired
    private UserQueryService userQueryService;

    /**
     * 根据token查询用户信息 => 这里是前台系统,需要向SSO系统查询
     * 这里通过dubbo实现
     * @param token
     * @return
     */
    public User queryByToken(String token) {
        return this.userQueryService.queryUserByToken(token);
    }

    /**
     * 根据token查询用户信息 => 这里是前台系统,需要向SSO系统查询
     * 这里通过httpclient(apiservice)实现 => 弃用,改用上面的dubbo方式
     * @param token
     * @return
     */
//    public User queryByToken(String token) {
//        try {
//            String url = WANBAO_SSO_URL + "/service/user/query/" + token;
//            String jsonData = this.apiService.doGet(url);
//            if (StringUtils.isNotEmpty(jsonData)) {
//                return MAPPER.readValue(jsonData, User.class);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
