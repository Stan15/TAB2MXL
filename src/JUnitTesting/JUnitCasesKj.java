package JUnitTesting;

import converter.measure_line.InvalidParameterValueException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class JUnitCasesKj {

    @Test(expected=InvalidParameterValueException.class)
    public void TestingremoveNameOf() throws InvalidParameterValueException {
        String lineTest = "";
        converter.measure_line.MeasureLine.removeNameOf(lineTest);
        //Assertions.assertThrows(removeNameOf(lineTest), throw new InvalidParameterValueException());
    }
    @Test(expected=InvalidParameterValueException.class)
    public void TestinggetNameOf() throws InvalidParameterValueException {
        String lineTest = "";
        converter.measure_line.MeasureLine.getNameOf(lineTest);
        //Assertions.assertThrows(removeNameOf(lineTest), throw new InvalidParameterValueException());
    }
    /*@Test(expected=NullPointerException.class)
    public void cannotConvertNulls() {
        service.convert(null);
    }*/

}
