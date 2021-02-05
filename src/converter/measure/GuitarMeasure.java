package converter.measure;

import converter.measure_line.MeasureLine;

import java.util.List;

public class GuitarMeasure extends Measure {
    private List<MeasureLine> measureLines;

    private GuitarMeasure(List<String> lines, int startIdx, int  endIdx, String rootStr) {
        super(lines, startIdx, endIdx, rootStr);
    }

    public static GuitarMeasure getInstance(List<String> lines, int startIdx, int  endIdx, String rootStr) {
        if (Measure.isGuitar(lines)) {
            return new GuitarMeasure(lines, startIdx, endIdx, rootStr);
        }else {
            return null;
        }
    }

    @Override
    public boolean validate() {
        //validate that each of the measure lines are valid and belong to a guitar,
        // and that they are placed in the right order (e, B, G, D, A, E).
        return false;
    }

    @Override
    public String toXML() {
        // TODO much later on, check the notes in all the measure lines for notes with the same duration and make a chord out of them. then
        //remove all the other notes that make up the chord and place the chord in the appropriate location
        return null;
    }
}
