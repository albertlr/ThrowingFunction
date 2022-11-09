/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pivovarit.function;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Represents a function that accepts one argument and returns a value;
 * Function might throw a checked exception instance.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the thrown checked exception
 * @author Grzegorz Piwowarek
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {
    R apply(T arg) throws E;

    /**
     * @return a Function that returns the result of the given function as an Optional instance.
     * In case of a failure, empty Optional is returned
     */
    static <T, R> Function<T, Optional<R>> lifted(final ThrowingFunction<? super T, ? extends R, ?> function) {
        requireNonNull(function);

        return t -> {
            try {
                return Optional.ofNullable(function.apply(t));
            } catch (final Exception e) {
                return Optional.empty();
            }
        };
    }

    static <T, R> Function<T, R> unchecked(final ThrowingFunction<? super T, ? extends R, ?> function) {
        requireNonNull(function);
        return t -> {
            try {
                return function.apply(t);
            } catch (final Exception e) {
                throw new CheckedException(e);
            }
        };
    }

    static <T1, R> Function<T1, R> sneaky(ThrowingFunction<? super T1, ? extends R, ?> function) {
        requireNonNull(function);
        return t -> {
            try {
                return function.apply(t);
            } catch (final Exception ex) {
                return SneakyThrowUtil.sneakyThrow(ex);
            }
        };
    }

    default Function<T, Optional<R>> lift() {
        return t -> {
            try {
                return Optional.ofNullable(apply(t));
            } catch (final Exception e) {
                return Optional.empty();
            }
        };
    }
}
