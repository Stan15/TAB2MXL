package converter;

import converter.measure.DrumMeasure;
import converter.measure.GuitarMeasure;
import converter.measure.Measure;
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

    /**
     * Instantiates a measure group object
     * @param lines The list of strings containing the lines which make up the measure group, formatted as
     *              follows: "[startIdx,endIdx]|measureLineName|---measure-|--group-|--line--|--stuff--|"
     * @param startIdx the index at which this measure group starts in the root string
     * @param endIdx the index at which this measure group ends in the root string
     * @param rootString the tablature string where this measure group belongs.
     */
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
        String measureLineStartRegex = this.patterns.MeasureGroupStartSOL+"|"+this.patterns.MeasureGroupStartMIDL;
        Pattern measureLineStartPttrn = Pattern.compile(measureLineStartRegex);
        Pattern measureLineNamePttrn = Pattern.compile(this.patterns.MeasureLineName);
        for (String measureLine : this.lines) {
            measureLine = measureLine.split(positionStampPtrn)[1];

            //first get the measure line name for this line
            Matcher measureLineStartMatcher = measureLineStartPttrn.matcher(measureLine);
            measureLineStartMatcher.find();
            String measureLineStart = measureLineStartMatcher.group();
            Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(measureLineStart);
            measureLineNameMatcher.find();
            String measureLineName = measureLineNameMatcher.group();

            Pattern msurInsidesPttrn = Pattern.compile("(?<=\\||"+measureLineStartRegex+")"+patterns.MeasureInsides);
            Matcher msurInsidesMatcher = msurInsidesPttrn.matcher(measureLine);
            int measureCount = 0;
            while (msurInsidesMatcher.find()) {
                //the information for this single line of a measure as [startIdx,endIdx]line-information
                int msurLineStartIdx = startIdx + measureLineStartMatcher.start();
                int msurLineEndIdx = startIdx + measureLineStartMatcher.end();
                String formattedMeasureLine = "[" + msurLineStartIdx + "," + msurLineEndIdx + "]" + measureLineName+"|"+msurInsidesMatcher.group();

                if (measureCount >= measuresStrList.size()) {
                    measuresStrList.add(new ArrayList<>());
                }
                //get the measure which this current measure line belongs to and add this line to it
                List<String> currMeasureGroup = measuresStrList.get(measureCount);
                currMeasureGroup.add(formattedMeasureLine);
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
        if (this.measures==null) {return null;}
        String strMeasures = "";
        for(Measure measure:this.measures)
            strMeasures += measure.toString() + "\n";
        return strMeasures;
    }

    /**
     * Validates that all measures in this measure group are of the same type
     * @return true if all measures are of the same, supported type, false otherwise
     */
    @Override
    public boolean validate() {
        // TODO validate that all measures in the measureGroup (this.measures) are of the same type by first implementing the Measure.isGuitar() and Measure.isDrum methods
        String prevType = "";
        for(Measure measure : measures) {
            if (measure instanceof GuitarMeasure) {
                if (prevType.equals("")) {
                    prevType = "Guitar";
                }else if (!prevType.equals("Guitar"))
                    return false;
            }else if (measure instanceof DrumMeasure) {
                if (prevType.equals("")) {
                    prevType = "Drum";
                }else if (!prevType.equals("Drum"))
                    return false;
            }else {
                return false;   //unsupported type
            }
        }
        return true;
    }
}
