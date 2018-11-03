package be.afhistos.linkdiscord;

import be.afhistos.linkdiscord.listener.BukkitListener;
import be.afhistos.linkdiscord.listener.DiscordListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends JavaPlugin implements CommandExecutor{

    private JDA jda;
    private final long channelId = 457599692600115210L;
    private final long consoleId = 471763056414687232L;
    private final long logsId = 457294146764406795L;

    LocalDateTime time = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    String formatDateTime = time.format(formatter);
    @Override
    public void onLoad() {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken("NDU1Nzg2MzYzMjQ2MzQ2MjYw.DgBDtg.A7Eabp3esdZB43yVjN59wKZSN38").addEventListener(new DiscordListener( this)).buildAsync();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        super.onLoad();
    }
    @Override
    public void onEnable() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String formatDateTime = time.format(formatter);
        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), this);
        jda.getTextChannelById(channelId).sendMessage(":white_check_mark: **Le serveur est démarré !**").queue();
        jda.getTextChannelById(channelId).getManager().setTopic("Serveur lancé le " + formatDateTime).queue();
        jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, "Arthi RP", "https://twitch.tv/afhistos_le_sang♥"));
        File f = new File("plugins/DiscordLink/storage.yml");
        if(!f.exists()){
            try{
                f.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        getCommand("move").setExecutor(this);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String formatDateTime = time.format(formatter);
        jda.getTextChannelById(channelId).sendMessage(":octagonal_sign: **Le serveur est maintenant éteint!**").queue();
        jda.getTextChannelById(channelId).getManager().setTopic("Serveur éteint depuis le " + formatDateTime).queue();
        jda.shutdownNow();
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("move")){
            if(strings.length >= 2){
                User user = getJda().getUserById(strings[0]);
                VoiceChannel channel = getJda().getVoiceChannelById(strings[1]);
                if(user == null){
                    commandSender.sendMessage("§4ERREUR:§c '"+ strings[0] +"' n'est pas l'id d'un utilisateur correct!");
                    return true;
                }
                else if(channel == null){
                    commandSender.sendMessage("§4ERREUR:§c '"+ strings[1] +"' n'est pas l'id d'un channel Vocal !");
                    return true;
                }
                else{
                    GuildController controller = new GuildController(channel.getGuild());
                    try {
                        controller.moveVoiceMember(channel.getGuild().getMember(user), channel);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }
        return false;
    }
    public JDA getJda() {
        return jda;
    }
    public long getChannelId() {
        return channelId;
    }
    public void sendMessageToMinecraft(Message message) {
        Bukkit.broadcastMessage("§b[Discord] " + message.getAuthor().getName() + " » " + message.getContentDisplay());
    }
    public void sendMessageToDiscord(String message) {
        jda.getTextChannelById(channelId).sendMessage("[Minecraft] " + message).queue();
    }
    public long getConsoleId() {
        return consoleId;
    }
    public long getLogsId() {
        return logsId;
    }

}
