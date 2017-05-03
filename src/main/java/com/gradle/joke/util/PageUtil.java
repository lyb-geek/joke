package com.gradle.joke.util;

/**
 * 
 * <p>
 * Title:PageUtil
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月18日
 */
public class PageUtil {

	public static final String PAGE = "page";

	public static final String TOTAL_PAGE = "totalPage";

	public static int getCountpage(int countRecord, int pageSize) {

		if (countRecord == 0 || pageSize == 0) {
			return 0;
		}

		if (countRecord % pageSize == 0) {
			return countRecord / pageSize;
		} else {
			return countRecord / pageSize + 1;
		}

	}

}
