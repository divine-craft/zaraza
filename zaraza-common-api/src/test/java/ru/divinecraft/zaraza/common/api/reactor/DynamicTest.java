/*
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

package ru.divinecraft.zaraza.common.api.reactor;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicTest {

    @Test
    void dynamic_create__multicasts() {
        val sink = Sinks.many()
                .unicast()
                .<String>onBackpressureError();
        val dynamic = Dynamic.create((oldValue, newValue) -> {
            sink.emitNext(newValue, Sinks.EmitFailureHandler.FAIL_FAST);
            return true;
        }, sink.asFlux(), "initial");

        assertDynamicReplays(dynamic, "initial", 1, 3, 54, 4, 64, 3, 2);

        StepVerifier.create(dynamic.trySet("new"))
                .expectNext(true)
                .expectComplete()
                .verify();

        assertDynamicReplays(dynamic, "new", 12, 12, 1, 3, 2, 2, 244, 357, 8);
    }

    @Test
    void dynamic_create__update() {
        val attemptsBeforeSuccess = new AtomicInteger(77);

        val sink = Sinks.many()
                .unicast()
                .<String>onBackpressureError();
        val dynamic = Dynamic.create((oldValue, newValue) -> {
            // succeed CAS only after multiple attempts
            if (attemptsBeforeSuccess.decrementAndGet() == 0) {
                sink.emitNext(newValue, Sinks.EmitFailureHandler.FAIL_FAST);

                return true;
            }

            return false;
        }, sink.asFlux(), "foo");

        assertDynamicReplays(dynamic, "foo", 121, 21, 486, 4, 525, 653, 364, 4);

        StepVerifier.<String>create(dynamic.update(old -> {
                    assertEquals("foo", old, "CAS request produces the invalid result");
                    return "bar";
                }))
                .expectNext("bar")
                .expectComplete()
                .verify();
        assertDynamicReplays(dynamic, "bar", 12, 46, 4735, 7, 3, 4, 45, 45, 35, 3);
    }

    private static <T> void assertDynamicReplays(final @NotNull Dynamic<T> dynamic,
                                                 final @NotNull T expectedValue,
                                                 final int @NotNull ... attempts) {
        val attemptsLength = attempts.length;
        for (var attemptsGroup = 0; attemptsGroup < attemptsLength; attemptsGroup++) {
            val currentAttempts = attempts[attemptsGroup];
            val asFlux = attemptsGroup % 2 == 0;
            //@formatter:off
            for (int attempt = 0; attempt < currentAttempts; attempt++) if (asFlux) StepVerifier
                    .<T>create(dynamic.asFlux())
                    .expectNext(expectedValue)
                    .thenCancel()
                    .verify();
            else assertEquals(expectedValue, dynamic.snapshot());
            //@formatter:on
        }
    }
}