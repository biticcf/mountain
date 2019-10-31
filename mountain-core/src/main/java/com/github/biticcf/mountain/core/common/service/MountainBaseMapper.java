/**
 * @info:      MountainBaseMapper.java
 * @copyright: 2019
 */
package com.github.biticcf.mountain.core.common.service;

import java.sql.SQLException;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * +MybatisPlus自定义基类，主要是禁用默认的带有分页功能的方法
 * +主要因为其分页采用的是逻辑分页，效果不如PageHelper的物理分页好
 * 
 * author: Daniel.Cao
 * date:   2019年10月31日
 * time:   上午09:10:47
 *
 * @param <T> 实体类泛型
 */
public interface MountainBaseMapper<T> extends BaseMapper<T> {
	
	// 禁用MybatisPlus的分页功能，使用PageHelper的分页功能
	@Deprecated
	@Override
	default IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper) {
		try {
			throw new SQLException("Not Supported Method[selectPage]!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// 禁用MybatisPlus的分页功能，使用PageHelper的分页功能
	@Deprecated
	@Override
	default IPage<Map<String, Object>> selectMapsPage(IPage<T> page, Wrapper<T> queryWrapper) {
		try {
			throw new SQLException("Not Supported Method[selectMapsPage]!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
