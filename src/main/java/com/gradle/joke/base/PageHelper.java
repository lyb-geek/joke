package com.gradle.joke.base;

import java.util.List;

/**
 * 
 * <p>
 * Title:PageHelper
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月18日
 */
public class PageHelper<T> {

	private int countPage = 0;

	private int totalRecord;

	private int pageSize = GlobalParams.PAGE_SIZE;

	private List<T> result;

	public PageHelper() {
	}

	public PageHelper(int totalRecord, int pageSize) {
		this.totalRecord = totalRecord;
		this.pageSize = pageSize;
		this.countPage = (totalRecord + pageSize - 1) / pageSize;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

}
