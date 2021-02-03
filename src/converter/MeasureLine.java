package converter;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine implements ScoreComponent {

    private static ArrayList<Object> Info;
    private static String Line;

    public MeasureLine(String s) {
        //i think you will need to extract the measure line name using the patterns Patterns().
        this.Line = s;
    }

    public static boolean isDrum(String measureLine) {
        return false;
    }

    public static List<String> getLineNames() {
        ArrayList<String> measureNames = new ArrayList<>();
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
                measureNames.add("" + Line.charAt(i)); //extracting info of note played at the instance played
                String t = ""; //temp object
                for (int j = 0; j < measureNames.size(); j++) {
                    t = t + measureNames.get(j); //
                }
                t = vertBarCounter + ", " + t + ", " + dashCounter; //organizes info of note played at the instance played on the bar
                Info.add(t); //storing into arraylist
                dashCounter = 0; //reset dash counter

                measureNames.addAll(GuitarMeasureLine.getLineNames());
                measureNames.addAll(DrumMeasureLine.getLineNames());
                return measureNames;
            }
        }
        return getLineNames();
    }
}
