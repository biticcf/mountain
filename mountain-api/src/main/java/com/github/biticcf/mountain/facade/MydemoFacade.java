/**
 * MydemoFacade.java
 */
package com.github.biticcf.mountain.facade;


import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.biticcf.mountain.core.common.result.ReturnResult;
import com.github.biticcf.mountain.generator.annotation.FacadeConfig;
import com.github.biticcf.mountain.generator.annotation.MethodConfig;
import com.github.biticcf.mountain.model.DemoModel;

/**
 * author: DanielCao
 * date:   2019-02-27
 * time:   09:21:17
 * +Demo接口定义说明
 */

@FacadeConfig(name = "Mydemo", description = "Mydemo接口定义", execGenerator = true, useSwagger = true, genDaoCode = true)
@Validated
@RequestMapping(value = {"/mountain/v1"}, produces = {"application/json"})
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
