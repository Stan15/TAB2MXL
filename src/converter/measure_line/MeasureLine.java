package converter.measure_line;

import converter.ScoreComponent;
import parser.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine implements ScoreComponent {

    private ArrayList<Object> Info;
    private String Line;
    private String name;
    private int startIdx;
    private int endIdx;

    public MeasureLine(String s) {
        //each line string "s" starts with a position stamp to at what index it is in the root string
        // (the tablature string we are converting). So it is in the form [startIdx,endIdx]measureLineInfo
        Integer[] position = Patterns.getPositionFromStamp(s);
        this.startIdx = position[0];
        this.endIdx = position[1];

        s = Patterns.removePositionStamp(s);
        this.Line = s;
        this.name = getName(s);

    }

    public static boolean isDrum(String measureLine) {
        return false;
    }

    public static boolean isGuitar(String line) {
        return true;
    }

    public ArrayList<String> calculateNoteDistance () {
        ArrayList<String> temp = new ArrayList<>();
        Info = new ArrayList<>();
        int vertBarCounter = 0;
        int dashCounter = 0;

        for (int i = 1; i < Line.length(); i++) {

            if (Line.charAt(i) == '-') { //accounts for each instance of dash
                dashCounter++;
            } else if (Line.charAt(i) == '|') { //accounts for each instance of vertical bar
                vertBarCounter++;
                dashCounter = 0; //reset dash counter
            } else {
                temp.add("" + Line.charAt(i)); //extracting info of note played at the instance played
                String t = ""; //temp object
                for (int j = 0; j < temp.size(); j++) {
                    t = t + temp.get(j); //
                }
                t = vertBarCounter + ", " + t + ", " + dashCounter; //organizes info of note played at the instance played on the bar
                Info.add(t); //storing into arraylist
                dashCounter = 0; //reset dash counter

                temp.addAll(GuitarMeasureLine.getLineNames());
                temp.addAll(DrumMeasureLine.getLineNames());
                return temp;
            }
        }
        return temp;
    }

    //this gets all possible measure names for all measure types
    public static List<String> getLineNames() {
        ArrayList<String> measureNames = new ArrayList<>();
        measureNames.addAll(GuitarMeasureLine.getLineNames());
        measureNames.addAll(DrumMeasureLine.getLineNames());
        return measureNames;
    }

    //gets the measure name of this particular measure
    public static String getName(String line) {
        Patterns patterns = new Patterns();
        Pattern measureLineNamePttrn = Pattern.compile("^"+patterns.WhiteSpace+"*"+patterns.MeasureLineName);
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(line);
        measureLineNameMatcher.find();
        return measureLineNameMatcher.group().strip();
    }

    @Override
    public String toString() {
        return this.Line + "|";
    }
}
