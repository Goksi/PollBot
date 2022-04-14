package tech.goksi.pollbot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlMain {
    private Connection connection;
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    public void connect(){
        if(!isConnected()){
            try{
                connection = DriverManager.getConnection("jdbc:sqlite:memory:");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private boolean isConnected(){
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect(){
        if(isConnected()){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

}

