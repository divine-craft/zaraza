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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Flow;

/**
 * {@link Flow.Subscriber} storing the result of its last call to {@link #onNext(Object)}.
 *
 * @param <T> the subscribed item type
 */
public interface MemoizingFlowSubscriber<@NotNull T> extends Flow.Subscriber<@NotNull T> {

    /**
     * Gets the last {@link #onNext(Object) handled} value.
     *
     * @return the last passed value or {@code null} if there was none
     */
    @Nullable T lastValue();
}
