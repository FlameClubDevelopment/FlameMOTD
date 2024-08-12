package club.flame.flamemotd.bungee;

import club.flame.flamemotd.utils.CC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class FlameMOTDCommand extends Command {
   private Main plugin;

   public FlameMOTDCommand(Main plugin) {
      super("flamemotd", "", "motd");
      this.plugin = plugin;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length == 1) {
         if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("flamemotd.reload")) {
               this.plugin.reloadConfig();
               sender.sendMessage(t("&c&lFlameMOTD&7 &areloaded successfully!"));
               sender.sendMessage(t("&7Reloaded files: &fconfig.yml"));
            } else {
               sender.sendMessage(t("&cYou don't have permissions."));
            }
         } else {
            sender.sendMessage(CC.translate("&4&m=============================="));
            sender.sendMessage(CC.translate("&c&lFlameMOTD&7 &8- &71.0.1"));
            sender.sendMessage(CC.translate(""));
            sender.sendMessage(CC.translate("&8- &7/flamemotd reload"));
            sender.sendMessage(CC.translate("&4&m=============================="));
         }
      } else {
         sender.sendMessage(CC.translate("&4&m=============================="));
         sender.sendMessage(CC.translate("&c&lFlameMOTD&7 &8- &71.0.1"));
         sender.sendMessage(CC.translate(""));
         sender.sendMessage(CC.translate("&8- &7/flamemotd reload"));
         sender.sendMessage(CC.translate("&4&m=============================="));
      }

   }

   public static String t(String i) {
      return ChatColor.translateAlternateColorCodes('&', i);
   }
}
