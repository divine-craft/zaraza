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

package ru.divinecraft.zaraza.common.paper.api.scheduler;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * {@link Plugin}-managed {@link BukkitScheduler} wrapped into a {@link ManagedBukkitScheduler}.
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class PluginManagedBukkitScheduler implements ManagedBukkitScheduler {

    /**
     * Internal Bukkit scheduler
     */
    @NotNull BukkitScheduler scheduler;

    /**
     * Owner of this scheduler wrapper
     */
    @NotNull Plugin owner;

    /**
     * Creates a {@link Plugin}-managed {@link BukkitScheduler} wrapped into a {@link ManagedBukkitScheduler}.
     *
     * @param plugin plugin owning the created scheduler wrapper
     * @return created wrapper around Bukkit scheduler owned by the plugin
     */
    public static @NotNull ManagedBukkitScheduler create(final @NotNull Plugin plugin) {
        return new PluginManagedBukkitScheduler(plugin.getServer().getScheduler(), plugin);
    }

    @Override
    public @NotNull BukkitScheduler asUnmanaged() {
        return scheduler;
    }

    @Override
    public int scheduleSyncDelayedTask(final @NonNull Runnable task, final long delay) {
        return scheduler.scheduleSyncDelayedTask(owner, task, delay);
    }

    @Override
    public int scheduleSyncDelayedTask(final @NonNull Runnable task) {
        return scheduler.scheduleSyncDelayedTask(owner, task);
    }

    @Override
    public int scheduleSyncRepeatingTask(final @NonNull Runnable task, final long delay, final long period) {
        return scheduler.scheduleSyncRepeatingTask(owner, task, delay, period);
    }

    @Override
    public @NotNull <T> Future<T> callSyncMethod(final @NonNull Callable<T> task) {
        return scheduler.callSyncMethod(owner, task);
    }

    @Override
    public void cancelTask(final int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void cancelTasks() {
        scheduler.cancelTasks(owner);
    }

    @Override
    public @NotNull BukkitTask runTask(final @NonNull Runnable task) {
        return scheduler.runTask(owner, task);
    }

    @Override
    public void runTask(final @NonNull Consumer<@NotNull BukkitTask> task) {
        scheduler.runTask(owner, task);
    }

    @Override
    public @NotNull BukkitTask runTaskAsynchronously(final @NonNull Runnable task) {
        return scheduler.runTaskAsynchronously(owner, task);
    }

    @Override
    public void runTaskAsynchronously(final @NonNull Consumer<@NotNull BukkitTask> task) {
        scheduler.runTaskAsynchronously(owner, task);
    }

    @Override
    public @NotNull BukkitTask runTaskLater(final @NonNull Runnable task, final long delay) {
        return scheduler.runTaskLater(owner, task, delay);
    }

    @Override
    public void runTaskLater(final @NonNull Consumer<@NotNull BukkitTask> task, final long delay) {
        scheduler.runTaskLater(owner, task, delay);
    }

    @Override
    public @NotNull BukkitTask runTaskLaterAsynchronously(final @NonNull Runnable task, final long delay) {
        return scheduler.runTaskLaterAsynchronously(owner, task, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(final @NonNull Consumer<@NotNull BukkitTask> task, final long delay) {
        scheduler.runTaskLaterAsynchronously(owner, task, delay);
    }

    @Override
    public @NotNull BukkitTask runTaskTimer(final @NonNull Runnable task, final long delay, final long period) {
        return scheduler.runTaskTimer(owner, task, delay, period);
    }

    @Override
    public void runTaskTimer(final @NonNull Consumer<@NotNull BukkitTask> task, final long delay, final long period) {
        scheduler.runTaskTimer(owner, task, delay, period);
    }

    @Override
    public @NotNull BukkitTask runTaskTimerAsynchronously(final @NonNull Runnable task,
                                                          final long delay, final long period) {
        return scheduler.runTaskTimerAsynchronously(owner, task, delay, period);
    }

    @Override
    public void runTaskTimerAsynchronously(final @NonNull Consumer<BukkitTask> task,
                                           final long delay, final long period) {
        scheduler.runTaskTimerAsynchronously(owner, task, delay, period);
    }

    @Override
    public @NotNull Executor getMainThreadExecutor() {
        return scheduler.getMainThreadExecutor(owner);
    }
}
