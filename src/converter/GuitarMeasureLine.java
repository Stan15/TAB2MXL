package converter;

import java.util.Arrays;
import java.util.List;

public class GuitarMeasureLine extends MeasureLine{

    public GuitarMeasureLine(String line) {
        super(line);
    }

    public static List<String> getLineNames() {
        String[] guitarMeasureNames = {"E", "A", "D", "G", "B", "e", "a", "d", "g", "b"};
        return Arrays.asList(guitarMeasureNames);
    }

    /**
     * TODO Validate that a given measure line string is a drum measure line
     * by ensuring that the measure line name belongs in the list of drum measure line
     * names only. If the measure line name belongs in both drums and guitar, you have to then
     * check the measure components, or "notes" to make sure that it belongs to drums
     *
     */
    @Override
    public boolean validate() {
        //validate that the measure line components, or notes, are valid and as expected of a guitar measure line
        return false;
    }
}
