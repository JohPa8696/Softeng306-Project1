package utils;

public class InvalidArgumentException extends Exception {
	private String message;
	
	public InvalidArgumentException(){}
	
	public InvalidArgumentException(String message){
		super(message);
	}
	
	
}
