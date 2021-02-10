package converter.measure;

import converter.measure_line.DrumMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.List;

public class DrumMeasure extends Measure {
    private List<MeasureLine> measureLines;
    private DrumMeasure(List<String> lines, int startIdx, int  endIdx, String rootStr, boolean isFirstMeasure) {
        super(lines, startIdx, endIdx, rootStr, isFirstMeasure);
    }
    public static DrumMeasure getInstance(List<String> lines, int startIdx, int  endIdx, String rootStr, boolean isFirstMeasure) {
        if (Measure.isGuitar(lines)) {
            return new DrumMeasure(lines, startIdx, endIdx, rootStr, isFirstMeasure);
        }else {
            return null;
        }
    }

    @Override
    public boolean validate() {
        // TODO verify that each of the measure line in the measure belongs to a drum and then validate each line

        for (MeasureLine measureLine : measureLines) {
            if (!(measureLine instanceof DrumMeasureLine) && measureLine.validate()) {
                return false;
            }
        }
        return true;
    }

    public String toXML() {
        return null;
    }
}
