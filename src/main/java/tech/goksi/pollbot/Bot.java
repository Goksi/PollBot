package tech.goksi.pollbot;

import tech.goksi.pollbot.database.SqlMain;

public class Bot {
    private SqlMain sql;
    private static Bot inst;
    public void start(){
        inst = this;
        sql = new SqlMain();
    }

    public SqlMain getSql(){
        return sql;
    }

    public static Bot getInst() {
        return inst;
    }
}
