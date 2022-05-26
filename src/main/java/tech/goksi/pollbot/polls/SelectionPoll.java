package tech.goksi.pollbot.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import tech.goksi.pollbot.utils.ConfigUtils;

import java.awt.*;

public class SelectionPoll extends Poll{
    public SelectionPoll(String name, String description) {
        super(name, description);
        String config = "{" +
                "    type: 'pie'," +
                "    data: {" +
                "      labels: [%labels]," +
                "      datasets: [{" +
                "        label: '%name statistics'," +
                "        backgroundColor: [%pieColors]," +
                "        data: [%d]" +
                "      }]" +
                "    }," +
                "    options: {" +
                "      title: {" +
                "        display: true," +
                "        text: '%name statistics'" +
                "      }" +
                "    }" +
                "}";
        setConfig(config);
    }

    @Override
    public EmbedBuilder createMessageEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(ConfigUtils.getString("General.EmbedColor")));
        eb.setDescription(getDescription());
        eb.setTitle(getName());
        eb.setFooter(ConfigUtils.getString("Commands.selection.pollEnded"), ConfigUtils.getString("General.IconUrl"));
        eb.setAuthor(ConfigUtils.getString("General.EmbedAuthor"));
        return eb;
    }
}
