package cn.blmdz.excel.model.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelEntity {
	/** 名称 */
	private String name;
	/** 类型 */
	private String type;
	/** 日期 */
	private String date;
	/** 金额 */
	private BigDecimal amount;
}
