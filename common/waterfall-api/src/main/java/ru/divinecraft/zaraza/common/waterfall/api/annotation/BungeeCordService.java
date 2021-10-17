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

package ru.divinecraft.zaraza.common.waterfall.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker indicating that this target should be provided at runtime
 * as a {@link net.md_5.bungee.api.ProxyServer Proxy} service.
 *
 * @apiNote there currently is no well-defined Bungeecord services API
 * but there is an <a href=https://github.com/SpigotMC/BungeeCord/issues/3199>open issue</a> for it
 */
// non-inherited and default retention is correct
@Documented
@Target(ElementType.TYPE)
public @interface BungeeCordService {

    /**
     * Gets the dependency names which is required for the annotated service to be available.
     *
     * @return required dependency names
     */
    String[] value() default {};
}
