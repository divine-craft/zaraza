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
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Flow;

/**
 * Utilities related to {@link Flow.Processor}.
 */
@UtilityClass
public class FlowProcessors {

    /**
     * Creates a new thread-unsafe {@link Flow.Processor processor}.
     *
     * @param <T> type of processed values
     *
     * @return created {@link Flow.Processor processor}
     */
    public static <T> Flow.@NotNull Processor<T, T> createProcessor() {
        return new ThreadUnsafeProcessor<>(new HashSet<>());
    }

    /**
     * Simple {@link Flow.Processor processor} for which no concurrency guarantees are given.
     *
     * @param <T> type of processed values
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class ThreadUnsafeProcessor<T> implements Flow.Processor<T, T> {

        /**
         * All subscribers of this processor
         */
        @NotNull Set<Flow.@NotNull Subscriber<? super T>> subscribers;

        @Override
        public void subscribe(final Flow.Subscriber<? super T> subscriber) {
            if (subscribers.add(subscriber)) subscriber.onSubscribe(new Flow.Subscription() {
                @Override
                public void request(final long amount) {} // no-op

                @Override
                public void cancel() {
                    subscribers.remove(subscriber);
                }
            });
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {} // no-op

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
}
