package com.gradle.joke.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gradle.joke.base.AjaxStatusInfo;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.entity.Audio;
import com.gradle.joke.service.AudioService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title:VideoController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月15日
 */
@Controller
@RequestMapping(value = "/audio/")
public class AudioController {
	private static final Logger logger = LoggerFactory.getLogger(AudioController.class);

	@Autowired
	private AudioService audioSvc;

	@RequestMapping(value = "add")
	public String add(Model model) {
		model.addAttribute("persist", "persist");
		return "admin/audio/add";
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(Audio audio) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			audioSvc.persist(audio);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "update/{id}")
	public String update(Model model, @PathVariable Long id) {

		Audio audio = audioSvc.getAudioById(id);

		model.addAttribute("audio", audio);

		model.addAttribute("update", "update");

		model.addAttribute("persist", "persist");

		return "admin/audio/add";

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		Audio audio = audioSvc.getAudioById(id);

		model.addAttribute("audio", audio);

		model.addAttribute("view", "view");

		return "admin/audio/add";

	}

	@RequestMapping(value = "list")
	public String list(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<Audio> page = audioSvc.getPageList(pageable);
		Long totalCount = audioSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/audio/list";
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<Audio>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<Audio> page = audioSvc.getPages(pageNo, pageSize, searchForm);

		List<Audio> result = page.getResult();
		AjaxStatusInfo<List<Audio>> ajaxStatusInfo = new AjaxStatusInfo<List<Audio>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<Audio>> search(SearchForm searchForm) {
		PageHelper<Audio> page = audioSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, searchForm);

		AjaxStatusInfo<PageHelper<Audio>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<Audio>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			audioSvc.deleteAudioById(id);
		} catch (Exception e) {
			logger.error("delete fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "batchDel")
	@ResponseBody
	public AjaxStatusInfo<String> batchDel(@RequestParam(value = "ids[]") Long[] ids) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			audioSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
