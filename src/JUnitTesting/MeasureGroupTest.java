package JUnitTesting;

import converter.MeasureGroup;
import converter.measure.Measure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MeasureGroupTest {

    private MeasureGroup mGroup;

    @BeforeEach
    void setup() {
        List<String> mLines = new ArrayList<>();
        mLines.add("[2,3]|e|---1--5-|--2--6-|");
        mLines.add("[2,3]|B|---1--5-|--2--6-|");
        mLines.add("[2,3]|G|---1--5-|--2--6-|");
        mLines.add("[2,3]|D|---1--5-|--2--6-|");
        mLines.add("[2,3]|A|---1--5-|--2--6-|");
        mLines.add("[2,3]|E|---1--5-|--2--6-|");
        this.mGroup = new MeasureGroup(mLines, 2, 5, "fhisodghi");
    }

    @Test
    void testGetMeasuresLineOneEqual() {
        //testing if first line is as expected
        String[] expected = {"---1--5-", "--2--6-"};
        for (int i=0; i<mGroup.measures.size(); i++) {
            Measure measure = mGroup.measures.get(i);
            assertEquals(measure.measureLines.get(1).Line, expected[i]);
        }
    }

    @Test
    void testGetMeasuresIsFirstMeasure() {
        //testing if only the first measure is dete
        Boolean[] expected = {true, false};
        for (int i=0; i<mGroup.measures.size(); i++) {
            Measure measure = mGroup.measures.get(i);
            assertEquals(measure.isFirstMeasure, expected[i]);
        }
    }
}
