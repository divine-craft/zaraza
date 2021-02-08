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

package ru.divinecraft.zaraza.common.api.player;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;

/**
 * This is a super-interface of {@link MutablePlayerSet}
 * representing its methods overlapping with {@link java.util.Set}.
 */
public interface MutablePlayerSetMethods extends PlayerSetMethods {

    void clear();

    boolean add(@NotNull Player player);

    @NotNull Iterator<@NotNull Player> iterator();

    boolean addAll(@NonNull Collection<? extends Player> players);

    @NotNull Spliterator<@NotNull Player> spliterator();

    boolean removeIf(@NonNull Predicate<? super @NotNull Player> filter);
}
