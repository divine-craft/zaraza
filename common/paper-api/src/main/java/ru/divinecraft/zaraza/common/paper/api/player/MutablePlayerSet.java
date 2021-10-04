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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

/**
 * Mutable {@link Player set of players}.
 */
public interface MutablePlayerSet
        extends PlayerSet, MutablePlayerSetMethods, Flow.Publisher<MutablePlayerSet.@NotNull Update> {

    /**
     * Removes the specified player from this set.
     *
     * @param player player removed from this set
     * @return {@code true} if this set changed as the result of this call and {@code false} otherwise
     */
    boolean remove(@NotNull Player player);

    /**
     * Gets a {@link Set} view of this set of players.
     *
     * @return {@link Set} view of this player set
     */
    @NotNull Set<@NotNull Player> asSet();

    /**
     * Keeps only the specified players in set.
     *
     * @param players only players kept in this set
     * @return {@code true} if this set changed as the result of this call and {@code false} otherwise
     *
     * @throws NullPointerException if {@code players} is {@code null}
     */
    boolean retainAll(@NonNull Collection<@NotNull Player> players);

    /**
     * Removes all specified players from this set.
     *
     * @param players players removed from this set
     * @return {@code true} if this set changed as the result of this call and {@code false} otherwise
     *
     * @throws NullPointerException if {@code players} is {@code null}
     */
    boolean removeAll(@NonNull Collection<@NotNull Player> players);

    @Override // this is required to resolve conflict with the same yet abstract method in PlayerSetMethods
    void forEach(@NonNull Consumer<? super @NotNull Player> action);

    /**
     * Update event of a {@link PlayerSet set of players}
     */
    interface Update {

        /**
         * Gets the action performed by this update.
         *
         * @return action performed by this update
         */
        @Contract(pure = true)
        @NotNull Action action();

        /**
         * Gets the updated players.
         *
         * @return updated players
         */
        @Contract(pure = true)
        @NotNull PlayerSet players();

        /**
         * Creates a new player set update with the given values.
         *
         * @param action action performed by this update
         * @param players the updated players
         * @return created player set update
         */
        static @NotNull Update create(final @NotNull Action action, final @NotNull PlayerSet players) {
            return new SimpleUpdate(action, players);
        }

        /**
         * Action of a {@link Update player set update}
         */
        enum Action {
            /**
             * Addition to the set.
             */
            ADD,
            /**
             * Removal from the set.
             */
            REMOVE
        }
    }

    /**
     * Simple implementation of {@link Update player set update}.
     */
    @Value
    @Accessors(fluent = true)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class SimpleUpdate implements Update {

        /**
         * The action performed by this update.
         */
        @NotNull Update.Action action;

        /**
         * The updated player
         */
        @NotNull PlayerSet players;
    }
}
