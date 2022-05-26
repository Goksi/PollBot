package tech.goksi.pollbot.polls;

import io.quickchart.QuickChart;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import tech.goksi.pollbot.Bot;
import tech.goksi.pollbot.utils.ConfigUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Poll {
    public abstract EmbedBuilder createMessageEmbed();
    public void addOptions(String... options) {
        for(String option : options){
            this.options.put(option, new ArrayList<>());
        }
    }
    private final String description;
    private final Map<String, ArrayList<Long>> options;
    private static final Map<String, Poll> polls = new HashMap<>();
    private  Message message;
    private final String name;
    private String config;


    public Poll(String name,String description ){
        options = new HashMap<>();
        this.name = name;
        this.description = description;
    }
    public List<String> getOptions(){
        List<String> temp = new ArrayList<>();
        for(Map.Entry<String, ArrayList<Long>> e : options.entrySet()) temp.add(e.getKey());
        return temp;
    }
    public boolean hasVoted(User u){
        for(Map.Entry<String, ArrayList<Long>> e : options.entrySet()){
            if(e.getValue().contains(u.getIdLong())) return true;
        }
        return false;
    }
    public int getVoteCount(String option){
        for(Map.Entry<String, ArrayList<Long>> e : options.entrySet()){
            if(e.getKey().equals(option)){
                return e.getValue().size();
            }
        }
        return 0;
    }
    public void addVote(String option, User u){
        for(Map.Entry<String, ArrayList<Long>> e : options.entrySet()){
            if(e.getKey().equals(option)) {
                e.getValue().add(u.getIdLong());
            }
        }
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    protected void setConfig(String config){
        this.config = config;
    }

    private Message getMessage() {
        return message;
    }
    public static boolean exist(String name) {
        return polls.containsKey(name);
    }
    public static Poll getPoll(String name){
        return polls.get(name);
    }

    public void start(long duration)  {
        polls.put(name, this);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() ->{
            EmbedBuilder eb = new EmbedBuilder();
            QuickChart chart = new QuickChart();
            StringBuilder opt = new StringBuilder();
            StringBuilder count = new StringBuilder();
            for(String option : getOptions()){
                opt.append("'");
                opt.append(option).append("'");
                opt.append(",");
                count.append(getVoteCount(option)).append(",");
            }
            String labels = opt.substring(0, opt.length()-1);
            config = config.replaceAll("%labels", labels).replaceAll("%d", count.substring(0, count.length()-1)).replaceAll("%name", getName())
                    .replaceAll("%color",  "rgb(" + Integer.valueOf(ConfigUtils.getString("Commands.yesno.BarColor").substring(1, 3), 16) + "," +
                            Integer.valueOf(ConfigUtils.getString("Commands.yesno.BarColor").substring(3, 5), 16) + "," +
                            Integer.valueOf(ConfigUtils.getString("Commands.yesno.BarColor").substring(5, 7), 16) + ")").replaceAll("%pieColors",
                            generateColorCodes(getOptions().size()));
            chart.setConfig(config);
            chart.setWidth(500);
            chart.setHeight(300);
            chart.setBackgroundColor("rgb(" + Integer.valueOf(ConfigUtils.getString("General.chartBackgroundColor").substring(1, 3), 16) + "," +
                    Integer.valueOf(ConfigUtils.getString("General.chartBackgroundColor").substring(3, 5), 16) + "," +
                    Integer.valueOf(ConfigUtils.getString("General.chartBackgroundColor").substring(5, 7), 16) + ")");
            /*finished chart eb*/
            eb.setColor(Color.decode(Bot.getInst().getConfig().getString("General.SuccessColor")));
            eb.setImage(chart.getUrl());
            getMessage().replyEmbeds(eb.build()).queue();
            /*editing original embed*/
            ActionRow row = getMessage().getActionRows().get(0);
            getMessage().editMessageEmbeds(createMessageEmbed().build()).setActionRows(row.asDisabled()).queue();
            polls.remove(getName());

        }, duration, TimeUnit.MILLISECONDS);
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    private String generateColorCodes(int count){
        StringBuilder sb = new StringBuilder();
        String colorCode;
        Random rnd = new Random();
        for(int i = 0; i<count; i++){
            colorCode = String.format("#%06x", rnd.nextInt(0xffffff + 1));
            if(sb.toString().contains(colorCode)){
                i--;
                continue;
            }
            sb.append("'").append(colorCode).append("'").append(",");
        }

        return sb.substring(0, sb.length()-1);
    }
}
