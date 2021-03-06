package tech.goksi.pollbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tech.goksi.pollbot.polls.Poll;
import tech.goksi.pollbot.utils.ConfigUtils;

import java.awt.*;
import java.util.Objects;

public class ButtonHandler extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getButton().getId()).startsWith("poll")) {
            String name = event.getButton().getId().split(":")[1];

            if(!Poll.exist(name)) return;
            Poll poll = Poll.getPoll(name);
            EmbedBuilder eb = new EmbedBuilder();
            if(!poll.hasVoted(event.getUser())){
                if(event.getButton().getId().contains("Yes")){
                    poll.addVote("YES", event.getUser());
                }else if(event.getButton().getId().contains("No")){
                    poll.addVote("NO", event.getUser());
                }
                eb.setColor(Color.decode(ConfigUtils.getString("General.SuccessColor")));
                eb.setDescription(ConfigUtils.getString("General.SuccessText"));
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }else {
                eb.setColor(Color.decode(ConfigUtils.getString("General.ErrorColor")));
                eb.setDescription(ConfigUtils.getString("General.AlreadyVoted"));
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
