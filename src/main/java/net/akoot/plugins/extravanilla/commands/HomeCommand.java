package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.serializable.Position;
import net.akoot.plugins.ultravanilla.util.RawComponent;
import net.akoot.plugins.ultravanilla.util.RawMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public HomeCommand(UltraPlugin instance) {
        super(instance, ChatColor.GRAY);
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
            case "homeof":
                return handleHomeOf(sender, args);
        }

        return false;
    }

    private boolean handleHomeOf(CommandSender sender, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;
            Player target = plugin.getServer().getPlayer(args[0]);

            boolean homeSpecified = args.length == 2;

            if (target == null) {
                return true;
            }

            Location home = getHome(target, homeSpecified ? args[1] : "home");
            if (home == null) {
                sender.sendMessage(error("home-not-set.other", "%p", target.getName()));
                return true;
            }

            player.teleport(home);
            sender.sendMessage(homeSpecified ? message("tp.player.other", "%p", target.getName(), "%h", args[1]) : message("tp.player.other.home"));

        } else {
            sender.sendMessage(playerOnly("teleport to someone's home"));
        }
        return true;
    }

    private boolean handleSetHome(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                setHome(player, player.getLocation(), "home");
                sender.sendMessage(message("set.main"));
            } else if (args.length == 1) {
                int size = getHomes(player).size();
                int max = plugin.getConfig().getInt(ExtraPaths.Config.MAX_HOMES);
                if ((size < max && hasPermission(sender, "set.max")) || hasPermission(sender, "set.unlimited")) {
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
                    sender.sendMessage(message("tp.self.main"));
                    player.teleport(home);
                } else {
                    sender.sendMessage(error("home-not-set.self.main"));
                }
            } else if (args.length == 1) {
                String homeName = args[0];
                Location home = getHome(player, homeName);
                if (home != null) {
                    sender.sendMessage(message("tp.self.other", "%h", homeName));
                    player.teleport(home);
                } else {
                    sender.sendMessage(error("home-not-set.self.other", "%h", homeName));
                }
            } else if (args.length == 2) {
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
                if (isValid(target)) {
                    String homeName = args[0];
                    Location home = getHome(target, homeName);
                    if (home != null) {
                        sender.sendMessage(message("tp.player.other", "%h", homeName, "%p", target.getName()));
                        player.teleport(home);
                    } else {
                        sender.sendMessage(error("home-not-set.player.other", "%h", homeName));
                    }
                } else {
                    sender.sendMessage(playerInvalid(args[0]));
                }
            }
        }
        return true;
    }

    private boolean handleHomes(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                sendHomeList((Player) sender, sender);
            } else {
                sender.sendMessage(playerOnly("list your homes"));
            }
        }
        //
        else if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("list")) {
                    sendHomeList(player, sender);
                } else {
                    Position home = getHomeAsPosition(player, "home");
                    if (home != null) {
                        String coordinates = getCoordinateString(home);
                        if (args[0].equalsIgnoreCase("coordinates")) {
                            sender.sendMessage(coordinates);
                        } else if (args[0].equalsIgnoreCase("chat")) {
                            player.chat(ChatColor.stripColor(coordinates));
                        } else {
                            return false;
                        }
                    } else {
                        sender.sendMessage(error("home-not-set.self.main"));
                    }
                }
            } else {
                sender.sendMessage(playerOnly("have homes"));
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[1]);
                if (isValid(offlinePlayer)) {
                    sendHomeList(offlinePlayer, sender);
                } else {
                    sender.sendMessage(playerInvalid(args[1]));
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args[0].equalsIgnoreCase("whisper")) {
                        Position home = getHomeAsPosition(player, "home");
                        if (home != null) {
                            if (messageHome(args[1], player, home)) return true;
                        }
                    } else {
                        Position home = getHomeAsPosition(player, args[1]);
                        if (home != null) {
                            if (args[0].equalsIgnoreCase("coordinates")) {
                                player.sendMessage(getCoordinateString(home));
                            } else if (args[0].equalsIgnoreCase("chat")) {
                                player.chat(ChatColor.stripColor(getCoordinateString(home)));
                            }
                        }
                    }
                } else {
                    sender.sendMessage(playerOnly("have homes"));
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("whisper")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Position home = getHomeAsPosition(player, args[2]);
                    if (home == null) {
                        sender.sendMessage(error("home-not-set.self.other", "%h", args[2]));
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
            String command = "[" + "w " + target.getName() + " " + ChatColor.stripColor(getCoordinateString(home)) + "]";
            System.out.println(command);
            player.performCommand("w " + target.getName() + " " + ChatColor.stripColor(getCoordinateString(home)));
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
        Location bed = player.getBedSpawnLocation();
        if (bed != null) {
            homes.put("bed", bed);
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

    private void sendHomeList(OfflinePlayer target, CommandSender sender) {
        boolean isSelf = target == sender;
        String key = String.format("list.%s", isSelf ? "self" : "player");
        Map<String, Location> homes = getHomes(target);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage(message(key + ".title", "%p", target.getName()));
            RawMessage message = new RawMessage();
            for (String homeName : homes.keySet()) {
                String coordinatesString = message(key + ".item", "%v", getCoordinateString(target, homeName));
                RawComponent component = new RawComponent(coordinatesString);
                component.setCommand("/home " + homeName + (isSelf ? "" : " " + target.getName()));
                message.addComponent(component);
            }
            message.trimLast(1);
            message.send(player);
        } else {
            List<String> homeNames = new ArrayList<>();
            for (String homeName : homes.keySet()) {
                homeNames.add(getCoordinateString(target, homeName));
            }
            sender.sendMessage(list(key, homeNames, "%p", target.getName()));
        }
    }

    private String getCoordinateString(OfflinePlayer player, String home) {
        Location location = getHome(player, home);
        String coordinates = Position.toStringTrimmed(location);
        return message("chat", "%h", home, "%c", coordinates);
    }

    private String getCoordinateString(Position home) {
        String coordinates = home.toStringTrimmed();
        return message("chat", "%h", home.getName(), "%c", coordinates);
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
                suggestions.add("coordinates");
                suggestions.add("chat");
                suggestions.add("whisper");
            } else if (args.length == 2) {
                if (args[0].matches("list")) {
                    suggestions = getOfflinePlayerNames();
                } else if (args[0].matches("whisper")) {
                    return null;
                } else if (args[0].matches("coordinates|chat")) {
                    suggestions.addAll(getHomes((Player) sender).keySet());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("whisper")) {
                    if (!getPlayers(args[1]).isEmpty()) {
                        suggestions.addAll(getHomes((Player) sender).keySet());
                    }
                }
            }
        } else if (command.getName().equals("homeof")) {
            if (args.length == 1) {
                suggestions = getOfflinePlayerNames();
            } else if (args.length == 2) {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
                if (isValid(player)) {
                    suggestions.addAll(getHomes(player).keySet());
                }
            }
        }

        return getSuggestions(suggestions, args);
    }
}
