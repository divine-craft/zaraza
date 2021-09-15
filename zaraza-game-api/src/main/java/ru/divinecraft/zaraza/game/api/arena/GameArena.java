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

package ru.divinecraft.zaraza.game.api.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import ru.divinecraft.zaraza.game.api.player.GamePlayer;
import ru.progrm_jarvis.javacommons.object.Result;

import java.util.Set;

/**
 * Individual game arena.
 *
 * @param <TPlayer> type of players playing on this arena
 */
public interface GameArena<TProperties extends GameArenaProperties, TPlayer extends GamePlayer> {

    // Properties

    /**
     * Gets the properties of this arena.
     *
     * @return properties of this arena
     */
    @Contract(pure = true)
    @NotNull TProperties properties();

    // Player management

    /**
     * Attempts to join the player to this arena.
     *
     * @param player player attempting to join the arena
     * @return a successful result of a newly created game player corresponding to the provided player
     * if the join succeeds or an error result otherwise
     */
    @NotNull Result<@NotNull TPlayer, @NotNull ArenaJoinError> join(@NotNull Player player);

    /**
     * An error which may occur on {@link #join(Player)}.
     */
    enum ArenaJoinError {
        /**
         * The arena is loading.
         */
        ARENA_LOADING,

        /**
         * The arena has reached its player limit.
         */
        OUT_OF_SLOTS,

        /**
         * The game is already running and does not support in-game joins.
         */
        GAME_RUNNING,

        /**
         * Arena-specific error.
         */
        OTHER
    }

    /**
     * Gets the {@link TPlayer game player} representation of the provided player for this arena.
     *
     * @param player player for whom game player representation should be retrieved
     * @return game player corresponding to the player if he is playing this arena or {@code null} otherwise
     */
    @Nullable TPlayer getPlayer(@NotNull Player player);

    /**
     * Attempts to leave the player from this arena.
     *
     * @param player player attempting to leave the arena
     * @return {@code true} if the leave succeeds (i.e. the player leaves the arena) and {@code false} otherwise
     */
    boolean leave(@NotNull Player player);

    /**
     * Gets the number of currently playing players.
     *
     * @return number of currently playing players
     */
    int getPlayerCount();

    /**
     * Gets the number of currently playing players.
     *
     * @return number of currently playing players
     */
    @NotNull @Unmodifiable Set<? extends TPlayer> getPlayers();
}