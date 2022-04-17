package tech.goksi.pollbot;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.config.ConfigUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bot bot = new Bot();
        Logger logger = LoggerFactory.getLogger("Main");
        String token = ConfigUtils.getString("BotInfo.Token");
        String guildId = ConfigUtils.getString("BotInfo.ServerId");
        String ownerId = ConfigUtils.getString("BotInfo.OwnerId");
        Scanner sc = new Scanner(System.in);
        if(token == null || token.equalsIgnoreCase("Put your token here")){
            logger.info("Looks like you didn't setup your token, no worries, you can enter it now: ");
            token = sc.nextLine();
            ConfigUtils.set("BotInfo.Token", token);
        }
        if(guildId == null || guildId.equalsIgnoreCase("Put your server id here")){
            logger.info("Looks like you didn't setup your server id, no worries, you can enter it now: ");
            guildId = sc.nextLine();
            ConfigUtils.set("BotInfo.ServerId", guildId);
        }
        if(ownerId == null || ownerId.equalsIgnoreCase("Put your discord id here")){
            logger.info("Looks like you didn't setup your owner id, no worries, you can enter it now: ");
            ownerId = sc.nextLine();
            ConfigUtils.set("BotInfo.OwnerId", ownerId);
        }
        bot.setToken(token);
        bot.setGuildId(guildId);
        bot.setOwnerId(ownerId);
        logger.info("Everything looks ready, starting websocket!");
        bot.start();
        while(true){
            if(sc.next().equalsIgnoreCase("stop")){
                //remove buttons/selection menu for active polls?
                Bot.getInst().getJDA().shutdownNow();
                System.exit(0);
            }else logger.warn("Wrong command, type stop to stop the bot");
        }

    }
}
