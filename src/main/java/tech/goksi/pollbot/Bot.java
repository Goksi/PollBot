package tech.goksi.pollbot;


import net.dv8tion.jda.api.JDA;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import tech.goksi.pollbot.config.Config;
import tech.goksi.pollbot.polls.Poll;

import java.util.HashMap;
import java.util.Map;

public class Bot {
    /*bot info*/
    private String token;
    private String guildId;
    private String ownerId;
    /*end*/
    private final Map<String, Poll> polls; //all the active polls actually
    private final Config config;
    private static Bot inst;
    private JDA jda;
    public Bot(){
        config = new Config();
        polls = new HashMap<>();
    }
    public void start(){
        inst = this;
        config.initConfig();

    }


    public static Bot getInst() {
        return inst;
    }

    public Map<String, Poll> getPolls() {
        return polls;
    }

    public FileConfiguration getConfig() {
        return config.getValues();
    }

    public JDA getJDA() {
        return jda;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
