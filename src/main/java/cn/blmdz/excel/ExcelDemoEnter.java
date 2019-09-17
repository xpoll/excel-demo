package cn.blmdz.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.blmdz.excel.model.entity.ModelEntity;
import cn.blmdz.excel.model.resp.RespVo;
import cn.blmdz.excel.model.vo.GroupVo;
import cn.blmdz.excel.util.ExcelExportUtil;
import cn.blmdz.excel.util.ExcelPoi;
import cn.blmdz.excel.util.ExcelPoi.ColumnDetail;
import cn.blmdz.excel.util.ExcelPoi.ExcelModel;
import cn.blmdz.excel.util.ExcelPoi.ExcelType;
import cn.blmdz.excel.util.JsonMapper;

public class ExcelDemoEnter {
	public static final String[] formats = { "yyyy-MM-dd hh:mm:ss" };

	public static final String[][] times = { { "2018-02-15 00:00:00", "2019-02-03 23:59:59" },
			{ "2019-02-04 00:00:00", "2020-01-15 23:59:59" }, };

	public static void main(String[] args) throws Exception {

		Long start = System.currentTimeMillis();
		String filePath = "C:/Users/yongzongyang/Desktop/excel.xlsx";
		String outPath = "C:/Users/yongzongyang/Desktop/";
		
		System.out.println("读取文件: " + filePath);

		// 读取Excel
		List<List<String>> result = ExcelPoi.excelRead(filePath, ExcelType.XSSFWorkbook, 1, 0);

		final List<ModelEntity> models = Lists.newArrayList();
		System.out.println("读取内容条数: " + result.size());

		// 读取数据封装
		result.forEach(item -> {
			if (item.size() >= 8 && StringUtils.isNotBlank(item.get(1)) && StringUtils.isNotBlank(item.get(3))
					&& StringUtils.isNotBlank(item.get(4)) && StringUtils.isNotBlank(item.get(7))) {
				for (int i = 0; i < times.length; i++) {
					if (item.get(4).compareTo(times[i][0]) >= 0 && times[i][1].compareTo(item.get(4)) >= 0) {
						models.add(new ModelEntity(item.get(1), item.get(3), String.valueOf(i),
								new BigDecimal(item.get(7))));
					}
				}
			}
		});
		System.out.println("可用内容条数: " + models.size());
//		models.forEach(System.out::println);

		// 分类
		Map<String, List<GroupVo>> collectMaps = models.stream().map(item -> {
			return new GroupVo(Joiner.on("_").join(item.getName(), item.getType(), item.getDate()), item.getAmount());
		}).collect(Collectors.groupingBy(GroupVo::getGroup));

		// 统计汇总
		List<GroupVo> res = collectMaps.values().stream().map(item -> {
			GroupVo vo = new GroupVo(item.get(0).getGroup(), BigDecimal.ZERO);
			item.forEach(abc -> {
				vo.setAmount(abc.getAmount().add(vo.getAmount()));
			});
			return vo;
		}).collect(Collectors.toList());
		// 排序
		res.sort((a, b) -> a.getGroup().compareTo(b.getGroup()));

		Map<String, RespVo> respMap = Maps.newTreeMap();

		// 返回封装
		res.forEach(item -> {
			List<String> group = Splitter.on("_").omitEmptyStrings().trimResults().splitToList(item.getGroup());
			RespVo resp = respMap.get(group.get(0));
			if (resp == null)
				resp = new RespVo();
			resp.setName(group.get(0));
			if (group.get(2).equals(String.valueOf(0))) {
				if (group.get(1).equalsIgnoreCase("RE")) {
					resp.setRe0(item.getAmount().toString());
				} else if (group.get(1).equalsIgnoreCase("ZF"))
					resp.setZf0(item.getAmount().toString());
				{
				}
			} else if (group.get(2).equals(String.valueOf(1))) {
				if (group.get(1).equalsIgnoreCase("RE")) {
					resp.setRe1(item.getAmount().toString());
				} else if (group.get(1).equalsIgnoreCase("ZF"))
					resp.setZf1(item.getAmount().toString());
				{
				}
			}
			respMap.put(resp.getName(), resp);
		});
		// 返回汇总
		respMap.values().forEach(item -> {
			BigDecimal amt = BigDecimal.ZERO
					.add(new BigDecimal(StringUtils.isBlank(item.getRe0()) ? "0" : item.getRe0()))
					.add(new BigDecimal(StringUtils.isBlank(item.getRe1()) ? "0" : item.getRe1()))
					.add(new BigDecimal(StringUtils.isBlank(item.getZf0()) ? "0"
							: item.getZf0()))
					.add(new BigDecimal(StringUtils.isBlank(item.getZf1()) ? "0"
							: item.getZf1()));
			item.setAll(amt.toString());
		});
		// 封装输出
		List<Map<String, String>> maps = respMap.values().stream().map(source -> {
			String dd = JsonMapper.nonEmptyMapper().toJson(source);
			Map<String, String> map = JsonMapper.nonEmptyMapper().fromJson(dd,
					JsonMapper.nonEmptyMapper().createCollectionType(Map.class, String.class, String.class));
			return map;
		}).collect(Collectors.toList());
		ColumnDetail ColumnDetail = ExcelExportUtil.getColumnDetail(RespVo.class);
		ExcelModel model = new ExcelModel();
		model.setAppend(true);
		model.setExcelType(ExcelType.XSSFWorkbook);
		model.setColumnCode(ColumnDetail.getColumnCode());
		model.setColumnDescription(ColumnDetail.getColumnDescription());
		model.setData(maps);
		File file = new File(outPath + "/out_" + System.currentTimeMillis() + ".xlsx");
		FileOutputStream fos = new FileOutputStream(file);
		model.setStream(fos);
		ExcelPoi.excelCreate(model);

		System.out.println("输出文件: " + file.getPath());
		System.out.println("耗时: " + (System.currentTimeMillis() - start) + "ms");
	}
}
