package converter.measure_line;

import converter.ScoreComponent;
import converter.measure_line.DrumMeasureLine;
import converter.measure_line.GuitarMeasureLine;

import java.util.ArrayList;
import java.util.List;

public abstract class MeasureLine implements ScoreComponent {

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
