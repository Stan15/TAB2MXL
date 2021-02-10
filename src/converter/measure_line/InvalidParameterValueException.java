package converter.measure_line;


public class InvalidParameterValueException extends Exception{

    public InvalidParameterValueException(){
        super();
    }
    public InvalidParameterValueException(String message){
        super(message);
    }

}
