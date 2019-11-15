package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.serializable.Position;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public HomeCommand(JavaPlugin instance, Strings strings) {
        super(instance, strings, ChatColor.GRAY);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = plugin.getCommand("home");

        switch (command.getName()) {
            case "home":
                return handleHome(sender, args);
            case "homes":
                return handleHomes(sender, args);
            case "delhome":
                return handleDelHome(sender, args);
            case "sethome":
                return handleSetHome(sender, args);
        }

        return false;
    }

    private boolean handleSetHome(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                setHome(player, player.getLocation(), "home");
                sender.sendMessage(message("set.main"));
            } else if (args.length == 1) {
                int size = getHomes(player).size();
                if (hasPermission(sender, "sethome." + size + 1)) {
                    String homeName = args[0];
                    setHome(player, player.getLocation(), homeName);
                    sender.sendMessage(message("set.other", "%h", homeName));
                } else {
                    sender.sendMessage(uvStrings.getString("no-permission", "%a", "set more than " + size + " homes"));
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(playerOnly("set a home"));
        }
        return true;
    }

    private boolean handleDelHome(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                delHome((Player) sender, "home");
                sender.sendMessage(message("unset.main"));
            } else if (args.length == 1) {
                String homeName = args[0];
                delHome((Player) sender, homeName);
                sender.sendMessage(message("unset.other", "%h", homeName));
            } else {
                return false;
            }
        } else {
            sender.sendMessage(playerOnly("unset a home"));
        }
        return true;
    }

    private boolean handleHome(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                Location home = getHome(player, "home");
                if (home != null) {
                    sender.sendMessage(message("tp.main"));
                    player.teleport(home);
                } else {
                    sender.sendMessage(error("home-not-set.main"));
                }
            } else if (args.length == 1) {
                String homeName = args[0];
                Location home = getHome(player, homeName);
                if (home != null) {
                    sender.sendMessage(message("tp.other", "%h", homeName));
                    player.teleport(home);
                } else {
                    sender.sendMessage(error("home-not-set.other", "%h", homeName));
                }
            }
        }
        return true;
    }

    private boolean handleHomes(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                sender.sendMessage(listHomes((Player) sender));
            } else {
                sender.sendMessage(playerOnly("list your homes"));
            }
        }
        //
        else if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(listHomes(player));
                } else {
                    Position home = getHomeAsPosition(player, "home");
                    if (home != null) {
                        String coords = getCoordsString(player, home);
                        if (args[0].equalsIgnoreCase("coords")) {
                            sender.sendMessage(coords);
                        } else if (args[0].equalsIgnoreCase("chat")) {
                            player.chat(ChatColor.stripColor(coords));
                        } else {
                            return false;
                        }
                    } else {
                        sender.sendMessage(error("home-not-set.main"));
                    }
                }
            } else {
                sender.sendMessage(playerOnly("have homes"));
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[1]);
                if (isValid(offlinePlayer)) {
                    sender.sendMessage(listHomes(offlinePlayer));
                } else {
                    sender.sendMessage(playerInvalid(args[1]));
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args[0].equalsIgnoreCase("msg")) {
                        Position home = getHomeAsPosition(player, "home");
                        if (home != null) {
                            if (messageHome(args[1], player, home)) return true;
                        }
                    } else {
                        Position home = getHomeAsPosition(player, args[1]);
                        if (home != null) {
                            if (args[0].equalsIgnoreCase("coords")) {
                                player.sendMessage(getCoordsString(player, home));
                            } else if (args[0].equalsIgnoreCase("chat")) {
                                player.chat(ChatColor.stripColor(getCoordsString(player, home)));
                            }
                        }
                    }
                } else {
                    sender.sendMessage(playerOnly("have homes"));
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("msg")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Position home = getHomeAsPosition(player, args[2]);
                    if (home == null) {
                        sender.sendMessage(error("home-not-set.other", "%h", args[2]));
                        return true;
                    }
                    if (messageHome(args[1], player, home)) return true;
                } else {
                    sender.sendMessage(playerOnly("have homes"));
                }
            }
        }
        return true;
    }

    private boolean messageHome(String playerString, Player player, Position home) {
        List<Player> players = getPlayers(playerString);
        if (players.isEmpty()) {
            player.sendMessage(playerOffline(playerString));
            return true;
        }
        for (Player target : players) {
            player.performCommand("msg " + target + " " + ChatColor.stripColor(getCoordsString(player, home)));
        }
        return false;
    }

    private void delHome(OfflinePlayer player, String home) {
        setHome(player, null, home);
    }

    private void setHome(OfflinePlayer player, Location location, String home) {
        Map<String, Location> homes = getHomes(player);
        homes.put(home, location);
        saveHomes(player, homes);
    }

    private Map<String, Location> getHomes(OfflinePlayer player) {
        Map<String, Location> homes = new HashMap<>();
        List<Position> positions = (List<Position>) Users.getUser(player).getList(ExtraPaths.User.HOMES, new ArrayList<Position>());
        for (Position position : positions) {
            homes.put(position.getName(), position.getLocation());
        }
        return homes;
    }

    private void saveHomes(OfflinePlayer player, Map<String, Location> homes) {
        List<Position> positions = new ArrayList<>();
        for (String k : homes.keySet()) {
            Location location = homes.get(k);
            if (location != null) {
                positions.add(new Position(k, location));
            }
        }
        Users.getUser(player).set(ExtraPaths.User.HOMES, positions);
        Users.saveUser(player);
    }

    private Position getHomeAsPosition(OfflinePlayer player, String home) {
        List<Position> positions = (List<Position>) Users.getUser(player).getList(ExtraPaths.User.HOMES, new ArrayList<Position>());
        for (Position position : positions) {
            if (position.getName().equals(home)) {
                return position;
            }
        }
        return null;
    }

    private Location getHome(OfflinePlayer player, String home) {
        Map<String, Location> homes = getHomes(player);
        return homes.get(home);
    }

    private String listHomes(OfflinePlayer player) {
        Map<String, Location> homes = getHomes(player);
        List<String> homeNames = new ArrayList<>();
        for (String homeName : homes.keySet()) {
            String coords = Position.toStringTrimmed(homes.get(homeName));
            homeNames.add(message("chat", "%h", homeName, "%c", coords));
        }
        return list("list.player", homeNames, "%p", player.getName());
    }

    private String getCoordsString(Player player, Position home) {
        String coords = home.toStringTrimmed();
        return message("chat", "%h", home.getName(), "%c", coords);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;

        List<String> suggestions = new ArrayList<>();

        if (command.getName().matches("home|sethome|delhome")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    suggestions.addAll(getHomes((Player) sender).keySet());
                }
            }
        } else if (command.getName().equals("homes")) {
            if (args.length == 1) {
                suggestions.add("list");
                suggestions.add("coords");
                suggestions.add("chat");
                suggestions.add("msg");
            } else if (args.length == 2) {
                if (args[0].matches("list")) {
                    suggestions = getOfflinePlayerNames();
                } else if (args[0].matches("msg")) {
                    return null;
                } else if (args[0].matches("coords|chat")) {
                    suggestions.addAll(getHomes((Player) sender).keySet());
                }
            } else if (args.length == 3) {
                if (args[1].equalsIgnoreCase("msg")) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        suggestions.addAll(getHomes((Player) sender).keySet());
                    }
                }
            }
        }

        return getSuggestions(suggestions, args);
    }
}
