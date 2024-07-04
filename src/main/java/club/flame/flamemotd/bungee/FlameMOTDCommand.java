package club.flame.flamemotd.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class FlameMOTDCommand extends Command {
   private Main plugin;

   public FlameMOTDCommand(Main plugin) {
      super("flamemotd");
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
               sender.sendMessage(t("&cNo permission."));
            }
         } else {
            sender.sendMessage("§7§m-------------------------");
            sender.sendMessage("§c§lFlameMOTD§7 §8- §71.0-SNAPSHOT");
            sender.sendMessage("");
            sender.sendMessage("§8- §7/flamemotd reload");
            sender.sendMessage("§7§m-------------------------");
         }
      } else {
         sender.sendMessage("§7§m-------------------------");
         sender.sendMessage("§c§lFlameMOTD§7 §8- §71.0-SNAPSHOT");
         sender.sendMessage("");
         sender.sendMessage("§8- §7/flamemotd reload");
         sender.sendMessage("§7§m-------------------------");
      }

   }

   public static String t(String i) {
      return ChatColor.translateAlternateColorCodes('&', i);
   }
}
