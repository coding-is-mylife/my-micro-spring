package com.lfw.ioc.annotation;

import com.lfw.ioc.context.BeanDefinition;
import com.lfw.ioc.context.BeanDefinitionReader;
import com.lfw.ioc.exception.FieldMissValueAnnotationException;
import com.lfw.ioc.factory.BeanDefinitionFactory;
import com.lfw.ioc.factory.TypeConverterFromValueFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
 * @Author Zzs
 * @Description
 * @DateTime 2023/9/14 22:32
 */
@SuppressWarnings("unused")
public class AnnotatedBeanDefinitionReader implements BeanDefinitionReader {
	
	@Override
	public List<BeanDefinition> reading (List<Class<?>> toBeReadingCopy) {
		List<BeanDefinition> beanDefinitionList = new ArrayList<>();
		for (Class<?> c : toBeReadingCopy) {
			/* TODO 默认方式是采用注解读，如果拓展的话，选择配置文件的方式，也可以使用XML方式
			 *     在配置文件中查询是否修改了约定(约定大于配置原则)，如果是，则改为其他方式读取*/
//			PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
			try {
				BeanDefinition beanDefinition = BeanDefinitionFactory.getBeanDefinition("annotation");
				/* 接下来将相应的信息存入`bd`
				   在属性中查找被@ZValue和@ZAutowired标识的内容*/
				beanDefinition.setBeanName(getBeanName(c.getSimpleName()));
				beanDefinition.setClass(c);
				Field[] declaredFields = c.getDeclaredFields();
				for (Field f : declaredFields) {
					f.setAccessible(true);
					Object o = initializeField(f);
//					System.out.println(o + o.getClass().toString());
					beanDefinition.addIntoDefinitionMap(f.getName(), o);
				}
				beanDefinitionList.add(beanDefinition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return beanDefinitionList;
	}
	
	private Object initializeField (Field f) throws Exception {
		// value注解和autowired注解一般不同时使用
		Class<?> type = f.getType();
		ZValue zValue = f.getAnnotation(ZValue.class);
		
		if (zValue == null || zValue.value().equals(""))
			throw new FieldMissValueAnnotationException();
		String value = zValue.value();
		// 现在根据字段的类型，将字符串转换成对应类型的对象
		return TypeConverterFromValueFactory.convertStringToType(value, type);
	}
	
	private String getBeanName (String beanName) {
		return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
	}
	
}
