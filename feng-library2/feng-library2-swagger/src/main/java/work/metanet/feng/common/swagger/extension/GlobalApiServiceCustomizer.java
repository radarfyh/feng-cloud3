package work.metanet.feng.common.swagger.extension;

import lombok.extern.slf4j.Slf4j;

import org.springdoc.core.OpenAPIService;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalApiServiceCustomizer implements OpenApiBuilderCustomizer {

    @Override
    public void customise(OpenAPIService openApiService) {

    }
}
