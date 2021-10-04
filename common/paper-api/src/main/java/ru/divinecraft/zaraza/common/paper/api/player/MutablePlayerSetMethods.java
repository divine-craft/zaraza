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

package ru.divinecraft.zaraza.common.paper.api.player;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This is a super-interface of {@link MutablePlayerSet}
 * representing its methods overlapping with {@link java.util.Set}.
 */
public interface MutablePlayerSetMethods extends PlayerSetMethods, Iterable<@NotNull Player> {

    /**
     * Clears this set.
     */
    void clear();

    /**
     * Adds the specified player to this set.
     *
     * @param player player added to this set
     * @return {@code true} if this set changed as the result of this call and {@code false} otherwise
     */
    boolean add(@NotNull Player player);

    /**
     * Adds all specified players to this set.
     *
     * @param players players added to this set
     * @return {@code true} if this set changed as the result of this call and {@code false} otherwise
     *
     * @throws NullPointerException if {@code players} is {@code null}
     */
    boolean addAll(@NonNull Collection<? extends Player> players);

    /**
     * Removes all players for which the filter returns {@code true}.
     *
     * @param filter filter checking which players to remove
     * @return {@code true} if this set changed as the result of this call and {@code false} otherwise
     *
     * @throws NullPointerException if {@code filter} is {@code null}
     */
    boolean removeIf(@NonNull Predicate<? super @NotNull Player> filter);

    @Override // this is required to resolve conflict with the same yet abstract method in PlayerSetMethods
    @NotNull Spliterator<@NotNull Player> spliterator();

    @Override // this is required to resolve conflict with the same yet abstract method in PlayerSetMethods
    void forEach(@NonNull Consumer<? super @NotNull Player> action);
}
