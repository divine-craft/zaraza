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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Represent any value slot<br>
 * Last value is cached and can be gotten by {@link #latest()} method
 *
 * @param <T> value type
 */
public interface Dynamic<T> {

    /**
     * Returns latest pushed element<br>
     * The invocation of {@code latest()} is equivalent to:
     * <pre>asFlux().blockFirst()</pre>
     * <li>Note that returned value may NOT be ACTUAL value</li>
     *
     * @return cached value
     */
    @Nullable T latest();

    /**
     * Returns flux than cache last pushed element, so {@link Flux#blockFirst()}
     * returns latest element without any blocks
     *
     * @return Flux view of this dynamic
     */
    @NotNull Flux<T> asFlux();

    /**
     * Asynchronously sets new value for this Dynamic and sends updates if success<br>
     * Mono signal indicates was operation successful or not (true for success)<br>
     * If no any value was cached (method {@link #latest()} returns null), then operation would not completed<br>
     *
     * @param value new value
     * @return operation mono
     */
    @NotNull Mono<Boolean> set(@NotNull T value);

    /**
     * Creates new Dynamic instance
     *
     * @param writer       function that saves new value and generate signals on successful write
     * @param publisher    value updates publisher. Supplies update signals when value was changed
     * @param initialValue first value that will be cached until publisher generates any value update
     * @param <T>          value type
     * @return new Dynamic
     */
    @Contract("_,_,_->new")
    static <T> Dynamic<T> create(@NonNull CompareAndSetAction<T> writer, @NonNull Publisher<T> publisher, @NotNull T initialValue) {
        Sinks.Many<T> sink = Sinks.many().replay().latestOrDefault(initialValue);
        Flux.from(publisher).subscribe(sink::tryEmitNext, sink::tryEmitError, sink::tryEmitComplete);
        return new ReactorDynamic<>(sink,
                sink.asFlux(),//replay sink cache last value manually. BTW #asFlux() for ReplayingSink returns its Sink instance
                writer);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    final class ReactorDynamic<T> implements Dynamic<T> {
        Sinks.Many<T> sink;
        Flux<T> asFlux;
        CompareAndSetAction<T> writer;

        @Override
        public @Nullable T latest() {
            return asFlux().blockFirst();
        }

        @Override
        public @NotNull Flux<T> asFlux() {
            return asFlux;
        }

        @Override
        public @NotNull Mono<Boolean> set(@NotNull T value) {
            Mono<Boolean> mono = Mono.fromCallable(() -> {
                T old;
                if ((old = latest()) == null) return false;
                return writer.compareAndSet(value, old);
            });
            mono.subscribe();
            return mono;
        }
    }
}
