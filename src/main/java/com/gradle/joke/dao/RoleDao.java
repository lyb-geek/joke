package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.Role;

/**
 * 
 * <p>
 * Title: RoleDao
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月30日
 */
public interface RoleDao extends CrudRepository<Role, Long>, JpaSpecificationExecutor<Role> {
	Page<Role> findAll(Pageable pageable);

	Role findByRoleCode(String roleCode);

	Role findByRoleName(String roleName);

	@Query("select new com.gradle.joke.entity.Role(r.id ,r.roleName) from Role r ")
	List<Role> findRoles();

}
