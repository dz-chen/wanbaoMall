package com.wanbao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wanbao.common.service.RedisService;
import com.wanbao.web.service.ItemService;

/**
 * 后台发起请求,通知前台删除对应的缓存
 * => 系统间耦合较大,后面会改为消息队列实现
 */
@RequestMapping("item/cache")
@Controller
public class ItemCacheController {

    @Autowired
    private RedisService redisService;

    /**
     * 接收商品id，删除对应的缓存数据
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.POST)
    public ResponseEntity<Void> deleteCache(@PathVariable("itemId") Long itemId) {
        try {
            String key = ItemService.REDIS_KEY + itemId;
            this.redisService.del(key);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  //204
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
    }

}
