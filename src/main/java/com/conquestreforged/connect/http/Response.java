package com.conquestreforged.connect.http;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface Response<T> {

    Response EMPTY = () -> null;

    T get() throws Exception;

    default boolean done() {
        return false;
    }

    default boolean present() {
        return this != EMPTY;
    }

    default boolean poll(Consumer<T> consumer) {
        if (done()) {
            try {
                T t = get();
                consumer.accept(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    default void await() {
        await(-1, TimeUnit.MILLISECONDS);
    }

    default void await(long timeout, TimeUnit unit) {
        if (!present()) {
            return;
        }
        if (timeout == -1) {
            while (!done()) {
            }
        } else {
            long period = unit.toMillis(timeout);
            long start = System.currentTimeMillis();
            while (!done()) {
                if (System.currentTimeMillis() - start > period) {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Response<T> empty() {
        return EMPTY;
    }

    static <T> Response<T> of(Future<T> task) {
        return new Response<T>() {
            @Override
            public T get() throws Exception {
                return task.get();
            }

            @Override
            public boolean done() {
                return task.isDone();
            }
        };
    }
}
