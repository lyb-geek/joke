package com.gradle.joke.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.Article;

/**
 * 
 * <p>
 * Title:ArticleDao
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月10日
 */
public interface ArticleDao extends CrudRepository<Article, Long>, JpaSpecificationExecutor<Article> {
	Page<Article> findAll(Pageable pageable);

	Page<Article> findByisDel(Integer isDel, Pageable pageable);

}
