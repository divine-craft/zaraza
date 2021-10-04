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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link TypeAdapterFactory} capable of working with only one specific type.
 *
 * @param <T> type for which the specific type adapter is provided
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SingleTypeTypeAdapterFactory<T> implements TypeAdapterFactory {

    /**
     * Exact type for which a {@link TypeAdapter} is provided
     */
    @NotNull Class<? super T> supportedType;

    /**
     * Type adapter provided for the specific type
     */
    @NotNull TypeAdapter<? extends T> adapter;

    /**
     * Creates a {@link TypeAdapterFactory} capable of working with only one specific type.
     *
     * @param supportedType exact type for which a {@link TypeAdapter} is provided
     * @param typeAdapter type adapter provided for the specific type
     * @param <T> type for which the specific type adapter is provided
     * @return created type adapter factory
     */
    public static <T> @NotNull TypeAdapterFactory create(final @NonNull Class<? super T> supportedType,
                                                         final @NonNull TypeAdapter<? extends T> typeAdapter) {
        return new SingleTypeTypeAdapterFactory<>(supportedType, typeAdapter);
    }

    @Override
    @SuppressWarnings("unchecked") // type is checked via `type` parameter
    public <R> @Nullable TypeAdapter<R> create(final Gson gson, final TypeToken<R> type) {
        return type.getType() == supportedType ? (TypeAdapter<R>) adapter : null;
    }
}
