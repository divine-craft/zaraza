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

package ru.divinecraft.zaraza.common.api.util;

import lombok.val;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NamespacedKeysTest {

    @SuppressWarnings("deprecation") // creation of NamespacedKey
    static @NotNull Stream<@NotNull Arguments> validNamespaceComponents() {
        return Stream.of(
                arguments("hello", "world", new NamespacedKey("hello", "world"))
        );
    }

    static @NotNull Stream<@NotNull Arguments> invalidNamespaceComponents() {
        val hugeString = "a".repeat(255);

        return Stream.of(
                arguments("ohyeah", "just great"),
                arguments("oh yeah", "justgreat"),
                arguments("oh yeah", "just great"),
                arguments("", "abc"),
                arguments("def", ""),
                arguments("", ""),
                arguments(hugeString, "a"),
                arguments("a", hugeString),
                arguments(hugeString, hugeString)
        );
    }

    @ParameterizedTest
    @MethodSource("validNamespaceComponents")
    void of_StringString_valid(final String key, final String value,
                               final NamespacedKey namespacedKey) {
        assertEquals(namespacedKey, NamespacedKeys.of(key, value));
    }

    @ParameterizedTest
    @MethodSource("invalidNamespaceComponents")
    void of_StringString_invalid(final String key, final String value) {
        assertThrows(IllegalArgumentException.class, () -> NamespacedKeys.of(key, value));
    }

    @ParameterizedTest
    @MethodSource("validNamespaceComponents")
    void parse_valid(final String key, final String value, final NamespacedKey namespacedKey) {
        assertEquals(namespacedKey, NamespacedKeys.parse(key + ':' + value));
    }

    @ParameterizedTest
    @MethodSource("invalidNamespaceComponents")
    void parse_valid(final String key, final String value) {
        assertThrows(IllegalArgumentException.class, () -> NamespacedKeys.parse(key + ':' + value));
    }

    @ParameterizedTest
    @MethodSource("validNamespaceComponents")
    void tryParse_valid(final String key, final String value, final NamespacedKey namespacedKey) {
        assertEquals(Optional.of(namespacedKey), NamespacedKeys.tryParse(key + ':' + value));
    }

    @ParameterizedTest
    @MethodSource("invalidNamespaceComponents")
    void tryParse_valid(final String key, final String value) {
        assertFalse(NamespacedKeys.tryParse(key + ':' + value).isPresent());
    }
}