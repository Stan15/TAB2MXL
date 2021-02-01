package parser;

public class InvalidMeasureFormatException extends Exception {
	public InvalidMeasureFormatException() {
		super();
	}
	
	public InvalidMeasureFormatException(String msg) {
		super(msg);
	}
}
