package com.wanbao.manage.service;

import java.util.Date;
import java.util.List;

import com.wanbao.manage.pojo.BasePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


/**
 * 注意是抽象类,使用其中的方法时需要在子类中实现
 *
 * @param <T>
 */
@Service
public abstract class BaseService<T extends BasePojo> {
		
	//封装以下方法
	/**
	 *  1、	queryById
		2、	queryAll
		3、	queryOne
		4、	queryListByWhere
		5、	queryPageListByWhere
		6、	save
		7、	update
		8、	deleteById
		9、	deleteByIds
		10、deleteByWhere

	 */
	
	//具体的service类需要实现这个方法，从而获取Mapper
	//public abstract Mapper<T> getMapper();
	
	
	// 使用spring4.x 的泛型注入,可直接按照如下方式写,不需要通过getMapper方法获取具体的mapper
	// spring可直接注入
	
	
	/*
	 * ItemCatMapper在编码时没有使用到，是否可以将其删除？ -- 不能。
	 * 原因：在Spring运行时会使用该对象，将其注入到BaseService中(即下面的这个mapper)
	 */
	@Autowired
	private Mapper<T> mapper;	// 注入时根据需要注入实际的实现Mapper
	
	
	/**
	 * 根据id查询数据
	 * @param id
	 * @return
	 */
	public T queryById(Long id) {
		//return this.getMapper().selectByPrimaryKey(id);
		return this.mapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 查询所有数据
	 * @return
	 */
	public List<T>  queryAll(){
		//return this.getMapper().select(null);
		return this.mapper.select(null);
	}
	
	/**
	 * 根据条件查询一条数据
	 * 如果查询的结果为多条会抛出异常
	 * @param record
	 * @return
	 */
	public T queryOne(T record) {
		//return this.getMapper().selectOne(record);
		return this.mapper.selectOne(record);
	}
	
	/**
	 * 根据条件查询多条数据
	 * @param record
	 * @return
	 */
	public List<T> queryListByWhere(T record){
		//return this.getMapper().select(record);
		return this.mapper.select(record);
	}
	
	/**
	 * 根据条件分页查询数据
	 * @param record
	 * @param pageNum
	 * @return
	 */
	public PageInfo<T> queryPageListByWhere(T record, Integer pageNum,Integer rows) {
		//设置分页参数
		PageHelper.startPage(pageNum,rows);
		//List<T> list=this.getMapper().select(record);
		List<T> list=this.mapper.select(record);
		return new PageInfo<T>(list);
	}
	
	/**
	 * 新增数据
	 * @param t
	 * @return
	 */
	public Integer save(T t) {
		t.setCreated(new Date());
		t.setUpdated(t.getCreated());
		
		//return this.getMapper().insert(t);
		return this.mapper.insert(t);
	}
	
	/**
	 * insertSelective对应的sql语句加入了NULL校验，即只会插入数据不为null的字段值。
	 * insert则会插入所有字段，会插入null。
	 * @param t
	 * @return
	 */
	public Integer saveSelective(T t) {
		t.setCreated(new Date());
		t.setUpdated(t.getCreated());
		//return this.getMapper().insertSelective(t);
		return this.mapper.insertSelective(t);
	}
	
	/**
	 * 更新数据
	 * @param t
	 * @return
	 */
	public Integer update(T t) {
		t.setUpdated(new Date());
		//return this.getMapper().updateByPrimaryKey(t);
		return this.mapper.updateByPrimaryKey(t);
	}
	
	/**
	 * 选择输入中不为null的字段进行更新.....
	 * @param t
	 * @return
	 */
	public Integer updateSelective(T t) {
		t.setUpdated(new Date());
		t.setCreated(null);   //强制设置创建时间为null, 永远不会被更新(为了严谨)
 		//return this.getMapper().updateByPrimaryKeySelective(t);
		return this.mapper.updateByPrimaryKeySelective(t);
	}
	
	/**
	 * 根据主键id删除数据
	 * @param id
	 * @return
	 */
	public Integer deleteById(Long id) {
		//return this.getMapper().deleteByPrimaryKey(id);
		return this.mapper.deleteByPrimaryKey(id);
	}
	
	/**
	 * 批量删除数据
	 * @param ids
	 * @param clazz
	 * @param property
	 * @return
	 */
	public Integer deleteByIds(List<Object> ids, Class<T> clazz,String property) {
		Example example=new Example(clazz);
		//设置条件
		example.createCriteria().andIn(property, ids);
		//return this.getMapper().deleteByExample(example);
		return this.mapper.deleteByExample(example);
	}
	
	/**
	 * 根据条件删除数据
	 * @param record
	 * @return
	 */
	public Integer deleteByWhere(T record) {
		//return this.getMapper().delete(record);
		return this.mapper.delete(record);
	}
}
