package converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//there should be a concrete guitar measure, drum measure, and bass measure class
public abstract class Measure implements ScoreComponent{
    protected String rootString;
    protected int startIdx;
    protected int endIdx;
    protected List<String> lines;
    
    public Measure(List<String> lines, int startIdx, int  endIdx, String rootStr) {
        this.lines = lines;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.rootString = rootStr;
    }

    /**
     * TODO complete this. use regex to figure out if all the measure line names in this measure are measure names which are for guitars, like E, A, D, etc.
     * @param measureLines a list of strings where each string is formatted as follows "[startIdx,endIdx]|measureLineName|---measure-stuff---|"
     *                    where startIdx and endIdx is the start and end index of this measure in the tablature string from which they originated.
     * @return true if the input measureLines are all guitar measure lines
     */
    public static boolean isGuitar(List<String> measureLines) {
        return true;
    }

    /**
     * TODO complete this. use regex to figure out if all the measure line names in this measure are measure names which are for drums.
     * @param measureLines a list of strings where each string is formatted as follows "[startIdx,endIdx]|measureLineName|---measure-stuff---|"
     *                     where startIdx and endIdx is the start and end index of this measure in the tablature string from which they originated.
     * @return true if the input measureLines are all guitar measure lines
     */
    public static boolean isDrum(List<String> measureLines) {
        return true;
    }

    @Override
    public String toString() {
        String str = "";
        for (String line: this.lines) {
            str+= line.split(positionStampPtrn)[1]+"\n";
        }
        return str;
    }
}
