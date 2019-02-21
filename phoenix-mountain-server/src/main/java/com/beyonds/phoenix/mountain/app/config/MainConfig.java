/**
 * 
 */
package com.beyonds.phoenix.mountain.app.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;

import com.beyonds.phoenix.mountain.core.common.framework.PerformFramework;
import com.beyonds.phoenix.mountain.domain.config.DatasourceConfig;
import com.beyonds.phoenix.mountain.domain.config.RedisCacheConfig;
import com.beyonds.phoenix.mountain.domain.config.KafkaConfig;
import com.beyonds.phoenix.mountain.web.config.Swagger2Configuration;
import com.beyonds.phoenix.mountain.web.config.WebMvcConfiguration;

/**
 * @Author: DanielCao
 * @Date:   2017年5月1日
 * @Time:   下午8:26:31
 *
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@Import(value = {
		PerformFramework.class, 
		DatasourceConfig.class,
		RedisCacheConfig.class,
		KafkaConfig.class,
		WebMvcConfiguration.class,
		Swagger2Configuration.class
		})
@ComponentScan(
        value = {"com.beyonds.phoenix.mountain"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
        })
public class MainConfig {
	
}
