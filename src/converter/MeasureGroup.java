package converter;

import parser.InvalidMeasureFormatException;
import parser.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureGroup implements ScoreComponent{
    private String rootString;
    private int startIdx;
    private int endIdx;
    private List<String> lines;
    private Patterns patterns = new Patterns();
    private List<Measure> measures = new ArrayList<>();

    public MeasureGroup(List<String> lines, int startIdx, int endIdx, String rootString) {
        this.lines = lines;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.rootString = rootString;
        try {
            this.measures = this.getMeasures();
        }catch(InvalidMeasureFormatException e) {
            e.printStackTrace();
            // TODO prompt the user to reedit the line
        }
    }

    private List<Measure> getMeasures() throws InvalidMeasureFormatException {
        //this holds a list of measure where each measure is represented as a list of the lines which make up the group.
        List<List<String>> measuresStrList = new ArrayList<>();


        // as each line of a measure group holds multiple measures, i am not too sure how we can use
        // regex to collect the individual measures so i have done it manually.
        // This for loop goes through the lines and puts each individual line of a measure into a list
        // where its list index is its measure number, starting from measure zero. When it goes to the next
        // line, it starts again from measure number zero and adds each measure in the next line to its
        // corresponding measure number.
        Pattern measureLineStartPttrn = Pattern.compile(this.patterns.MeasureGroupStartSOL+"|"+this.patterns.MeasureGroupStartMIDL);
        Pattern measureLineNamePttrn = Pattern.compile(this.patterns.MeasureLineName);
        for (String strLine : this.lines) {
            //first get the measure line name for this line

            Matcher ptrnMatcher = measureLineStartPttrn.matcher(strLine);
            ptrnMatcher.find();
            String measureLineStart = ptrnMatcher.group();
            ptrnMatcher = measureLineNamePttrn.matcher(measureLineStart);
            ptrnMatcher.find();
            String measureLineName = ptrnMatcher.group();

            Pattern msurInsidesPttrn = Pattern.compile(patterns.MeasureInsides);
            Matcher msurInsidesMatcher = msurInsidesPttrn.matcher(strLine);
            int measureCount = 0;
            while (msurInsidesMatcher.find()) {
                //the information for this single line of a measure as [startIdx,endIdx]line-information
                int msurLineStartIdx = startIdx + ptrnMatcher.start();
                int msurLineEndIdx = startIdx + ptrnMatcher.end();
                String measureLine = "[" + msurLineStartIdx + "," + msurLineEndIdx + "]" + "|"+measureLineName+"|"+ptrnMatcher.group();

                if (measureCount >= measuresStrList.size()) {
                    measuresStrList.add(new ArrayList<>());
                }
                //get the measure which this current measure line belongs to and add this line to it
                List<String> currMeasureGroup = measuresStrList.get(measureCount);
                currMeasureGroup.add(measureLine);
                measureCount++;
            }
        }
        List<Measure> measuresList = new ArrayList<>();
        for(List<String> msurStrLines:measuresStrList) {
            if (Measure.isGuitar(msurStrLines))
                measuresList.add(GuitarMeasure.getInstance(msurStrLines, startIdx, endIdx, rootString));
            else if (Measure.isDrum(msurStrLines))
                measuresList.add(DrumMeasure.getInstance(msurStrLines, startIdx, endIdx, rootString));
            else
                throw new InvalidMeasureFormatException("measure type not supported");
        }
        return measuresList;
    }

    @Override
    public String toString() {
        String str = "";
        for (String line: this.lines) {
            str+= line.split(positionStampPtrn)[1]+"\n";
        }
        return str;
    }

    @Override
    public boolean validate() {
        // TODO validate that all measures in the measureGroup (this.measures) are of the same type by first implementing the Measure.isGuitar() and Measure.isDrum methods
        return false;
    }
}
