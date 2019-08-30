/**
 * MydemoFacade.java
 */
package com.beyonds.phoenix.mountain.facade;


import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.beyonds.phoenix.mountain.core.common.result.ReturnResult;
import com.beyonds.phoenix.mountain.generator.annotation.FacadeConfig;
import com.beyonds.phoenix.mountain.generator.annotation.MethodConfig;
import com.beyonds.phoenix.mountain.model.DemoModel;

/**
 * @Author: DanielCao
 * @Date:   2019-02-27
 * @Time:   09:21:17
 * +Demo接口定义说明
 */

@FacadeConfig(name = "Mydemo", description = "Mydemo接口定义", execGenerator = true, useSwagger = true, genDaoCode = true)
@Validated
@RequestMapping(value = {"/phoenix/mountain/v1"}, produces = {"application/json"})
public interface MydemoFacade {
    /**
     * +方法定义说明
     * @param page 参数定义说明
     * @param name 用户名称
     * 
     * @return 添加说明
     * @throws Exception 添加说明
     * 
     */
	@MethodConfig(name = "queryBy", description = "queryBy方法定义说明", listResultFlag = true, paginationFlag = true, withTransaction = false)
    @RequestMapping(value = {"/demo"}, method = {RequestMethod.GET}, consumes = {"application/json"}, produces = {"application/json"})
    ReturnResult<List<DemoModel>> queryBy(@RequestParam(required = true, value = "page", defaultValue = "0") @Valid Integer page, @RequestParam(required = true, value = "name") String name) throws Exception;
}
