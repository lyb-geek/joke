package com.gradle.joke.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * <p>
 * Title: Role
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
@Table(name = "t_role")
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -197001282946628628L;
	private Long id;
	private String roleName;

	private String roleCode;

	@JsonIgnore
	private Set<Permission> permissionList;// 一个角色对应多个权限

	@JsonIgnore
	private Set<User> userList;// 一个角色对应多个用户

	private Long operatorId;

	private String createTime;

	private String updateTime;

	public Role(Long id, String roleName) {
		super();
		this.id = id;
		this.roleName = roleName;
	}

	public Role() {
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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@ManyToMany
	@JoinTable(name = "t_permission_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
			@JoinColumn(name = "permission_id") })
	public Set<Permission> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(Set<Permission> permissionList) {
		this.permissionList = permissionList;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_user_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
			@JoinColumn(name = "user_id") })
	public Set<User> getUserList() {
		return userList;
	}

	public void setUserList(Set<User> userList) {
		this.userList = userList;
	}

	@Transient
	public List<String> getPermissionsName() {
		List<String> list = new ArrayList<String>();
		Set<Permission> permissions = getPermissionList();
		for (Permission permission : permissions) {
			list.add(permission.getPermissionCode());
		}
		return list;
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

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

}
