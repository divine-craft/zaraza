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

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import ru.divinecraft.zaraza.common.api.player.PlayerSet;

/**
 * A floating text or image visible to {@link org.bukkit.entity.Player players}.
 */
public interface Hologram {

    /**
     * Viewers of this hologram.
     *
     * @return viewers of this hologram
     */
    @NotNull PlayerSet viewers();

    /**
     * Gets the location of this hologram.
     *
     * @return current location of this hologram
     */
    default @NotNull Location location() {
        return locationView().clone();
    }

    /**
     * Gets the view of this hologram's location.
     *
     * @return view of this hologram's location
     *
     * @apiNote this location is <b>unsafe</b> to modify thus it should only be read
     */
    @NotNull Location locationView();
}
