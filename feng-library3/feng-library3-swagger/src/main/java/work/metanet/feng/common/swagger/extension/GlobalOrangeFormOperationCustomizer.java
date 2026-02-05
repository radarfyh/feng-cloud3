package work.metanet.feng.common.swagger.extension;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Component
public class GlobalOrangeFormOperationCustomizer implements GlobalOperationCustomizer {

    /**
     * 注解包路径名称
     */
    private final static String CUSTOM_ANNOTATION_NAME="com.xiaominfo.knife4j.demo.config.MyRequestBody";
    final static String REF_KEY="$ref";
    final static String REF_SCHEMA_PREFIX="#/components/schemas/";
    private final Map<Class<?>, Set<String>> cacheClassProperties = new HashMap<>();
    /**
     * 扩展前缀
     */
    final static String EXTENSION_ORANGE_FORM_NAME="x-orangeforms";
    final static String EXTENSION_ORANGE_FORM_IGNORE_NAME="x-orangeforms-ignore-parameters";

    /**
     * 校验判断当前请求方法是否包含{@link MyRequestBody}注解
     * @param method 接口method
     * @return
     */
    private boolean hasBodyAnnotation(Method method){
        if (method.getParameterCount()>0){
            //必须包含参数
            Annotation[][] annotations = method.getParameterAnnotations();
            if (annotations!=null&&annotations.length>0){
                for (Annotation[] paramAnnotations:annotations){
                    if (paramAnnotations!=null&&paramAnnotations.length>0){
                        long count=Stream.of(paramAnnotations).filter(annotation -> annotation.annotationType().getName().equalsIgnoreCase(CUSTOM_ANNOTATION_NAME)).count();
                        if (count>0){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        log.info("operation.");
        if (handlerMethod.getMethod().getParameterCount()>0){
            Parameter[] parameters = handlerMethod.getMethod().getParameters();
            if (parameters!=null&&parameters.length>0){
                Map<String,Object> properties=new HashMap<>();
                Map<String,Object> extensions=new HashMap<>();
                Set<String> fieldNames=new HashSet<>();
                List<String> required=new ArrayList<>();
                Map<String,io.swagger.v3.oas.annotations.Parameter> map=getParameterDescription(handlerMethod.getMethod());
                for (Parameter parameter:parameters){
                    Annotation[] annotations = parameter.getAnnotations();
                    if (annotations!=null&&annotations.length>0){
                        // 校验判断当前请求方法是否包含{@link com.xiaominfo.knife4j.demo.config.TestBody}注解
                        long count=Stream.of(annotations).filter(annotation -> annotation.annotationType().getName().equalsIgnoreCase(CUSTOM_ANNOTATION_NAME)).count();
                        if (count>0){
                            Class<?> parameterType=parameter.getType();
                            String schemaName=parameterType.getSimpleName();
                            //添加忽律参数名称
                            fieldNames.addAll(getClassFields(parameterType));
                            //处理schema注解别名的情况
                            Schema schema = parameterType.getAnnotation(Schema.class);
                            if (schema!=null&& StrUtil.isNotBlank(schema.name())){
                                schemaName=schema.name();
                            }
                            Map<String,Object> value=new HashMap<>();
                            //此处需要判断parameter的基础数据类型
                            if (parameterType.isPrimitive()||parameterType.getName().startsWith("java.lang")) {
                                fieldNames.add(parameter.getName());
                                //基础数据类型
                                value.put("type", parameterType.getSimpleName().toLowerCase());
                                //判断format
                            }else if(Collection.class.isAssignableFrom(parameterType)){
                                //集合类型
                                value.put("type","array");
                                //获取泛型
                                getGeric(parameterType).ifPresent(s -> value.put("items",MapUtil.builder(REF_KEY,REF_SCHEMA_PREFIX+s).build()));
                            }else {
                                //引用类型
                                value.put(REF_KEY,REF_SCHEMA_PREFIX+schemaName);
                            }
                            //补一个description
                            io.swagger.v3.oas.annotations.Parameter paramAnnotation= map.get(parameter.getName());
                            if (paramAnnotation!=null){
                                //忽略该参数
                                fieldNames.add(paramAnnotation.name());
                                value.put("description",paramAnnotation.description());
                                if (StrUtil.isNotBlank(paramAnnotation.example())){
                                    value.put("default",paramAnnotation.example());
                                }
                                // required参数
                                if (paramAnnotation.required()){
                                    required.add(parameter.getName());
                                }
                            }
                            properties.put(parameter.getName(), value);
                            log.info("paramName:{}",parameter.getName());
                        }
                    }
                }
                if (!properties.isEmpty()){
                    extensions.put("properties",properties);
                    extensions.put("type","object");
                    //required字段
                    if (!required.isEmpty()){
                        extensions.put("required",required);
                    }
                    String generateSchemaName=handlerMethod.getMethod().getName()+"DynamicReq";

                    Map<String,Object> orangeExtensions=new HashMap<>();
                    orangeExtensions.put(generateSchemaName,extensions);
                    //增加扩展属性
                    operation.addExtension(EXTENSION_ORANGE_FORM_NAME,orangeExtensions);
                    if (!fieldNames.isEmpty()){
                        operation.addExtension(EXTENSION_ORANGE_FORM_IGNORE_NAME,fieldNames);
                    }
                }
            }

        }
        return operation;
    }


    private Optional<String> getGeric(Class<?> clazz){
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                String name=((Class) actualTypeArgument).getSimpleName();
                return Optional.of(name);
                //System.out.println(actualTypeArgument.getTypeName()); // 输出: MyOrderParam.OrderInfo
            }
        }
        return Optional.empty();
    }


    private Set<String> getClassFields(Class<?> parameterType){
        if (parameterType==null){
            return Collections.emptySet();
        }
        if (cacheClassProperties.containsKey(parameterType)){
            return cacheClassProperties.get(parameterType);
        }
        Set<String> fieldNames=new HashSet<>();
        try{
            Field[] fields = parameterType.getDeclaredFields();
            if (fields.length>0){
                for (Field field:fields){
                    fieldNames.add(field.getName());
                }
                cacheClassProperties.put(parameterType,fieldNames);
                return fieldNames;
            }
        }catch (Exception e){
            //ignore
        }

        return Collections.emptySet();
    }
    private Map<String,io.swagger.v3.oas.annotations.Parameter> getParameterDescription(Method method){
        //读取OpenAPI3的注解
        Parameters parameters=method.getAnnotation(Parameters.class);
        Map<String,io.swagger.v3.oas.annotations.Parameter> map=new HashMap<>();
        if (parameters!=null){
            io.swagger.v3.oas.annotations.Parameter[] parameters1= parameters.value();
            if (parameters1!=null&&parameters1.length>0){
                for (io.swagger.v3.oas.annotations.Parameter parameter:parameters1){
                    map.put(parameter.name(),parameter);
                }
                return map;
            }
        }else{
            io.swagger.v3.oas.annotations.Parameter parameter=method.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
            if (parameter!=null){
                map.put(parameter.name(),parameter);
            }


        }
        return  map;
    }
}
