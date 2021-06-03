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

package ru.divinecraft.zaraza.common.api.hologram;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.divinecraft.zaraza.common.api.annotation.BukkitService;
import ru.progrm_jarvis.javacommons.ownership.annotation.Own;
import ru.progrm_jarvis.ultimatemessenger.format.model.TextModel;

/**
 * Manager of {@link Hologram holograms} responsible for their
 * creation and {@link #removeHologram(ViewableHologram) removal}.
 */
@BukkitService("ZarazaCommon")
public interface HologramManager {

    /**
     * Creates a hologram consisting of single text line.
     *
     * @param location location of the created hologram
     * @param text text of the created hologram
     * @return created hologram
     *
     * @throws NullPointerException if {@code location} is {@code null}
     * @throws NullPointerException if {@code text} is {@code null}
     *
     * @see #removeHologram(ViewableHologram)
     */
    @NotNull ViewableHologram createHologramLine(@NonNull @Own Location location,
                                                 @NonNull TextModel<? super Player> text);

    /**
     * Creates a new hologram consisting of a title and multiple lines.
     *
     * @param location location of the created hologram
     * @param title title (the first line) of the created hologram
     * @param lines lines of the created hologram following the title
     * @return created hologram
     *
     * @throws NullPointerException if {@code location} is {@code null}
     * @throws NullPointerException if {@code title} is {@code null}
     * @throws NullPointerException if {@code lines} is {@code null}
     *
     * @see #removeHologram(ViewableHologram)
     */
    @NotNull ViewableHologram createMultilineHologram(@NonNull @Own Location location,
                                                      @NonNull TextModel<? super Player> title,
                                                      @NonNull HologramLineWithOffset @NonNull ... lines);

    /**
     * Removes the hologram created via this manager.
     *
     * @param hologram hologram to be removed
     *
     * @see #createHologramLine(Location, TextModel)
     * @see #createMultilineHologram(Location, TextModel, HologramLineWithOffset...)
     */
    void removeHologram(@NonNull ViewableHologram hologram);
}
