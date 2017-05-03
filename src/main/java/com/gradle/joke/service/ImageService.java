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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.dao.ImageDao;
import com.gradle.joke.entity.Image;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

/**
 * 
 * <p>
 * Title: ImageService
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
public class ImageService {
	private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

	@Autowired
	private ImageDao imageDao;

	@Value("${img.upload.path}")
	private String rootImgPath;

	@CacheEvict(value = { "getFirstSixImagesCache" }, allEntries = true)
	public void persist(Image image) throws Exception {
		if (image.getId() == null) {
			image.setCreateTime(DateUtil.getNow());
		}

		image.setUpdateTime(DateUtil.getNow());

		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			image.setOperatorId(operatorId);
		}

		imageDao.save(image);
	}

	public List<Image> findAll() {
		return imageDao.findAll();
	}

	@Cacheable(value = "getFirstSixImagesCache", keyGenerator = "wiselyKeyGenerator")
	public List<Image> getFirstSixImages() {
		logger.info("Image数据来源于数据库而非缓存");
		Pageable pageable = new PageRequest(0, 6, Direction.DESC, "updateTime");

		Page<Image> page = imageDao.findByisShow(GlobalParams.YES, pageable);

		return page.getContent();
	}

	public List<String> getImgPathsForShow() {
		String defaultImgPath = "front/img/default.jpg";
		List<String> imgPaths = Lists.newArrayList();
		List<Image> images = this.getFirstSixImages();
		if (CollectionUtils.isNotEmpty(images)) {
			int index = 0;
			for (Image image : images) {
				switch (index) {
				case 0:
					defaultImgPath = "front/img/default-0.jpg";
					break;
				case 1:
					defaultImgPath = "front/img/default-1.jpg";
					break;
				case 2:
					defaultImgPath = "front/img/default-2.jpg";
					break;

				case 3:
					defaultImgPath = "front/img/default-3.jpg";
					break;
				case 4:
					defaultImgPath = "front/img/default-4.jpg";
					break;
				case 5:
					defaultImgPath = "front/img/default-5.jpg";
					break;

				default:
					break;
				}
				index++;
				String path = rootImgPath + File.separatorChar + image.getImgPath();
				File file = new File(path);
				if (!file.exists()) {
					imgPaths.add(defaultImgPath);
				} else {
					String imagePath = GlobalParams.IMAGE_SHOW_PATH_PREFIX + image.getImgPath();
					imgPaths.add(imagePath);
				}
			}
		}

		return imgPaths;
	}

	public Long getTotalCount() {
		return imageDao.count();

	}

	public Page<Image> getPageList(Pageable pageable) {
		return imageDao.findAll(pageable);
	}

	public Image getImageById(Long id) {
		return imageDao.findOne(id);
	}

	@CacheEvict(value = { "getFirstSixImagesCache" }, allEntries = true)
	public void deleteImageById(Long id) {
		Image image = imageDao.findOne(id);
		if (image != null && !StringUtils.isEmpty(image.getImgPath())) {
			String path = rootImgPath + File.separatorChar + image.getImgPath();
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
		imageDao.delete(id);
	}

	@CacheEvict(value = { "getFirstSixImagesCache" }, allEntries = true)
	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			Image image = imageDao.findOne(id);
			if (image != null && !StringUtils.isEmpty(image.getImgPath())) {
				String path = rootImgPath + File.separatorChar + image.getImgPath();
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
			}
			imageDao.delete(id);
		}
	}

	public PageHelper<Image> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<Image> pageHelper = null;

		Page<Image> page = imageDao.findAll(new Specification<Image>() {

			@Override
			public Predicate toPredicate(Root<Image> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = imageDao.count(new Specification<Image>() {

			@Override
			public Predicate toPredicate(Root<Image> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<Image> root, CriteriaBuilder cb, final SearchForm searchForm) {
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
