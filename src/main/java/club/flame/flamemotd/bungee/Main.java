package club.flame.flamemotd.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener {
   private Configuration config;
   ScheduledTask countdown;

   public void onEnable() {
      this.getProxy().getPluginManager().registerListener(this, this);
      this.getProxy().getPluginManager().registerCommand(this, new FlameMOTDCommand(this));
      if (!this.getDataFolder().exists()) {
         this.getDataFolder().mkdir();
         File file = new File(this.getDataFolder(), "config.yml");
         if (!file.exists()) {
            try {
               Object t = null;

               try {
                  InputStream in = this.getResourceAsStream("config.yml");

                  try {
                     Files.copy(in, file.toPath(), new CopyOption[0]);
                  } finally {
                     if (in != null) {
                        in.close();
                     }

                  }
               } finally {
                  Object t2;
                  if (t == null) {
                     t2 = null;
                  } else {
                     t2 = null;
                     if (t != t2) {
                        ((Throwable)t).addSuppressed((Throwable)t2);
                     }
                  }

               }
            } catch (IOException var15) {
               var15.printStackTrace();
            }
         }
      }

      this.saveDefaultConfig();
      this.reloadConfig();
   }

   protected Configuration getConfig() {
      return this.config;
   }

   protected void reloadConfig() {
      try {
         this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
      } catch (IOException var2) {
         throw new RuntimeException("Unable to load configuration", var2);
      }
   }

   protected void saveConfig() {
      try {
         ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.getConfig(), new File(this.getDataFolder(), "config.yml"));
      } catch (IOException var2) {
         throw new RuntimeException("Unable to save configuration", var2);
      }
   }

   private void saveDefaultConfig() {
   }

   @EventHandler
   public void onPing(ProxyPingEvent e) {
      ServerPing sp = e.getResponse();
      String message = this.getConfig().getString("motd").replaceAll("%time", this.getTime()).replaceAll("%newline", "\n");
      sp.getPlayers().setMax(this.config.getInt("slots"));
      sp.setDescription(t(message));
      e.setResponse(sp);
   }

   public String getTime() {
      Configuration config = this.getConfig();
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

   public void startCountdown() {
      this.countdown = this.getProxy().getScheduler().schedule(this, new Runnable() {
         public void run() {
            if (Main.this.getCountdown() == 0) {
               Main.this.getProxy().getScheduler().cancel(Main.this.countdown);
               Main.this.getProxy().getPluginManager().dispatchCommand(Main.this.getProxy().getConsole(), Main.this.getConfig().getString("command-finish"));
               System.out.println("Command-Finish has been successfully executed!");
            }

         }
      }, 0L, 1L, TimeUnit.SECONDS);
   }

   public int getCountdown() {
      this.config = this.getConfig();
      String dateStop = this.config.getString("date");
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
      Date date = null;

      try {
         format.setTimeZone(TimeZone.getTimeZone(this.config.getString("timezone")));
         date = format.parse(dateStop);
      } catch (ParseException var8) {
         var8.printStackTrace();
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

   public static String t(String i) {
      return ChatColor.translateAlternateColorCodes('&', i);
   }
}
