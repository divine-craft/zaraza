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

package ru.divinecraft.zaraza.common.paper.api.player;

import com.google.common.collect.UnmodifiableIterator;
import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import ru.divinecraft.zaraza.common.api.flow.FlowProcessors;

import java.util.*;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Helper methods for {@link PlayerSet player sets}.
 */
@UtilityClass
public class PlayerSets {

    /**
     * Empty array of {@link Player players}.
     */
    private static final @NotNull Player @NotNull [] EMPTY_PLAYER_ARRAY = new Player[0];

    /**
     * Wraps the given {@link Set} of {@link Player players} into a {@link PlayerSet player set}.
     *
     * @param set set to be wrapped
     * @return wrapped {@link Set} of {@link Player players}
     *
     * @apiNote the behaviour of the created set's methods is undefined if the original set gets mutated
     */
    public @NotNull PlayerSet wrapToPlayerSet(final @NonNull @Unmodifiable Set<@NotNull Player> set) {
        return new UncheckedPlayerSetWrapper(set);
    }

    /**
     * Creates a new {@link MutablePlayerSet mutable player set}.
     *
     * @return newly created player set
     */
    public @NotNull MutablePlayerSet newMutablePlayerSet() {
        return DelegatingMutablePlayerSet.wrap(new HashSet<>());
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class UncheckedPlayerSetWrapper implements PlayerSet {

        @Delegate(types = {Iterable.class, PlayerSetMethods.class, MutablePlayerSetMethods.class})
        @NotNull @Unmodifiable Set<@NotNull Player> set;

        @Override
        public boolean contains(@NotNull final Player player) {
            return set.contains(player);
        }

        @Override
        public @NotNull Player @NotNull [] toArray() {
            return set.toArray(EMPTY_PLAYER_ARRAY);
        }

        @Override
        public boolean containsAll(final @NonNull Collection<@NotNull Player> players) {
            return set.containsAll(players);
        }

        @Override
        public @NotNull @UnmodifiableView Set<@NotNull Player> asUnmodifiableSet() {
            return set;
        }

        @Override
        public @NotNull Enumeration<@NotNull Player> enumeration() {
            return Collections.enumeration(set);
        }

        @Override
        public @NotNull UnmodifiableIterator<@NotNull Player> unmodifiableIterator() {
            return new UnmodifiablePlayerIterator(set.iterator());
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // TODO efficient bulk operations (publish Sets)
    private static final class PublishingPlayerSetWrapper extends AbstractSet<@NotNull Player> {

        /**
         * {@link Set Set} to which all root operations are delegated
         */
        @Delegate(types = PlayerSetMethods.class)
        @NotNull Set<@NotNull Player> set;

        /**
         * Subscriber to which all updates get published
         */
        Flow.@NotNull Subscriber<MutablePlayerSet.@NotNull Update> subscriber;

        // Non-generic non-mutating operations

        @Override
        public boolean contains(final Object entry) {
            return set.contains(entry);
        }

        @Override
        public boolean containsAll(final @NotNull Collection<?> entries) {
            return set.containsAll(entries);
        }

        @Override
        public @NotNull Object @NotNull [] toArray() {
            return set.toArray();
        }

        @Override
        public <T> @NotNull T @NotNull [] toArray(final @NotNull T @NotNull /* verified by method call */ [] array) {
            //noinspection SuspiciousToArrayCall: contract requirement, will get verified by internal set
            return set.toArray(array);
        }

        // Basic mutating operations

        @Override
        public @NotNull Iterator<@NotNull Player> iterator() {
            return new PublishingPlayerIterator(set.iterator(), subscriber);
        }

        @Override
        public boolean add(final @NotNull Player player) {
            final boolean updated;
            if (updated = set.add(player)) subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.ADD, PlayerSet.of(player)));

            return updated;
        }

        @Override
        public boolean remove(final Object entry) {
            final boolean updated;
            if (updated = set.remove(entry)) {
                assert entry instanceof Player
                        : "entry should be of type Player as it was remove from set of Players";
                subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.REMOVE, PlayerSet.of((Player) entry)));
            }

            return updated;
        }

        // Bulk operations

