package com.wanbao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis 缓存的相关service
 * => 通过函数式接口Function封装了get set方法的公共部分
 * => 此部分代码思想很重要,常复习!!!
 * @author cdz
 *
 */

@Service
public class RedisService {
	
	@Autowired(required=false)   //从spring容器中查找,找不到就算了,找到就注入   => 不是很能理解,见day06 视频23
	private ShardedJedisPool shardedJedisPool;
	
	/**
	 * redis各种操作的公共代码
	 * @param <T>
	 * @param fun
	 * @return
	 */
	private <T> T  execute(Function<T,ShardedJedis> fun) {
		  ShardedJedis shardedJedis = null;
	        try {
	            // 从连接池中获取到jedis分片对象
	            shardedJedis = shardedJedisPool.getResource();
	            return fun.callback(shardedJedis);
	        } 
	        finally {
	            if (null != shardedJedis) {
	                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
	                shardedJedis.close();
	            }
	        }
	}
	
	
	/**
	 * 执行set操作
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(final String key,final String value) {
        return this.execute(new Function<String,ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value);        //闭包,使用的参数必须是不可变 => final
			}
        });
	}
	
	/**
	 * 执行set操作并且设置生存时间
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public String set(final String key,final String value,final Integer seconds) {
        return this.execute(new Function<String,ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				String str= e.set(key, value);        //闭包,使用的参数必须是不可变 => fina
				e.expire(key, seconds);
				return str;
			}
        });
	}
	
	
	/**
	 * 执行get操作
	 * @param key
	 * @param value
	 * @return
	 */
	public String get(final String key) {
		return this.execute(new Function<String,ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.get(key);        //闭包,使用的参数必须是不可变 => final
			}
        });
	}
	
	
	/**
	 * 执行删除操作
	 * @param key
	 * @return
	 */
	public Long del(final String key) {
		return this.execute(new Function<Long,ShardedJedis>(){

			@Override
			public Long callback(ShardedJedis e) {
				return e.del(key);
			}
			
		});
	}
	
	/**
	 * 设置生存时间
	 * @param key
	 * @param value
	 * @return
	 */
	public Long expire(final String key,final Integer seconds) {
        return this.execute(new Function<Long,ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				return e.expire(key, seconds);        //闭包,使用的参数必须是不可变 => final
			}
        });
	}
}
