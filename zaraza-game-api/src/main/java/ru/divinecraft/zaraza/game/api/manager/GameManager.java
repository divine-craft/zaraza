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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import ru.divinecraft.zaraza.common.api.annotation.BukkitService;
import ru.divinecraft.zaraza.game.api.arena.GameArena;

import java.util.Collection;

/**
 * Manager responsible for managing {@link GameArena game arenas}.
 */
@BukkitService("ZarazaGame")
public interface GameManager {

    /**
     * Gets the registered arenas.
     *
     * @return registered arenas
     */
    @Contract(pure = true)
    @NotNull @Unmodifiable Collection<? extends @NotNull GameArena<?, ?>> arenas();
}
