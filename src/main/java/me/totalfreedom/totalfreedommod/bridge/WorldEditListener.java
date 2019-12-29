package me.totalfreedom.totalfreedommod.bridge;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.worldedit.LimitChangedEvent;
import me.totalfreedom.worldedit.SelectionChangedEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class WorldEditListener extends FreedomService
{

    public WorldEditListener()
    {
        super();
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    @EventHandler
    public void onSelectionChange(final SelectionChangedEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.al.isAdmin(player))
        {
            return;
        }

        if (plugin.pa.isInProtectedArea(
                event.getMinVector(),
                event.getMaxVector(),
                event.getWorld().getName()))
        {
            player.sendMessage(ChatColor.RED + "The region that you selected contained a protected area. Selection cleared.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLimitChanged(LimitChangedEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.al.isAdmin(player))
        {
            return;
        }

        if (!event.getPlayer().equals(event.getTarget()))
        {
            player.sendMessage(ChatColor.RED + "Only admins can change the limit for other players!");
            event.setCancelled(true);
        }

        if (event.getLimit() < 0 || event.getLimit() > 200000)
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot set your limit higher than 200000 or to -1!");
        }
    }

}
