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

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link Set Set} of {@link Player players}.
 */
public interface PlayerSet extends PlayerSetMethods {

    Comparator<@NotNull Player> PLAYER_COMPARATOR = Comparator
            .<Player>comparingLong(player -> player.getUniqueId().getMostSignificantBits())
            .thenComparingLong(player -> player.getUniqueId().getLeastSignificantBits());

    static int hashCodeOf(final @NotNull PlayerSet playerSet) {
        var hashCode = 0;
        for (final var enumeration = playerSet.enumeration(); enumeration.hasMoreElements(); ) hashCode
                += enumeration.nextElement().hashCode();

        return hashCode;
    }

    static boolean contentsEqual(final @NotNull PlayerSet left, final @NotNull PlayerSet right) {
        if (left.size() != right.size()) return false;

        for (final var enumeration = left.enumeration(); enumeration.hasMoreElements(); ) if (
                !right.contains(enumeration.nextElement())) return false;

        return true;
    }

    boolean contains(@NotNull Player player);

    @NotNull Player @NotNull [] toArray();

    boolean containsAll(@NonNull Collection<@NotNull Player> players);

    /**
     * Gets an unmodifiable view of this set of players.
     *
     * @return unmodifiable view of this player set
     */
    @NotNull @UnmodifiableView Set<@NotNull Player> asUnmodifiableSet();

    @NotNull Enumeration<@NotNull Player> enumeration();

    @NotNull Iterator<@NotNull Player> unmodifiableIterator();

    @NotNull @Unmodifiable Spliterator<@NotNull Player> unmodifiableSpliterator();

    @NotNull Stream<@NotNull Player> unmodifiableStream();

    @NotNull Stream<@NotNull Player> unmodifiableParallelStream();

    /**
     * Creates a new player set consisting of the given player.
     *
     * @param player the only player to be contained by the given player set
     * @return created player set
     *
     * @throws NullPointerException of {@code player} is null
     */
    static @NotNull PlayerSet of(final @NonNull Player player) {
        return new SinglePlayerSet(player);
    }

    /**
     * Creates a new player set consisting of the given players.
     *
     * @param players collection of players which will be stored in the given set
     * @return created player set
     *
     * @apiNote the behaviour is undefined if {@code players} is not distinct
     */
    static @NotNull PlayerSet of(final @NonNull @Unmodifiable Collection<? extends @NotNull Player> players) {
        return of(players.toArray(Player[]::new));
    }

    /**
     * Creates a new player set consisting of the given players
     * assuming that {@code players} does not get mutated while created player set is accessed.
     *
     * @param players array of players which will be stored in the given set
     * @return created player set
     *
     * @apiNote the behaviour is undefined if {@code players} is not distinct
     * @apiNote the behaviour is undefined if any method of the created set
     * gets called after mutation of {@code players}
     */
    static @NotNull PlayerSet of(final @NotNull Player @NonNull @Unmodifiable ... players) {
        if (players.length == 1) return new SinglePlayerSet(players[0]);

        Arrays.sort(players, PLAYER_COMPARATOR);

        return new ArrayBasedPlayerSet(players);
    }

    /**
     * Creates a new player set consisting of the given players.
     *
     * @param players array of players which will copied to the created player set
     * @return created player set
     */
    static @NotNull PlayerSet ofCopy(final @NotNull Player @NonNull @Unmodifiable [] players) {
        return of(players.clone());
    }

    /**
     * Creates a new player set consisting of the given players
     * assuming that {@code players} is naturally ordered and does not get mutated while created player set is accessed.
     *
     * @param players array of players which will be stored in the given set
     * @return created player set
     *
     * @apiNote the behaviour is undefined if {@code players} is not naturally ordered
     * @apiNote the behaviour is undefined if any method of the created set
     * gets called after mutation of {@code players}
     */
    static @NotNull PlayerSet ofSorted(final @NotNull Player @NonNull @Unmodifiable [] players) {
        if (players.length == 1) return new SinglePlayerSet(players[0]);

        return new ArrayBasedPlayerSet(players);
    }

