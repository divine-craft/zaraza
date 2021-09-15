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

package ru.divinecraft.zaraza.game.api.manager;

import lombok.NonNull;
import lombok.val;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.divinecraft.zaraza.common.api.annotation.BukkitService;
import ru.divinecraft.zaraza.game.api.arena.GameArena;
import ru.progrm_jarvis.javacommons.service.PendingService;

/**
 * Loading {@link GameManager} allowing arena registration.
 */
@BukkitService("ZarazaGame")
public interface LoadingGameManager
        extends PendingService<@NotNull Plugin, LoadingGameManager.@NotNull PreLoaders, @NotNull GameManager> {

    interface PreLoaders {

        /**
         * Registers the provided arena.
         *
         * @param arena arena to be registered
         * @throws NullPointerException if {@code arena} is {@code null}
         */
        void registerArena(@NonNull GameArena<?, ?> arena);

        /**
         * Registers the provided arenas.
         *
         * @param arenas arenas to be registered
         * @throws NullPointerException if {@code arenas} is {@code null}
         * @throws NullPointerException any of the arenas is {@code null}
         */
        default void registerArenas(final @NonNull GameArena<?, ?> @NonNull ... arenas) {
            for (val arena : arenas) {
                if (arena == null) throw new NullPointerException("One of the arenas is `null`");
                registerArena(arena);
            }
        }

        /**
         * Registers the provided arenas.
         *
         * @param arenas arenas to be registered
         * @throws NullPointerException if {@code arenas} is {@code null}
         * @throws NullPointerException any of the arenas is {@code null}
         */
        default void registerArenas(final @NonNull Iterable<@NonNull GameArena<?, ?>> arenas) {
            for (val arena : arenas) {
                if (arena == null) throw new NullPointerException("One of the arenas is `null`");
                registerArena(arena);
            }
        }
    }
}
