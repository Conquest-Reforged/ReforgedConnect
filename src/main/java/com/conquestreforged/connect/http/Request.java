package com.conquestreforged.connect.http;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public interface Request<T> extends Callable<T> {

    @Override
    T call() throws Exception;

    default <R> Request<R> then(Function<T, R> func) {
        Request<T> self = this;
        return () -> func.apply(self.call());
    }

    default Request<T> peek(Consumer<T> consumer) {
        Request<T> self = this;
        return () -> {
            T t = self.call();
            consumer.accept(t);
            return t;
        };
    }

    default Request<Void> done(Consumer<T> consumer) {
        Request<T> self = this;
        return () -> {
            T t = self.call();
            consumer.accept(t);
            return null;
        };
    }

    default Response<T> send() {
        return Response.of(ForkJoinPool.commonPool().submit(this));
    }

    @FunctionalInterface
    interface Function<T, R> {

        R apply(T t) throws Exception;
    }
}
