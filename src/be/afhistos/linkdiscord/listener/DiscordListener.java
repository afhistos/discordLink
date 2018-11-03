package be.afhistos.linkdiscord.listener;

import be.afhistos.linkdiscord.Lag;
import be.afhistos.linkdiscord.Main;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class DiscordListener extends ListenerAdapter {
    public static HashMap<String, LocalDateTime> timePlayed = new HashMap<String, LocalDateTime>();

    private final Main main;

    public DiscordListener(Main main) {
        this.main = main;
    }
    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot connecté!");
        super.onReady(event);
    }


    public static String getIpFrom(String adresse){
        String toreturn = null;
        try{
            URL url = new URL(adresse);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            int c = in.read();
            StringBuilder builder = new StringBuilder();
            while (c != -1){
                builder.append((char) c);
                c = in.read();
            }
            toreturn = builder.toString();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return toreturn;
    }



    @SuppressWarnings("deprecation")
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.getGuild() == null) return;
        if(event.getTextChannel().getIdLong() != main.getChannelId() && event.getTextChannel().getIdLong() != main.getLogsId() && event.getTextChannel().getIdLong() != main.getConsoleId()) {
            User u = event.getAuthor();
            Color userColor = event.getGuild().getMember(u).getColor();

            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
            String formatDateTime = time.format(formatter);

            EmbedBuilder embed = new EmbedBuilder();
            String title = "Logs|Channel: " + event.getChannel().getName();
            String footer = u.getName()+ " le " + formatDateTime;
            embed.setTitle(title);
            embed.setFooter(footer, u.getAvatarUrl());
            embed.setAuthor(main.getJda().getSelfUser().getName(), null, main.getJda().getSelfUser().getAvatarUrl());
            embed.setColor(userColor);
            embed.addField("Message", event.getMessage().getContentDisplay(), true);
            main.getJda().getTextChannelById(main.getLogsId()).sendMessage(embed.build()).queue();
            return;
        } else if(event.getTextChannel().getIdLong() == main.getConsoleId()){
            if(event.getMessage().getContentDisplay().startsWith("/kick")){
                String[] args = event.getMessage().getContentDisplay().split(" ");
                StringBuilder builder = new StringBuilder();
                for(String str : args){
                    if(builder.length() > 0) builder.append(" ");
                    builder.append(str);
                }
                String reason = builder.toString().replace(args[0], "").replace(args[1], "");
                if(reason == null || reason == ""){
                    try {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick "+ args[1]  + " [Sans raison]");
                    } catch (Exception e){
                        event.getTextChannel().sendMessage("Erreur lors de l'envoi de la commande '"+ event.getMessage().getContentDisplay()+"':\n ```Java\n"+e.getMessage()+"```").queue();
                    }
                } else {
                    try {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick " + args[1] + " " + reason);
                    } catch (Exception e) {
                        event.getTextChannel().sendMessage("Erreur lors de l'envoi de la commande '" + event.getMessage().getContentDisplay() + "':\n ```Java\n" + e.getMessage() + "\n```").queue();
                    }
                }
                return;
            }else if(event.getMessage().getContentDisplay().startsWith("/ban")){
                event.getTextChannel().sendMessage("```diff\n- La commande BAN est désactivée depuis le discord !\n```").queue();
                return;
            } else if(event.getMessage().getContentDisplay().startsWith("/")){
                try{
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentDisplay().replace("/", ""));
                } catch (Exception e){
                    event.getTextChannel().sendMessage("Erreur lors de l'envoi de la commande '"+event.getMessage().getContentDisplay()+"':\n ```Java\n"+e.getMessage()+"\n```").queue();
                }
                return;
            }
            else{
                try{
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say " + event.getMessage().getContentDisplay());
                }catch (Exception e){
                    event.getTextChannel().sendMessage("Erreur lors de l'envoi du message ! \n```Java\n"+e.getMessage()+"```").queue();
                }
                return;
            }
        }
        else {
            if(event.getMessage().getContentDisplay().startsWith("/info")) {
                String str = event.getMessage().getContentDisplay();
                String[] args = str.split(" ");
                Player ps = (Player)Bukkit.getPlayer(args[1]);
                if(ps.isOnline()) {
                    int ping = ((CraftPlayer)ps).getHandle().ping;
                    //long t = ps.getPlayerTime();
                    LocalDateTime start = timePlayed.get(ps.getName());
                    LocalDateTime now = LocalDateTime.now();
                    //Duration duration = Duration.between(start, now);
                    long time = ChronoUnit.MINUTES.between(start, now);
                    URL url = null;
                    try {
                        url = new URL("https://dummyimage.com/16:9x1080/000/fff.png&text=" + ps.getName() + "++++ping:+" + ping + "+ms|connect%C3%A9+depuis+" + time + "+minutes");
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    InputStream in = null;
                    try {
                        in = new BufferedInputStream(url.openStream());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
                    try {
                        while (-1!=(n=in.read(buf)))
                        {
                            out.write(buf, 0, n);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    byte[] response = out.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream("plugins/DiscordLink/" + event.getAuthor().getId()+ ".png");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.write(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    main.getJda().getTextChannelById(main.getChannelId()).sendFile(new File("plugins/DiscordLink/" + event.getAuthor().getId() + ".png")).queue();
                    return;
                }
                else {
                    main.getJda().getTextChannelById(main.getChannelId()).sendMessage("Erreur: ce joueur n'est pas connecté, impossible de récupérer des informations").queue();
                    return;
                }
            }
            else if (event.getMessage().getContentDisplay().startsWith("/serveur")){
                DecimalFormat df = new DecimalFormat("#########.00");
                String tps = df.format(Lag.getTPS());
                String p = getIpFrom("https://minecraft-api.com/api/ping/ping.php?ip=94.23.221.162&port=26815");
                float ping = Float.parseFloat(p) * 1000;
                String pConnected = getIpFrom("https://minecraft-api.com/api/ping/playeronline.php?ip=94.23.221.162&port=26815");
                int pMax = Bukkit.getMaxPlayers();
                String version = Bukkit.getBukkitVersion();
                String name = Bukkit.getServerName();
                URL url = null;
                try {
                    url = new URL("https://dummyimage.com/16:9x1080/000/fff.png&text=Serveur+" + name + "++++Ping:+" + ping +"+ms|" + pConnected + "+/+" + pMax + "+joueurs+connectés|+TPS:+" + tps +"+Version:+" + version);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                InputStream in = null;
                try {
                    in = new BufferedInputStream(url.openStream());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                try {
                    while (-1!=(n=in.read(buf)))
                    {
                        out.write(buf, 0, n);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                byte[] response = out.toByteArray();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream("plugins/DiscordLink/" + event.getAuthor().getId()+ "_ServerRequest.png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File f = new File("plugins/DiscordLink/" + event.getAuthor().getId() + "_ServerRequest.png");
                if(!f.exists()){
                    main.getJda().getTextChannelById(main.getChannelId()).sendMessage("Une erreur est apparue lors de l'envoi des informations.\n Veuillez réessayer").queue();
                    return;
                }
                else {
                    main.getJda().getTextChannelById(main.getChannelId()).sendFile(f).queue();
                    return;
                }
            }
        }
        main.sendMessageToMinecraft(event.getMessage());
        super.onMessageReceived(event);
    }
    public static void setTimePlayed(String name, LocalDateTime localDateTime) {
        timePlayed.put(name, localDateTime);

    }

}
