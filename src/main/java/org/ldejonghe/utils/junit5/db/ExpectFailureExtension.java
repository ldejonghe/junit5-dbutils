package org.ldejonghe.utils.junit5.db;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * JUnit 5 extension that allows marking a test as "expected to fail"
 * with a specific exception type using the @ExpectFailure annotation.
 */
public class ExpectFailureExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext context) throws Throwable {

        Method testMethod = context.getRequiredTestMethod();
        ExpectFailure expectFailure = testMethod.getAnnotation(ExpectFailure.class);

        if (expectFailure != null) {
            Class<? extends Throwable> expectedException = expectFailure.value();
            try {
                invocation.proceed();  // Run the test and all extensions
                throw new AssertionError("Expected exception of type " + expectedException.getName() + " but none was thrown.");
            } catch (Throwable actual) {
                if (!expectedException.isInstance(actual)) {
                    throw new AssertionError("Expected exception of type " + expectedException.getName() +
                                             " but got " + actual.getClass().getName(), actual);
                }
                // Expected failure happened, test passes
            }
        } else {
            invocation.proceed();
        }
    }
}
