/**
 * 
 */
package com.github.biticcf.mountain.domain.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.biticcf.mountain.core.common.service.ReferContext;
import com.github.biticcf.mountain.domain.repository.DemoDomainRepository;

/**
 * author: DanielCao
 * date:   2017年5月8日
 * time:   下午6:27:27
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
