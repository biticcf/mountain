/**
 * 
 */
package com.beyonds.phoenix.mountain.domain.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.beyonds.phoenix.mountain.core.common.service.ReferContext;
import com.beyonds.phoenix.mountain.domain.repository.DemoDomainRepository;

/**
 * @Author: DanielCao
 * @Date:   2017年5月8日
 * @Time:   下午6:27:27
 *
 */
@Service("constantContext")
public class ConstantContext implements ReferContext {
	@Autowired
	private DemoDomainRepository demoDomainRepository;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	public DemoDomainRepository getDemoDomainRepository() {
		return demoDomainRepository;
	}
	
	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}
}
