package parser;
import converter.MeasureGroup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.*;

public class Parser {
    private Path filePath;
    private String rootString;
    private final Patterns patterns = new Patterns();
    private List<MeasureGroup> score = new ArrayList<>();
    public static boolean checkForGaps = true;  //check for gaps of information which are not understood to mean anything by the parser and may need to be corrected.

    public static void main(String[] args) {
        Parser p = new Parser(Path.of("C:\\Users\\stani\\IdeaProjects\\TAB2MXL\\src\\testing files\\guitar - a thousand matches by passenger.txt"));
        p.parse();
    }

    public Parser(Path filePath) {
        this.filePath = filePath;
        this.rootString = "";
        try {
            rootString = Files.readString(filePath).replace("\r\n","\n");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public Parser(String rootString) {
        this.rootString = rootString;
    }

    public void parse() {
        List<MeasureGroup> measureGroups = new ArrayList<>();
        //the regex patterns are too powerful!!! it cant handle a large piece of text, so we break the text down wherever there's \n\n
        Iterator<String> feeder = this.tabStringFeeder(this.rootString);
        while (feeder.hasNext()) {
            String tabString = feeder.next();
            Pattern pattern = Pattern.compile(patterns.MeasureGroupCollection);
            Matcher ptrnMatcher = pattern.matcher(tabString);
            while(ptrnMatcher.find()) {
                try {
                    measureGroups.addAll(parseMeasureGroupCollection(ptrnMatcher.group(), ptrnMatcher.start(), ptrnMatcher.end()));
                    if (checkForGaps && !measureGroups.isEmpty()) {
                        this.correctMeasureGroupCollectionGaps();
                    }
                    for (MeasureGroup group: measureGroups) {
                        System.out.print(group);
                    }
                    score.addAll(measureGroups);
                }catch(InvalidMeasureFormatException e) {
                    e.printStackTrace();    //prompt user to correct whatever is going on there.
                }

            }
        }
    }

    private Iterator<String> tabStringFeeder(String tablatureString) {
        String[] feed = tablatureString.split("[\\n\\r][^\\S\\n\\r]*[\\n\\r]");
        for (int i=0; i<feed.length; i++) {
            feed[i] = "\n\n" + feed[i];
        }
        return Arrays.asList(feed).iterator();
    }

    /**
     * This is an idea that may be implemented later if you guys agree, but I think it should be left for later as it
     * may be a little time consuming.
     * This class should look at the content in between the different measure group collecitons and see if there is any
     * content there which the program doesn't understand but could be something meaningful. Then it should ask the user
     * if it is a measure as it was incorrectly identified. If so, it would ask the user to edit that part and re-parse it
     * real time and if accepted, we can then add it into the proper place in the measure collection list.
     */
    private void correctMeasureGroupCollectionGaps() {

    }

    /**
     * Converts a collection of measure groups (e.g one line of a measure group collection containing two measure groups: E|----|---| A|-----|---|)
     * into a list of individual measure groups
     * @param measureGroupCollection a string containing one measure group collection
     * @param startIdx the start index of the measure group collection in the string which is being parsed, this.tabStr
     * @param endIdx the end index of the measure group collection in the string which is being parsed, this.tabStr
     * @return a list of MeasureGroup objects which hold information about the measure groups extracted from the collection.
     * @throws InvalidMeasureFormatException if there are an uneven number of measure lines in the measures of a measure group collection.
     */
    private List<MeasureGroup> parseMeasureGroupCollection(String measureGroupCollection, int startIdx, int endIdx) throws InvalidMeasureFormatException {
        //this holds a list of measure groups where each measure group is represented as a list of the lines which make up the group.
        List<List<String>> measureGroupsStrList = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();
        for (String line : measureGroupCollection.split("\n")) {
            if (Pattern.matches(new Patterns().MeasureGroupCollectionLine, line)) {
                Matcher matcher = Pattern.compile(Patterns.StripString).matcher(line);
                matcher.find();
                lines.add(matcher.group());
            }
        }

        //line group-count is the number of measure groups there are on a line e.g this line  A|----|---| E|-----|----|---|
        //has a line group-count of 2 as there are two measure groups on the line.
        int prevLineGroupCount = 0;


        // as each line of a measure group collection holds multiple measure groups, i am not too sure how we can use
        // regex to collect the individual measure groups so i have done it manually.
        // This for loop goes through the lines and puts each individual line of a measure group into a list
        // where its list index is its measure group number, starting from measure group zero. When it goes to the next
        // line, it starts again from measure group number zero and adds each measure group in the next line to its
        // corresponding measure group number.
        for (String strLine:lines) {
            int currLineGroupCount = 0;
            Matcher ptrnMatcher = Pattern.compile(new Patterns().MeasureGroupLine).matcher(strLine);
            while(ptrnMatcher.find()) {
                //the number of lines in each measure group has to be the same. It is not difficult to handle when they're
                //not the same, it is just that i don't think it makes sense when they are different as shown below, so
                //i reason that it is better to catch the error earlier than wait till we are parsing that measure group.
                //I may be wrong in my reasoning, so feel free to have a discussion if you think otherwise.


                //I am catching the case where the number of measures is larger than in the previous line.
                if(prevLineGroupCount!=0 && currLineGroupCount>prevLineGroupCount) {
                    // throw error if something like this first diagram occurs.
                    //E|-----|------|------| E|--------|-------|---
                    //A|-----|------|------| A|--------|-------|---
                    //G|-----|------|------| G|--------|-------|--- A|-----|------|------|    this line <----
                    //                                                                                      |
                    //we know that this is wrong because the number of groups on the line we are currently parsing is
                    //different from the number of groups on the line we previously parsed
                    //
                    //
                    //E|-----|------|------| E|--------|-------|---
                    //E|-----|------|------| E|--------|-------|---
                    //E|-----|------|------|
                    //                       ^^^^^^^^^^^^^^^^^^^^^^
                    //(we can't assume this is incorrect yet because this missing part could be added in the next
                    // iteration of the while loop.) if this were the final result, we could say for sure that it is
                    // wrong, but since we don't know if it is the final result, we can't throw an error in this case yet.
                    //that is why the while if statement above only handles the first scenario (currLineGroupCount>prevLineGroupCount)
                    throw new InvalidMeasureFormatException();
                }

                //the information for this single line of a measure group as [startIdx,endIdx]line-information
                int groupLineStartIdx = startIdx + ptrnMatcher.start();
                int groupLineEndIdx = startIdx + ptrnMatcher.end();
                String measureGroupLine = "["+groupLineStartIdx+","+groupLineEndIdx+"]" + ptrnMatcher.group();

                if (currLineGroupCount>=measureGroupsStrList.size()) {
                    measureGroupsStrList.add(new ArrayList<>());
                }
                //get the measure group which this current measure group line belongs to and add this line to it
                List<String> currMeasureGroup = measureGroupsStrList.get(currLineGroupCount);
                currMeasureGroup.add(measureGroupLine);
                currLineGroupCount++;
            }

            //this is the other part of the if statement right above which we couldn't yet handle
            if(prevLineGroupCount!=0 && currLineGroupCount<prevLineGroupCount) {
                //now that we are out of the while loop, we know that this is the final result, so we can throw an error
                //if the number of groups detected on the line we just finished adding is less from the number of
                //groups on the previous line.
                //
                //E|-----|------|------| E|--------|-------|---
                //E|-----|------|------| E|--------|-------|---
                //E|-----|------|------|
                //there are not equal number of tab group lines in each tab group
                throw new InvalidMeasureFormatException();
            }
            prevLineGroupCount = currLineGroupCount;
        }

        List<MeasureGroup> measureGroups = new ArrayList<>();
        for(List<String> groupStrLines:measureGroupsStrList) {
            measureGroups.add(new MeasureGroup(groupStrLines, startIdx, endIdx, this.rootString));
        }
        return measureGroups;
    }

    /**
     * Calculates the line number of a character at the specified index of the string tabStr
     * @param idx the index at which we want to calculate its line number
     * @return the line number
     */
    public static int getLineNum(int idx) {
        return 0;
    }
}
