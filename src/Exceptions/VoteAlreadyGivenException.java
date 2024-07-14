package Exceptions;

public class VoteAlreadyGivenException extends Exception{
    public VoteAlreadyGivenException(String message) {
        super(message);
    }
}
