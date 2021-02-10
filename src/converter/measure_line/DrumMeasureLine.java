package converter.measure_line;

import converter.Beat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrumMeasureLine extends MeasureLine {
    protected List<Beat> beats = new ArrayList<>();

    public DrumMeasureLine(String line) throws InvalidParameterValueException {
        super(line);
    }

    public static List<String> getLineNames() {
        String[] drumMeasureNames = {"CC", "Ch", "C2", "HH", "Rd", "R", "SN", "T1", "T2", "FT", "BD", "Hf", "FH", "C", "H", "s", "S", "B", "Hh", "F", "F2", "Ht", "Mt", "f1", "f2", "Hhf"};
        return Arrays.asList(drumMeasureNames);
    }

    /**
     * TODO validate that the symbols in the measure line correspond to the measure line name.
     * look at this wikipedia page. If the measure line is a Cymbal measure line,
     * only certain types of symbols, or "notes" can be in that measure line
     * https://en.wikipedia.org/wiki/Drum_tablature#:~:text=Drum%20tablature,%20commonly%20known%20as%20a%20drum%20tab,,to%20stroke.%20Drum%20tabs%20frequently%20depict%20drum%20patterns.
     * @return true if the drum measure line is valid, false otherwise
     */
    @Override
    public boolean validate() {
        //validate that the measure line components, or notes, are valid and as expected of a guitar measure line
        return false;
    }

    public String toXML() {
        return null;
    }
}
