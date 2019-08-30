/**
 * 
 */
package com.beyonds.phoenix.mountain.domain.feign.fallback;

import com.beyonds.phoenix.mountain.domain.feign.CategoryFeignClient;

/**
 * @Author: DanielCao
 * @Date:   2017年6月29日
 * @Time:   上午1:15:24
 *
 */
public class CategoryFeignClientFallback implements CategoryFeignClient {
	@Override
	public String getCategorys(String categoryIds) {
		return "{\"status\":5001}";
	}
}
