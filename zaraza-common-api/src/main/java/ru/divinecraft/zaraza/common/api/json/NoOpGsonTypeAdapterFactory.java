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

package ru.divinecraft.zaraza.common.api.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * {@link TypeAdapterFactory} which returns to custom {@link TypeAdapter type adapters}.
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoOpGsonTypeAdapterFactory implements TypeAdapterFactory {

    /**
     * Singleton instance of this type adapter.
     */
    private static final TypeAdapterFactory INSTANCE = new NoOpGsonTypeAdapterFactory();

    /**
     * Creates a no-op {@link TypeAdapterFactory type adapter factory}.
     *
     * @return created no-op {@link TypeAdapterFactory type adapter factory}
     */
    public static @NotNull TypeAdapterFactory create() {
        return INSTANCE;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        return null;
    }
}
