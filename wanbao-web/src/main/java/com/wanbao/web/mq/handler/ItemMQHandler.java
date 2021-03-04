package com.wanbao.web.mq.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanbao.common.service.RedisService;
import com.wanbao.web.service.ItemService;

/**
 * 收到消息后,对前台的缓存数据进行处理
 */
public class ItemMQHandler {

    @Autowired
    private RedisService redisService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 删除前台缓存中的商品数据，完成数据同步
     * 
     * @param msg
     */
    public void execute(String msg) {
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            Long itemId = jsonNode.get("itemId").asLong();
            String key = ItemService.REDIS_KEY + itemId;
            this.redisService.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
