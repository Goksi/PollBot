package tech.goksi.pollbot.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.goksi.pollbot.utils.ConfigUtils;

import java.awt.*;

public class YesNoPoll extends Poll{
    private final String config = "{" +
            "   type:'bar'," +
            "   data:{" +
            "      labels:[" +
            "         'YES'," +
            "         'NO'" +
            "      ]," +
            "      datasets:[" +
            "         {" +
            "            label:'%name statistics'," +
            "            data:[" +
            "               %d" +
            "            ]," +
            "            backgroundColor: ['%color']" +
            "         }" +
            "      ]" +
            "   }," +
            "   options:{" +
            "      scales:{" +
            "         yAxes:[" +
            "            {" +
            "               ticks:{" +
            "                  stepSize:1," +
            "                  beginAtZero:true" +
            "               }" +
            "            }" +
            "         ]" +
            "      }" +
            "   }" +
            "}";
    public YesNoPoll(String name,String description , Message msg) {
        super(name, description, msg);
        addOptions("YES", "NO");
        setConfig(config);
    }

    @Override
    public MessageEmbed createMessageEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(ConfigUtils.getString("General.EmbedColor")));
        eb.setDescription(getDescription());
        eb.setTitle(getName());
        eb.setFooter(ConfigUtils.getString("Commands.yesno.pollEnded"), ConfigUtils.getString("General.IconUrl"));
        eb.setAuthor(ConfigUtils.getString("General.EmbedAuthor"));
        return eb.build();
    }

}
