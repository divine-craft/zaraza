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

package ru.divinecraft.zaraza.common.api.flow;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Consumer;

/**
 * Utilities related to {@link Subscriber}.
 */
@UtilityClass
public class FlowSubscribers {

    /**
     * Creates a new subscriber.
     *
     * @param nextHandler handler to be notified when {@link Flow.Subscriber#onNext(Object)} gets called
     * @param errorHandler handler to be notified when {@link Flow.Subscriber#onError(Throwable)} gets called
     * @param <T> type of value whose updates will be listened
     * @return created subscriber
     *
     * @throws NullPointerException if {@code nextHandler} is {@code null}
     * @throws NullPointerException if {@code errorHandler} is {@code null}
     */
    public <T> @NotNull Subscriber<T> createSubscriber(
            final @NonNull Consumer<? super T> nextHandler,
            final @NonNull Consumer<@NotNull Throwable> errorHandler
    ) {
        return new DelegatingSubscriber<>(nextHandler, errorHandler);
    }

    /**
     * Creates a new subscriber with re-throwing error handler.
     *
     * @param nextHandler handler to be notified when {@link Flow.Subscriber#onNext(Object)} gets called
     * @param <T> type of value whose updates will be listened
     * @return created subscriber
     *
     * @throws NullPointerException if {@code nextHandler} is {@code null}
     */
    public <@NotNull T> @NotNull Subscriber<T> createSubscriber(
            final Consumer<@NotNull T> nextHandler
    ) {
        return createSubscriber(nextHandler, error -> {
            throw new InternalError("An unexpected error was passed to ChangeListener", error);
        });
    }

    /**
     * Creates a new thread-unsafe subscriber.
     *
     * @param nextHandler handler to be notified when {@link Flow.Subscriber#onNext(Object)} gets called
     * @param errorHandler handler to be notified when {@link Flow.Subscriber#onError(Throwable)} gets called
     * @param <T> type of value whose updates will be listened
     * @return created memoizing subscriber
     *
     * @throws NullPointerException if {@code nextHandler} is {@code null}
     * @throws NullPointerException if {@code errorHandler} is {@code null}
     */
    public <T> @NotNull MemoizingFlowSubscriber<T> createMemoizingSubscriber(
            final @NonNull Consumer<? super T> nextHandler,
            final @NonNull Consumer<@NotNull Throwable> errorHandler
    ) {
        return new ThreadUnsafeMemoizingDelegatingSubscriber<>(nextHandler, errorHandler);
    }

    /**
     * Creates a new thread-unsafe memoizing subscriber with re-throwing error handler.
     *
     * @param nextHandler handler to be notified when {@link Flow.Subscriber#onNext(Object)} gets called
     * @param <T> type of value whose updates will be listened
     * @return created memoizing subscriber
     *
     * @throws NullPointerException if {@code nextHandler} is {@code null}
     */
    public <@NotNull T> @NotNull MemoizingFlowSubscriber<T> createMemoizingSubscriber(
            final Consumer<? super @NotNull T> nextHandler
    ) {
        return createMemoizingSubscriber(nextHandler, error -> {
            throw new InternalError("An unexpected error was passed to ChangeListener", error);
        });
    }

    /**
     * Creates a new thread-unsafe memoizing subscriber with no-op value handler and re-throwing error handler.
     *
     * @param <T> type of value whose updates will be listened
     * @return created memoizing subscriber
     */
    public <@NotNull T> @NotNull MemoizingFlowSubscriber<T> createMemoizingSubscriber() {
        return createMemoizingSubscriber(newValue -> {});
    }

    //<editor-fold desc="Inner implementations" defaultstate="collapsed">
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class DelegatingSubscriber<T> implements Subscriber<T> {

        @NotNull Consumer<? super T> nextHandler;
        @NotNull Consumer<@NotNull Throwable> errorHandler;

        @Override
        public void onNext(final T item) {
            nextHandler.accept(item);
        }

        @Override
        public void onError(final Throwable error) {
            errorHandler.accept(error);
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {} // this one is usually unneeded

        @Override
        public void onComplete() {} // this one is usually unneeded
    }

    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class ThreadUnsafeMemoizingDelegatingSubscriber<T>
            extends DelegatingSubscriber<T> implements MemoizingFlowSubscriber<T> {

        /**
         * Last value passed to {@link #onNext(Object)}.
         */
        @Getter @Nullable T lastValue; // default-initialized to null

        private ThreadUnsafeMemoizingDelegatingSubscriber(final @NotNull Consumer<? super T> nextHandler,
                                                          final @NotNull Consumer<@NotNull Throwable> errorHandler) {
            super(nextHandler, errorHandler);
        }
    }
    //</editor-fold>
}
