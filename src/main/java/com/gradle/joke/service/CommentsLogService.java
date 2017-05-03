package com.gradle.joke.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.dao.CommentsLogDao;
import com.gradle.joke.entity.CommentsLog;
import com.gradle.joke.util.DateUtil;

@Service
@Transactional
public class CommentsLogService {
	@Autowired
	private CommentsLogDao commentsLogDao;

	public void save(CommentsLog commentsLog) throws Exception {
		commentsLog.setCreateTime(DateUtil.getNow());
		commentsLogDao.save(commentsLog);
	}

	public List<CommentsLog> getCommentsLogList() {
		return commentsLogDao.findAllOrderByCreateTimeDesc();
	}

	public Long getTotalCount() {
		return commentsLogDao.count();

	}

	public Page<CommentsLog> getPageList(Pageable pageable) {
		return commentsLogDao.findAll(pageable);
	}

	public CommentsLog getCommentsLogById(Long id) {
		return commentsLogDao.findOne(id);
	}

	public void deleteCommentsLogById(Long id) {
		commentsLogDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			commentsLogDao.delete(id);
		}
	}

	public PageHelper<CommentsLog> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<CommentsLog> pageHelper = null;

		Page<CommentsLog> page = commentsLogDao.findAll(new Specification<CommentsLog>() {

			@Override
			public Predicate toPredicate(Root<CommentsLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = commentsLogDao.count(new Specification<CommentsLog>() {

			@Override
			public Predicate toPredicate(Root<CommentsLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<CommentsLog> root, CriteriaBuilder cb, final SearchForm searchForm) {
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
			list.add(cb.like(root.get("comment").as(String.class), "%" + searchForm.getKeyword() + "%"));
		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

}
