package tech.goksi.pollbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tech.goksi.pollbot.polls.Poll;
import tech.goksi.pollbot.utils.ConfigUtils;


import java.awt.*;
import java.util.Objects;

public class SelectionHandler extends ListenerAdapter {
    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if(Objects.requireNonNull(event.getSelectMenu().getId()).startsWith("pollSelect")){
            String name = event.getSelectMenu().getId().split(":")[1];
            if(!Poll.exist(name)) return;
            Poll poll = Poll.getPoll(name);
            EmbedBuilder eb = new EmbedBuilder();
            if(!poll.hasVoted(event.getUser())){
                poll.addVote(event.getSelectedOptions().get(0).getLabel(),event.getUser());
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
