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
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Stream;

/**
 * {@link Set Set} of {@link Player players}.
 */
public interface PlayerSet extends PlayerSetMethods {

    boolean contains(@NotNull Player player);

    @NotNull Player @NotNull [] toArray();

    boolean containsAll(@NonNull Collection<Player> players);

    /**
     * Gets an unmodifiable view of this set of players.
     *
     * @return unmodifiable view of this player set
     */
    @NotNull @UnmodifiableView Set<@NotNull Player> asUnmodifiableSet();

    @NotNull Enumeration<@NotNull Player> enumeration();

    @NotNull Iterator<@NotNull Player> unmodifiableIterator();

    @NotNull @Unmodifiable Spliterator<@NotNull Player> unmodifiableSpliterator();

    @NotNull @Unmodifiable Stream<@NotNull Player> unmodifiableStream();

    @NotNull @Unmodifiable Stream<@NotNull Player> unmodifiableParallelStream();
}
