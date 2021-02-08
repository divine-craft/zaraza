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
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Set;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

class PlayerSetsTest {

    @SuppressWarnings("unchecked") // generic mock instantiation
    private static @NotNull Flow.Subscriber<MutablePlayerSet.Update> mockSubscriber() {
        return mock(Flow.Subscriber.class);
    }

    private static @NotNull Player playerMock(final @NonNull String name) {
        val mock = mock(Player.class);

        when(mock.getName()).thenReturn(name);
        when(mock.getDisplayName()).thenReturn(name);
        when(mock.getPlayerListName()).thenReturn(name);
        when(mock.getCustomName()).thenReturn(name);

        return mock;
    }

    @Test
    void newMutablePlayerSet_onSubscribeGetsCalled() {
        val subscriber = mockSubscriber();
        val set = PlayerSets.newMutablePlayerSet();

        set.subscribe(subscriber);
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, times(0)).onNext(any());
    }

    @Test
    void newMutablePlayerSet_subscriptionCancelDoesNotFail() {
        val subscriber = mockSubscriber();
        val set = PlayerSets.newMutablePlayerSet();

        set.subscribe(subscriber);

        val subscription = new Flow.Subscription[1];
        verify(subscriber, times(1)).onSubscribe(argThat(newSubscription -> {
            subscription[0] = newSubscription;

            return newSubscription != null;
        }));

        assertDoesNotThrow((Executable) () -> subscription[0].cancel());
    }

    @Test
    void newMutablePlayerSet_subscriptionIsSame() {
        val subscriber = mockSubscriber();
        val set = PlayerSets.newMutablePlayerSet();

        set.subscribe(subscriber);
        verify(subscriber, times(1)).onSubscribe(any());

        set.subscribe(subscriber);
        verify(subscriber, times(1)).onSubscribe(any());
    }

    @Test
    void newMutablePlayerSet_correctSimpleLogic() {
        val john = playerMock("John");
        val jack = playerMock("Jack");
        val bob = playerMock("Bob");
        val joper = playerMock("Joper");

        @SuppressWarnings("unchecked") final Flow.Subscriber<MutablePlayerSet.Update> subscriber
                = mock(Flow.Subscriber.class);

        val set = PlayerSets.newMutablePlayerSet();

        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertFalse(set.contains(john));
        assertFalse(set.contains(jack));
        assertFalse(set.contains(bob));
        assertFalse(set.contains(joper));

        // initial subscription
        set.subscribe(subscriber);
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, times(0)).onNext(any());
        // obviously no changes to the size should happen
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());

        set.add(john);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertTrue(set.contains(john));
        assertFalse(set.contains(jack));
        assertFalse(set.contains(bob));
        assertFalse(set.contains(joper));
        verify(subscriber).onNext(eq(MutablePlayerSet.Update.of(MutablePlayerSet.Update.Action.ADD, Set.of(john))));

        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertTrue(set.contains(john));
        assertFalse(set.contains(jack));
        assertFalse(set.contains(bob));
        assertFalse(set.contains(joper));
        // no extra publish should happen
        verify(subscriber).onNext(eq(MutablePlayerSet.Update.of(MutablePlayerSet.Update.Action.ADD, Set.of(john))));

        set.remove(john);
        verify(subscriber).onNext(eq(MutablePlayerSet.Update.of(MutablePlayerSet.Update.Action.REMOVE, Set.of(john))));
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertFalse(set.contains(john));
        assertFalse(set.contains(jack));
        assertFalse(set.contains(bob));
        assertFalse(set.contains(joper));

        set.add(jack);
        verify(subscriber).onNext(eq(MutablePlayerSet.Update.of(MutablePlayerSet.Update.Action.ADD, Set.of(jack))));
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertFalse(set.contains(john));
        assertTrue(set.contains(jack));
        assertFalse(set.contains(bob));
        assertFalse(set.contains(joper));

        set.add(john);
        verify(subscriber).onNext(eq(MutablePlayerSet.Update.of(MutablePlayerSet.Update.Action.ADD, Set.of(jack))));
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertTrue(set.contains(john));
        assertTrue(set.contains(jack));
        assertFalse(set.contains(bob));
        assertFalse(set.contains(joper));
    }
}