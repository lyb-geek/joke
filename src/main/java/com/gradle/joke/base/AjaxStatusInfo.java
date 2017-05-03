package com.gradle.joke.base;

/**
 * 
 * <p>
 * Title:AjaxStatusInfo
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月12日
 */
public class AjaxStatusInfo<T> {
	private int status = GlobalParams.SUCCESS_CODE;
	private String msg = GlobalParams.SUCCESS_MSG;
	private T result;

	public AjaxStatusInfo() {
		super();
	}

	public AjaxStatusInfo(T result) {
		super();
		this.result = result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

}
