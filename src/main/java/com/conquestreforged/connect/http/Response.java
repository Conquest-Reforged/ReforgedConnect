package com.conquestreforged.connect.http;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Response<T> {

    private final Future<T> future;

    private Response(Future<T> future) {
        this.future = future;
    }

    public T get() throws Exception {
        return future.get();
    }

    public Optional<T> get(long timeout, TimeUnit unit) {
        if (await(timeout, unit)) {
            try {
                return Optional.ofNullable(get());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public boolean done() {
        return future.isDone();
    }

    public boolean poll(Consumer<T> consumer) {
        if (done()) {
            try {
                consumer.accept(get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void await() {
        //noinspection StatementWithEmptyBody
        while (!done()) {}
    }

    public boolean await(long timeout, TimeUnit unit) {
        if (timeout < 0) {
            return false;
        }
        long period = unit.toMillis(timeout);
        long start = System.currentTimeMillis();
        while (!done()) {
            if (System.currentTimeMillis() - start > period) {
                return false;
            }
        }
        return true;
    }

    public static <T> Response<T> of(Supplier<T> task) {
        return new Response<>(new FutureTask<>(task::get));
    }

    public static <T> Response<T> of(Future<T> task) {
        return new Response<>(task);
    }
}
