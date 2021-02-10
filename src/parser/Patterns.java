package parser;


import converter.ScoreComponent;
import converter.measure_line.MeasureLine;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//enfunufnsuf
/**
 * The purpose of this class is to define the regex patterns used to identify measure groups, measures, instructions notes, etc.
 */
public class Patterns {
    public static final String WhiteSpace = "[^\\S\\n\\r]";   //whitespace excluding newline and return.
    private static final String Comment = "([\\n\\r]"+WhiteSpace+"*"+"#[^\\n\\r]+)";  //newline, #, and one or more of anything that's not a newline
    private static final String Instruction = "([\\n\\r]"+WhiteSpace+"*"+"#[^\\n\\r]+)";  // TODO same as comment for now. just a dummy placeholder
    public static final String StripString = "(?<=^\\s*).*(?=\\s*$)";


    public final String MeasureLineName;
    //one line of one measure group (measure group, not collection of measure groups. i.e      e|---|----|      not         e|---|---| e|----|----|)
    public final String MeasureGroupLine;
    private final String MeasureComponents = "(\\(?[.a-zA-Z0-9~\\/\\\\]+\\)?)+";   // TODO the regex pattern for the individual components like "6h3r8" or "(10)" that are in a measure. should also include multiple measure components next to each other, enclosed in brackets (e.g (10)(7h2))
    //              The measure could be empty(made up of only at least one "-" and spaces)       or first optional "-"or whitespace, then a measure compomnent, where the measure component either has to end or has to start with a "-"                           and then you can optionally have only one measure component(the last component in the measure) that doesnt end with a "-"
    //                                                      |                                                       |                                                                                                                                                                             |
    public final String MeasureInsides = "(" + "("+WhiteSpace+"*-[ -]*(?=\\|))" +       "|("     +    "[ -]*((" + MeasureComponents + WhiteSpace+"*" + "-[ -]*)|(" +WhiteSpace+"*" +"-[ -]*"+ MeasureComponents + ")"+")+"                 +               "("+MeasureComponents+")?" + WhiteSpace+"*)" + ")";
    public final String MeasureGroupCollection;
    public final String MeasureGroupCollectionLine;
    //----------------------Detecting the start of a measure group---------------------------
    public final String MeasureGroupStartSOL;   //identifies the starting point of measure groups that start at the start of a line
    public final String MeasureGroupStartMIDL;   //identifies the starting point of measure groups that start in the middle of a line, after a previous measure group ended
    public final String MeasureGroupStartGen;   //identifies a point where a measure group starts on a line, looking only from when a measure line name is seen. This pattern should not be used on its own. It is only used as a means of abstracting shared parts of the getMeasureGroupStartSOL and getMeasureGroupStartMIDL patterns.

    public Patterns() {
        this.MeasureLineName = getMeasureLineName();
        this.MeasureGroupStartGen = getMeasureGroupStartGEN();
        this.MeasureGroupStartSOL = getMeasureGroupStartSOL();
        this.MeasureGroupStartMIDL = getMeasureGroupStartMIDL();
        this.MeasureGroupCollectionLine = getMeasureGroupCollectionLine();
        this.MeasureGroupCollection = getMeasureGroupCollecion();
        this.MeasureGroupLine = getMeasureGroupLine();
    }

    public static String removePositionStamp(String line) {
        String[] stuff = line.split(ScoreComponent.positionStampPtrn);
        for (String s : stuff) {
            if (!s.equals(""))
                return s;
        }
        return "";
    }
    public static Integer[] getPositionFromStamp(String s) {
        Pattern pattern = Pattern.compile(ScoreComponent.positionStampPtrn);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        String positionStamp = matcher.group();

        pattern = Pattern.compile("(?<=\\[)-?[0-9]+(?=,)");
        matcher = pattern.matcher(positionStamp);
        matcher.find();
        int startIdx = Integer.valueOf(matcher.group());

        pattern = Pattern.compile("(?<=,)-?[0-9]+(?=\\])");
        matcher = pattern.matcher(positionStamp);
        matcher.find();
        int endIdx = Integer.valueOf(matcher.group());

        Integer[] position = {startIdx, endIdx};
        return position;
    }

