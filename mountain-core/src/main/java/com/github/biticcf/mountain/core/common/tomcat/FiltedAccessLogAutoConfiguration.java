/**
 * 
 */
package com.github.biticcf.mountain.core.common.tomcat;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * author:  Daniel.Cao
 * date:    2019年12月18日
 * time:    下午4:32:10
 * + 自动加载具有url的accesslog过滤性质的tomcat容器
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ ServerProperties.class, FiltedAccessLogProperties.class })
@ConditionalOnExpression("${server.tomcat.accesslog.filted-enable:true}")
public class FiltedAccessLogAutoConfiguration {

    /**
     * +自定义带有access log过滤功能的tomcat容器
     * @param environment environment
     * @param serverProperties serverProperties
     * @param filtedAccessLogProperties filtedAccessLogProperties
     * @return FiltedTomcatWebServerFactoryCustomizer
     */
    @Primary
    @ConditionalOnClass({ Tomcat.class, UpgradeProtocol.class })
    @Bean
    public FiltedTomcatWebServerFactoryCustomizer filtedTomcatWebServerFactoryCustomizer(Environment environment,
                ServerProperties serverProperties, FiltedAccessLogProperties filtedAccessLogProperties) {
        return new FiltedTomcatWebServerFactoryCustomizer(environment, serverProperties, filtedAccessLogProperties);
    }
}
