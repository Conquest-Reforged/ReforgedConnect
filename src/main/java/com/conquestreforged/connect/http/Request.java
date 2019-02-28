package com.conquestreforged.connect.http;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface Request<T> extends Callable<T> {

    @Override
    T call() throws Exception;

    default <R> Request<R> then(Mapper<T, R> func) {
        Request<T> self = this;
        return () -> func.map(self.call());
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

    default Optional<T> get(long timeout, TimeUnit unit) {
        return send().get(timeout, unit);
    }

    default T get() throws Exception {
        return send().get();
    }

    @FunctionalInterface
    interface Mapper<T, R> {

        R map(T t) throws Exception;
    }
}
