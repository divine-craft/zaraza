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

import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper around {@link TaskScheduler}.
 */
public interface ManagedTaskScheduler {

    /**
     * Gets an unmanaged equivalent of this scheduler.
     *
     * @return unmanaged equivalent of this scheduler
     */
    @NotNull TaskScheduler asUnmanaged();

    /**
     * Cancel a task to prevent it from executing, or if it's a repeating task, prevent its further execution.
     *
     * @param id the id of the task to cancel
     * @see TaskScheduler#cancel(int) unmanaged analog
     */
    void cancel(int id);

    /**
     * Cancel a task to prevent it from executing, or if it's a repeating task, prevent its further execution.
     *
     * @param task the task to cancel
     * @see TaskScheduler#cancel(ScheduledTask) unmanaged equivalent
     */
    void cancel(@NonNull ScheduledTask task);

    /**
     * Cancel all owned tasks, this preventing them from being executed hereon in.
     *
     * @return the number of tasks cancelled by this method
     *
     * @see TaskScheduler#cancel(Plugin) unmanaged equivalent
     */
    int cancel();

    /**
     * Schedule a task to be executed asynchronously. The task will commence
     * running as soon as this method returns.
     *
     * @param task the task to run
     * @return the scheduled task
     *
     * @see TaskScheduler#runAsync(Plugin, Runnable) unmanaged equivalent
     */
    @NotNull ScheduledTask runAsync(@NonNull Runnable task);

    /**
     * Schedules a task to be executed asynchronously after the specified delay is up.
     *
     * @param task the task to run
     * @param delay the delay before this task will be executed
     * @param unit the unit in which the delay will be measured
     * @return the scheduled task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @throws NullPointerException if {@code unit} is {@code null}
     * @see TaskScheduler#schedule(Plugin, Runnable, long, TimeUnit) unmanaged equivalent
     */
    @NotNull ScheduledTask schedule(@NonNull Runnable task, long delay, @NonNull TimeUnit unit);

    /**
     * Schedules a task to be executed asynchronously after the specified delay is up.
     * The scheduled task will continue running at the specified interval.
     * The interval will not begin to count down until the last task invocation is complete.
     *
     * @param task the task to run
     * @param delay the delay before this task will be executed
     * @param period the interval before subsequent executions of this task
     * @param unit the unit in which the delay and period will be measured
     * @return the scheduled task
     *
     * @throws NullPointerException if {@code task} is {@code null}
     * @throws NullPointerException if {@code unit} is {@code null}
     * @see TaskScheduler#schedule(Plugin, Runnable, long, long, TimeUnit) unmanaged equivalent
     */
    @NotNull ScheduledTask schedule(@NonNull Runnable task, long delay, long period, @NonNull TimeUnit unit);

    /**
     * Get the unsafe methods of this class.
     *
     * @return the unsafe method interface
     */
    @NotNull Unsafe unsafe();

    /**
     * Accessor of unsafe methods of {@link ManagedTaskScheduler}.
     *
     * @see TaskScheduler.Unsafe unmanaged equivalent
     */
    @SuppressWarnings("InterfaceMayBeAnnotatedFunctional") // Mirror of Bungeecord API member
    interface Unsafe {

        /**
         * An executor service which underlies this scheduler.
         *
         * @return the underlying executor service or compatible wrapper
         *
         * @see TaskScheduler.Unsafe#getExecutorService(Plugin) unmanaged equivalent
         */
        @NotNull ExecutorService getExecutorService();
    }
}
