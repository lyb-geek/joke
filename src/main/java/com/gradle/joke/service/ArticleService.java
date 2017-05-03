package com.gradle.joke.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import com.gradle.joke.dao.ArticleDao;
import com.gradle.joke.entity.Article;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

/**
 * 
 * <p>
 * Title:ArticleService
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月16日
 */
@Service
@Transactional
public class ArticleService {
	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Autowired
	private ArticleDao articleDao;

	@CacheEvict(value = { "getFirstTwoArticle" }, allEntries = true)
	public void persist(Article article) throws Exception {
		if (article.getId() == null) {
			article.setCreateTime(DateUtil.getNow());
			article.setIsDel(GlobalParams.NO);
		}

		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			article.setOperatorId(operatorId);
		}
		article.setUpdateTime(DateUtil.getNow());

		articleDao.save(article);
	}

	@Cacheable(value = "getFirstTwoArticle", keyGenerator = "wiselyKeyGenerator")
	public List<Article> getFirstTwoArticle() {
		logger.info("Image数据来源于数据库而非缓存");
		Pageable pageable = new PageRequest(0, 2, Direction.DESC, "updateTime");

		Page<Article> page = articleDao.findByisDel(GlobalParams.NO, pageable);

		return page.getContent();
	}

	public Long getTotalCount() {
		return articleDao.count();

	}

	public Page<Article> getPageList(Pageable pageable) {
		return articleDao.findAll(pageable);
	}

	public Article getArtcleById(Long id) {
		return articleDao.findOne(id);
	}

	@CacheEvict(value = { "getFirstTwoArticle" }, allEntries = true)
	public void deleteArtcleById(Long id) {
		articleDao.delete(id);
	}

	@CacheEvict(value = { "getFirstTwoArticle" }, allEntries = true)
	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			articleDao.delete(id);
		}
	}

	public PageHelper<Article> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "updateTime");
		PageHelper<Article> pageHelper = null;

		Page<Article> page = articleDao.findAll(new Specification<Article>() {

			@Override
			public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = articleDao.count(new Specification<Article>() {

			@Override
			public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<Article> root, CriteriaBuilder cb, final SearchForm searchForm) {
		List<Predicate> list = new ArrayList<Predicate>();
		if (StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.greaterThanOrEqualTo(root.get("updateTime").as(String.class), searchForm.getStartDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.between(root.get("updateTime").as(String.class), searchForm.getStartDate(),
					searchForm.getEndDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.lessThanOrEqualTo(root.get("updateTime").as(String.class), searchForm.getEndDate()));
		}

		if (!StringUtils.isEmpty(searchForm.getKeyword())) {
			list.add(cb.like(root.get("title").as(String.class), "%" + searchForm.getKeyword() + "%"));
		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

}
