package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.Image;

/**
 * 
 * <p>
 * Title:ImageDao
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
public interface ImageDao extends CrudRepository<Image, Long>, JpaSpecificationExecutor<Image> {
	Page<Image> findAll(Pageable pageable);

	Page<Image> findByisShow(Integer isShow, Pageable pageable);

	List<Image> findAll();
}
