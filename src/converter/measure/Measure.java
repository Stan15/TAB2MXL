package converter.measure;

import converter.ScoreComponent;
import converter.measure_line.DrumMeasureLine;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import parser.Patterns;

import java.util.ArrayList;
import java.util.List;

//there should be a concrete guitar measure, drum measure, and bass measure class
public abstract class Measure implements ScoreComponent {
    protected String rootString;
    protected int startIdx;
    protected int endIdx;
    protected List<String> lines;
    protected List<MeasureLine> measureLines;
    protected boolean isFirstMeasure;
    public static int measureNum = 0;
    
    public Measure(List<String> lines, int startIdx, int endIdx, String rootStr, boolean isFirstMeasure) {
        this.lines = lines;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.rootString = rootStr;
        this.isFirstMeasure = isFirstMeasure;
        this.measureLines = getMeasureLines();
    }

    public List<MeasureLine> getMeasureLines() {
        List<MeasureLine> measureLines = new ArrayList<>();
        for (String line : this.lines) {
            if (MeasureLine.isGuitar(line)) {
                GuitarMeasure measure = (GuitarMeasure)this;
                measureLines.add(new GuitarMeasureLine(line, measure.beats, measure.beatType));
            }else if (MeasureLine.isDrum(line))
                measureLines.add(new DrumMeasureLine(line));
        }
        return measureLines;
    }

    /**
     * TODO complete this. use regex to figure out if all the measure line names in this measure are measure names which are for guitars, like E, A, D, etc.
     * @param measureLines a list of strings where each string is formatted as follows "[startIdx,endIdx]|measureLineName|---measure-stuff---|"
     *                    where startIdx and endIdx is the start and end index of this measure(position stamp) in the tablature string from which they originated.
     * @return true if the input measureLines are all guitar measure lines
     */
    public static boolean isGuitar(List<String> measureLines) {
        for (String line : measureLines) {
            line = Patterns.removePositionStamp(line);
            if (!MeasureLine.isGuitar(line))
                return false;
        }
        return true;
    }

    /**
     * TODO complete this. use regex to figure out if all the measure line names in this measure are measure names which are for drums.
     * @param measureLines a list of strings where each string is formatted as follows "[startIdx,endIdx]|measureLineName|---measure-stuff---|"
     *                     where startIdx and endIdx is the start and end index of this measure(position stamp) in the tablature string from which they originated.
     * @return true if the input measureLines are all guitar measure lines
     */
    public static boolean isDrum(List<String> measureLines) {
        for (String line : measureLines) {
            line = Patterns.removePositionStamp(line);
            if (!MeasureLine.isDrum(line))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String str = "";
        for (MeasureLine line: this.measureLines) {
            str+= line.toString()+"\n";
        }
        return str;
    }

    public String toXML() {
        return null;
    }
}
