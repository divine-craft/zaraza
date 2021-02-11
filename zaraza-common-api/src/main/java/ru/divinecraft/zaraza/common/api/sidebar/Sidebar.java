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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.divinecraft.zaraza.common.api.player.PlayerViewed;
import ru.progrm_jarvis.ultimatemessenger.format.model.TextModel;

import java.util.concurrent.Flow;

/**
 * Dynamic {@link Player player} sidebar display.
 */
public interface Sidebar<L extends Sidebar.@NotNull Line> extends PlayerViewed {

    /**
     * Gets the title of this sidebar.
     *
     * @return title of this sidebar
     */
    @NotNull Flow.Publisher<@NotNull TextModel<@NotNull Player>> title();

    /**
     * Gets the lines of this sidebar.
     *
     * @return lines of this sidebar
     */
    @NotNull Lines<L> lines();

    /**
     * Line of {@link Sidebar sidebar}.
     */
    interface Line {

        /**
         * Gets the text of this line.
         *
         * @return text of this line
         */
        @NotNull Flow.Publisher<@NotNull TextModel<@NotNull Player>> text();

        /**
         * Gets the value of this line.
         *
         * @return value of this line
         */
        @NotNull Flow.Publisher<@NotNull Integer> value();
    }

    /**
     * An immutable array of {@link Line sidebar lines}.
     */
    interface Lines<L extends @NotNull Line> extends Iterable<L> {

        /**
         * Gets the count of the lines.
         *
         * @return count of the lines
         */
        @Contract(pure = true)
        int count();

        /**
         * Gets the line at the specified index.
         *
         * @param index index of the line
         * @return line at the gicen index
         *
         * @throws IndexOutOfBoundsException if the index is negative or is greater
         * or equal to the {@link #count() count}.
         */
        @Contract(pure = true)
        @NotNull L at(int index);
    }
}

