package com.gradle.joke.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gradle.joke.entity.Audio;

/**
 * 
 * <p>
 * Title:VideoDao
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
public interface AudioDao extends CrudRepository<Audio, Long>, JpaSpecificationExecutor<Audio> {
	Page<Audio> findAll(Pageable pageable);

	@Query("from Audio a where a.isPlay = 1 order by updateTime desc")
	List<Audio> findPlayAudios();

	List<Audio> findAll();
}
