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

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>{@link TypeAdapterFactory} capable of providing polymorphic {@link TypeAdapter}
 * capable of serializing and deserializing multiple different types
 * having one common super-type by having a discriminant field in JSON structure.</p>
 *
 * <p>This is identical to {@code RuntimeTypeAdapterFactory} which is part of {@code gson-extras}
 * but seems to be unsupported</p>
 *
 * @param <B> base type of all serialized types
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class PolymorphicGsonTypeAdapter<B> implements TypeAdapterFactory {

    /**
     * Base type of all serialized types
     */
    @NotNull Class<B> baseType;

    /**
     * Internal type adapter provided by the factory when needed.
     */
    @NotNull TypeAdapter<B> typeAdapter;

    /**
     * Creates a new builder used for creation of polymorphic type adapter factory.
     *
     * @param baseType base type of all serialized types
     * @param discriminantFieldName name of the field used to store type name
     * @param <B> base type of all serialized types
     * @return builder to be used for creation of polymorphic type adapter factory
     *
     * @throws NullPointerException if {@code baseType} is {@code null}
     * @throws NullPointerException if {@code discriminantFieldName} is {@code null}
     */
    public static <B> @NotNull Builder<B> builder(final @NonNull Class<B> baseType,
                                                  final @NonNull String discriminantFieldName) {
        return new SimpleBuilder<>(baseType, discriminantFieldName, new HashMap<>(), new HashMap<>());
    }

    @Override
    @SuppressWarnings("unchecked") // Type-checking is done via `type` partameter
    public <T> @Nullable TypeAdapter<T> create(final @NotNull Gson gson, final @NotNull TypeToken<T> type) {
        return baseType.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) typeAdapter : null;
    }

    /**
     * Builder used for creation of {@link PolymorphicGsonTypeAdapter}.
     *
     * @param <B> base type of all serialized types
     */
    public interface Builder<B> {

        /**
         * Adds a new supported subtype to the created polymorphic type adapter.
         *
         * @param name name by which the subtype should be identified
         * @param type type being added
         * @param adapter adapter to be used for serialization of the type
         * @param <T> type being added
         * @return this builder
         *
         * @throws NullPointerException if {@code name} is {@code null}
         * @throws NullPointerException if {@code type} is {@code null}
         * @throws NullPointerException if {@code adapter} is {@code null}
         */
        @Contract("_, _, _ -> this")
        <T extends B> @NotNull Builder<B> subtype(@NonNull String name, @NonNull Class<T> type,
                                                  @NonNull TypeAdapter<? extends T> adapter);

        /**
         * Creates a new polymorphic type adapter from this builder.
         *
         * @return created polymorphic type adapter
         */
        @NotNull TypeAdapterFactory build();
    }

    //<editor-fold desc="PolymorhicTypeAdapter implementation" defaultstate="collapsed">

    /**
     * Type adapter capable of serializing and deserializing multiple different types
     * having one common super-type by having a discriminant field in JSON structure.
     *
     * @param <B> base type of all serialized types
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class PolymorhicTypeAdapter<B> extends TypeAdapter<B> {

        /**
         * Name of the field used to store type name
         */
        @NotNull String discriminantFieldName;

        /**
         * Adapters by their names
         */
        @NotNull @Unmodifiable Map<@NotNull String, @NotNull TypeAdapter<? extends B>> adaptersByName;

        /**
         * Names adapters by their types
         */
        @NotNull @Unmodifiable Map<@NotNull Class<? extends B>, @NotNull NamedTypeAdapter<? extends B>> adaptersByType;

        /**
         * Finds the named type adapter by the given type.
         *
         * @param type type by which to find the adapter
         * @return named adapter for the given type
         */
        @SuppressWarnings("unchecked")
        private @Nullable <T extends B> NamedTypeAdapter<T> findTypeAdapter(final @NotNull Class<T> type) {
            //@formatter:off
            for (val adapterByType : adaptersByType.entrySet()) if (adapterByType.getKey()
                    .isAssignableFrom(type)) return (NamedTypeAdapter<T>) adapterByType.getValue();
            //@formatter:on

            return null;
        }

        /**
         * Finds the adapter by the given name.
         *
         * @param name name by which to find the adapter
         * @return adapter for the given name
         */
        private @Nullable TypeAdapter<? extends B> findTypeAdapter(final @NotNull String name) {
            return adaptersByName.get(name);
        }

        /**
         * Gets the generic class of the provided value.
         *
         * @param value value whose type should be resolved
         * @param <T> expected generic type of the value
         * @return generic class of the value
         */
        @SuppressWarnings("unchecked")
        private static @NotNull <T> Class<T> genericClassOf(final @NotNull T value) {
            return (Class<T>) value.getClass();
        }

        @Override
        @SuppressWarnings("unchekced")
        public void write(final @NotNull JsonWriter out, final @Nullable B value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            final JsonObject structure;
            {
                final NamedTypeAdapter<B> namedTypeAdapter;
                final Class<B> type; // note: generic bound is unavailable here
                if ((namedTypeAdapter = findTypeAdapter(type = genericClassOf(value)))
                        == null) throw new IllegalArgumentException("Unknown value of type \"" + type + '"');

                {
                    final JsonElement structureElement;
                    if (!(structureElement = namedTypeAdapter.typeAdapter().toJsonTree(value))
                            .isJsonObject()) throw new IllegalArgumentException(
                            "TypeAdapter for type " + type + " should create a JSON object"
                    );
                    structure = structureElement.getAsJsonObject();
                }

                final String thisDiscriminantFieldName;
                if (structure
                        .has(thisDiscriminantFieldName = discriminantFieldName)
                ) throw new IllegalArgumentException(
                        "TypeAdapter for type " + type + " should not create a field named \""
                                + thisDiscriminantFieldName + "\""
                );

                structure.addProperty(thisDiscriminantFieldName, namedTypeAdapter.name());
            }

            Streams.write(structure, out);
        }

        @Override
        public B read(final @NotNull JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) return null;

            // JSON structure has to be parsed fully because location of discriminant is unspecific
            final JsonObject structure;
            {
                final JsonElement structureElement;
                if (!(structureElement = Streams.parse(in)).isJsonObject()) throw new JsonParseException(
                        "JSON structure should be an object"
                );
                structure = structureElement.getAsJsonObject();
            }

            final TypeAdapter<? extends B> adapter;
            {
                final String thisDiscriminantFieldName;
                final JsonPrimitive discriminantPrimitive;
                {
                    final JsonElement discriminantElement;
                    if ((discriminantElement = structure.get(thisDiscriminantFieldName = discriminantFieldName))
                            == null) throw new JsonParseException(
                            "Missing discriminant field named \"" + thisDiscriminantFieldName + '"'
                    );

                    if (!discriminantElement.isJsonPrimitive()
                            || !(discriminantPrimitive = discriminantElement.getAsJsonPrimitive()).isString()
                    ) throw new JsonParseException(
                            "Discriminant named \"" + thisDiscriminantFieldName + "\" should be a string"
                    );
                }

                final String discriminant;
                if ((adapter = findTypeAdapter(discriminant = discriminantPrimitive.getAsString()))
                        == null) throw new JsonParseException("Unknown type by discriminant \"" + discriminant + '"');
                structure.remove(thisDiscriminantFieldName);
            }

            return adapter.fromJsonTree(structure);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Builder implementation" defaultstate="collapsed">

    /**
     * Simple {@link Builder} implementation.
     *
     * @param <B> base type of all serialized types
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class SimpleBuilder<B> implements Builder<B> {

        /**
         * Base type of all serialized types
         */
        @NotNull Class<B> baseType;

        /**
         * Name of the field used to store type name
         */
        @NotNull String discriminantFieldName;

        /**
         * Adapters by their names
         */
        @NotNull Map<@NotNull String, @NotNull TypeAdapter<? extends B>> adaptersByName;

        /**
         * Names adapters by their types
         */
        @NotNull Map<@NotNull Class<? extends B>, @NotNull NamedTypeAdapter<? extends B>> adaptersByType;

        @Override
        public @NotNull <T extends B> Builder<B> subtype(final @NonNull String name, final @NonNull Class<T> type,
                                                         final @NonNull TypeAdapter<? extends T> adapter) {
            adaptersByName.put(name, adapter);
            adaptersByType.put(type, new ImmutableNamedTypeAdapter<>(name, adapter));

            return this;
        }

        @Override
        public @NotNull TypeAdapterFactory build() {
            return new PolymorphicGsonTypeAdapter<>(
                    baseType,
                    new PolymorhicTypeAdapter<>(discriminantFieldName, adaptersByName, adaptersByType)
            );
        }
    }
    //</editor-fold>

    //<editor-fold desc="NamedTypeAdapter interface & implementation" defaultstate="collapsed">

    /**
     * {@link TypeAdapter} which has an associated{@link #name()} name.
     *
     * @param <T> type of the type-adapter's value
     */
    private interface NamedTypeAdapter<T> {

        /**
         * Gets the name of this type adapter.
         *
         * @return name of this type adapter
         */
        @NotNull String name();

        /**
         * Gets the wrapped type adapter.
         *
         * @return wrapped type adapter
         */
        @NotNull TypeAdapter<T> typeAdapter();
    }

    /**
     * Simple immutable implementation of {@link NamedTypeAdapter}.
     *
     * @param <T> type of the type-adapter's value
     */
    @Value
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ImmutableNamedTypeAdapter<T> implements NamedTypeAdapter<T> {

        /**
         * Name of this type adapter
         */
        @NotNull String name;

        /**
         * Wrapped type adapter
         */
        @NotNull TypeAdapter<T> typeAdapter;
    }
    //</editor-fold>
}
