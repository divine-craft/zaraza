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

package ru.divinecraft.zaraza.common.api.bungeecord;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;

/**
 * Legacy alias of {@link  ru.divinecraft.zaraza.common.paper.api.bungeecord.BungeeCordUtil}.
 *
 * @deprecated this class is in the invalid package and thus will get removed once all our callers migrated
 */
@Slf4j
@UtilityClass
@Deprecated(forRemoval = true)
public class BungeeCordUtil {

    /**
     * Enables <b>BungeeCord</b> Plugin Messaging for the given plugin.
     *
     * @param plugin plugin for which to enable <b>BungeeCord</b> Plugin Messaging
     *
     * @apiNote this is requires before using other <b>BungeeCord</b> Plugin Messaging methods
     */
    public void enableBungeeCordMessaging(final @NonNull Plugin plugin) {
        reportLegacyCaller(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass());

        ru.divinecraft.zaraza.common.paper.api.bungeecord.BungeeCordUtil.enableBungeeCordMessaging(plugin);
    }

    /**
     * Disables <b>BungeeCord</b> Plugin Messaging for the given plugin.
     *
     * @param plugin plugin for which to disable <b>BungeeCord</b> Plugin Messaging
     */
    public void disableBungeeCordMessaging(final @NonNull Plugin plugin) {
        ru.divinecraft.zaraza.common.paper.api.bungeecord.BungeeCordUtil.disableBungeeCordMessaging(plugin);
    }

    /**
     * Sends the given player to the given server.
     *
     * @param plugin plugin requesting the server switch for the player
     * @param player player to be sent to the other server
     * @param targetServerName name of the target server
     */
    public void sendToServer(final @NonNull Plugin plugin,
                             final @NonNull Player player,
                             final @NonNull String targetServerName) {
        ru.divinecraft.zaraza.common.paper.api.bungeecord.BungeeCordUtil.sendToServer(plugin, player, targetServerName);
    }

    private static void reportLegacyCaller(final @NotNull Class<?> legacyCaller) {
        final ClassLoader classLoader;
        log.warn("{} is using legacy `BungeeCordUtil` "
                        + "and should migrate to `ru.divinecraft.zaraza.common.paper.api.bungeecord.BungeeCordUtil`",
                (classLoader = legacyCaller.getClassLoader()) instanceof PluginClassLoader
                        ? "Plugin " + ((PluginClassLoader) classLoader).getPlugin().getName()
                        : "Unknown plugin"
        );
    }
}
