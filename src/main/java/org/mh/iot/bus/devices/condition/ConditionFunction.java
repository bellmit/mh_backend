package org.mh.iot.bus.devices.condition;

import org.mh.iot.models.ParameterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by evolshan on 01.02.2020.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConditionFunction {
    String description() default "";
    ParameterType[] forParameters();
}
