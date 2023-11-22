package com.zerobase.munbanggu.validation;


import com.zerobase.munbanggu.studyboard.model.dto.VoteOptionRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = OptionSizeValidator.class)
public @interface OptionSize {

    String message() default "하나 이상의 투표 항목이 입력되어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class OptionSizeValidator implements ConstraintValidator<OptionSize, List<VoteOptionRequest>> {


    @Override
    public boolean isValid(List<VoteOptionRequest> value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty();
    }
}
