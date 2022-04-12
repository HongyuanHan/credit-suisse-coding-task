package com.creditsuisse.hhy.codingtask.functions;

import com.creditsuisse.hhy.codingtask.services.impl.LogEventServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception> {
    static final Logger logger = LoggerFactory.getLogger(LogEventServiceImpl.class.getName());

    R apply(T t) throws E;

    public static <T, R, E extends Exception> Function<T, R> wrapper(FunctionWithException<T, R, E> function) {
        return arg -> {
            try {
                return function.apply(arg);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        };
    }
}
