package tech.goksi.pollbot.exceptions;

public class PollExistException extends Exception{
    public PollExistException(String message){
        super(message);
    }
}
