package com.ruinscraft.addresslimit;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class AddressLimitPlugin extends JavaPlugin implements Listener {

    public static final int MAX_CONNECTIONS_FROM_ADDRESS = 3;
    private Map<String, Integer> connectionCounts;

    @Override
    public void onEnable() {
        connectionCounts = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String address = event.getAddress().getHostAddress();

        final int currentConnections;

        if (connectionCounts.containsKey(address)) {
            currentConnections = connectionCounts.get(address);

            if (currentConnections + 1 > MAX_CONNECTIONS_FROM_ADDRESS) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ChatColor.RED + "Too many connections from this IP address");
                return;
            }
        } else {
            currentConnections = 0;
        }

        connectionCounts.put(address, currentConnections + 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String address = event.getPlayer().getAddress().getAddress().getHostAddress();

        if (connectionCounts.containsKey(address)) {
            int currentConnections = connectionCounts.get(address);

            if (currentConnections - 1 < 1) {
                connectionCounts.remove(address);
            } else {
                connectionCounts.put(address, currentConnections - 1);
            }
        }
    }

}
