/**
 * 
 */
package com.github.biticcf.mountain.domain.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.alibaba.druid.wall.WallFilter;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.biticcf.mountain.core.common.service.WdServiceTemplate;
import com.github.biticcf.mountain.core.common.service.WdServiceTemplateImpl;
import com.github.biticcf.mountain.core.common.transaction.ManualManagedTransactionFactory;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.autoconfigure.PageHelperProperties;

/**
 * author: DanielCao
 * date:   2017年5月3日
 * time:   下午11:04:35
 * 
 * +配置自定义数据库连接池和自定义事务模板
 * +单数据源配置
 */
@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = {"${mybatis.type-dao-package:com.github.biticcf.mountain.domain.dao}"}, 
            sqlSessionFactoryRef = "sqlSessionFactory")  
@EnableTransactionManagement
@AutoConfigureOrder(-100)
@AutoConfigureAfter({MybatisAutoConfiguration.class})
public class DatasourceConfig {
	protected static Log logger = LogFactory.getLog("DAO.LOG");
	
	@Value("${spring.datasource.type}")
	private Class<? extends DataSource> datasourceType;
	
	/**
	 * 定义服务模板
	 * @param transactionTemplate 事务模板
	 * @return 服务模板
	 */
	@Bean(name = "wdServiceTemplate")
	public WdServiceTemplate wdServiceTemplate(
			@Qualifier("transactionTemplate") TransactionTemplate transactionTemplate) {
		return new WdServiceTemplateImpl(transactionTemplate);
	}
	
	/**
	 * 定义主数据源
	 * @return 主数据源
	 */
	@Bean(name = "dataSource", destroyMethod = "close")
	@Primary
	@ConfigurationProperties(prefix = "datasource.master")
	public DataSource dataSource() {
	    return DataSourceBuilder.create().type(datasourceType).build();
	}
	
