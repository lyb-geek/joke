package com.gradle.joke.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Lists;
import com.gradle.joke.base.AjaxStatusInfo;
import com.gradle.joke.base.GlobalParams;

/**
 * 
 * <p>
 * Title:FileController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月19日
 */
@Controller
@RequestMapping(value = "/file/")
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Value("${file.upload.path}")
	private String rootFilePath;

	@Value("${img.upload.path}")
	private String rootImgPath;

	@Value("${audio.support.type}")
	private String audioSupportType;

	@RequestMapping("upload/{isImg}")
	@ResponseBody
	public AjaxStatusInfo<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@PathVariable Integer isImg) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();
		if (!file.isEmpty()) {
			try {

				if (GlobalParams.NO == isImg) {
					String suffix = file.getOriginalFilename()
							.substring(file.getOriginalFilename().lastIndexOf(".") + 1);
					if (!audioSupportType.contains(suffix.toUpperCase())) {
						ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
						ajaxStatusInfo.setMsg("文件仅支持" + audioSupportType + "格式");
						return ajaxStatusInfo;
					}
				}

				String path = isImg == GlobalParams.YES ? rootImgPath : rootFilePath;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String fileName = new Date().getTime() + GlobalParams.STR_SPLIT + file.getOriginalFilename();
				path = path + File.separatorChar + fileName;
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(path)));
				out.write(file.getBytes());
				out.flush();
				out.close();
				ajaxStatusInfo.setResult(fileName);
			} catch (Exception e) {
				logger.error("文件上传失败：" + e.getMessage(), e);
				ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
				ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
			}
		} else {
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	/**
	 * 多文件具体上传时间，主要是使用了MultipartHttpServletRequest和MultipartFile
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "batch/upload/{isImg}", method = RequestMethod.POST)
	@ResponseBody
	public AjaxStatusInfo<List<String>> handleMultFileUpload(HttpServletRequest request, @PathVariable Integer isImg) {
		AjaxStatusInfo<List<String>> ajaxStatusInfo = new AjaxStatusInfo<>();
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		MultipartFile file = null;
		BufferedOutputStream stream = null;
		List<String> list = Lists.newArrayList();
		for (int i = 0; i < files.size(); ++i) {
			file = files.get(i);
			if (!file.isEmpty()) {
				try {
					byte[] bytes = file.getBytes();

					String path = isImg == GlobalParams.YES ? rootImgPath : rootFilePath;
					File dir = new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					String fileName = new Date().getTime() + GlobalParams.STR_SPLIT + file.getOriginalFilename();
					path = path + File.separatorChar + fileName;
					stream = new BufferedOutputStream(new FileOutputStream(new File(path)));
					stream.write(bytes);
					stream.close();
					list.add(fileName);
				} catch (Exception e) {
					logger.error("文件上传失败：" + e.getMessage(), e);
					stream = null;
					ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
					ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
					break;
				}
			} else {
				ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
				ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
			}
			ajaxStatusInfo.setResult(list);
		}
		return ajaxStatusInfo;
	}

	@RequestMapping(value = "/download/{isImg}")
	public ResponseEntity<byte[]> download(@RequestParam("fileName") String fileName, @PathVariable Integer isImg)
			throws IOException {
		String path = isImg == GlobalParams.YES ? rootImgPath : rootFilePath;
		path = path + File.separatorChar + fileName;
		File file = new File(path);
		HttpHeaders headers = new HttpHeaders();
		fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/audio")
	public String audio(@RequestParam("fileName") String fileName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String range = request.getHeader("Range");
		String[] rs = range.split("\\=");
		range = rs[1].split("\\-")[0];
		String path = rootFilePath + File.separatorChar + fileName;
		File file = new File(path);
		if (file.exists()) {
			OutputStream os = response.getOutputStream();
			FileInputStream fis = new FileInputStream(file);
			long length = file.length();
			int irange = Integer.parseInt(range);
			length = length - irange;

			// // 播放进度
			// int count = 0;
			// // 播放百分比
			// int percent = (int)(length * 0.4);

			response.addHeader("Accept-Ranges", "bytes");
			response.addHeader("Content-Length", length + "");
			response.addHeader("Content-Range", "bytes " + range + "-" + length + "/" + length);
			response.addHeader("Content-Type", "audio/mpeg;charset=UTF-8");

			int len = 0;
			byte[] b = new byte[1024];
			while ((len = fis.read(b)) != -1) {
				os.write(b, 0, len);
				// count += len;
				// if(count >= percent){
				// break;
				// }
			}

			fis.close();
			os.close();
		}

		return null;
	}

}
