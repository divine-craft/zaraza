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

import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Wrapper around {@link BukkitScheduler}.
 */
public interface ManagedBukkitScheduler {

    /**
     * Gets an unmanaged equivalent of this scheduler.
     *
     * @return unmanaged equivalent of this scheduler
     */
    @NotNull BukkitScheduler asUnmanaged();

    // `scheduleSync*`

    /**
     * <p>Schedules a once off task to occur after a delay.</p>
     * <p>This task will be executed by the main server thread.</p>
     *
     * @param task task to be executed
     * @param delay delay in server ticks before executing the task
     * @return task ID or {@code -1} if scheduling failed
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#scheduleSyncDelayedTask(org.bukkit.plugin.Plugin, Runnable, long) unmanaged equivalent
     */
    int scheduleSyncDelayedTask(@NonNull Runnable task, long delay);

    /**
     * <p>Schedules a once off task to occur as soon as possible.</p>
     * <p>This task will be executed by the main server thread.</p>
     *
     * @param task task to be executed
     * @return task ID or {@code -1} if scheduling failed
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#scheduleSyncDelayedTask(org.bukkit.plugin.Plugin, Runnable) unmanaged equivalent
     */
    int scheduleSyncDelayedTask(@NonNull Runnable task);

    /**
     * <p>Schedules a repeating task.</p>
     * <p>This task will be executed by the main server thread.</p>
     *
     * @param task task to be executed
     * @param delay delay in server ticks before executing the task
     * @param period number of ticks to wait between runs
     * @return task ID or {@code -1} if scheduling failed
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#scheduleSyncRepeatingTask(org.bukkit.plugin.Plugin, Runnable, long, long)
     * unmanaged equivalent
     */
    int scheduleSyncRepeatingTask(@NonNull Runnable task, long delay, long period);

    // general purpose methods

    /**
     * <p>Calls a method on the main thread and returns a {@link Future} object.</p>
     * <p>This task will be executed by the main server thread.</p>
     *
     * @param task task to be executed
     * @param <T> result of the callable's execution
     * @return future which will contain the result once the task is executed
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#callSyncMethod(org.bukkit.plugin.Plugin, Callable) unmanaged equivalent
     */
    <T> @NotNull Future<T> callSyncMethod(@NonNull Callable<T> task);

    // cancellation methods

    /**
     * Removes task from scheduler.
     *
     * @param taskId ID number of task to be removed
     *
     * @see BukkitScheduler#cancelTask(int) unmanaged equivalent
     */
    void cancelTask(int taskId);

    /**
     * Removes all tasks associated with this managed scheduler's owner.
     *
     * @see BukkitScheduler#cancelTasks(Plugin) unmanaged equivalent
     */
    void cancelTasks();

    // `runTask[Asynchronously]`

    /**
     * Creates a task to be run on the next server tick.
     *
     * @param task task to be executed
     * @return created task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTask(org.bukkit.plugin.Plugin, Runnable) unmanaged equivalent
     */
    @NotNull BukkitTask runTask(@NonNull Runnable task);

    /**
     * Creates a task to be run on the next server tick.
     *
     * @param task task to be executed
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTask(org.bukkit.plugin.Plugin, Consumer) unmanaged equivalent
     */
    void runTask(@NonNull Consumer<@NotNull BukkitTask> task);

    /**
     * Creates a task to be run asynchronously.
     *
     * @param task task to be executed
     * @return created task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskAsynchronously(org.bukkit.plugin.Plugin, Runnable) unmanaged equivalent
     */
    @NotNull BukkitTask runTaskAsynchronously(@NonNull Runnable task);

    /**
     * Creates a task to be run asynchronously.
     *
     * @param task task to be executed
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskLaterAsynchronously(org.bukkit.plugin.Plugin, Consumer, long) unmanaged equivalent
     */
    void runTaskAsynchronously(@NonNull Consumer<@NotNull BukkitTask> task);

    // `runTaskLater[Asynchronously]`

    /**
     * Creates a task to be run after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @return created task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskLater(org.bukkit.plugin.Plugin, Runnable, long) unmanaged equivalent
     */
    @NotNull BukkitTask runTaskLater(@NonNull Runnable task, long delay);

    /**
     * Creates a task to be run after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskLater(org.bukkit.plugin.Plugin, Consumer, long) unmanaged equivalent
     */
    void runTaskLater(@NonNull Consumer<@NotNull BukkitTask> task, long delay);

    /**
     * Creates a task to be run asynchronously after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @return created task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskLaterAsynchronously(org.bukkit.plugin.Plugin, Runnable, long) unmanaged equivalent
     */
    @NotNull BukkitTask runTaskLaterAsynchronously(@NonNull Runnable task, long delay);

    /**
     * Creates a task to be run asynchronously after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskLaterAsynchronously(org.bukkit.plugin.Plugin, Consumer, long) unmanaged equivalent
     */
    void runTaskLaterAsynchronously(@NonNull Consumer<@NotNull BukkitTask> task, long delay);

    // `runTaskTimer[Asynchronously]`

    /**
     * Creates a task to be repeatedly run until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @param period number of ticks to wait between runs
     * @return created task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskTimer(org.bukkit.plugin.Plugin, Runnable, long, long) unmanaged equivalent
     */
    @NotNull BukkitTask runTaskTimer(@NonNull Runnable task, long delay, long period);

    /**
     * Creates a task to be repeatedly run until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @param period number of ticks to wait between runs
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskTimer(org.bukkit.plugin.Plugin, Consumer, long, long) unmanaged equivalent
     */
    void runTaskTimer(@NonNull Consumer<@NotNull BukkitTask> task, long delay, long period);

    /**
     * Creates a task to be repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @param period number of ticks to wait between runs
     * @return created task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskTimerAsynchronously(org.bukkit.plugin.Plugin, Runnable, long, long)
     * unmanaged equivalent
     */
    @NotNull BukkitTask runTaskTimerAsynchronously(@NonNull Runnable task, long delay, long period);

    /**
     * Creates a task to be repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task task to be executed
     * @param delay number of ticks to wait before running the task
     * @param period number of ticks to wait between runs
     * @throws NullPointerException if {@code task} is {@code null}
     * @see BukkitScheduler#runTaskTimerAsynchronously(org.bukkit.plugin.Plugin, Consumer, long, long)
     * unmanaged equivalent
     */
    void runTaskTimerAsynchronously(@NonNull Consumer<BukkitTask> task, long delay, long period);

    /**
     * Returns an executor that will run tasks on the next server tick.
     *
     * @return an executor associated with the given plugin
     *
     * @see BukkitScheduler#getMainThreadExecutor(org.bukkit.plugin.Plugin) unmanaged equivalent
     */
    @NotNull Executor getMainThreadExecutor();
}
