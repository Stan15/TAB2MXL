package converter;

import converter.measure.DrumMeasure;
import converter.measure.GuitarMeasure;
import converter.measure.Measure;
import converter.measure_line.MeasureLine;
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
            measureLine = Patterns.removePositionStamp(measureLine);

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

    /**
     * Validates that all measures in this measure group are of the same type
     * @return true if all measures are of the same, supported type, false otherwise
     */
    @Override
    public boolean validate() {
        // ensure that all measures in this measrue group
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

        //validate all the measures that are contained in this measure group
        for (Measure measure : this.measures) {
            if (!measure.validate())
                return false;
        }
        return true;
    }

    @Override
    public String toXML() {
        String xmlString = "";
        for (Measure measure : this.measures) {
            xmlString += measure.toXML() + "\n";
        }
        return xmlString;
    }

    @Override
    public String toString() {
        String str = "";
        List<String[]> measures = new ArrayList<>();
        for (Measure measure : this.measures) {
            measures.add(measure.toString().split("\n"));
        }
        int lineLength = measures.get(0).length;
        for (int measureLineNum=0; measureLineNum<lineLength; measureLineNum++) {
            for (int measureNum=0; measureNum<measures.size(); measureNum++) {
                //print the measureName only for the first measure
                if (measureNum==0) {
                   str += MeasureLine.getName(measures.get(measureNum)[measureLineNum]) + "|";
                }
                Pattern measureContentPttrn = Pattern.compile(patterns.MeasureInsides);
                Matcher measureContentMatcher = measureContentPttrn.matcher(measures.get(measureNum)[measureLineNum]);
                measureContentMatcher.find();
                str += measureContentMatcher.group() + "|";
            }
            str+="\n";
        }
        return str;
    }
}
