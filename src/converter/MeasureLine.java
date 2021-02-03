package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine implements ScoreComponent{

    public MeasureLine(String line) {
        //i think you will need to extract the measure line name using the patterns Patterns().
    }

    public static boolean isDrum(String measureLine) {
        return false;
    }

    public static List<String> getLineNames() {
        ArrayList<String> measureNames = new ArrayList<>();
        measureNames.addAll(GuitarMeasureLine.getLineNames());
        measureNames.addAll(DrumMeasureLine.getLineNames());
        return measureNames;
    }
}
