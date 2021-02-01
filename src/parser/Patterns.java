package parser;

import converter.Measure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to define the regex patterns used to identify measure groups, measures, instructions notes, etc.
 */
public class Patterns {
    private static final String WhiteSpace = "[^\\S\\n\\r]";   //whitespace excluding newline and return.
    private static final String Comment = "([\\n\\r]"+WhiteSpace+"*"+"#[^\\n\\r]+)";  //newline, #, and one or more of anything that's not a newline
    private static final String Instruction = "([\\n\\r]"+WhiteSpace+"*"+"#[^\\n\\r]+)";  // TODO same as comment for now. just a dummy placeholder

    public Patterns() {
        this.MeasureLineName = getMeasureLineName();
        this.MeasureGroupStartGen = getMeasureGroupStartGEN();
        this.MeasureGroupStartSOL = getMeasureGroupStartSOL();
        this.MeasureGroupStartMIDL = getMeasureGroupStartMIDL();
        this.MeasureGroupCollection = getMeasureGroupCollecion();
        this.MeasureGroupLine = getMeasureGroupLine();
    }

    private String getMeasureLineName() {
        String[] measureLineNames = Measure.measureLineNames;
        String pattern = "("+measureLineNames[0];
        for (int i=1; i<measureLineNames.length; i++) {
            pattern+= "|"+measureLineNames[i];
        }
        pattern+=")";
        return pattern;
    }

    public final String MeasureLineName;
    //one line of one measure group (measure group, not collection oof measure groups. i.e      e|---|----|      not         e|---|---| e|----|----|)
    public final String MeasureGroupLine;
    private String getMeasureGroupLine(){
        String pattern = "(" + "("+MeasureGroupStartSOL+"|"+MeasureGroupStartMIDL+")" + "("+ MeasureInsides + "\\|)+" + ")";
        return pattern;
    }

    private static final String MeasureComponents = "(\\(?[a-zB-Z0-9]+\\)?)+";   // TODO the regex pattern for the individual components like "6h3r8" or "(10)" that are in a measure. should also include multiple measure components next to each other, enclosed in brackets (e.g (10)(7h2))
    //              The measure could be empty(made up of only at least one "-" and spaces)       or first optional "-"or whitespace, then a measure compomnent, where the measure component has to end with a "-"       and then you can optionally have only one measure component(the last component in the measure) that doesnt end with a "-"
    //                                                      |                                                       |                                                                                                            |
    public static final String MeasureInsides = "(" + "("+WhiteSpace+"*-[ -]*(?=\\|))" +       "|("     +    "[ -]*(" + MeasureComponents + WhiteSpace+"*" + "-[ -]*)+"                 +               "("+MeasureComponents+"){0,1}" + WhiteSpace+"*)" + ")";
    //private static String MeasureComponents = "(" + "(-*(?=\\|))" +       "|"  +    "(-*("+ WhiteSpace+"*" + MeasureComponent+"+" + WhiteSpace+"*" + "-+" +")+"                 +                   WhiteSpace+"*" + MeasureComponent+"{0,1}" + WhiteSpace+"*)" + ")";

    public final String MeasureGroupCollection;
    /**
     * This pattern identifies a collection of measure groups which lie on the same lines, next to each
     * other horizontally, as long as their corresponding instructions (like repeat) and comments (like chorus)
     * @return a String containing the regex pattern group required to identify a collection of measure groups which lie on the same line
     */
    private String getMeasureGroupCollecion() {
        //                               Measure starts at the start of line      (Measure insides and "|")once or more       zero or more measure groups that start in the middle of the line     new line or end of file
        //                                              |                                       |                                                           |                                                  |
        //                                  --------------------             ----------------------------------           ----------------------------------------------------------------------      -------------
        String collection = "(^|[\\n\\r])" + "("+MeasureGroupStartSOL     +       "("+ MeasureInsides + "\\|)+"     +     "(" + MeasureGroupStartMIDL + "("+ MeasureInsides + "\\|"+")+" + ")*"  +  "(\\n|\\r|$)" + ")+";

        //something that's not a measure group line, followed by (instruction or comment)zero or more followed by measure group collection, followed by zero or more instructions, followed by a newline or end of file
        String pattern = "(" +"(?<!"+MeasureGroupStartSOL+"[^\\n\\r])" + WhiteSpace+"*" + "("+Instruction+"|"+Comment+")*" + collection + Instruction+"*" + "($|\\n)" + ")";
        return collection;
    }

    public static void main(String[] args){
        String filePath = "C:\\Users\\stani\\IdeaProjects\\TAB2MXL\\src\\testing files\\guitar - a thousand matches by passenger.txt";
        Path path = Path.of(filePath);

        try {
            String rootStr = Files.readString(path);
            Pattern pattern = Pattern.compile(new Patterns().MeasureGroupCollection);
            Matcher ptrnMatcher = pattern.matcher(rootStr);
            while (ptrnMatcher.find()) {
                System.out.println(ptrnMatcher.group());
                System.out.println("###########################");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    //----------------------Detecting the start of a measure group---------------------------
    public final String MeasureGroupStartSOL;   //identifies the starting point of measure groups that start at the start of a line
    public final String MeasureGroupStartMIDL;   //identifies the starting point of measure groups that start in the middle of a line, after a previous measure group ended
    private final String MeasureGroupStartGen;   //identifies a point where a measure group starts on a line, looking only from when a measure line name is seen. This pattern should not be used on its own. It is only used as a means of abstracting shared parts of the getMeasureGroupStartSOL and getMeasureGroupStartMIDL patterns.

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
