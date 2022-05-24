package tech.goksi.pollbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.polls.Poll;
import tech.goksi.pollbot.polls.YesNoPoll;
import tech.goksi.pollbot.utils.ConfigUtils;
import tech.goksi.pollbot.utils.Convert;
import tech.goksi.pollbot.utils.FailFormatException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class YesNoCommand extends SlashCommand {
    private final Logger logger;
    public YesNoCommand(){
        this.name = "yesno";
        this.help = "Command for creating Yes/No polls";
        this.cooldown = ConfigUtils.getInt("Commands.yesno.CoolDown");
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "This value must be unique", true));
        options.add(new OptionData(OptionType.STRING, "description", "Description of the poll", true));
        options.add(new OptionData(OptionType.STRING, "duration", "Duration of the poll", true));
        this.options = options;
        logger = LoggerFactory.getLogger(this.getClass().getName());
        if(!ConfigUtils.getBoolean("Commands.yesno.DefaultEnabled")){
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        }
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String name = event.optString("name");
        String desc = event.optString("description");
        EmbedBuilder eb = new EmbedBuilder();
        if(Poll.exist(name)){
            eb.setColor(Color.decode(ConfigUtils.getString("General.ErrorColor")));
            eb.setDescription(ConfigUtils.getString("General.NameExist"));
            logger.error(event.getUser().getAsTag() + " tried to create poll with name that already exists");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
        Poll poll = new YesNoPoll(name, desc);
        long duration;
        try {
            duration = Convert.toMilli(Objects.requireNonNull(event.optString("duration")));
        } catch (FailFormatException e) {
            eb.setColor(Color.decode(ConfigUtils.getString("General.ErrorColor")));
            eb.setDescription(ConfigUtils.getString("General.FailFormat"));
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            logger.error(event.getUser().getAsTag() + " didn't put good duration value when creating poll");
            return;
        }
        Date due = new Date(System.currentTimeMillis() + duration);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
        eb = poll.createMessageEmbed().setFooter(ConfigUtils.getString("Commands.yesno.EmbedFooter").replaceAll("%date%", date.format(due)).replaceAll("%time%", time.format(due)),
                ConfigUtils.getString("General.IconUrl"));
        event.replyEmbeds(eb.build()).addActionRow(Button.of(ButtonStyle.SUCCESS, "pollYes:" + name, "YES", Emoji.fromUnicode(ConfigUtils.getString("Commands.yesno.YesEmoji"))),
                Button.of(ButtonStyle.DANGER, "pollNo:" + name, "NO", Emoji.fromUnicode(ConfigUtils.getString("Commands.yesno.NoEmoji")))).queue();
        event.getHook().retrieveOriginal().queue(poll::setMessage);
        poll.start(duration);
        logger.info("YES/NO poll named " + name + " was successfully created by " + event.getUser().getAsTag());
    }
}
