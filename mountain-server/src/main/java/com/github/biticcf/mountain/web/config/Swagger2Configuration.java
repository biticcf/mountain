/**
 * 
 */
package com.github.biticcf.mountain.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * author: DanielCao
 * date:   2018年7月25日
 * time:   下午7:39:02
 *
 */
@EnableSwagger2
@ConditionalOnProperty(prefix = "swagger2", name = {"enable"}, havingValue = "true", matchIfMissing = false)
@Configuration(proxyBeanMethods = false)
public class Swagger2Configuration implements WebMvcConfigurer {
	/**
	 * 定义swagger
	 * @return Docket
	 */
	@Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.biticcf.mountain"))
                .paths(PathSelectors.any())
                .build();
    }

	/**
	 * 定义附加信息
	 * @return ApiInfo
	 */
    private ApiInfo apiInfo() {
    	Contact contact = new Contact("CaoChaofeng", "url", "19070443@qq.com");
        return new ApiInfoBuilder()
                .title("Mountain相关RESTful APIs")
                .description("Mountain相关API调用方式及参数详解")
                .termsOfServiceUrl("")
                .contact(contact)
                .version("2.0.RELEASE")
                .build();
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/**")
    	        .addResourceLocations("classpath:/static/");
    	registry.addResourceHandler("swagger-ui.html")
    	        .addResourceLocations("classpath:/META-INF/resources/");
    	registry.addResourceHandler("/webjars/**")
    	        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    	registry.addResourceHandler("doc.html")
    			.addResourceLocations("classpath:/META-INF/resources/");
    }
}
