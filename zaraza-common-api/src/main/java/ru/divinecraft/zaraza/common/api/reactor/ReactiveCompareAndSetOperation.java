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

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Reactive atomic compare-and-set operation.
 *
 * @param <T> type of manipulated values
 * @see CompareAndSetOperation non-reactive equivalent
 */
@FunctionalInterface
public interface ReactiveCompareAndSetOperation<T> {

    /**
     * Asynchronously performs compare-and-set operation
     *
     * @param oldValue the expected value
     * @param newValue the new value
     * @return mono emitting {@code true} if the operation succeeds or {@code false} otherwise
     */
    @NotNull Mono<Boolean> compareAndSet(T oldValue, T newValue);

    /**
     * Creates a reactive compare-ans-set operation which never succeeds.
     *
     * @param <T> type of manipulated values
     * @return created impossible reactive compare-and-set operation
     */
    static @NotNull <T> ReactiveCompareAndSetOperation<T> impossible() {
        return (oldValue, newValue) -> Mono.just(false);
    }
}
