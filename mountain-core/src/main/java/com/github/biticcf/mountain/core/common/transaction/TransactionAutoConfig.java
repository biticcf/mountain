/**
 * 
 */
package com.github.biticcf.mountain.core.common.transaction;

import java.io.File;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.autoconfigure.transaction.jta.JtaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.jta.atomikos.AtomikosProperties;
import org.springframework.boot.jta.atomikos.AtomikosXADataSourceWrapper;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.github.biticcf.mountain.core.common.service.WdServiceTemplate;
import com.github.biticcf.mountain.core.common.service.WdServiceTemplateImpl;

/**
 * author： Daniel.Cao
 * date:   2020年6月25日
 * time:   上午11:54:45
 * + 事务自动配置
 * + 根据配置文件决定启用本地事务或者分布式事务
 */
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement(proxyTargetClass = false)
public class TransactionAutoConfig {
	/**
	 * +定义服务模板
	 * @param transactionTemplate 事务模板
	 * @return 服务模板
	 */
	@Bean(name = "wdServiceTemplate")
	@Primary
	public WdServiceTemplate wdServiceTemplate(
			@Qualifier("transactionTemplate") TransactionTemplate transactionTemplate) {
		return new WdServiceTemplateImpl(transactionTemplate);
	}

	/**
	 * +定义事务模板
	 * @param transactionManager 事务管理器
	 * @return 事务模板
	 * @throws Exception 异常
	 */
	@Bean(name = "transactionTemplate")
	@Primary
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) throws Exception {
		DefaultTransactionDefinition defaultTransactionDefinition = 
				new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
		defaultTransactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		defaultTransactionDefinition.setTimeout(600); // 秒钟
	    
	    return new TransactionTemplate(transactionManager, defaultTransactionDefinition);
	}
	
	/**
	 * + 本地事务管理器
	 * author： Daniel.Cao
	 * date:   2020年6月25日
	 * time:   下午12:06:05
	 *
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnExpression("${spring.transaction.xa.enabled:false} == false")
	public static class NonXATransactionManager {
		/**
		 * +定义本地事务管理器
		 * @param dataSource 数据源
		 * @return 本地事务管理器
		 */
		@Bean
		@Primary
		public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		    return new DataSourceTransactionManager(dataSource);
	    }
	}
	
	/**
	 * + XA事务管理器
	 * author： Daniel.Cao
	 * date:   2020年6月25日
	 * time:   下午12:06:05
	 *
	 */
	@ConditionalOnExpression("${spring.transaction.xa.enabled:false}")
	@Configuration(proxyBeanMethods = false)
	public static class XATransactionManager {
		/**
		 * + 定义分布式事务管理器
		 * @param userTransaction 事务
		 * @param transactionManager 事务管理器
		 * @param transactionManagerCustomizers 事务定义
		 * @return 分布式事务管理器
		 */
		@Bean(name = "transactionManager")
		@Primary
		public JtaTransactionManager transactionManager(
				UserTransaction userTransaction, 
				TransactionManager transactionManager,
				ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
			transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(jtaTransactionManager));
			
			return jtaTransactionManager;
		}
		
		/**
		 * + 定义UserTransactionService
		 * @param atomikosProperties
		 * @param jtaProperties
		 * @return UserTransactionServiceImp
		 */
		@Bean(initMethod = "init", destroyMethod = "shutdownWait")
		@Primary
		public UserTransactionServiceImp userTransactionService(AtomikosProperties atomikosProperties,
				JtaProperties jtaProperties) {
			Properties properties = new Properties();
			if (StringUtils.hasText(jtaProperties.getTransactionManagerId())) {
				properties.setProperty("com.atomikos.icatch.tm_unique_name", jtaProperties.getTransactionManagerId());
			}
			properties.setProperty("com.atomikos.icatch.log_base_dir", getLogBaseDir(jtaProperties));
			properties.putAll(atomikosProperties.asProperties());
			return new UserTransactionServiceImp(properties);
		}
		
		/**
		 * + jta日志路径
		 * @param jtaProperties
		 * @return 日志路径
		 */
		private String getLogBaseDir(JtaProperties jtaProperties) {
			if (StringUtils.hasLength(jtaProperties.getLogDir())) {
				return jtaProperties.getLogDir();
			}
			File home = new ApplicationHome().getDir();
			
			return new File(home, "transaction-logs").getAbsolutePath();
		}
		
		/**
		 * + 初始化UserTransactionManager
		 * @param userTransactionService
		 * @return UserTransactionManager
		 * 
		 * @throws Exception
		 */
		@Bean(initMethod = "init", destroyMethod = "close")
		@Primary
		public UserTransactionManager atomikosTransactionManager(UserTransactionService userTransactionService) throws Exception {
			UserTransactionManager manager = new UserTransactionManager();
			manager.setStartupTransactionService(false);
			manager.setForceShutdown(true);
			return manager;
		}
		
		/**
		 * + 定义配置
		 * @return AtomikosXADataSourceWrapper
		 */
		@Bean
		@Primary
		AtomikosXADataSourceWrapper xaDataSourceWrapper() {
			return new AtomikosXADataSourceWrapper();
		}

		/**
		 * + 定义AtomikosDependsOnBeanFactoryPostProcessor
		 * @return AtomikosDependsOnBeanFactoryPostProcessor
		 */
		@Bean
		@Primary
		static AtomikosDependsOnBeanFactoryPostProcessor atomikosDependsOnBeanFactoryPostProcessor() {
			return new AtomikosDependsOnBeanFactoryPostProcessor();
		}
		
		/**
		 * + 定义AtomikosProperties
		 * @return AtomikosProperties
		 */
		@Bean
		@ConfigurationProperties(prefix = "transaction.atomikos")
		@Primary
		public AtomikosProperties getAtomikosProperties() {
			return new AtomikosProperties();
		}
		
		/**
		 * + 定义JtaProperties
		 * @return JtaProperties
		 */
		@Bean
		@ConfigurationProperties(prefix = "transaction.jta")
		@Primary
		public JtaProperties getJtaProperties() {
			return new JtaProperties();
		}
	}
}
