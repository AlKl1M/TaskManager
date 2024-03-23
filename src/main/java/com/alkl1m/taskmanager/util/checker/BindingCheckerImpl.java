package com.alkl1m.taskmanager.util.checker;



import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class BindingCheckerImpl {
    @Before(value = "@annotation(com.alkl1m.taskmanager.util.checker.BindingChecker)" + " && args(bindingResult)", argNames = "bindingResult")
    public void checkBindingErrors(BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        }
    }
}
