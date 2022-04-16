package tech.goksi.pollbot.polls;



import net.dv8tion.jda.api.entities.User;
import tech.goksi.pollbot.Bot;

import java.util.*;

public  class Poll {

    enum Types{
        YESNO,
        SELECTMENU
    }
    private String name;
    private List<Long> voted = new ArrayList<>();
    private Map<String, List<Long>>  options = new HashMap<>(); //Option, list of users who voted for that option
    private Types type;

    Poll(String name, Types type){
        this.name = name;
        this.type = type;
    }

    public void addOption(){
        if(type.equals(Types.YESNO)) {
            options.put("YES", Collections.emptyList());
            options.put("NO", Collections.emptyList());
        }
    }
    public void addOption(List<String> options){
        for(String s : options){
            this.options.put(s, Collections.emptyList());
        }
    }

    public List<String> getOptions(){
        List<String> temp = new ArrayList<>();
        for(Map.Entry<String, List<Long>> e : options.entrySet()) temp.add(e.getKey());
        return temp;
    }

    public boolean hasVoted(User u){
        for(Map.Entry<String, List<Long>> e : options.entrySet()){
            if(e.getValue().contains(u.getIdLong())) return true;
        }
        return false;
    }

    public int getVoteCount(String option){
        for(Map.Entry<String, List<Long>> e : options.entrySet()){
            if(e.getKey().equals(option)){
                return e.getValue().size();
            }
        }
        return 0;
    }

    public void addVote(String option, User u){
        for(Map.Entry<String, List<Long>> e : options.entrySet()){
            if(e.getKey().equals(option))e.getValue().add(u.getIdLong());
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
