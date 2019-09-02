/**
 * 
 */
package com.github.biticcf.mountain.app;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.github.biticcf.mountain.shackle.EnableShackleTemplates;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;

/**
 * author: DanielCao
 * date:   2017年5月1日
 * time:   下午5:39:52
 * 
 * SpringBoot 主方法
 * SpringBootApplication相当于
 *     Configuration+EnableAutoConfiguration+ComponentScan
 * 
 */
@SpringBootApplication(scanBasePackages = {"com.github.biticcf.mountain"}, exclude = {PageHelperAutoConfiguration.class})
@EnableFeignClients(basePackages = {"com.github.biticcf.mountain.domain.feign"})
@EnableShackleTemplates(basePackages = {"com.github.biticcf.mountain.service"})
@EnableEurekaClient
public class Main/* extends SpringBootServletInitializer*/ {
	// war启动入口
	// @Override
	// protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	//	return configureApplication(builder);
	// }
	
	/**
	 * 主程序入口(jar格式)
	 * @param args 命令行参数
	 * @throws Exception 执行异常
	 */
	public static void main(String[] args) throws Exception {
		configureApplication(new SpringApplicationBuilder()).run(args);
	}
	
	/**
	 * 定义程序入口
	 * @param builder SpringApplicationBuilder
	 * @return SpringApplicationBuilder
	 */
	private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(Main.class).bannerMode(Banner.Mode.CONSOLE).logStartupInfo(true).registerShutdownHook(true).web(WebApplicationType.SERVLET);
    }
}
