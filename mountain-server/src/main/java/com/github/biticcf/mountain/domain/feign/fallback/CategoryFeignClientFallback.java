/**
 * 
 */
package com.github.biticcf.mountain.domain.feign.fallback;

import com.github.biticcf.mountain.domain.feign.CategoryFeignClient;

/**
 * author: DanielCao
 * date:   2017年6月29日
 * time:   上午1:15:24
 *
 */
public class CategoryFeignClientFallback implements CategoryFeignClient {
	@Override
	public String getCategorys(String categoryIds) {
		return "{\"status\":5001}";
	}
}
