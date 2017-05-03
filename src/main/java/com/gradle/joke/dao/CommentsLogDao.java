package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.CommentsLog;

/**
 * 
 * <p>
 * Title:CommentsLogDao
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月14日
 */
public interface CommentsLogDao extends CrudRepository<CommentsLog, Long>, JpaSpecificationExecutor<CommentsLog> {

	@Query(value = "from CommentsLog c order by c.createTime desc")
	public List<CommentsLog> findAllOrderByCreateTimeDesc();

	Page<CommentsLog> findAll(Pageable pageable);

}
