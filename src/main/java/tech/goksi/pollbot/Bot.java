package tech.goksi.pollbot;


import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.config.Config;
import tech.goksi.pollbot.polls.Poll;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;


public class Bot {
    /*bot info*/
    private String token;
    private String guildId;
    private String ownerId;
    /*end*/
    private final Logger logger;
    private Config config;
    private final Map<String, Poll> polls; //all the active polls actually
    //private final Config config;
    private static Bot inst;
    private JDA jda;
    public Bot(){
        inst = this;
        config = new Config();
        config.initConfig();
        polls = new HashMap<>();
        logger = LoggerFactory.getLogger(this.getClass().getName());
    }
    public void start(){

      //  config.initConfig();
        /*init of bot*/
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.forceGuildOnly(guildId);
        builder.setOwnerId(ownerId);

        //should add status
        /*start of commands*/
        /*end of commands*/

        CommandClient client = builder.build();
        //jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        try{
            jda = jdaBuilder.build();
        }catch (LoginException e){
            logger.error("Wrong bot token !", e);
            System.exit(12);
        }
        jda.addEventListener(client);
        try{
            jda.awaitReady();
            logger.info("Looks like your bot started successfully!");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /*setters and getters*/
    public static Bot getInst() {
        return inst;
    }

    public Map<String, Poll> getPolls() {
        return polls;
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

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public void setToken(String token) {
        this.token = token;
    }
    /*end of setters and getters*/
}
