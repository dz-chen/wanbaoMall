package com.wanbao.web.service;

import com.wanbao.web.bean.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wanbao.common.service.ApiService;
import com.wanbao.common.service.RedisService;
import com.wanbao.manage.pojo.ItemDesc;
import com.wanbao.manage.pojo.ItemParamItem;

@Service
public class ItemService {

    @Autowired
    private ApiService apiService;

    @Value("${WANBAO_MANAGE_URL}")
    private String WANBAO_MANAGE_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private RedisService redisService;

    public static final String REDIS_KEY = "WANBAO_WEB_ITEM_DETAIL_";

    private static final Integer REDIS_TIME = 60 * 60 * 24;

    /**
     * 根据商品id查询商品数据
     * => 先在redis中查找
     * 如果没有找到,再通过后台系统提供的接口服务进行查询
     * 
     * @param itemId
     * @return
     */
    public Item queryById(Long itemId) {

        String key = REDIS_KEY + itemId;
        try {
            String cacheData = this.redisService.get(key);
            if (StringUtils.isNotEmpty(cacheData)) {
                return MAPPER.readValue(cacheData, Item.class);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String url = WANBAO_MANAGE_URL + "/rest/api/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }

            try {
                this.redisService.set(key, jsonData, REDIS_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 将json数据反序列为Item对象
            return MAPPER.readValue(jsonData, Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询商品描述数据
     * @param itemId
     * @return
     */
    public ItemDesc queryDescByItemId(Long itemId) {
        String url = WANBAO_MANAGE_URL + "/rest/api/item/desc/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            // 将json数据反序列为ItemDesc对象
            return MAPPER.readValue(jsonData, ItemDesc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String queryItemParamItemByItemId(Long itemId) {
        String url = WANBAO_MANAGE_URL + "/rest/api/item/param/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            // 将json数据反序列
            ItemParamItem itemParamItem = MAPPER.readValue(jsonData, ItemParamItem.class);
            String paramData = itemParamItem.getParamData();

            ArrayNode arrayNode = (ArrayNode) MAPPER.readTree(paramData);

            StringBuilder sb = new StringBuilder();
            sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\"><tbody>");

            for (JsonNode param : arrayNode) {
                sb.append("<tr><th class=\"tdTitle\" colspan=\"2\">" + param.get("group").asText()
                        + "</th></tr>");
                ArrayNode params = (ArrayNode) param.get("params");
                for (JsonNode p : params) {
                    sb.append("<tr><td class=\"tdTitle\">" + p.get("k").asText() + "</td><td>"
                            + p.get("v").asText() + "</td></tr>");
                }
            }

            sb.append("</tbody></table>");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
