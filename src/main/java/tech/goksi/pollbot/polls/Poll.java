package tech.goksi.pollbot.polls;



import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.goksi.pollbot.Bot;

import java.util.*;

public  class Poll {


    public enum Types{
        YESNO,
        SELECTMENU
    }
    private String name;
    private final List<Long> voted = new ArrayList<>();
    private final Map<String, ArrayList<Long>>  options = new HashMap<>(); //Option, list of users who voted for that option
    private Types type;
    private Message message;

    public Poll(String name, Types type){
        this.name = name;
        this.type = type;
    }

    public void addOption(){
        if(type.equals(Types.YESNO)) {
            options.put("YES", new ArrayList<>());
            options.put("NO", new ArrayList<>());
        }
    }
    public void addOption(String... options){
        for(String s : options){
            this.options.put(s, new ArrayList<>());
        }
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void addVote(String option, User u){
        for(Map.Entry<String, ArrayList<Long>> e : options.entrySet()){
            if(e.getKey().equals(option)) {
                e.getValue().add(u.getIdLong());
            }
        }
    }
    public void save(){
        Bot.getInst().getPolls().put(name, this);
    }

    public void end(){
        Bot.getInst().getPolls().remove(name);
    }

    public String getName() {
        return name;
    }
}
