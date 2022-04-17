package tech.goksi.pollbot.config;

import tech.goksi.pollbot.Bot;

import java.io.IOException;
import java.util.List;

public class ConfigUtils {

    public static String getString(String path){
        return Bot.getInst().getConfig().getString(path);
    }

    public static void set(String path, Object value){
        try {
            Bot.getInst().getConfig().set(path, value);
            Bot.getInst().getConfig().save();
        } catch (IOException e) {
            Bot.getInst().getLogger().error("Error while saving config file", e);
        }
    }
    public static int getInt(String path) {
        return Bot.getInst().getConfig().getInt(path);
    }
    public static List<String> getStringList(String path){
        return Bot.getInst().getConfig().getStringList(path);
    }
}
