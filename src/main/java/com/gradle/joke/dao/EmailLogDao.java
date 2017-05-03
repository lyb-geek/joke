package com.gradle.joke.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.EmailLog;

/**
 * 
 * <p>
 * Title:EmailLogDao
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
public interface EmailLogDao extends CrudRepository<EmailLog, Long>, JpaSpecificationExecutor<EmailLog> {
	Page<EmailLog> findAll(Pageable pageable);

}
