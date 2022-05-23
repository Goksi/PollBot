package tech.goksi.pollbot;


import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.commands.Reload;
import tech.goksi.pollbot.config.Config;
import javax.security.auth.login.LoginException;
import java.io.File;


public class Bot {
    /*bot info*/
    private String token;
    private String guildId;
    private String ownerId;
    /*end*/
    private final Logger logger;
    private final Config config;
    //private final Map<String, Poll> polls; //all the active polls actually
    private static Bot inst;
    private JDA jda;

    public Bot() {
        inst = this;
        config = new Config();
        config.initConfig();
        logger = LoggerFactory.getLogger(this.getClass().getName());
    }

    public void start() {
        /*init of bot*/
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.forceGuildOnly(guildId);
        builder.setOwnerId(ownerId);
        switch (getConfig().getString("BotInfo.Status")) {
            case "DND":
                builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
                break;
            case "IDLE":
                builder.setStatus(OnlineStatus.IDLE);
                break;
            case "INVISIBLE":
                builder.setStatus(OnlineStatus.INVISIBLE);
                break;
            default:
                builder.setStatus(OnlineStatus.ONLINE);
        }
        if(getConfig().getBoolean("BotInfo.EnableActivity")){
            switch (getConfig().getString("BotInfo.Activity")){
                case "WATCHING":
                    builder.setActivity(Activity.watching(getConfig().getString("BotInfo.Game")));
                    break;
                case "LISTENING":
                    builder.setActivity(Activity.listening(getConfig().getString("BotInfo.Game")));
                    break;
                case "STREAMING":
                    builder.setActivity(Activity.streaming(getConfig().getString("BotInfo.Game"), getConfig().getString("BotInfo.StreamUrl")));
                    break;
                default:
                    builder.setActivity(Activity.playing(getConfig().getString("BotInfo.Game")));
            }
        }

        //should add status
        /*start of commands*/
        builder.addSlashCommands(new Reload(config));
        /*end of commands*/

        CommandClient client = builder.build();
        //jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            logger.error("Wrong bot token !", e);
            System.exit(12);
        }
        jda.addEventListener(client);
        try {
            jda.awaitReady();
            logger.info("Looks like your bot started successfully!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*setters and getters*/
    public static Bot getInst() {
        return inst;
    }


    public Logger getLogger() {
        return logger;
    }

    public JDA getJDA() {
        return jda;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public YamlFile getConfig() {
        return config.getConfig();
    }
    public File getConfigFile(){
        return config.getConfigFile();
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public void setToken(String token) {
        this.token = token;
    }
    /*end of setters and getters*/
}
