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

package ru.divinecraft.zaraza.common.api.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicTest {

    @Test
    void cashingInitialValueTest() {
        Dynamic<String> dyn = Dynamic.create((n, i) -> true, Flux.empty(), "foo");
        assertEquals(dyn.latest(), "foo");
        assertEquals(dyn.latest(), "foo");
        assertEquals(dyn.latest(), "foo");
    }

    @Test
    void cashingSomeReceivedValueTest() {
        Dynamic<String> dyn = Dynamic.create((n, i) -> true, Flux.just("bar", "nobar", "hello"), "foo");
        assertEquals(dyn.latest(), "hello");
        assertEquals(dyn.latest(), "hello");
    }

}