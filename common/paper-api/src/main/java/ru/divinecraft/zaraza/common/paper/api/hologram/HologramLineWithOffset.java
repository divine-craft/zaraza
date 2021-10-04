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

package ru.divinecraft.zaraza.common.paper.api.hologram;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.javacommons.ownership.annotation.Own;
import ru.progrm_jarvis.ultimatemessenger.format.model.TextModel;

/**
 * Information about a {@link Hologram hologram} line used for its creation.
 */
@Value.Immutable
public interface HologramLineWithOffset {

    /**
     * Gets the text of this line.
     *
     * @return text of this line
     */
    @Value.Parameter(order = 1)
    @NotNull TextModel<? super Player> text();

    /**
     * Gets the offset of this line relative to the previous one.
     *
     * @return offset of this line relative to the previous one
     */
    @Value.Parameter(order = 2)
    @NotNull Vector offset();

    /**
     * Creates a new hologram line "record".
     *
     * @param offset offset of the created line relative to the previous one
     * @param text text of the created line
     * @return create hologram line with offset
     */
    static @NotNull HologramLineWithOffset of(final @NonNull TextModel<? super Player> text,
                                              final @NonNull @Own Vector offset) {
        return ImmutableHologramLineWithOffset.of(text, offset);
    }
}