        @Override
        public boolean addAll(final @NonNull Collection<? extends @NotNull Player> added) {
            SortedSet<Player> addedPlayers = null;
            for (val entry : added) if (set.add(entry)) {
                if (addedPlayers == null) addedPlayers = new TreeSet<>(PlayerSet.PLAYER_COMPARATOR);
                addedPlayers.add(entry);
            }

            if (addedPlayers == null) return false;

            subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.ADD, PlayerSet.ofSorted(addedPlayers)));

            return true;
        }

        @Override
        public boolean removeIf(final @NonNull Predicate<? super @NotNull Player> filter) {
            SortedSet<Player> removedPlayers = null;

            for (final var iterator = set.iterator(); iterator.hasNext(); ) {
                final Player player;
                if (filter.test(player = iterator.next())) {
                    iterator.remove();

                    if (removedPlayers == null) removedPlayers = new TreeSet<>(PlayerSet.PLAYER_COMPARATOR);
                    removedPlayers.add(player);
                }
            }

            if (removedPlayers == null) return false;

            subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.REMOVE, PlayerSet.ofSorted(removedPlayers)));

            return true;
        }

        @Override
        @SuppressWarnings("SuspiciousMethodCalls") // the way this method works
        public boolean removeAll(final @NonNull Collection<?> removed) {
            SortedSet<Player> removedPlayers = null;

            // use smaller collection for iteration
            if (size() <= removed.size()) for (final var iterator = set.iterator(); iterator.hasNext(); ) {
                final Player player;
                if (removed.contains(player = iterator.next())) {
                    iterator.remove();

                    if (removedPlayers == null) removedPlayers = new TreeSet<>(PlayerSet.PLAYER_COMPARATOR);
                    removedPlayers.add(player);
                }
            } else for (val entry : removed) if (set.remove(entry)) {
                assert entry instanceof Player
                        : "entry should be of type Player as it was removed from the set containing Players";

                if (removedPlayers == null) removedPlayers = new TreeSet<>(PlayerSet.PLAYER_COMPARATOR);
                removedPlayers.add((Player) entry);
            }

            if (removedPlayers == null) return false;

            subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.REMOVE, PlayerSet.ofSorted(removedPlayers)));

            return true;
        }

        @Override
        public boolean retainAll(final @NonNull Collection<?> kept) {
            SortedSet<Player> removedPlayers = null;
            for (final var iterator = set.iterator(); iterator.hasNext(); ) {
                final Player player;
                if (!kept.contains(player = iterator.next())) {
                    iterator.remove();

                    if (removedPlayers == null) removedPlayers = new TreeSet<>(PlayerSet.PLAYER_COMPARATOR);
                    removedPlayers.add(player);
                }
            }

            if (removedPlayers == null) return false;

            subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.REMOVE, PlayerSet.ofSorted(removedPlayers)));

            return true;
        }
    }

    /**
     * {@link PlayerSet Player set} delegating storage logic to a {@link Set} of {@link Player players}.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class DelegatingMutablePlayerSet implements MutablePlayerSet {

        /**
         * {@link Set Set} to which all root operations are delegated
         */
        @Delegate(types = {Iterable.class, PlayerSetMethods.class, MutablePlayerSetMethods.class})
        @NotNull Set<@NotNull Player> set;

        /**
         * {@link Flow.Publisher Publisher} of {@link Player players} to which all {@link Flow}
         */
        @Delegate(types = Flow.Publisher.class)
        Flow.@NotNull Publisher<@NotNull Update> publisher;

        /**
         * Creates a new {@link PlayerSet player set} wrapping the given {@link Set} of {@link Player players}.
         *
         * @param set wrapped set of players
         * @return created {@link PlayerSet player set}
         */
        public static @NotNull MutablePlayerSet wrap(final @NotNull Set<@NotNull Player> set) {
            final Flow.Processor<@NotNull Update, @NotNull Update> processor;
            return new DelegatingMutablePlayerSet(
                    new PublishingPlayerSetWrapper(set, processor = FlowProcessors.createProcessor()), processor
            );
        }

        // Methods of PlayerSet unavailable via PlayerSetMethods

        @Override
        public boolean contains(@NotNull final Player player) {
            return set.contains(player);
        }

        @Override
        public @NotNull Player @NotNull [] toArray() {
            return set.toArray(Player[]::new);
        }

        @Override
        public boolean containsAll(final @NonNull Collection<@NotNull Player> players) {
            return set.containsAll(players);
        }

        // Methods of MutablePlayerSet unavailable via MutablePlayerSetMethods

        @Override
        public boolean remove(@NotNull final Player player) {
            return set.remove(player);
        }

        @Override
        public boolean retainAll(final @NonNull Collection<@NotNull Player> players) {
            return set.retainAll(players);
        }

        @Override
        public boolean removeAll(final @NonNull Collection<@NotNull Player> players) {
            return set.removeAll(players);
        }

        // Conversions to unmodifiable views

        @Override
        public @NotNull @UnmodifiableView Set<@NotNull Player> asUnmodifiableSet() {
            return Collections.unmodifiableSet(set);
        }

        @Override
        public @NotNull Enumeration<@NotNull Player> enumeration() {
            return Collections.enumeration(set);
        }

        @Override
        public @NotNull UnmodifiableIterator<@NotNull Player> unmodifiableIterator() {
            return new UnmodifiablePlayerIterator(set.iterator());
        }

        // Conversion to modifiable view

        @Override
        @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // this is part of contract
        public @NotNull Set<@NotNull Player> asSet() {
            return set;
        }
    }

    /**
     * {@link Iterator Iterator} over {@link Player players} which does not permit any modification operations.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class UnmodifiablePlayerIterator extends UnmodifiableIterator<@NotNull Player> {

        /**
         * {@link Iterator Iterator} to which all read-logic is delegated
         */
        @NotNull Iterator<@NotNull Player> set;

        @Override
        public boolean hasNext() {
            return set.hasNext();
        }

        @Override
        public @NotNull Player next() {
            return set.next();
        }

        @Override
        public void forEachRemaining(final @NotNull Consumer<? super @NotNull Player> action) {
            set.forEachRemaining(action);
        }
    }

    /**
     * {@link Iterator Iterator} over {@link Player players} publishing updates done via it to the specified subscriber.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class PublishingPlayerIterator implements Iterator<@NotNull Player> {

        /**
         * {@link Iterator Iterator} to which all read-logic is delegated
         */
        @NotNull Iterator<@NotNull Player> set;

        /**
         * Subscriber being notified on modifications.
         */
        Flow.@NotNull Subscriber<MutablePlayerSet.@NotNull Update> subscriber;

        @NonFinal @Nullable Player last;

        @Override
        public boolean hasNext() {
            return set.hasNext();
        }

        @Override
        public @NotNull Player next() {
            return last = set.next();
        }

        @Override
        public void remove() {
            set.remove();

            val thisLast = last;
            assert thisLast != null : "last cannot be null as something was removed from iterator of non-null Players";

            subscriber.onNext(MutablePlayerSet.Update.create(MutablePlayerSet.Update.Action.REMOVE, PlayerSet.of(thisLast)));
        }

        @Override
        public void forEachRemaining(final @NotNull Consumer<? super @NotNull Player> action) {
            set.forEachRemaining(action);
        }
    }
}
