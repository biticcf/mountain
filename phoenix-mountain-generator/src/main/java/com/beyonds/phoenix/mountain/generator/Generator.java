/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月13日
 * @Time:   上午11:59:36
 *
 */
interface Generator {
	/**
	 * +根据xml定义生成文件元数据
	 * @param project xml接口定义
	 * @param metaList 参照的文件元数据,可以空
	 * @param type 1-支持Spring-WebMvc,2-支持Spring-WebFlux
	 * 
	 * @return 生成对应文件的元文件
	 * @throws Exception 解析异常
	 */
	List<FileMeta> generatorFileMeta(Project project, List<FileMeta> metaList, Integer type) throws Exception;
}
