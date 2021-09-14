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

import net.kyori.adventure.text.Component;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Constant properties of a game arena.
 */
@Value.Immutable
@Gson.TypeAdapters
public interface GameArenaProperties {

    /**
     * Gets the simple name of this arena.
     *
     * @return simple name of this arena
     */
    @Contract(pure = true)
    @NotNull Component name();

    /**
     * Gets the human-friendly name of this arena.
     *
     * @return human-friendly name of this arena
     */
    @Contract(pure = true)
    @NotNull Component displayName();

    /**
     * Gets the minimal number of player required to start the arena.
     *
     * @return minimal number of player required to start the arena
     */
    @Contract(pure = true)
    int minPlayers();

    /**
     * Gets the maximal allowed number of players.
     *
     * @return maximal allowed number of players
     */
    @Contract(pure = true)
    int maxPlayers();
}
