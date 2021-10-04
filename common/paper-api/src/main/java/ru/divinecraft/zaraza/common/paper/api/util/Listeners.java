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

package ru.divinecraft.zaraza.common.paper.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities related to {@link Listeners Bukkit's event listeners}.
 */
@UtilityClass
public class Listeners {

    /**
     * Creates a new empty {@link Listener Bukkit's event listener}.
     * This is intended to be used with {@link
     * org.bukkit.plugin.PluginManager#registerEvent(Class, Listener, EventPriority, EventExecutor, Plugin)
     * } and {@link
     * org.bukkit.plugin.PluginManager#registerEvent(Class, Listener, EventPriority, EventExecutor, Plugin, boolean)
     * } as they require passing an instance of {@link Listener} to them
     * requiring it to be unique to use it in {@link org.bukkit.event.HandlerList#unregister(Listener)}.
     *
     * @return newly created unique {@link Listener Bukkit's event listener}
     */
    @Contract("-> new")
    public @NotNull Listener newEmptyListener() {
        return new EmptyListener();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EmptyListener implements Listener {}
}