	/**
	 * 定义事务管理器
	 * @param dataSource 数据源
	 * @return 事务管理器
	 */
	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
	    return new DataSourceTransactionManager(dataSource);
    }
	
	/**
	 * 定义事务模板
	 * @param transactionManager 事务管理器
	 * @return 事务模板
	 * @throws Exception 异常
	 */
	@Bean(name = "transactionTemplate")
	public TransactionTemplate transactionTemplate(
			@Qualifier("transactionManager") DataSourceTransactionManager transactionManager) 
					throws Exception {
		DefaultTransactionDefinition defaultTransactionDefinition = 
				new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
		defaultTransactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		defaultTransactionDefinition.setTimeout(60); // 秒钟
	    
	    return new TransactionTemplate(transactionManager, defaultTransactionDefinition);
	}
	
	/**
     * +配置主MyBatis SqlSession
     * @param dataSource 主dataSource
     * @param properties properties
     * @param resourceLoader resourceLoader
     * @param configurationCustomizersProvider configurationCustomizersProvider
     * @param interceptorsProvider interceptorsProvider
     * @param pageInterceptor pageInterceptor
     * @param databaseIdProvider databaseIdProvider
     * @param manualManagedTransactionFactory manualManagedTransactionFactory
     * @param languageDriversProvider languageDriversProvider
     * @param applicationContext applicationContext
     * @param paginationInterceptor paginationInterceptor
     * 
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("dataSource") DataSource dataSource,
            @Qualifier("myBatisPlusProperties") MybatisPlusProperties properties,
            ResourceLoader resourceLoader,
            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
            ObjectProvider<Interceptor[]> interceptorsProvider,
            @Qualifier("pageInterceptor") Interceptor pageInterceptor,
            ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            @Qualifier("manualManagedTransactionFactory") @Nullable ManualManagedTransactionFactory manualManagedTransactionFactory, 
            ObjectProvider<LanguageDriver[]> languageDriversProvider,
            ApplicationContext applicationContext,
            @Qualifier("paginationInterceptor") Interceptor paginationInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        
         // 自定义事务处理器
        if (manualManagedTransactionFactory != null) {
			factory.setTransactionFactory(manualManagedTransactionFactory);
		}
        
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(properties.getConfigLocation())) {
          factory.setConfigLocation(resourceLoader.getResource(properties.getConfigLocation()));
        }
        com.baomidou.mybatisplus.core.MybatisConfiguration configuration = properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(properties.getConfigLocation())) {
            configuration = new com.baomidou.mybatisplus.core.MybatisConfiguration();
        }
        List<ConfigurationCustomizer> configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        if (configuration != null && !CollectionUtils.isEmpty(configurationCustomizers)) {
          for (ConfigurationCustomizer customizer : configurationCustomizers) {
            customizer.customize(configuration);
          }
        }
        factory.setConfiguration(configuration);
        if (properties.getConfigurationProperties() != null) {
          factory.setConfigurationProperties(properties.getConfigurationProperties());
        }
        Interceptor[] interceptors = filterInterceptors(interceptorsProvider.getIfAvailable(), 
                pageInterceptor, paginationInterceptor);
        if (!ObjectUtils.isEmpty(interceptors)) {
          factory.setPlugins(interceptors);
        }
        DatabaseIdProvider _databaseIdProvider = databaseIdProvider.getIfAvailable();
        if (_databaseIdProvider != null) {
          factory.setDatabaseIdProvider(_databaseIdProvider);
        }
        if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
          factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
          factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
          factory.setMapperLocations(properties.resolveMapperLocations());
        }
        
        // Mybatis Plus 相关配置
        LanguageDriver[] languageDrivers = languageDriversProvider.getIfAvailable();
        Class<? extends LanguageDriver> defaultLanguageDriver = properties.getDefaultScriptingLanguageDriver();
        if (!ObjectUtils.isEmpty(languageDrivers)) {
            factory.setScriptingLanguageDrivers(languageDrivers);
        }
        Optional.ofNullable(defaultLanguageDriver).ifPresent(factory::setDefaultScriptingLanguageDriver);
        
        if (StringUtils.hasLength(properties.getTypeEnumsPackage())) {
            factory.setTypeEnumsPackage(properties.getTypeEnumsPackage());
        }
        
        // 此处必为非 NULL
        GlobalConfig globalConfig = properties.getGlobalConfig();
        if (applicationContext.getBeanNamesForType(MetaObjectHandler.class, false, false).length > 0) {
            MetaObjectHandler metaObjectHandler = applicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }
        if (applicationContext.getBeanNamesForType(IKeyGenerator.class, false, false).length > 0) {
            IKeyGenerator keyGenerator = applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }
        if (applicationContext.getBeanNamesForType(ISqlInjector.class, false, false).length > 0) {
            ISqlInjector iSqlInjector = applicationContext.getBean(ISqlInjector.class);
            globalConfig.setSqlInjector(iSqlInjector);
        }
        factory.setGlobalConfig(globalConfig);
        
        return factory.getObject();
    }
    
    /**
	 * +自定义事务管理工厂,用以管理事务的生命周期
	 * @return TransactionFactory
	 */
    @ConditionalOnExpression("${spring.transaction.with-strict-flag:true}")
	@Bean(name = "manualManagedTransactionFactory")
	public ManualManagedTransactionFactory manualManagedTransactionFactory() {
		return new ManualManagedTransactionFactory();
	}
    
    /**
     * +过滤PageInterceptor
     * @param interceptors 环境上下文中的Interceptor
     * @param pageInterceptor 分页的Interceptor
     * @param paginationInterceptor MybatisPlus的Interceptor
     * 
     * @return 过滤其他PageInterceptor之后的interceptors
     */
    private Interceptor[] filterInterceptors(Interceptor[] interceptors, Interceptor pageInterceptor, 
            Interceptor paginationInterceptor) throws Exception {
        List<Interceptor> otherInterceptors = new ArrayList<>();
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                if (interceptor instanceof PageInterceptor) {
                    continue;
                }
                otherInterceptors.add(interceptor);
            }
        }
        if (pageInterceptor != null) {
            otherInterceptors.add(pageInterceptor);
        }
        if (paginationInterceptor != null) {
            otherInterceptors.add(paginationInterceptor);
        }
        
        if (!otherInterceptors.isEmpty()) {
            return otherInterceptors.toArray(new Interceptor[otherInterceptors.size()]);
        }
        
        return null;
    }
    
    /**
     * +主Mybatis+MybatisPlus属性配置
     * @return MybatisPlusProperties
     */
    @Primary
    @Bean(name = "myBatisPlusProperties")
    @ConfigurationProperties(prefix = "mybatis.master")
    public MybatisPlusProperties myBatisPlusProperties() {
        return new MybatisPlusProperties();
    }
    
    /**
     * pageHelper属性配置
     * @return Properties
     */
    @Bean(name = "pageHelperProperties")
    @ConfigurationProperties(prefix = "mybatis.master.pagehelper")
    public PageHelperProperties pageHelperProperties() {
        return new PageHelperProperties();
    }
    
    /**
     * page插件配置
     * @param extProperties pageHelper附加属性(closeConn问题)
     * @return Interceptor
     */
    @Bean(name = "pageInterceptor")
    public Interceptor pageInterceptor(@Qualifier("pageHelperProperties") PageHelperProperties extProperties) {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        if (extProperties != null) {
            properties.putAll(extProperties.getProperties());
        }
        interceptor.setProperties(properties);
        
        return interceptor;
    }
    
    /**
     * +Druid防火墙过滤器
     * @return 自定义的WallFilter
     */
    @Bean(name = "wallFilter")
    @ConfigurationProperties(prefix = "druid.filters.wall")
    public WallFilter wallFilter() {
        return new WallFilter();
    }
    
    /**
     * +Mybatis Plus 拦截器
     * @return 自定义的PaginationInterceptor
     */
    @Bean(name = "paginationInterceptor")
    public Interceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
