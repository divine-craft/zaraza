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

package ru.divinecraft.zaraza.common.api.sidebar;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.divinecraft.zaraza.common.api.annotation.BukkitService;
import ru.progrm_jarvis.ultimatemessenger.format.model.TextModel;

/**
 * Manager of dynamic {@link Player player} sidebars.
 */
@BukkitService("ZarazaCommon")
public interface SidebarManager {

    /**
     * Creates a new dynamic sidebar with the given title.
     *
     * @param title title of the created sidebar
     * @return created sidebar
     */
    @NotNull MutableSidebar<@NotNull ?> createSidebar(@NotNull TextModel<@NotNull Player> title);
}
