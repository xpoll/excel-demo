package cn.blmdz.excel.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 里面不能用 带 Bean 的 @JsonSerialize
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportDescription {

	/**
	 * Excel 展示的列名
	 */
	String value();
	
	/**
	 * Excel 列顺序
	 * 初始开发最好10的倍数, 以便后面修改
	 */
	int order();
}
