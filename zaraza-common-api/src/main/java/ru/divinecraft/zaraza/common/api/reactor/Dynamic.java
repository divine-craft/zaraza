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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.UnaryOperator;

/**
 * <p>A dynamically changed value.</p>
 * <p>The last observed value is always cached
 * and can be synchronously retrieved via call to {@link #snapshot()}.</p>
 *
 * @param <T> type of dynamic value
 */
@NonBlocking
public interface Dynamic<T> {

    /**
     * Returns the latest observed (cached) value.
     *
     * @return the cached value
     *
     * @apiNote the returned value is the latest observed snapshot and may be globally "old",
     * as there may be a foreground process eventually leading to the value being changed
     */
    @Nullable T snapshot();

    /**
     * <p>Asynchronously attempts to set this value to the new state.</p>
     * <p>The operation succeeds iff compare-and-set from currently observed value to the new one succeeds.</p>
     *
     * @param newValue the new value
     * @return mono emitting {@code true} on success or {@code false} if the value was not changed to the provided one
     *
     * @apiNote the value is not directly set via call to this method,
     * it will only get changed via an external call to update
     */
    @NotNull Mono<Boolean> trySet(@NotNull T newValue);

    /**
     * Updates this dynamic value using the provided operator.
     *
     * @param operator function used to convert the current value to the new one
     * @return mono emitting the resulting value
     *
     * @apiNote operator may be invoked multiple times
     */
    @NotNull Mono<? extends T> update(@NotNull UnaryOperator<T> operator);

    /**
     * <p>Returns a {@link Flux}-view of this dynamic value emitting values on its updates.</p>
     * <p>The current value will always be emitted as the first signal.</p>
     *
     * @return Flux view of this dynamic
     */
    @NotNull Flux<? extends T> asFlux();

    /**
     * Creates a new externally changed dynamic value.
     *
     * @param externalWriter function used to perform atomic compare-and-set of the value globally
     * @param publisher publisher emitting values on external value changes
     * @param initialValue initial value
     * @param <T> type of dynamic value
     * @return new dynamic value
     *
     * @throws NullPointerException if {@code externalWriter} is {@code null}
     * @throws NullPointerException if {@code publisher} is {@code null}
     * @throws NullPointerException if {@code initialValue} is {@code null}
     */
    @Contract("null, _, _ -> fail;"
            + "_, null, _ -> fail;"
            + "_, _, null -> fail;"
            + "_, _, _ -> new;")
    static <T> @NotNull Dynamic<T> createExternal(
            final @NonNull ReactiveCompareAndSetOperation<@NotNull ? super T> externalWriter,
            final @NonNull Publisher<? extends T> publisher,
            final @NonNull T initialValue
    ) {
        val sink = Sinks.many().replay().latestOrDefault(initialValue);
        Flux.from(publisher)
                .subscribe(
                        value -> sink.emitNext(value, Sinks.EmitFailureHandler.FAIL_FAST),
                        error -> sink.emitError(error, Sinks.EmitFailureHandler.FAIL_FAST),
                        () -> sink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST)
                );

        return new ExternalDynamic<>(sink.asFlux(), externalWriter);
    }

    /**
     * {@link Dynamic} implementation which stores the {@link Flux} representation of {@link Sinks.Many}
     * and relies on external changes.
     *
     * @param <T> type of dynamic value
     */
    //@formatter:off (don't merge last annotation and class declaration lines)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    final class ExternalDynamic<T> implements Dynamic<T> {
        //@formatter:on

        /**
         * {@link Flux} representation of replaying multicast {@link Sinks.Many}.
         */
        @NotNull Flux<? extends T> asFlux;

        /**
         * Operation responsible for performing atomic compare-and-swap.
         */
        @NotNull ReactiveCompareAndSetOperation<@NotNull ? super T> externalWriter;

        @Override
        public @Nullable T snapshot() {
            // non-blocking: `asFlux` is *replaying* thus it always produces the value instantly
            return asFlux.blockFirst();
        }

        @Override
        public @NotNull Flux<? extends T> asFlux() {
            return asFlux;
        }

        @Override
        public @NotNull Mono<Boolean> trySet(final @NotNull T newValue) {
            return asFlux.next()
                    .flatMap(current -> externalWriter.compareAndSet(current, newValue));
        }

        @Override
        public @NotNull Mono<? extends T> update(final @NotNull UnaryOperator<T> updater) {
            return Mono.defer(() -> asFlux.next().flatMap(oldValue -> {
                final T newValue;
                return trySet(newValue = updater.apply(oldValue))
                        .flatMap(successful -> successful ? Mono.just(newValue) : Mono.error(ShouldRetry.INSTANCE));
            })).retry();
        }

        /**
         * Pseudo-error used to implement retry logic of {@link #update(UnaryOperator)}.
         */
        private static final class ShouldRetry extends Throwable {

            /**
             * Singleton instance of this pseudo-error
             */
            private static final Throwable INSTANCE = new ShouldRetry(
                    null, null, true, false
            );

            private ShouldRetry(final @Nullable String message,
                                final @Nullable Throwable cause,
                                final boolean enableSuppression,
                                final boolean writableStackTrace) {
                super(message, cause, enableSuppression, writableStackTrace);
            }
        }
    }
}
