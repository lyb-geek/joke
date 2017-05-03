package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.User;

/**
 * 
 * <p>
 * Title:UserDao
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
public interface UserDao extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
	Page<User> findAll(Pageable pageable);

	User findByUsernameAndPassword(String username, String password);

	User findByUsername(String username);

	@Query("from User u where u.status = 1 and u.username = ?")
	User findUserByLoginName(String username);

	User findByEmail(String email);

	List<User> findAll();
}
