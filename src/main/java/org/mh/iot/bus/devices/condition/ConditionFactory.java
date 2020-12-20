package org.mh.iot.bus.devices.condition;

import org.mh.iot.models.Condition;
import org.mh.iot.models.ConditionParameter;

import org.mh.iot.models.ParameterType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by evolshan on 12.12.2020.
 */
@Component
public class ConditionFactory {

    private List<Method> allConditionMethods = new ArrayList<>();

    @PostConstruct
    private void init(){

        Class<?> klass = this.getClass();
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            final List<Method> allMethods = new ArrayList<>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                ConditionFunction annotation = method.getAnnotation(ConditionFunction.class);
                if (annotation != null) {
                    allConditionMethods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
    }
    public List<Condition> getConditionsByParameterType(ParameterType parameterType){
        List<Condition> conditions = new ArrayList<>();

        for (final Method method : allConditionMethods) {
            ConditionFunction annotation = method.getAnnotation(ConditionFunction.class);
            if (Arrays.stream(annotation.forParameters()).anyMatch(item -> item == parameterType)) {

                conditions.add(new Condition()
                        .name(method.getName())
                        .description(annotation.description())
                        .parameters(
                                Arrays.stream(method.getParameters()).map(
                                        item -> new ConditionParameter()
                                                .name(item.getName())
                                                .typeName(item.getType().getTypeName()))
                                        .collect(Collectors.toList()))
                        );
            }
        }
        return conditions;
    }




    @ConditionFunction(forParameters = {ParameterType.ON_OFF}, description = "Has state ON?")
    public static boolean isOn(String value){
        return (value != null && value.equals("ON"));
    }

    @ConditionFunction(forParameters = {ParameterType.NUMBER}, description = "Value greater than?")
    public static boolean graterThan(String value, String valueToCompareWith){
        float currentValue = Float.parseFloat(value);
        float compareValue = Float.parseFloat(valueToCompareWith);
        return (currentValue > compareValue);
    }


    private Method getConditionMethod(String methodName, List<ConditionParameter> parameters){
        for (final Method method : allConditionMethods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == parameters.size()) {
                // ToDo add parameter type compare
                return method;
            }
        }

        return null;
    }

    public boolean executeCondition(Condition condition, String paramValue, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method method = getConditionMethod(condition.getName(), condition.getParameters());
        if (method != null)
            return (Boolean)method.invoke(null, paramValue, args);
        else
            return false;
    }



}
