package converter;

import java.util.Collection;
import java.util.List;

public class DrumMeasure extends Measure{
    private List<MeasureLine> measureLines;
//ddd
    private DrumMeasure(List<String> lines, int startIdx, int  endIdx, String rootStr) {
        super(lines, startIdx, endIdx, rootStr);
    }
    public static DrumMeasure getInstance(List<String> lines, int startIdx, int  endIdx, String rootStr) {
        if (Measure.isGuitar(lines)) {
            return new DrumMeasure(lines, startIdx, endIdx, rootStr);
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
}
