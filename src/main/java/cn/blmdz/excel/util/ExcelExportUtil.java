package cn.blmdz.excel.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.blmdz.excel.util.ExcelPoi.ColumnDetail;

public class ExcelExportUtil {

	public static Map<String, ColumnDetail> COLUMN_DETAILS = Maps.newConcurrentMap();
	
	public static ColumnDetail getColumnDetail(Class<?> clazz) {
		if (COLUMN_DETAILS.containsKey(clazz.getName())) return COLUMN_DETAILS.get(clazz.getName());
		
        List<ExportModelDescription> models = Lists.newArrayList();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
        	ExportDescription export = field.getAnnotation(ExportDescription.class);
        	if (export != null) {
        		models.add(new ExportModelDescription(export.order(), field.getName(), export.value()));
        	}
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods){
        	ExportDescription export = method.getAnnotation(ExportDescription.class);
        	if (export != null) {
        		String name = method.getName().replace("get", "");
        		name = (new StringBuilder()).append(Character.toLowerCase(name.charAt(0))).append(name.substring(1)).toString();
        		models.add(new ExportModelDescription(export.order(), name, export.value()));
        	}
        }
        models.sort((a, b) -> a.getOrder().compareTo(b.getOrder()));
    
        List<String> codes = Lists.newArrayList();
        List<String> descs = Lists.newArrayList();
        for (ExportModelDescription model : models) {
        	codes.add(model.getCode());
        	descs.add(model.getDescription());
		}
        COLUMN_DETAILS.put(clazz.getName(), new ColumnDetail(codes, descs));
        return COLUMN_DETAILS.get(clazz.getName());
	}
}
