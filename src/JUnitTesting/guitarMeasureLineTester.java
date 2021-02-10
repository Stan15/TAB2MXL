package JUnitTesting;

import converter.Note;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class guitarMeasureLineTester {

    @Test
    void test(){
        String s = "[1,5]|e|---12---6-4-|";
        MeasureLine measureLine = new GuitarMeasureLine(s, 4, 4);
        Integer[] expected = {12, 6, 4};
        for(int i = 0; i < measureLine.notes.size(); i++){
            Note note = measureLine.notes.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }


}
