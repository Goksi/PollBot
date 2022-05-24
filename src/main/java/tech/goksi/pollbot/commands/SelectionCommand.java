package tech.goksi.pollbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.polls.Poll;
import tech.goksi.pollbot.polls.SelectionPoll;
import tech.goksi.pollbot.utils.ConfigUtils;
import tech.goksi.pollbot.utils.Convert;
import tech.goksi.pollbot.utils.FailFormatException;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SelectionCommand extends SlashCommand {
    private final Logger logger;
    public SelectionCommand(){
        this.name = "selection";
        this.help = "Command for creating selection polls";
        this.cooldown = ConfigUtils.getInt("Commands.selection.CoolDown");
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "This value must be unique", true));
        options.add(new OptionData(OptionType.STRING, "description", "Description of the poll", true));
        options.add(new OptionData(OptionType.STRING, "options", "Options for this poll, split them with \":\"", true));
        options.add(new OptionData(OptionType.STRING, "duration", "Duration of the poll", true));
        this.options = options;
        logger = LoggerFactory.getLogger(this.getClass().getName());
        if(!ConfigUtils.getBoolean("Commands.selection.DefaultEnabled")){
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        }

    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String name = event.optString("name");
        String description = event.optString("description");
        String[] options = Objects.requireNonNull(event.optString("options")).split(":");
        EmbedBuilder eb = new EmbedBuilder();
        if(Poll.exist(name)){
            eb.setColor(Color.decode(ConfigUtils.getString("General.ErrorColor")));
            eb.setDescription(ConfigUtils.getString("General.NameExist"));
            logger.error(event.getUser().getAsTag() + " tried to create poll with name that already exists");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
        Poll poll = new SelectionPoll(name, description);
        long duration;
        poll.addOptions(options);
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
        eb = poll.createMessageEmbed().setFooter(ConfigUtils.getString("Commands.selection.EmbedFooter").replaceAll("%date%", date.format(due)).replaceAll("%time%", time.format(due)),
                ConfigUtils.getString("General.IconUrl"));
        SelectMenu.Builder menuBuilder = SelectMenu.create("pollSelect:" + name);
        menuBuilder.setPlaceholder(ConfigUtils.getString("Commands.selection.PlaceHolder"));
        for(int i = 0; i<options.length; i++){
            menuBuilder.addOption(options[i], String.valueOf(i));
        }
        event.replyEmbeds(eb.build()).addActionRow(menuBuilder.build()).queue();
        event.getHook().retrieveOriginal().queue(poll::setMessage);
        poll.start(duration);
        logger.info("Selection poll named " + name + " was successfully created by " + event.getUser().getAsTag());
    }
}
