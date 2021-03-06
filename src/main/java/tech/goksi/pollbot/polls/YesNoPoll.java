package tech.goksi.pollbot.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import tech.goksi.pollbot.utils.ConfigUtils;

import java.awt.*;

public class YesNoPoll extends Poll{
    public YesNoPoll(String name,String description) {
        super(name, description);
        addOptions("YES", "NO");
        String config = "{" +
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
                "            backgroundColor: '%color'" +
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
        setConfig(config);
    }

    @Override
    public EmbedBuilder createMessageEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(ConfigUtils.getString("General.EmbedColor")));
        eb.setDescription(getDescription());
        eb.setTitle(getName());
        eb.setFooter(ConfigUtils.getString("Commands.yesno.pollEnded"), ConfigUtils.getString("General.IconUrl"));
        eb.setAuthor(ConfigUtils.getString("General.EmbedAuthor"));
        return eb;
    }

}
