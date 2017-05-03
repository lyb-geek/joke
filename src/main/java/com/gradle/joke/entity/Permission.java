package com.gradle.joke.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * 
 * <p>
 * Title: Permission
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月26日
 */
@Entity
@Table(name = "t_permission")
public class Permission implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2953778818589792888L;
	private Long id;
	private String permissionName;
	private String permissionCode;

	private Integer isParentId;
	private Long parentId;

	private Set<Role> roleList;// 一个权限对应多个角色

	private Long operatorId;

	private String createTime;

	private String updateTime;

	public Permission(Long id, String permissionName, Integer isParentId, Long parentId) {
		super();
		this.id = id;
		this.permissionName = permissionName;
		this.isParentId = isParentId;
		this.parentId = parentId;
	}

	public Permission() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_permission_role", joinColumns = { @JoinColumn(name = "permission_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	public Set<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(Set<Role> roleList) {
		this.roleList = roleList;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public Integer getIsParentId() {
		return isParentId;
	}

	public void setIsParentId(Integer isParentId) {
		this.isParentId = isParentId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
