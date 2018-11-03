package be.afhistos.linkdiscord.commands;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import be.afhistos.linkdiscord.Main;

public class Move implements CommandExecutor {

    private final Main main;

    public Move(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("move")){
            if(strings.length >= 2){
                User user = main.getJda().getUserById(strings[0]);
                VoiceChannel channel = main.getJda().getVoiceChannelById(strings[1]);
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
}
