package tech.goksi.pollbot.polls;


import tech.goksi.pollbot.Bot;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Poll {
    private String name;
    private List<Integer> voted = new ArrayList<>();
    private List<Integer> options = new ArrayList<>();
    Poll(String name){
        this.name = name;
    }

    abstract public void addOption(int option);
    public void createPool(List<Integer> options){
        try{
            PreparedStatement ps = Bot.getInst().getSql().getConnection().prepareStatement("");
        }catch (SQLException e){

        }

    }


}
