/**
 * 
 */
package com.beyonds.phoenix.mountain.domain.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.beyonds.phoenix.mountain.domain.feign.config.FeignConfig;
import com.beyonds.phoenix.mountain.domain.feign.fallback.CategoryFeignClientFallback;

/**
 * author: DanielCao
 * date:   2017年6月28日
 * time:   下午11:50:11
 *
 */
@FeignClient(name = "categoryFeignClient",
		     url = "http://127.0.0.1:10001", 
             fallback = CategoryFeignClientFallback.class,
             configuration = {FeignConfig.class})
public interface CategoryFeignClient {
	/**
	 * 第三方业务接口
	 * @param categoryIds 参数
	 * @return 结果
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/v1/cdaservice/categorys/multi")
	String getCategorys(@RequestParam("categoryIds") String categoryIds);
}
