package cn.blmdz.excel.model.resp;

import cn.blmdz.excel.util.ExportDescription;
import lombok.Data;

@Data
public class RespVo {

	@ExportDescription(value="公司名称", order=1)
	private String name;
	@ExportDescription(value="RE", order=2)
	private String re0;
	@ExportDescription(value="ZF", order=3)
	private String zf0;
	@ExportDescription(value="RE", order=4)
	private String re1;
	@ExportDescription(value="ZF", order=5)
	private String zf1;
	@ExportDescription(value="总计", order=6)
	private String all;
}
