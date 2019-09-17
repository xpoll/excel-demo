package cn.blmdz.excel.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导出Excel对应封装与排序
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportModelDescription {
	private Integer order;
	private String code;
	private String description;
}
