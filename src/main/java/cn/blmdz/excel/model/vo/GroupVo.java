package cn.blmdz.excel.model.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupVo {
	/** 排序key */
	private String group;
	/** 值 */
	private BigDecimal amount;
}
