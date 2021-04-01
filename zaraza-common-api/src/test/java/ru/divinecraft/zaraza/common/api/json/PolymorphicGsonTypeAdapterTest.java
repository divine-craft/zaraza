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
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.Value;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("HardcodedLineSeparator") // these are used in JSONs
class PolymorphicGsonTypeAdapterTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(PolymorphicGsonTypeAdapter.builder(Foo.class, "type")
                        .subtype("bar", Bar.class, Bar.typeAdapter())
                        .subtype("baz", Baz.class, Baz.typeAdapter())
                        .subtype("qux", Qux.class, Qux.typeAdapter())
                        .build()
                )
                .create();
    }

    @Test
    void testPicksCorrectType() {
        assertEquals(
                new Bar("hello", "world"),
                gson.fromJson("{\n"
                                // language=JSON
                                + "  \"type\": \"bar\",\n"
                                + "  \"common\": \"hello\",\n"
                                + "  \"bar-unique\": \"world\"\n"
                                + "}",
                        Foo.class
                )
        );
    }

    @Test
    void testWorksWithGenericType() {
        assertEquals(
                new Baz<>("omagad", 123),
                gson.fromJson("{\n"
                                // language=JSON
                                + "  \"type\": \"baz\",\n"
                                + "  \"common\": \"omagad\",\n"
                                + "  \"baz-unique\": 123\n"
                                + "}",
                        Foo.class
                )
        );
    }

    @Test
    void testWorksWithInterface() {
        assertEquals(
                ImmutableQux.of(8800),
                gson.fromJson("{\n"
                                // language=JSON
                                + "  \"type\": \"qux\",\n"
                                + "  \"value\": 8800\n"
                                + "}",
                        Foo.class
                )
        );
    }

    @Test
    void testDoesNotProduceFalseMatches() {
        assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson("{\n"
                        // language=JSON
                        + "  \"type\": \"pong\",\n"
                        + "  \"common\": \"hello\",\n"
                        + "  \"bar-unique\": \"world\"\n"
                        + "}", Foo.class
                )
        );
        assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson("{\n"
                        // language=JSON
                        + "  \"type\": \"bonk\",\n"
                        + "  \"common\": \"omagad\",\n"
                        + "  \"baz-unique\": 123\n"
                        + "}", Foo.class
                )
        );
        assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson("{\n"
                        // language=JSON
                        + "  \"type\": \"yup\",\n"
                        + "  \"value\": 8800\n"
                        + "}", Foo.class
                )
        );
    }

    interface Foo<T> {
        @SuppressWarnings("unused" /* just utilize the generic */)
        T genericValue();
    }

    @Value
    private static class Bar implements Foo<String> {
        @Nullable String commonField;
        @Nullable String barUniqueField;

        @Override
        public @Nullable String genericValue() {
            return barUniqueField;
        }

        public static @NotNull TypeAdapter<Bar> typeAdapter() {
            return new TypeAdapter<>() {
                @Override
                public void write(final JsonWriter out, final Bar value) throws IOException {
                    out
                            .beginObject()
                            .name("common").value(value.commonField)
                            .name("bar-unique").value(value.barUniqueField)
                            .endObject();
                }

                @Override
                public Bar read(final JsonReader in) throws IOException {
                    in.beginObject();

                    String commonField = null;
                    String barUniqueField = null;
                    while (in.hasNext()) switch (in.nextName()) {
                        case "common": {
                            commonField = in.nextString();
                            break;
                        }
                        case "bar-unique": {
                            barUniqueField = in.nextString();
                            break;
                        }
                    }
                    in.endObject();

                    return new Bar(commonField, barUniqueField);
                }
            };
        }
    }

    @Value
    private static class Baz<T extends Number> implements Foo<@Nullable T> {
        @Nullable String commonField;
        @Nullable T bazUniqueField;

        @Override
        public @Nullable T genericValue() {
            return bazUniqueField;
        }

        public static @NotNull TypeAdapter<Baz<?>> typeAdapter() {
            return new TypeAdapter<>() {
                @Override
                public void write(final JsonWriter out, final Baz<?> value) throws IOException {
                    out
                            .beginObject()
                            .name("common").value(value.commonField)
                            .name("baz-unique").value(value.bazUniqueField)
                            .endObject();
                }

                @Override
                public Baz<?> read(final JsonReader in) throws IOException {
                    in.beginObject();

                    String commonField = null;
                    Number bazUniqueField = null;
                    while (in.hasNext()) switch (in.nextName()) {
                        case "common": {
                            commonField = in.nextString();
                            break;
                        }
                        case "baz-unique": {
                            bazUniqueField = in.nextInt(); // fine for test purposes
                            break;
                        }
                    }
                    in.endObject();

                    return new Baz<>(commonField, bazUniqueField);
                }
            };
        }
    }

    private interface Qux<T extends Number> extends Foo<@Nullable T> {

        @Nullable T value();

        @Override
        default @Nullable T genericValue() {
            return value();
        }

        static @NotNull TypeAdapter<Qux<?>> typeAdapter() {
            return new TypeAdapter<>() {
                @Override
                public void write(final JsonWriter out, final Qux<?> value) throws IOException {
                    out
                            .beginObject()
                            .name("value").value(value.value())
                            .endObject();
                }

                @Override
                public Qux<?> read(final JsonReader in) throws IOException {
                    in.beginObject();

                    Number value = null;
                    while (in.hasNext()) if ("value" .equals(in.nextName())) value = in.nextInt();
                    in.endObject();

                    return ImmutableQux.of(value);
                }
            };
        }
    }

    @Accessors(fluent = true)
    @Value(staticConstructor = "of")
    private static class ImmutableQux<T extends Number> implements Qux<T> {
        @Nullable T value;
    }
}