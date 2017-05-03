package com.gradle.joke.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.dao.AudioDao;
import com.gradle.joke.entity.Audio;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

/**
 * 
 * <p>
 * Title: VideoService
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月23日
 */
@Service
@Transactional
public class AudioService {

	@Autowired
	private AudioDao audioDao;

	@Value("${file.upload.path}")
	private String rootFilePath;

	public void persist(Audio audio) throws Exception {
		if (audio.getId() == null) {
			audio.setCreateTime(DateUtil.getNow());
		}

		audio.setUpdateTime(DateUtil.getNow());

		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			audio.setOperatorId(operatorId);
		}

		audioDao.save(audio);
	}

	public Long getTotalCount() {
		return audioDao.count();

	}

	public List<Audio> findAll() {
		return audioDao.findAll();
	}

	public String getAudioPath() {
		List<Audio> list = audioDao.findPlayAudios();
		String audioPath = "front/audio/default.mp3";
		if (!CollectionUtils.isEmpty(list)) {
			Audio audio = list.get(0);
			audioPath = audio.getAudioPath();
			String path = rootFilePath + File.separatorChar + audio.getAudioPath();
			File file = new File(path);
			if (!file.exists()) {
				audioPath = "front/audio/default.mp3";
			} else {
				audioPath = GlobalParams.AUDIO_PLAY_PATH_PREFIX + audio.getAudioPath();
			}
		}

		return audioPath;
	}

	public Page<Audio> getPageList(Pageable pageable) {
		return audioDao.findAll(pageable);
	}

	public Audio getAudioById(Long id) {
		return audioDao.findOne(id);
	}

	public void deleteAudioById(Long id) {
		Audio audio = audioDao.findOne(id);
		if (audio != null && !StringUtils.isEmpty(audio.getAudioPath())) {
			String path = rootFilePath + File.separatorChar + audio.getAudioPath();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
		audioDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			deleteAudioById(id);
		}
	}

	public PageHelper<Audio> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<Audio> pageHelper = null;

		Page<Audio> page = audioDao.findAll(new Specification<Audio>() {

			@Override
			public Predicate toPredicate(Root<Audio> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = audioDao.count(new Specification<Audio>() {

			@Override
			public Predicate toPredicate(Root<Audio> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<Audio> root, CriteriaBuilder cb, final SearchForm searchForm) {
		List<Predicate> list = new ArrayList<Predicate>();
		if (StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), searchForm.getStartDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.between(root.get("createTime").as(String.class), searchForm.getStartDate(),
					searchForm.getEndDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), searchForm.getEndDate()));
		}

		if (!StringUtils.isEmpty(searchForm.getKeyword())) {
			list.add(cb.like(root.get("title").as(String.class), "%" + searchForm.getKeyword() + "%"));
		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

}
