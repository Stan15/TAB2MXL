package converter;

import java.util.List;

public class GuitarMeasure extends Measure{

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
        return false;
    }
}
