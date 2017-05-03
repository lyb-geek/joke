package com.gradle.joke.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.gradle.joke.dao.RoleDao;
import com.gradle.joke.dao.UserDao;
import com.gradle.joke.entity.Role;
import com.gradle.joke.entity.User;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

/**
 * 
 * <p>
 * Title:UserService
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
@Service
@Transactional
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	public void persist(User user) {
		if (user.getId() == null) {
			user.setCreateTime(DateUtil.getNow());
		}
		user.setUpdateTime(DateUtil.getNow());
		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			user.setOperatorId(operatorId);
		}
		userDao.save(user);
	}

	public List<User> findAll() {

		return userDao.findAll();
	}

	public void userRoleBind(Long userId, Long[] roleIds) {
		User user = userDao.findOne(userId);
		if (roleIds != null && roleIds.length > 0) {
			Set<Role> roles = new HashSet<>();
			for (Long roleId : roleIds) {
				Role role = roleDao.findOne(roleId);
				if (role != null) {
					roles.add(role);
				}
			}

			if (user != null && !roles.isEmpty()) {
				user.setRoleList(roles);
				this.persist(user);
			}
		}

	}

	public boolean isExistUser(String username, String password) {

		return userDao.findByUsernameAndPassword(username, password) == null;
	}

	public Long getTotalCount() {
		return userDao.count();

	}

	public Page<User> getPageList(Pageable pageable) {
		return userDao.findAll(pageable);
	}

	public User getUserById(Long id) {
		return userDao.findOne(id);
	}

	public void deleteUserById(Long id) {
		userDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			userDao.delete(id);
		}
	}

	public PageHelper<User> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<User> pageHelper = null;

		Page<User> page = userDao.findAll(new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = userDao.count(new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<User> root, CriteriaBuilder cb, final SearchForm searchForm) {
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
			Predicate usernamePredicate = cb.like(root.get("username").as(String.class),
					"%" + searchForm.getKeyword() + "%");
			Predicate emailPredicate = cb.like(root.get("email").as(String.class), "%" + searchForm.getKeyword() + "%");
			Predicate mobilePredicate = cb.like(root.get("mobile").as(String.class),
					"%" + searchForm.getKeyword() + "%");
			list.add(cb.or(usernamePredicate, emailPredicate, mobilePredicate));

		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

	public User getUserByUsername(String username) {
		// TODO Auto-generated method stub
		return userDao.findByUsername(username);
	}

	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return userDao.findByEmail(email);
	}

	public User findUserByLoginName(String username) {
		// TODO Auto-generated method stub
		return userDao.findUserByLoginName(username);
	}

	public void changePwd(Long userId, String newpassword) {
		User user = userDao.findOne(userId);
		if (user != null) {
			user.setPassword(newpassword);
			this.persist(user);
		}

	}

}
