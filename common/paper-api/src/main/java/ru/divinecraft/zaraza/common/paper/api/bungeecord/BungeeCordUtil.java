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

package ru.divinecraft.zaraza.common.paper.api.bungeecord;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Utility for manipulating <b>BungeeCord</b> Plugin Messaging API.
 *
 * @deprecated this API is to be replaced with a more useful channel-object one
 */
@UtilityClass
@Deprecated(forRemoval = true) // should be replaced with `BungeecordClient` API
public class BungeeCordUtil {

    /**
     * Name of the special <b>BungeeCord</b> channel
     */
    private final @NotNull String BUNGEE_CORD_CHANNEL_NAME = "BungeeCord";

    /**
     * Name of the special <b>BungeeCord</b> sub-channel used for connecting players to the server
     */
    private final @NotNull String CONNECT_SUB_CHANNEL_NAME = "Connect";

    /**
     * Length of {@link #CONNECT_SUB_CHANNEL_NAME}
     */
    private final int CONNECT_SUB_CHANNEL_NAME_LENGTH = CONNECT_SUB_CHANNEL_NAME.length();

    /**
     * Enables <b>BungeeCord</b> Plugin Messaging for the given plugin.
     *
     * @param plugin plugin for which to enable <b>BungeeCord</b> Plugin Messaging
     *
     * @apiNote this is requires before using other <b>BungeeCord</b> Plugin Messaging methods
     */
    public void enableBungeeCordMessaging(final @NonNull Plugin plugin) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CORD_CHANNEL_NAME);
    }

    /**
     * Disables <b>BungeeCord</b> Plugin Messaging for the given plugin.
     *
     * @param plugin plugin for which to disable <b>BungeeCord</b> Plugin Messaging
     */
    public void disableBungeeCordMessaging(final @NonNull Plugin plugin) {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, BUNGEE_CORD_CHANNEL_NAME);
    }

    /**
     * Sends the given player to the given server.
     *
     * @param plugin plugin requesting the server switch for the player
     * @param player player to be sent to the other server
     * @param targetServerName name of the target server
     */
    public void sendToServer(final @NonNull Plugin plugin,
                             @SuppressWarnings("TypeMayBeWeakened" /* otherwise UB */) final @NonNull Player player,
                             final @NonNull String targetServerName) {
        final byte[] payload;
        try (val byteArrayOutput = new ByteArrayOutputStream(
                2 + CONNECT_SUB_CHANNEL_NAME_LENGTH + 2 + targetServerName.length()
        ); val output = new DataOutputStream(byteArrayOutput)) {
            output.writeUTF(CONNECT_SUB_CHANNEL_NAME);
            output.writeUTF(targetServerName);

            payload = byteArrayOutput.toByteArray();
        } catch (final IOException e) {
            throw new AssertionError("An error occurred while writing data", e);
        }

        player.sendPluginMessage(plugin, BUNGEE_CORD_CHANNEL_NAME, payload);
    }
}