    /**
     * Creates a new player set consisting of the given players
     * assuming that {@code players} is naturally ordered.
     *
     * @param players array of players which will be stored in the given set
     * @return created player set
     *
     * @apiNote the behaviour is undefined if {@code players} is not naturally ordered
     */
    static @NotNull PlayerSet ofSortedCopy(final @NotNull Player @NonNull [] players) {
        return ofSorted(players.clone());
    }

    /**
     * Creates a new player set consisting of the given players
     * assuming that {@code players} is naturally ordered.
     *
     * @param players collection of players which will be stored in the given set
     * @return created player set
     *
     * @apiNote the behaviour is undefined if {@code players} is not distinct
     */
    static @NotNull PlayerSet ofSorted(final @NonNull @Unmodifiable Collection<? extends @NotNull Player> players) {
        return of(players.toArray(Player[]::new));
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    final class SinglePlayerSet implements PlayerSet {

        /**
         * The only player contained by this player set
         */
        @NotNull Player player;

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(final @NotNull Player player) {
            return this.player.equals(player);
        }

        @Override
        public @NotNull Player @NotNull [] toArray() {
            return new Player[]{player};
        }

        @Override
        public boolean containsAll(@NonNull final Collection<@NotNull Player> players) {
            return players.size() == 1 && player.equals(players.iterator().next());
        }

        @Override
        public @NotNull @UnmodifiableView Set<@NotNull Player> asUnmodifiableSet() {
            return Set.of(player);
        }

        @Override
        public @NotNull Enumeration<@NotNull Player> enumeration() {
            return new SimpleEnumeration();
        }

        @Override
        public @NotNull Iterator<@NotNull Player> unmodifiableIterator() {
            return new SimpleIterator();
        }

        @Override
        public @NotNull @Unmodifiable Spliterator<@NotNull Player> unmodifiableSpliterator() {
            return new SimpleSpliterator();
        }

        @Override
        public @NotNull Stream<@NotNull Player> unmodifiableStream() {
            return Stream.of(player);
        }

        @Override
        public @NotNull Stream<@NotNull Player> unmodifiableParallelStream() {
            return Stream.of(player);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (!(other instanceof PlayerSet)) return false;

            final PlayerSet playerSet;
            return (playerSet = (PlayerSet) other).size() == 1
                    && player.equals(playerSet.enumeration().nextElement());
        }

        @Override
        public int hashCode() {
            return player.hashCode();
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE)
        private final class SimpleEnumeration implements Enumeration<@NotNull Player> {

            /**
             * Flag indicating if {@link #nextElement()} was called
             */
            boolean wasUsed; // default-initialized to false

            @Override
            public boolean hasMoreElements() {
                return !wasUsed;
            }

            @Override
            public @NotNull Player nextElement() {
                if (wasUsed) throw new NoSuchElementException(
                        "There is no more elements available via this PlayerSet iterator"
                );
                wasUsed = true;

                return player;
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE)
        private final class SimpleIterator implements Iterator<@NotNull Player> {

            /**
             * Flag indicating if {@link #next()} was called
             */
            boolean wasUsed; // default-initialized to false

            @Override
            public boolean hasNext() {
                return !wasUsed;
            }

            @Override
            public @NotNull Player next() {
                if (wasUsed) throw new NoSuchElementException(
                        "There is no more elements available via this PlayerSet iterator"
                );
                wasUsed = true;

                return player;
            }

            @Override
            public void forEachRemaining(final Consumer<? super @NotNull Player> action) {
                if (!wasUsed) {
                    wasUsed = true;
                    action.accept(player);
                }
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE)
        private final class SimpleSpliterator implements Spliterator<@NotNull Player> {

            private static final int ALL_SPLITERATOR_CHARACTERISTICS
                    = Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED
                    | Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.SUBSIZED | Spliterator.CONCURRENT;

            boolean wasUsed; // default-initialized to false

            @Override
            public boolean tryAdvance(final Consumer<? super @NotNull Player> action) {
                if (wasUsed) return false;

                wasUsed = true;
                action.accept(player);

                return true;
            }

            @Override
            public void forEachRemaining(final Consumer<? super @NotNull Player> action) {
                if (!wasUsed) {
                    wasUsed = true;
                    action.accept(player);
                }
            }

            @Override
            public @Nullable Spliterator<@NotNull Player> trySplit() {
                return null /* cannot be split */;
            }

            @Override
            public long estimateSize() {
                return wasUsed ? 0 : 1;
            }

            @Override
            public long getExactSizeIfKnown() {
                return wasUsed ? 0 : 1;
            }

            @Override
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED
                        | Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.SUBSIZED | Spliterator.CONCURRENT;
            }

            @Override
            public boolean hasCharacteristics(final int characteristics) {
                return (ALL_SPLITERATOR_CHARACTERISTICS & characteristics) == characteristics;
            }

            @Override
            public @Nullable Comparator<? super @NotNull Player> getComparator() {
                return PLAYER_COMPARATOR;
            }
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    final class ArrayBasedPlayerSet implements PlayerSet {

        // should be sorted
        @NotNull Player @NotNull [] array;

        @Override
        public int size() {
            return array.length;
        }

        @Override
        public boolean isEmpty() {
            return array.length == 0;
        }

        @Override
        public boolean contains(@NotNull final Player player) {
            return false;
        }

        @Override
        public @NotNull Player @NotNull [] toArray() {
            return array.clone();
        }

        @Override
        public boolean containsAll(@NonNull final Collection<@NotNull Player> players) {
            for (val player : players) if (Arrays.binarySearch(array, player, PLAYER_COMPARATOR) < 0) return false;
            return true;
        }

        @Override
        public @NotNull @UnmodifiableView Set<@NotNull Player> asUnmodifiableSet() {
            return Set.of(array);
        }

        @Override
        public @NotNull Enumeration<@NotNull Player> enumeration() {
            return new SimpleEnumeration();
        }

        @Override
        public @NotNull Iterator<@NotNull Player> unmodifiableIterator() {
            return new SimpleIterator();
        }

        @Override
        public @NotNull @Unmodifiable Spliterator<@NotNull Player> unmodifiableSpliterator() {
            return Spliterators.spliterator(array, Spliterator.IMMUTABLE | Spliterator.ORDERED);
        }

        @Override
        public @NotNull Stream<@NotNull Player> unmodifiableStream() {
            return StreamSupport.stream(unmodifiableSpliterator(), false);
        }

        @Override
        public @NotNull Stream<@NotNull Player> unmodifiableParallelStream() {
            return StreamSupport.stream(unmodifiableSpliterator(), true);
        }

        @Override
        public boolean equals(final @Nullable Object other) {
            return other == this || other instanceof PlayerSet && contentsEqual(this, (PlayerSet) other);
        }

        @Override
        public int hashCode() {
            return hashCodeOf(this);
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE)
        private final class SimpleEnumeration implements Enumeration<@NotNull Player> {

            int nextIndex; // default-initialized to 0

            @Override
            public boolean hasMoreElements() {
                return nextIndex < array.length;
            }

            @Override
            public @NotNull Player nextElement() {
                final int index;
                if ((index = nextIndex++) >= array.length) throw new NoSuchElementException(
                        "There is no more elements available via this PlayerSet iterator");

                return array[index];
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        @FieldDefaults(level = AccessLevel.PRIVATE)
        private final class SimpleIterator implements Iterator<@NotNull Player> {

            int nextIndex; // default-initialized to 0

            @Override
            public boolean hasNext() {
                return nextIndex < array.length;
            }

            @Override
            public @NotNull Player next() {
                final int index;
                if ((index = nextIndex++) >= array.length) throw new NoSuchElementException(
                        "There is no more elements available via this PlayerSet iterator");

                return array[index];
            }

            @Override
            public void forEachRemaining(final Consumer<? super @NotNull Player> action) {
                final Player[] thisArray;
                val length = (thisArray = array).length;
                for (var i = 0; i < length; i++) action.accept(thisArray[i]);
                nextIndex = length;
            }
        }
    }
}
