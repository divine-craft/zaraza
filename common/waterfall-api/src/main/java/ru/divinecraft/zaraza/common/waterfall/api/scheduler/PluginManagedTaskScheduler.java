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

package ru.divinecraft.zaraza.common.waterfall.api.scheduler;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@link Plugin}-managed {@link TaskScheduler} wrapped into a {@link ManagedTaskScheduler}.
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class PluginManagedTaskScheduler implements ManagedTaskScheduler, ManagedTaskScheduler.Unsafe {

    /**
     * Internal Bukkit scheduler
     */
    @NotNull TaskScheduler scheduler;

    /**
     * Owner of this scheduler wrapper
     */
    @NotNull Plugin owner;

    /**
     * Creates a {@link Plugin}-managed {@link TaskScheduler} wrapped into a {@link ManagedTaskScheduler}.
     *
     * @param plugin plugin owning the created scheduler wrapper
     * @return created wrapper around Bukkit scheduler owned by the plugin
     */
    public static @NotNull ManagedTaskScheduler create(final @NotNull Plugin plugin) {
        return new PluginManagedTaskScheduler(plugin.getProxy().getScheduler(), plugin);
    }

    @Override
    public void cancel(final int id) {
        scheduler.cancel(id);
    }

    @Override
    public void cancel(final @NonNull ScheduledTask task) {
        scheduler.cancel(task);
    }

    @Override
    public int cancel() {
        return scheduler.cancel(owner);
    }

    @Override
    public @NotNull TaskScheduler asUnmanaged() {
        return scheduler;
    }

    @Override
    public @NotNull ScheduledTask runAsync(final @NonNull Runnable task) {
        return scheduler.runAsync(owner, task);
    }

    @Override
    public @NotNull ScheduledTask schedule(final @NonNull Runnable task,
                                           final long delay,
                                           final @NonNull TimeUnit unit) {
        return scheduler.schedule(owner, task, delay, unit);
    }

    @Override
    public @NotNull ScheduledTask schedule(final @NonNull Runnable task,
                                           final long delay,
                                           final long period,
                                           final @NonNull TimeUnit unit) {
        return scheduler.schedule(owner, task, delay, period, unit);
    }

    @Override
    public @NotNull Unsafe unsafe() {
        return this;
    }

    // as `Unsafe`

    @Override
    public @NotNull ExecutorService getExecutorService() {
        return scheduler.unsafe().getExecutorService(owner);
    }
}
