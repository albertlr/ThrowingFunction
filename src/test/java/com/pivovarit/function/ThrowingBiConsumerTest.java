package com.pivovarit.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ThrowingBiConsumerTest {

    @Test
    void shouldConsume() throws Exception {
        // given
        LongAdder input = new LongAdder();

        ThrowingBiConsumer<Integer, Integer, Exception> consumer = (i, j) -> input.increment();

        // when
        consumer.accept(2, 3);

        // then
        assertThat(input.sum()).isEqualTo(1);
    }

    @Test
    void shouldConsumeAndThrowUnchecked() {
        IOException cause = new IOException("some message");

        // given
        ThrowingBiConsumer<Integer, Integer, IOException> consumer = (i, j) -> { throw cause; };

        // when
        assertThatThrownBy(() -> ThrowingBiConsumer.unchecked(consumer).accept(3, 3))
          .hasMessage(cause.getMessage())
          .isInstanceOf(CheckedException.class)
          .hasCauseInstanceOf(cause.getClass());
    }

    @Test
    void shouldConsumeUnchecked() {
        // given
        ThrowingBiConsumer<Integer, Integer, IOException> consumer = (i, j) -> {};

        // when
        ThrowingBiConsumer.unchecked(consumer).accept(3, 4);

        // then no exception thrown
    }

    @Test
    void shouldConsumeAndSneakyThrow() {
        IOException cause = new IOException("some message");

        // given
        ThrowingBiConsumer<Integer, Integer, IOException> consumer = (i, j) -> { throw cause; };

        // when
        assertThatThrownBy(() -> ThrowingBiConsumer.sneaky(consumer).accept(3, 3))
          .hasMessage(cause.getMessage())
          .isInstanceOf(IOException.class)
          .hasNoCause();
    }
}