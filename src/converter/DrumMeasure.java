package converter;

import java.util.List;

public class DrumMeasure extends Measure{

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
        return false;
    }
}
