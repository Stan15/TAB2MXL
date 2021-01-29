package parser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class Parser {
    public static final String instructionPattern = "[^\\s\\S]";   //the regex pattern that finds instructions
    public static final String commentPattern = "(\\n#[^\\n]+)";   //the pattern group that finds comments


    //the regex pattern that finds measure groups.
    //                                                          start of string or new line  Any whitespace that's not a new line(0 or more)  1-3 alphabets and optional whitespace      one or more "|" and any length of characters(excluding newlines).       match one or more of everything I just mentioned.
    //                                                                      |                            |                                            |                                                     |                                                              |
    public static final String measureGroupPattern = "(" +              "(^|[\\n])"      +         "[^\\S\\r\\n]*"              +          "[a-zA-Z]{1,3}[^\\S\\r\\n]*"           +               "\\|+[^\\r\\n]*"                         +                             ")+";

    public static void parseTabFile(File file) {

    }

    /**
     * gets the individual measure groups in a tablature file including all instructions and comments associated with them.
     * @param tabStr the string representation of the whole tablature file
     * @return a List of strings which are as such: "[startIndex, endIndex]-measureGroup"
     */
    public static List<String> getMeasureGroups(String tabStr) {
        //                      start of string or new line    zero or more comments or instructions          a measure group     zero or more instructions
        //                                     |                             |                                       |                       |
        //                                     |  -----------------------------------------------------              |           -------------------------
        //                                     |  |                                                   |              |           |                       |
        //                                     V  V                                                   V              V           V                       V

        Pattern pattern = Pattern.compile("(^|\\n)(" + commentPattern + "*|" + instructionPattern + "*)" + measureGroupPattern + instructionPattern + "*");
        Matcher ptrnMatcher = pattern.matcher(tabStr);

        //tmpMeasureGroups is used to initially store all the lines with measure groups,
        // but there might be multiple measure groups occupying the same lines.
        //e.g two measure groups are occupying the same line:       E|----5----3|---4h3----2|    A|-----6--2-|-3-----2|
        //we will separate them and put the individual measure groups into the measureGroups variable.
        ArrayList<String> tmpMeasureGroups = new ArrayList<>();
        while(ptrnMatcher.find()) {
            if(ptrnMatcher.group().length() != 0) {
                tmpMeasureGroups.add(ptrnMatcher.group().trim());
            }
        }

        ArrayList<String> measureGroups = new ArrayList<>();
        for (String tmp : tmpMeasureGroups) {

        }

        return measureGroups;
    }
}
