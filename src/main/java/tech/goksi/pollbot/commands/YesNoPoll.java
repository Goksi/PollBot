package tech.goksi.pollbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;


import io.quickchart.QuickChart;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.goksi.pollbot.Bot;
import tech.goksi.pollbot.polls.Poll;
import tech.goksi.pollbot.utils.ConfigUtils;
import tech.goksi.pollbot.utils.Convert;
import tech.goksi.pollbot.utils.FailFormatException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YesNoPoll extends SlashCommand {
    private final String chartConfig = "{"
                               + "    type: 'bar',"
                               + "    data: {"
                               + "        labels: ['YES', 'NO'],"
                               + "        datasets: [{"
                               + "            label: 'Votes',"
                               + "            data: [%d]"
                               + "        }]"
                               + "    }"
                               + "}";
    private final Logger logger;
    public YesNoPoll(){
        this.name = "yesno";
        this.help = "Command for creating Yes/No polls";
        this.cooldown = ConfigUtils.getInt("Commands.yesno.CoolDown");
        this.defaultEnabled = ConfigUtils.getBoolean("Commands.yesno.DefaultEnabled");
        this.enabledRoles = ConfigUtils.getStringList("Commands.yesno.AllowedRoles").toArray(new String[0]);
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "This value must be unique", true));
        options.add(new OptionData(OptionType.STRING, "description", "Description of the poll", true));
        options.add(new OptionData(OptionType.STRING, "duration", "Duration of the poll", true));
        this.options = options;
        logger = LoggerFactory.getLogger(this.getClass().getName());
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String name = event.optString("name");
        EmbedBuilder eb = new EmbedBuilder();
        if(Bot.getInst().getPolls().containsKey(name)){
            eb.setColor(Color.decode(ConfigUtils.getString("General.ErrorColor")));
            eb.setDescription(ConfigUtils.getString("General.NameExist"));
            logger.error(event.getUser().getAsTag() + " tried to create poll with name that already exists");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }
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
        String description = event.optString("description");
        Date due = new Date(System.currentTimeMillis() + duration);
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
        Poll poll = new Poll(name, Poll.Types.YESNO);
        poll.addOption();
        eb.setColor(Color.decode(ConfigUtils.getString("General.EmbedColor")));
        eb.setDescription(description);
        eb.setTitle(name);
        eb.setFooter(ConfigUtils.getString("Commands.yesno.EmbedFooter").replaceAll("%date%", date.format(due)).replaceAll("%time%", time.format(due)));

        event.replyEmbeds(eb.build()).addActionRow(Button.of(ButtonStyle.SUCCESS, "pollYes:" + name, "YES", Emoji.fromUnicode(ConfigUtils.getString("Commands.yesno.YesEmoji"))),
                Button.of(ButtonStyle.DANGER, "pollNo:" + name, "NO", Emoji.fromUnicode(ConfigUtils.getString("Commands.yesno.NoEmoji")))).queue();
        Message m = event.getHook().retrieveOriginal().complete();
        poll.setMessage(m);
        poll.save();
        logger.info("Poll named " + name + " was successfully created by " + event.getUser().getAsTag() );
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            eb.clear();
            int votesYes = poll.getVoteCount("YES");
            int voteNo = poll.getVoteCount("NO");
            String chConf = chartConfig.replace("%d", votesYes + ", " + voteNo);
            QuickChart chart = new QuickChart();
            chart.setConfig(chConf);
            chart.setWidth(500);
            chart.setHeight(300);

            chart.setBackgroundColor(ConfigUtils.getString("General.chartBackgroundColor"));
            eb.clear();
            eb.setColor(Color.decode(Bot.getInst().getConfig().getString("General.SuccessColor")));
            eb.setImage(chart.getUrl());
            poll.getMessage().replyEmbeds(eb.build()).queue();
            eb.clear();
            eb.setColor(Color.decode(ConfigUtils.getString("General.EmbedColor")));
            eb.setDescription(description);
            eb.setTitle(name);
            eb.setFooter(ConfigUtils.getString("Commands.yesno.pollEnded"));
            poll.getMessage().editMessageEmbeds(eb.build()).setActionRows().queue();
            poll.end();
        }, duration, TimeUnit.MILLISECONDS);



    }
}
