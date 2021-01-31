package parser;

import converter.Measure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to define regex patterns and
 */
public class Patterns {
    private static String WhiteSpace = "[^\\S\\n\\r]";   //whitespace excluding newline and return.
    private static String Comment = "([\\n\\r]"+WhiteSpace+"*"+"#[^\\n\\r]+)";  //newline, #, and one or more of anything that's not a newline
    private static String Instruction = "([\\n\\r]"+WhiteSpace+"*"+"#[^\\n\\r]+)";  // TODO same as comment for now. just a dummy placeholder
    private static String MeasureComponents = "(-| |[a-zA-Z0-9()])+";
    private final String MeasureLineName;

    public Patterns() {
        this.MeasureLineName = getMeasureLineName();
        this.MeasureGroupStartGen = getMeasureGroupStartGEN();
        this.MeasureGroupStartSOL = getMeasureGroupStartSOL();
        this.MeasureGroupStartMIDL = getMeasureGroupStartMIDL();
        this.MeasureGroupCollection = getMeasureGroupCollecion();
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


    //tester main method
    public static void main(String[] args) {
        String str = "";
        File file = new File("C:\\Users\\stani\\IdeaProjects\\TAB2MXL\\src\\testing files\\guitar - a thousand matches by passenger.txt");
        try (Scanner s = new Scanner(file).useDelimiter("\\n")) {
            while(s.hasNext()) {
                str += s.next();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile(new Patterns().MeasureGroupCollection);
        Matcher ptrnMatcher = pattern.matcher(str);
        while(ptrnMatcher.find()) {
            System.out.println(ptrnMatcher.group());
            System.out.println("!!____________________________________________!!");
        }
        System.out.println(new Patterns().MeasureGroupCollection);
    }

    public static String MeasureGroup;
    private String getMeasureGroup{

    }

    public final String MeasureGroupCollection;
    /**
     * This pattern identifies a collection of measure groups which lie on the same lines, next to each
     * other horizontally, as long as their corresponding instructions (like repeat) and comments (like chorus)
     * @return a String containing the regex pattern group required to identify a collection of measure groups which lie on the same line
     */
    private String getMeasureGroupCollecion() {
        //                               Measure starts at the start of line      (Measure components and "|")once or more       zero or more measure groups that start in the middle of the line     new line or end of file
        //                                              |                                       |                                                           |                                                  |
        //                                  --------------------             ----------------------------------           ----------------------------------------------------------------------      -------------
        String collection = "((^|\\n|\\r)" + MeasureGroupStartSOL     +       "("+MeasureComponents + "\\|"+")+"     +     "(" + MeasureGroupStartMIDL + "("+MeasureComponents + "\\|"+")+" + ")*"  +  "(?=\\n|\\r|$)" + ")+";

        //newline or start of file, followed by (instruction or comment)zero or more followed by measure group collection, followed by zero or more instructions, followed by a newline or end of file
        String pattern = "(" + "(?<=^|\\n|\\r)" + WhiteSpace+"*" + "("+Instruction+"|"+Comment+")*" + collection + Instruction+"*" + "($|\\n)" + ")";
        return pattern;
    }


    //----------------------Detecting the start of a measure group---------------------------
    private String MeasureGroupStartSOL;   //identifies the starting point of measure groups that start at the start of a line
    private String MeasureGroupStartMIDL;   //identifies the starting point of measure groups that start in the middle of a line, after a previous measure group ended
    private String MeasureGroupStartGen;   //identifies a point where a measure group starts on a line, looking only from when a measure line name is seen. This pattern should not be used on its own. It is only used as a means of abstracting shared parts of the getMeasureGroupStartSOL and getMeasureGroupStartMIDL patterns.

    private String getMeasureGroupStartGEN() {
        //          v                  vv
        //Example:  E----2---9|   or   E|---2- -  -4-|
        //                                                             this part is basically ensuring that what's ahead is components that make up the insides of a measure, followed by a "|" or an end of line or end of file
        //                                                                                 |
        //                                                                                 v
        //                                                                 ------------------------------------------
        String pattern = "("+ MeasureLineName + WhiteSpace+"*" + "\\|*" + "(?="+MeasureComponents+"(\\||\\r|\\n|$))" + ")";
        return pattern;
    }

    private String getMeasureGroupStartSOL() {
        // Examples
        // vvvvvv                vvvvvv
        // \nA  |---|---|   or   || A |---|---|
        String pattern = "(" + "(?<=(^|\\n|\\r))" + WhiteSpace+"*" + "\\|*" + WhiteSpace+"*" +  MeasureGroupStartGen + ")";
        return pattern;
    }

    private String getMeasureGroupStartMIDL() {
        //                 you have a "|" right before
        //                          |
        //                          v
        String pattern = "(" + "(?<=\\|)" + WhiteSpace+"*" + MeasureGroupStartGen + ")";
        return pattern;
    }
}
