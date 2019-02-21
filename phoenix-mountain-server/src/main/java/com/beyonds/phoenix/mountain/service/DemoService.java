
package com.beyonds.phoenix.mountain.service;

import com.beyonds.phoenix.mountain.core.common.annotation.ProductCode;
import com.beyonds.phoenix.mountain.core.common.annotation.ProductLogLevelEnum;
import com.beyonds.phoenix.mountain.core.common.result.CallResult;
import com.beyonds.phoenix.mountain.core.common.util.PaginationSupport;
import com.beyonds.phoenix.mountain.domain.DemoInsertDomain;
import com.beyonds.phoenix.mountain.domain.DemoQueryDomain;
import com.beyonds.phoenix.mountain.domain.DemoQueryListDomain;
import com.beyonds.phoenix.mountain.model.DemoModel;
import com.beyonds.phoenix.mountain.shackle.ShackleDomain;
import com.beyonds.phoenix.mountain.shackle.ShackleTemplate;

/**
 * 
 * @Author: DanielCao
 * @Date:   2017年5月8日
 * @Time:   下午1:41:07
 *
 */
@ShackleTemplate(name = "demoService", wdServiceTemplateBeanName = "wdServiceTemplate")
public interface DemoService {
	/**
	 * 查询数据
	 * @param id 参数
	 * @return 查询结果
	 */
	@ShackleDomain(
			value = "queryById", 
			domain = DemoQueryDomain.class, 
			domainName = "demoQueryDomain", 
			withTrans = false, 
			wdServiceTemplateBeanName = "wdServiceTemplate")
	@ProductCode(code = "00000", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<DemoModel> queryById(Long id);
	
	/**
	 * 保存数据对象
	 * @param demoModel 数据对象
	 * @return 保存执行结果
	 */
	@ShackleDomain(value = "insertDemo", domain = DemoInsertDomain.class, domainName = "demoInsertDomain", withTrans = true)
	@ProductCode(code = "00000", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<DemoModel> insertDemo(DemoModel demoModel);
	
	/**
	 * 分页查询数据
	 * @param p 分页参数
	 * @param ps 分页参数
	 * @return 查询结果集
	 */
	@ShackleDomain(value = "queryList", domain = DemoQueryListDomain.class, domainName = "demoQueryListDomain", withTrans = false)
	@ProductCode(code = "00000", version = "1.0", logLevel = ProductLogLevelEnum.INFO)
	CallResult<PaginationSupport<DemoModel>> queryList(int p, int ps);
}
