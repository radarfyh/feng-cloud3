package work.metanet.feng.common.swagger.extension;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.swagger.annotation.MyRequestBody;

import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;

@Slf4j
public class GlobalOperationCustomizer implements ParameterCustomizer {



    @Override
    public Parameter customize(Parameter parameterModel, MethodParameter methodParameter) {
        if (methodParameter.getMethod().getName().equals("fileModule")){
            log.info("method:{},paramName:{}",methodParameter.getMethod().getName(),methodParameter.getParameterName());
            log.info("parameterModel:{}",parameterModel);
            if (parameterModel!=null){
                parameterModel.addExtension("x-body",true);
            }
        }
        MyRequestBody myRequestBody =methodParameter.getParameterAnnotation(MyRequestBody.class);
        if (myRequestBody !=null){
            log.info("method:{},paramName:{}",methodParameter.getMethod().getName(),methodParameter.getParameterName());
            log.info("parameterModel:{}",parameterModel);
            if (parameterModel==null){
                parameterModel=new Parameter();
                parameterModel.setRequired(true);
                parameterModel.set$ref("#/components/schemas/"+methodParameter.getParameterType().getSimpleName());
                parameterModel.setName(methodParameter.getParameterName());
                parameterModel.addExtension("x-body",true);
            }
        }
        return parameterModel;
    }
}
