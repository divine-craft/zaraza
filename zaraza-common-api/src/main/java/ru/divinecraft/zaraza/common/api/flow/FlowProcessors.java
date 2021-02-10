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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Utilities related to {@link Processor}.
 */
@UtilityClass
public class FlowProcessors {

    /**
     * Creates a new thread-unsafe {@link Processor processor}.
     *
     * @param <T> type of processed values
     *
     * @return created {@link Processor processor}
     */
    public <T> @NotNull Processor<T, T> createProcessor() {
        return new ThreadUnsafeProcessor<>(new HashSet<>());
    }

    /**
     * Creates a new thread-unsafe {@link Processor processor}.
     *
     * @param <T> type of processed values
     *
     * @return created {@link Processor processor}
     */
    public <T> @NotNull MemoizingFlowProcessor<T, T> createMemoizingProcessor() {
        return new MemoizingThreadUnsafeProcessor<>(new HashSet<>());
    }

    /**
     * Simple {@link Processor processor} for which no concurrency guarantees are given.
     *
     * @param <T> type of processed values
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class ThreadUnsafeProcessor<T> implements Processor<T, T> {

        /**
         * All subscribers of this processor
         */
        @NotNull Set<@NotNull Subscriber<? super T>> subscribers;

        @Override
        public void subscribe(final Subscriber<? super T> subscriber) {
            if (subscribers.add(subscriber)) subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(final long amount) {} // no-op

                @Override
                public void cancel() {
                    subscribers.remove(subscriber);
                }
            });
        }

        @Override
        public void onSubscribe(final Subscription subscription) {} // no-op

        @Override
        public void onNext(final T item) {
            for (val subscriber : subscribers) subscriber.onNext(item);
        }

        @Override
        public void onError(final Throwable error) {
            for (val subscriber : subscribers) subscriber.onError(error);
        }

        @Override
        public void onComplete() {
            for (val subscriber : subscribers) subscriber.onComplete();
        }
    }

    /**
     * {@link MemoizingFlowProcessor Memoizing} {@link ThreadUnsafeProcessor}.
     *
     * @param <T> type of processed values
     */
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class MemoizingThreadUnsafeProcessor<T>
            extends ThreadUnsafeProcessor<T> implements MemoizingFlowProcessor<T, T> {

        /**
         * Last value passed to {@link #onNext(Object)}.
         */
        @Getter @Nullable T lastValue; // default-initialized to null

        private MemoizingThreadUnsafeProcessor(final @NotNull Set<@NotNull Subscriber<? super T>> subscribers) {
            super(subscribers);
        }

        @Override
        public void onNext(final T item) {
            super.onNext(lastValue = item);
        }
    }
}
