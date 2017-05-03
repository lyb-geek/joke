package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.PermissionConfig;

/**
 * 
 * <p>
 * Title: PermissionConfigDao
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
public interface PermissionConfigDao
		extends CrudRepository<PermissionConfig, Long>, JpaSpecificationExecutor<PermissionConfig> {
	Page<PermissionConfig> findAll(Pageable pageable);

	List<PermissionConfig> findAll();
}
