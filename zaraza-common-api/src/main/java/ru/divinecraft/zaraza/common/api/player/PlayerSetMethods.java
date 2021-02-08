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

package ru.divinecraft.zaraza.common.api.player;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This is a super-interface of {@link PlayerSet}
 * representing its methods overlapping with {@link java.util.Set}.
 */
public interface PlayerSetMethods {

    /**
     * Gets the size of this set of players.
     *
     * @return size of this set of players
     */
    int size();

    /**
     * Checks if this set is empty.
     *
     * @return {@code true} if this set os empty and {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Creates a {@link Spliterator spliterator} over this set's {@link Player players}.
     *
     * @return {@link Spliterator spliterator} over this set's {@link Player players}
     */
    @NotNull Spliterator<@NotNull Player> spliterator();

    /**
     * Creates a {@link Stream stream} over this set's {@link Player players}.
     *
     * @return {@link Stream stream} over this set's {@link Player players}
     */
    @NotNull Stream<@NotNull Player> stream();

    /**
     * Creates a parallel {@link Stream stream} over this set's {@link Player players}.
     *
     * @return parallel {@link Stream stream} over this set's {@link Player players}
     */
    @NotNull Stream<@NotNull Player> parallelStream();

    /**
     * Applies the given action to each element of this set.
     *
     *  @param action action to be applied to each element of this set
     *
     * @throws NullPointerException if {@code action} is {@code null}
     */
    void forEach(@NonNull Consumer<? super @NotNull Player> action);
}
