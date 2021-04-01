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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.val;
import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.Test;
import ru.divinecraft.zaraza.common.api.util.NamespacedKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BukkitGsonTypeAdapterFactoriesTest {

    @Test
    void namespacedKey() {
        val gson = new GsonBuilder()
                .registerTypeAdapterFactory(BukkitGsonTypeAdapterFactories.namespacedKeyTypeAdapterFactory())
                .create();

        assertEquals(
                NamespacedKeys.of("hello", "world"),
                gson.fromJson("\"hello:world\"", NamespacedKey.class)
        );

        assertThrows(JsonParseException.class,
                () -> gson.fromJson("\"hello:world with spaces\"", NamespacedKey.class)
        );
    }
}