package io.github.gonalez.znpcs.utility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtils {
   private final Plugin plugin;

   public SchedulerUtils(Plugin plugin) {
      this.plugin = plugin;
   }

   public void runTaskTimer(BukkitRunnable bukkitRunnable, int delay) {
      this.runTaskTimer(bukkitRunnable, delay, delay);
   }

   public void runTaskTimer(BukkitRunnable bukkitRunnable, int delay, int continuousDelay) {
      bukkitRunnable.runTaskTimer(this.plugin, delay, continuousDelay);
   }

   public BukkitTask runTaskTimerAsynchronously(Runnable runnable, int delay, int continuousDelay) {
      return Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay, continuousDelay);
   }

   public void scheduleSyncDelayedTask(Runnable runnable, int delay) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, runnable, delay);
   }

   public void runTask(Runnable runnable) {
      Bukkit.getScheduler().runTask(this.plugin, runnable);
   }
}
