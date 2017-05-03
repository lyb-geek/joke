package com.gradle.joke.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 权限配置信息表
 * <p>
 * Title: PermissionConfig
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
@Table(name = "t_permission_config")
public class PermissionConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4072270238032415813L;

	private Long id;

	private String url;

	private String permissionPattern;// 可以访问的url的权限格式，用逗号隔开，形如authc,perms[user:edit]

	private Long operatorId;

	private String createTime;

	private String updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPermissionPattern() {
		return permissionPattern;
	}

	public void setPermissionPattern(String permissionPattern) {
		this.permissionPattern = permissionPattern;
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
