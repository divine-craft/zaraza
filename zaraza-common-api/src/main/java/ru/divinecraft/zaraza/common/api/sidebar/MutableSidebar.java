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
import ru.progrm_jarvis.ultimatemessenger.format.model.TextModel;

import java.util.concurrent.Flow;

/**
 * Mutable {@link Sidebar sidebar}.
 */
public interface MutableSidebar<L extends MutableSidebar.@NotNull MutableLine> extends ViewableSidebar<L> {

    @Override
    @NotNull Flow.Processor<@NotNull TextModel<@NotNull Player>, @NotNull TextModel<@NotNull Player>> title();

    /**
     * Mutable {@link Sidebar.Line sidebar line}.
     */
    interface MutableLine extends Line {

        @Override
        @NotNull Flow.Processor<@NotNull TextModel<@NotNull Player>, @NotNull TextModel<@NotNull Player>> text();

        @Override
        @NotNull Flow.Processor<@NotNull Integer, @NotNull Integer> value();
    }
}
