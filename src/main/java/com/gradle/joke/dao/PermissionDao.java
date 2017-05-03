package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.Permission;

/**
 * 
 * <p>
 * Title: PermissionDao
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月29日
 */
public interface PermissionDao extends CrudRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
	Page<Permission> findAll(Pageable pageable);

	@Query("from Permission p where p.isParentId = 1")
	List<Permission> findParentPermissions();

	Permission findByPermissionName(String permissionName);

	@Query("select new com.gradle.joke.entity.Permission(p.id ,p.permissionName, p.isParentId ,p.parentId) from Permission p ")
	List<Permission> findPermissions();

}
