package tech.goksi.pollbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.config.Config;
import tech.goksi.pollbot.utils.ConfigUtils;

import java.io.IOException;


public class Reload extends SlashCommand {
    private Config config;
    private final Logger logger;
    public Reload(Config config){
        logger = LoggerFactory.getLogger(this.getClass().getName());
        this.config = config;
        this.name = "reload";
        this.help = "Reloads configuration";
        this.defaultEnabled = false;
        this.enabledUsers = ConfigUtils.getStringList("Commands.reload.AllowedUsers").toArray(new String[0]);
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        try{
            config.reloadConfig();
            event.reply(ConfigUtils.getString("Commands.reload.SuccessMessage")).setEphemeral(true).queue();
            logger.info("Config successfully reloaded by " + event.getUser().getAsTag() + " !");
        } catch (IOException e) {
            event.reply(ConfigUtils.getString("Commands.reload.ErrorMessage")).setEphemeral(true).queue();
            logger.error("Error while reloading config", e);
        }

    }
}
