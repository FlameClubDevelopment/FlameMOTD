package club.flame.flamemotd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class FlameMOTD extends JavaPlugin implements Listener {
   private static FlameMOTD instance;
   int taskId = -1;

   public FlameMOTD() {
      instance = this;
   }

   public static FlameMOTD getInstance() {
      return instance;
   }

   public void onEnable() {
      FileConfiguration config = this.getConfig();
      Bukkit.getServer().getPluginManager().registerEvents(this, this);
      config.options().copyDefaults(true);
      this.saveDefaultConfig();
      this.startCountdown();
   }

   public void startCountdown() {
      BukkitScheduler scheduler = this.getServer().getScheduler();
      this.taskId = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
         public void run() {
            if (FlameMOTD.this.getCountdown() == 0) {
               FlameMOTD.this.cancelTask(FlameMOTD.this.taskId);
               FlameMOTD.this.taskId = -1;
               Bukkit.getServer().dispatchCommand(FlameMOTD.this.getServer().getConsoleSender(), FlameMOTD.this.getConfig().getString("command-finish"));
               System.out.println("Command-Finish has been successfully executed!");
            }

         }
      }, 0L, 20L);
   }

   public void cancelTask(int id) {
      Bukkit.getServer().getScheduler().cancelTask(id);
   }

   public int getCountdown() {
      FileConfiguration config = this.getConfig();
      String dateStop = config.getString("date");
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
      Date date = null;

      try {
         format.setTimeZone(TimeZone.getTimeZone(config.getString("timezone")));
         date = format.parse(dateStop);
      } catch (ParseException var9) {
         var9.printStackTrace();
      }

      Date current = new Date();
      long diff = date.getTime() - current.getTime();
      if (diff > 0L) {
         Integer seconds = (int)TimeUnit.MILLISECONDS.toSeconds(diff);
         return seconds;
      } else {
         return 0;
      }
   }

   @EventHandler
   public void onPing(ServerListPingEvent e) {
      FileConfiguration config = this.getConfig();
      String message = config.getString("motd").replaceAll("%time", this.getTime()).replaceAll("%newline", "\n");
      e.setMotd(t(message));
      e.setMaxPlayers(config.getInt("slots"));
   }

   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (cmd.getName().equalsIgnoreCase("flamemotd")) {
         if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
               if (sender.hasPermission("flamemotd.reload")) {
                  this.reloadConfig();
                  sender.sendMessage(t("&c&lFlameMOTD&7 &areloaded successfully!"));
                  sender.sendMessage(t("&7Reloaded files: &fconfig.yml"));
                  return true;
               }
            } else {
               sender.sendMessage(t("&cUsage: /flamemotd reload"));
            }
         } else {
            sender.sendMessage(t("&cUsage: /flamemotd reload"));
         }
      }

      return true;
   }

   public String getTime() {
      FileConfiguration config = this.getConfig();
      String dateStop = config.getString("date");
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
      Date date = null;

      try {
         format.setTimeZone(TimeZone.getTimeZone(config.getString("timezone")));
         date = format.parse(dateStop);
      } catch (ParseException var23) {
         var23.printStackTrace();
      }

      Date current = new Date();
      long diff = date.getTime() - current.getTime();
      if (diff < 0L) {
         return config.getString(t("time-value-end"));
      } else {
         long days;
         long hours;
         long minutes;
         long days2;
         long rhours;
         long rminutes;
         long rseconds;
         if (config.getInt("clock-type") == 1) {
            days = TimeUnit.MILLISECONDS.toDays(diff);
            hours = TimeUnit.MILLISECONDS.toHours(diff);
            minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            days2 = TimeUnit.MILLISECONDS.toSeconds(diff);
            rhours = days == 0L ? hours : hours % (days * 24L);
            rminutes = hours == 0L ? minutes : minutes % (hours * 60L);
            rseconds = minutes == 0L ? days2 : days2 % (minutes * 60L);
            StringBuilder sb = new StringBuilder();
            if (days > 1L) {
               sb.append(String.valueOf(days) + config.getString("time-day"));
            }

            if (days == 1L) {
               sb.append(String.valueOf(days) + config.getString("time-day"));
            }

            if (rhours > 1L) {
               sb.append(String.valueOf(days > 0L ? " " : "") + rhours + config.getString("time-hours"));
            }

            if (rhours == 1L) {
               sb.append(String.valueOf(days > 0L ? " " : "") + rhours + config.getString("time-hours"));
            }

            if (rminutes > 1L) {
               sb.append(String.valueOf((days <= 0L || hours > 0L) && hours <= 0L ? "" : " ") + rminutes + config.getString("time-minutes"));
            }

            if (rminutes == 1L) {
               sb.append(String.valueOf((days <= 0L || hours > 0L) && hours <= 0L ? "" : " ") + rminutes + config.getString("time-minutes"));
            }

            if (rseconds > 1L) {
               sb.append(String.valueOf((days <= 0L && hours <= 0L || minutes > 0L) && minutes <= 0L ? "" : " ") + rseconds + config.getString("time-seconds"));
            }

            if (rseconds == 1L) {
               sb.append(String.valueOf((days <= 0L && hours <= 0L || minutes > 0L) && minutes <= 0L ? "" : " ") + rseconds + config.getString("time-seconds"));
            }

            return sb.toString();
         } else if (config.getInt("clock-type") == 2) {
            days = TimeUnit.MILLISECONDS.toSeconds(diff);
            hours = TimeUnit.MILLISECONDS.toMinutes(diff);
            minutes = TimeUnit.MILLISECONDS.toHours(diff);
            days2 = TimeUnit.MILLISECONDS.toDays(diff);
            rhours = days2 == 0L ? minutes : minutes % (days2 * 24L);
            rminutes = minutes == 0L ? hours : hours % (minutes * 60L);
            rseconds = hours == 0L ? days : days % (hours * 60L);
            return String.valueOf(days2) + ":" + rhours + ":" + rminutes + ":" + rseconds;
         } else {
            return null;
         }
      }
   }

   public static String t(String i) {
      return ChatColor.translateAlternateColorCodes('&', i);
   }
}
