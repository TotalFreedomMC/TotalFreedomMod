package me.totalfreedom.totalfreedommod.command;

import io.papermc.lib.PaperLib;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.world.WorldTime;
import me.totalfreedom.totalfreedommod.world.WorldWeather;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Go to the hub.", usage = "/<command> <time [morning | noon | evening | night] | weather [off | rain | storm]>", aliases = "hw,hub")
public class Command_hubworld extends FreedomCommand
{
// this will need to be redone when actual hub is added
    private enum CommandMode
    {
        TELEPORT, TIME, WEATHER
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        CommandMode commandMode = null;

        if (args.length == 0)
        {
            commandMode = CommandMode.TELEPORT;
        }
        else if (args.length >= 2)
        {
            if ("time".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.TIME;
            }
            else if ("weather".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.WEATHER;
            }
        }

        if (commandMode == null)
        {
            return false;
        }

        try
        {
            switch (commandMode)
            {
                case TELEPORT:
                {
                    if (!(sender instanceof Player) || playerSender == null)
                    {
                        return false;
                    }

                    World hubWorld = null;
                    try
                    {
                        hubWorld = plugin.wm.hubworld.getWorld();
                    }
                    catch (Exception ex)
                    {
                    }

                    if (hubWorld == null || playerSender.getWorld() == hubWorld)
                    {
                        msg("Going to the main world.");
                        PaperLib.teleportAsync(playerSender, server.getWorlds().get(0).getSpawnLocation());
                    }
                    else
                    {
                        msg("Going to the hub world.");
                        plugin.wm.hubworld.sendToWorld(playerSender);
                    }

                    break;
                }
                case TIME:
                {
                    assertCommandPerms(sender, playerSender);

                    if (args.length == 2)
                    {
                        WorldTime timeOfDay = WorldTime.getByAlias(args[1]);
                        if (timeOfDay != null)
                        {
                            plugin.wm.hubworld.setTimeOfDay(timeOfDay);
                            msg("Hub world time set to: " + timeOfDay.name());
                        }
                        else
                        {
                            msg("Invalid time of day. Can be: sunrise, noon, sunset, or midnight.");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                case WEATHER:
                {
                    assertCommandPerms(sender, playerSender);

                    if (args.length == 2)
                    {
                        WorldWeather weatherMode = WorldWeather.getByAlias(args[1]);
                        if (weatherMode != null)
                        {
                            plugin.wm.hubworld.setWeatherMode(weatherMode);
                            msg("Hub world weather set to: " + weatherMode.name());
                        }
                        else
                        {
                            msg("Invalid weather mode. Can be: off, rain, or storm.");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                default:
                {
                    return false;
                }
            }
        }
        catch (PermissionDeniedException ex)
        {
            if (ex.getMessage().isEmpty())
            {
                return noPerms();
            }
            sender.sendMessage(ex.getMessage());
            return true;
        }

        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.sl.isStaff(sender))
        {
            return Collections.emptyList();
        }
        if (args.length == 1)
        {
            return Arrays.asList("time", "weather");
        }
        else if (args.length == 2)
        {
            if (args[0].equals("time"))
            {
                return Arrays.asList("morning", "noon", "evening", "night");
            }
            else if (args[0].equals("weather"))
            {
                return Arrays.asList("off", "rain", "storm");
            }
        }
        return Collections.emptyList();
    }

    // TODO: Redo this properly
    private void assertCommandPerms(CommandSender sender, Player playerSender) throws PermissionDeniedException
    {
        if (!(sender instanceof Player) || playerSender == null || !plugin.sl.isAdmin(playerSender))
        {
            throw new PermissionDeniedException();
        }
    }

    private class PermissionDeniedException extends Exception
    {

        private static final long serialVersionUID = 1L;

        private PermissionDeniedException()
        {
            super("");
        }

        private PermissionDeniedException(String string)
        {
            super(string);
        }
    }
}
