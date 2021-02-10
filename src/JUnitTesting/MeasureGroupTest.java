package JUnitTesting;

import converter.MeasureGroup;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MeasureGroupTest {
    @Test
    void testGetMeasures() {
        List<String> mLines = new ArrayList<>();
        mLines.add("");
        MeasureGroup mGroup = new MeasureGroup(mLines, 2, 5, "fhisodghi");
        assertEquals(1,1, "hi");
    }
}
