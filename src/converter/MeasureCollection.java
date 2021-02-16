package converter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureCollection {
    String origin;  //the string that was passed to the constructor upon the instantiation of this class
    int position;   //the index in Score.rootString at which the String "MeasureCollection().origin" is located
    int endIndex;
    Map<String, List<String>> components;
    List<MeasureGroup> measureGroupList;
    public static String PATTERN = createMeasureCollectionPattern();
    public static String LINE_PATTERN = createLinePattern();

    /**
     * Static factory method which verifies that the input String "origin" can be recognised as a measure collection
     * before instantiation.
     * @param origin The String representation of a measure collection (a collection of measure groups that happen to be
     *              on the same line. e.g P|----|---|  Hh|----|----| are drum measures which are on the same line)
     * @param position the start position of the input String "origin" in Score.ROOT_STRING, from where it was extracted
     * @return a MeasureCollection object if the input String "origin" is properly recognised to be a representation of
     * a measure group(regardless of if the measure group it is representing is valid or not). null otherwise
     */
    public static MeasureCollection getInstance(String origin, int position) {
        Matcher matcher = Pattern.compile(MeasureCollection.PATTERN).matcher(origin);
        if (matcher.find())
            return new MeasureCollection(matcher.group(), position+matcher.start());
        else
            return null;
    }

    private MeasureCollection(String origin, int position) {
        this.origin = origin;
        this.position = position;
        this.endIndex = position+this.origin.length();
        this.components = this.separateComponents(origin);
        this.measureGroupList = this.createMeasureGroupList(this.components.get("measure-group-collection").get(0));    //there is only one measure group collection per measure collection, so it is the only item in the list of measure group collections. because the type that other components map to is a List<String>, I had to make this a list too.
    }

    /**
     * Creates a List of MeasureGroup objects from the provided string representation of a measure group collection.
     * These MeasureGroup objects are not guaranteed to be valid. you can find out if all the MeasureGroup
     * objects in this MeasureCollection are actually valid by calling the MeasureCollection().validate() method.
     * @param measureGroupCollectionString The String representation of the MeasureGroup objects to be created, beginning with
     *                             a String tag indicating the index at which the provided String begins in Score.rootString
     *                             (i.e "[startIdx]stringContent")
     * @return A List of MeasureGroup objects.
     */
    private List<MeasureGroup> createMeasureGroupList(String measureGroupCollectionString) {
        List<MeasureGroup> measureGroupList = new ArrayList<>();

        // separating the start index from the input String
        Matcher tagMatcher = Pattern.compile("^\\[[0-9]+\\]").matcher(measureGroupCollectionString);
        tagMatcher.find();
        int startIdx = Integer.parseInt(tagMatcher.group().replaceAll("[\\[\\]]",""));
        measureGroupCollectionString = measureGroupCollectionString.substring(tagMatcher.end());

        // I can split measureGroupCollectionString by newlines and then go through each line and create the measure groups,
        // but splitting by newlines will make us lose the information of what index each line is positioned at. We may be able
        // to figure the index out, but there will always be an uncertainty of +-1

        List<List<String>> measureGroupStringList = new ArrayList<>();

        //matches every line containing something
        Matcher lineContentMatcher = Pattern.compile("(?<=^|\\n)[^\\n]+(?=$|\\n)").matcher(measureGroupCollectionString);
        while (lineContentMatcher.find()) { // go through each line
            String line = lineContentMatcher.group();
            int lineStartIdx = startIdx+lineContentMatcher.start();

            if (!line.matches(MeasureCollection.LINE_PATTERN)) continue;

            Matcher measureGroupLineMatcher = Pattern.compile(MeasureGroup.LINE_PATTERN).matcher(line);

            int measureGroupCount = 0;   //the number of measure groups on this line
            while (measureGroupLineMatcher.find()) {
                measureGroupCount++;
                int measureGroupLineStartIdx = lineStartIdx + measureGroupLineMatcher.start();
                String measureGroupLine = "["+measureGroupLineStartIdx+"]"+measureGroupLineMatcher.group();
                if (measureGroupStringList.size()<measureGroupCount)
                    measureGroupStringList.add(new ArrayList<>());

                List<String> measureGroupLines = measureGroupStringList.get(measureGroupCount-1);    //-1 cuz of zero indexing.
                measureGroupLines.add(measureGroupLine);
            }
        }
        for (List<String> measureGroupString : measureGroupStringList) {
            measureGroupList.add(new MeasureGroup(measureGroupString));
        }
        return measureGroupList;
    }

    /**
     * Separates the provided String representation of a MeasureCollection into Instructions, a measure group collection,
     * and Comments.
     * @param origin the String representation of a MeasureCollection from which the instructions, comments, and the
     *              measure group collection are extracted
     * @return a Map with the following mappings: measure-group-colleciton -> a list<String> containing a single
     * measure group collection; "instructions" -> a List<String> containing the instructions, and "comments" ->
     * containing a List<String> of the comments in the origin String. Each String stored in this Map begins with a tag
     * (i.e "[startIdx]stringContent" ) specifying the start index of the String in Score.rootString
     */
    private Map<String, List<String>> separateComponents(String origin) {
        Map<String, List<String>> componentsMap = new HashMap<>();
        List<String> measureGroupCollctn = new ArrayList<>();
        List<String> instructionList = new ArrayList<>();
        List<String> commentList = new ArrayList<>();
        componentsMap.put("measure-group-collection", measureGroupCollctn);
        componentsMap.put("instructions", instructionList);
        componentsMap.put("comments", commentList);

        HashSet<Integer> identifiedComponents = new HashSet<>();    //to prevent the same thing being identified as two different components

        //extract the measure group collection
        Matcher matcher = Pattern.compile("((^|\\n)"+MeasureCollection.LINE_PATTERN+")+").matcher(origin);
        matcher.find(); // we don't use while loop because we are guaranteed that there is going to be just one of this pattern in this.origin. Look at the static factory method and the createMeasureCollectionPattern method
        measureGroupCollctn.add("["+this.position+matcher.start()+"]"+matcher.group());
        identifiedComponents.add(matcher.start());

        //extract the instruction
        matcher = Pattern.compile("((^|\\n)"+Instruction.LINE_PATTERN+")+").matcher(origin);
        while(matcher.find()) {
            if (identifiedComponents.contains(matcher.start())) continue;
            instructionList.add("["+this.position+matcher.start()+"]"+matcher.group());
            identifiedComponents.add(matcher.start());
        }

        //extract the comments
        matcher = Pattern.compile("((^|\\n)"+Patterns.COMMENT+")+").matcher(origin);
        while(matcher.find()) {
            if (identifiedComponents.contains(matcher.start())) continue;
            commentList.add("["+this.position+matcher.start()+"]"+matcher.group());
            identifiedComponents.add(matcher.start());
        }
        return componentsMap;
    }

    /**
     * Validates the aggregated MeasureGroup objects of this class. It stops evaluation at the first aggregated object
     * which fails validation.
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public HashMap<String, String> validate() {
        HashMap<String, String> result = new HashMap<>();

        //--------------Validating your aggregates-------------------
        for (MeasureGroup mGroup : this.measureGroupList) {
            HashMap<String,String> response = mGroup.validate();
            if (response.get("success").equals("false"))
                return response;
        }

        result.put("success", "true");
        return result;
    }

    private static String createLinePattern() {
        return "("+Patterns.WHITESPACE+"*("+MeasureGroup.LINE_PATTERN+Patterns.WHITESPACE+"*)+)";
    }

    /**
     * Creates the regex pattern for detecting a measure collection (i.e a collection of measure groups and their corresponding instructions  and comments)
     * @return a String regex pattern enclosed in brackets that identifies a measure collection pattern (the pattern also captures the newline right before the measure group collection)
     */
    private static String createMeasureCollectionPattern() {
        // zero or more instructions, followed by one or more measure group lines, followed by zero or more instructions
        return "((^|\\n)"+Instruction.LINE_PATTERN+"+|"+Patterns.COMMENT+")*"       // 0 or more lines separated by newlines, each containing a group of instructions or comments
                + "("                                                                   // then the measure collection line, which is made of...
                +       "(^|\\n)"                                                           // a start of line or a new line
                +       MeasureCollection.createLinePattern()                               // a measure group line followed by whitespace, all repeated one or more times
                + ")+"                                                                  // the measure collection line I just described is repeated one or more times.
                + "(\\n"+Instruction.LINE_PATTERN+"+)*";
    }
}
// TODO limit the number of consecutive whitespaces there are inside a measure
