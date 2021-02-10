package JUnitTesting;

import converter.Note;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class guitarMeasureLineTester {

    @Test
    void test1(){
        String s1 = ("[1,5]|e|---12---6-4-|");
        MeasureLine measureLine = new GuitarMeasureLine(s1, 4, 4);
        Integer[] expected = {12, 6, 4};
        for(int i = 0; i < measureLine.notes.size(); i++){
            Note note = measureLine.notes.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void test2(){
        String s2 = ("[5,10]|B|-1-------1-1-1-1--|");
        MeasureLine measureLine = new GuitarMeasureLine(s2, 4, 4);
        Integer[] expected = {1, 1, 1, 1, 1};
        for(int i = 0; i < measureLine.notes.size(); i++){
            Note note = measureLine.notes.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }
    @Test
    void test3(){
        String s3 = ("[3,3]|D|-----------------------------28-|");
        MeasureLine measureLine = new GuitarMeasureLine(s3, 4, 4);
        Integer[] expected = {28};
        for(int i = 0; i < measureLine.notes.size(); i++){
            Note note = measureLine.notes.get(i);
            assertEquals(note.fret, expected[i]);
        }
    }


}