    private String getMeasureLineName() {
        List<String> measureLineNames = MeasureLine.getAllLineNames();

        String pattern = "(?<=^\\|?)"+WhiteSpace+"*"+"("+measureLineNames.get(0);
        for (int i=1; i<measureLineNames.size(); i++) {
            pattern+= "|"+measureLineNames.get(i);
        }
        pattern+=")";

        return pattern;
    }

    private String getMeasureGroupLine(){
        String pattern = "(" + "("+MeasureGroupStartSOL+"|"+MeasureGroupStartMIDL+")" + "("+ MeasureInsides + "\\|)+" + ")";
        return pattern;
    }

    private String getMeasureGroupCollectionLine() {
        //                 Measure starts at the start of line      (Measure insides and "|")once or more       zero or more measure groups that start in the middle of the line                    new line or end of file
        //                                |                                       |                                                           |                                                                 |
        //                    --------------------             ----------------------------------           ----------------------------------------------------------------------                        -------------

        String pattern =   "("+MeasureGroupStartSOL        +       "("+ MeasureInsides + "\\|)+"        +     "(" + MeasureGroupStartMIDL + "("+ MeasureInsides + "\\|"+")+" + ")*"  + WhiteSpace+"*" +    "(\\n|\\r|$)" + ")";
        return pattern;
    }
    /**
     * This pattern identifies a collection of measure groups which lie on the same lines, next to each
     * other horizontally, as well as their corresponding instructions (like repeat) and comments (like chorus)
     * @return a String containing the regex pattern group required to identify a collection of measure groups which lie on the same line
     */
    private String getMeasureGroupCollecion() {
        String collection = "(^|[\\n\\r])" + MeasureGroupCollectionLine+"+";

        //              first, something that's NOT a measure group line,       followed by zero or more (instruction or comment)       followed by measure group collection,     optionally followed by (zero or more instructions followed by a newline or end of file)
        //                                      |                                                           |                                          |                                                    |                                             |
        //                     --------------------------------------                      ------------------------------                       -----------                                 --------------------------------
        String pattern = "(" +"(?<!"+MeasureGroupStartSOL+"[^\\n\\r])" + WhiteSpace+"*" + "("+Instruction+"|"+Comment+")*"           +           collection                 +          "("+  Instruction+"*" + "(?=$|\\n)" + ")?)";
        return collection;
    }

    private String getMeasureGroupStartGEN() {
        //          v                  vv
        //Example:  E----2---9|   or   E|---2- -  -4-|
        //                                                             this part is basically ensuring that what's ahead is the insides of a measure, followed by a "|" or an end of line or end of file
        //                                                                                 |
        //                                                                                 v
        //                                                                 ------------------------------------------
        String pattern = "(" + MeasureLineName + WhiteSpace+"*" + "\\|*" + "(?="+ MeasureInsides +"[|\\r\\n]|$)" + ")";
        return pattern;
    }

    private String getMeasureGroupStartSOL() {
        // Examples
        // vvvvvv                vvvvvv
        // \nA  |---|---|   or   || A |---|---|
        String pattern = "(" + "(?<=^|[\\n\\r])" + WhiteSpace+"*" + "\\|*" + WhiteSpace+"*" +  MeasureGroupStartGen + ")";
        return pattern;
    }

    private String getMeasureGroupStartMIDL() {
        //Example
        //    vvv                   v
        //---| A|---|--|     or ---|A---|---|
        //                 you have a "|" right before
        //                          |
        //                          v
        String pattern = "(" + "(?<=\\|)" + WhiteSpace+"*" + MeasureGroupStartGen + ")";
        return pattern;
    }
}
