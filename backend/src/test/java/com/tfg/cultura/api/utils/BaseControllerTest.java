package com.tfg.cultura.api.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.cultura.api.core.exception.ApiErrorBuilder;
import com.tfg.cultura.api.core.exception.GlobalExceptionHandler;

public abstract class BaseControllerTest {
    protected MockMvc mockMvc;
    protected ApiErrorBuilder apiErrorBuilder = new ApiErrorBuilder();

    protected static String toJson(Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    protected MockMvc buildMockMvc(Object controller, Class<?>... extraAdvices) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        List<Object> advices = new ArrayList<>();
        advices.add(new GlobalExceptionHandler(apiErrorBuilder));
        
        if (extraAdvices != null) {
            for (Class<?> adviceClass : extraAdvices) {
                advices.add(createAdvice(adviceClass));
            }
        }

        return MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(advices.toArray())
                .build();
    }

    private Object createAdvice(Class<?> adviceClass) {
        try {
            // Asume constructor con ApiErrorBuilder
            return adviceClass
                    .getConstructor(ApiErrorBuilder.class)
                    .newInstance(apiErrorBuilder);
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se pudo instanciar el advice: " + adviceClass.getSimpleName(), e
            );
        }
    }
}
