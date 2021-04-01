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

package ru.divinecraft.zaraza.common.api.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.divinecraft.zaraza.common.api.util.NamespacedKeys;

import java.io.IOException;

/**
 * Common {@link TypeAdapterFactory type adapter factories} forBukkit-specific types.
 */
@UtilityClass
public class BukkitGsonTypeAdapterFactories {

    /**
     * Creates a {@link TypeAdapterFactory} for {@link NamespacedKey}.
     *
     * @return {@link TypeAdapterFactory} for {@link NamespacedKey}
     */
    public @NotNull TypeAdapterFactory namespacedKeyTypeAdapterFactory() {
        return SingleTypeTypeAdapterFactory.create(NamespacedKey.class, new TypeAdapter<>() {
            @Override
            public void write(final @NotNull JsonWriter out, final @Nullable NamespacedKey value)
                    throws IOException {
                if (value == null) out.nullValue();
                else out.value(value.toString());
            }

            @Override
            public @Nullable NamespacedKey read(final @NotNull JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                return NamespacedKeys.tryParse(in.nextString())
                        .orElseThrow(() -> new JsonParseException("Expected valid namespaced key"));
            }
        });
    }
}
