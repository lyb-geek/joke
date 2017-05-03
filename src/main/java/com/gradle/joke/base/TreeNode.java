package com.gradle.joke.base;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * <p>
 * Title: TreeNodes
 * </p>
 * <p>
 * Description:ztree bean
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月30日
 */
public class TreeNode {
	private Long id;
	private Long pId;
	private String name;
	private boolean open;
	private boolean checked;
	private boolean isParent = false;// 只有父节点没有子节点则可以设置为true

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getpId() {
		return pId;
	}

	public void setpId(Long pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@JsonProperty(value = "isParent")
	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

}
