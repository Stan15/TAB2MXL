package converter;

import java.util.List;

//there should be a concrete guitar measure, drum measure, and bass measure class
public abstract class Measure implements ScoreComponent{
    public static final String[] measureLineNames = {"E", "A", "D", "G", "B", "e", "a", "d", "g", "b"};
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
    public boolean isGuitar() {
        return this instanceof GuitarMeasure;
    }
    /**
     * TODO complete this. use regex to figure out if all the measure line names in this measure are measure names which are for guitars, like E, A, D, etc.
     * @param measureInfo a list of strings where each string is formatted as follows "[startIdx,endIdx]|measureLineName|---measure-stuff---|"
     *                    where startIdx and endIdx is the start and end index of this measure in the tablature string from which they originated.
     * @return
     */
    public static boolean isGuitar(List<String> measureInfo) {
        return false;
    }
    public boolean isDrum() {
        return this instanceof DrumMeasure;
    }

    /**
     * TODO complete this. use regex to figure out if all the measure line names in this measure are measure names which are for drums.
     * @param measureInfo a list of strings where each string is formatted as follows "[startIdx,endIdx]|measureLineName|---measure-stuff---|"
     * @return
     */
    public static boolean isDrum(List<String> measureInfo) {
        return false;
    }
}
