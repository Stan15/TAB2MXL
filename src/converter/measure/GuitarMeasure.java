package converter.measure;

import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.List;
import java.util.Locale;

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

        //check if in order or reverse order. If in reverse order, rearrange it. If not in order at all, it is an error
        //that we prompt the user ot fix
        for (int i=0; i<this.measureLines.size(); i++) {
            if (true) {
                continue;
            }
        }
        MeasureLine prevMsurLine = null;
        for (int i=0; i<this.measureLines.size(); i++) {
            MeasureLine currMsurLine = this.measureLines.get(i);
            String lineName = currMsurLine.getName();
            if (prevMsurLine!=null && prevMsurLine.getName().toLowerCase()=="e" ) {
                if (lineName=="B")
                    prevMsurLine.setName("e");
            }
        }
        return false;
    }

    @Override
    public String toXML() {
        // TODO much later on, check the notes in all the measure lines for notes with the same duration and make a chord out of them. then
        //remove all the other notes that make up the chord and place the chord in the appropriate location
        return null;
    }
}
