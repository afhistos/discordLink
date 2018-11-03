package be.afhistos.linkdiscord.listener;

import java.io.File;
import java.time.LocalDateTime;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import be.afhistos.linkdiscord.Main;


public class BukkitListener implements Listener{

    private final Main main;

    public BukkitListener(Main main) {
        this.main = main;
    }
    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        String tosend = e.getPlayer().getName() + " » " + e.getMessage();
        main.sendMessageToDiscord(tosend);
    }
    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        main.getJda().getTextChannelById(main.getChannelId()).sendMessage(":skull_crossbones: " + e.getDeathMessage()).queue();
    }
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if(e.getPlayer().hasPlayedBefore()) {
            e.setJoinMessage("§8§f[§a+§8§f] §r"+e.getPlayer().getDisplayName().replace("~","")+" §aà rejoint le serveur !");
            main.getJda().getTextChannelById(main.getChannelId()).sendMessage(":heavy_plus_sign: **" + e.getPlayer().getName() + "** à rejoint le serveur").queue();
            DiscordListener.setTimePlayed(e.getPlayer().getName(), LocalDateTime.now());
        }
        else {
            e.setJoinMessage(null);
            Bukkit.broadcastMessage( "§8§f[§a+§8§f] §r"+e.getPlayer().getDisplayName().replace("~","")+" §aà rejoint le serveur !");
            Bukkit.broadcastMessage(e.getPlayer().getName()+"§6 à rejoint le serveur pour la première fois !");
            main.getJda().getTextChannelById(main.getChannelId()).sendMessage("**" + e.getPlayer().getName() + " à rejoint le serveur pour la première fois!** :tada:").queue();
        }
    }
    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("§8§f[§c-§8§f] §r"+e.getPlayer().getDisplayName().replace("~","")+" §cà quitté le serveur !");
        main.getJda().getTextChannelById(main.getChannelId()).sendMessage(":heavy_minus_sign: **" + e.getPlayer().getName() + "** à quitté le serveur").queue();
    }
    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        main.getJda().getTextChannelById(main.getConsoleId()).sendMessage("**"+ e.getPlayer().getName() +"** ═> "+ e.getMessage()).queue();
    }

}