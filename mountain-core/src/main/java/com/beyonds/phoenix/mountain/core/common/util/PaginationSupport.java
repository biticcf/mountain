
package com.beyonds.phoenix.mountain.core.common.util;

import java.util.ArrayList;
import java.util.List;

import com.beyonds.phoenix.mountain.core.common.model.WdBaseModel;

/**
 * 
 * author: DanielCao
 * date:   2018年7月20日
 * time:   下午3:11:34
 * 
 * +添加自定义最大分页大小功能
 * 
 * @param <T>
 */
public class PaginationSupport<T> extends WdBaseModel {
	private static final long serialVersionUID = 5630859931214794398L;
	
	public static final int DEFAULT_PAGESIZE = 20; //默认分页大小
	public static final int DEFAULT_MAX_PAGESIZE = 200; //最大分页大小
	private static final int MAX_MAX_PAGESIZE = 2000; //自定义最大分页大小
	
	private int pageSize = DEFAULT_PAGESIZE;
	private int totalCount;
	private int totalPages;
	private List<T> items;
	private int startIndex;
	private int realSize;
	private int currentPage;
	
	private static int maxPageSize = DEFAULT_MAX_PAGESIZE;
	
	public PaginationSupport() {
		this(null, 0);
	}
	public PaginationSupport(List<T> items, int totalCount) {
		this(items, totalCount, DEFAULT_PAGESIZE, 1);
	}
	
	public PaginationSupport(List<T> items, int totalCount, int pageSize, int currentPage) {
		this.items = (items == null ? new ArrayList<>() : items);
		
		if (pageSize <= 0) {
			this.pageSize = DEFAULT_PAGESIZE;
		} else if (pageSize > maxPageSize) {
			this.pageSize = maxPageSize;
		} else {
			this.pageSize = pageSize;
		}
		this.totalCount = (totalCount <= 0 ? 0 : totalCount);
		
		if (currentPage <= 0) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}
		
		this.startIndex = (this.currentPage - 1) * this.pageSize;
		
		this.totalPages = ((this.totalCount % this.pageSize == 0) 
				? (this.totalCount / this.pageSize) : (this.totalCount / this.pageSize + 1));
		
		this.realSize = this.items.size();
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public List<T> getItems() {
		return items;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getRealSize() {
		return realSize;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public static int getMaxPageSize() {
		return maxPageSize;
	}
	
	/**
	 * 自定义最大分页大小
	 * @param maxPageSize 输入参数
	 */
	public static void setMaxPageSize(int maxPageSize) {
		if (maxPageSize <= 0) {
			PaginationSupport.maxPageSize = DEFAULT_PAGESIZE;
		} else if (maxPageSize > MAX_MAX_PAGESIZE) {
			PaginationSupport.maxPageSize = MAX_MAX_PAGESIZE;
		} else {
			PaginationSupport.maxPageSize = maxPageSize;
		}
	}
}
