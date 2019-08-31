/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result;

import com.beyonds.phoenix.mountain.core.common.model.WdBaseModel;

/**
 * +分页信息
 * author: DanielCao
 * date:   2018年7月24日
 * time:   下午2:44:38
 * +用于结果返回
 */
public class PaginationMeta extends WdBaseModel {
	private static final long serialVersionUID = -2260013026657548176L;
	
	public static final int DEFAULT_PAGESIZE = 20; //默认分页大小
	
	private int pageSize = DEFAULT_PAGESIZE; //分页大小
	private int totalCount; //记录总条数
	private int totalPages; //记录总分页数
	private int startIndex; //当前页起始索引
	private int realSize; //当前页实际记录条数(保留字段)
	private int currentPage; //当前页码
	
	public PaginationMeta() {
		this(0);
	}
	
	public PaginationMeta(int totalCount) {
		this(totalCount, DEFAULT_PAGESIZE, 1);
	}
	
	public PaginationMeta(int totalCount, int pageSize, int currentPage) {
		if (pageSize <= 0) {
			this.pageSize = DEFAULT_PAGESIZE;
		} else {
			this.pageSize = pageSize;
		}
		
		if (totalCount < 0) {
			this.totalCount = 0;
		} else {
			this.totalCount = totalCount;
		}
		
		if (currentPage < 1) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}
		
		this.startIndex = (this.currentPage - 1) * this.pageSize;
		
		this.totalPages = (totalCount % this.pageSize == 0) 
				? (totalCount / this.pageSize) : (totalCount / this.pageSize + 1);
		
		this.realSize = -1;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getRealSize() {
		return realSize;
	}

	public void setRealSize(int realSize) {
		this.realSize = realSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
