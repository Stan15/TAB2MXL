package converter.measure_line;

import converter.Note;
import converter.ScoreComponent;
import parser.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MeasureLine implements ScoreComponent {
    public List<Note> notes = new ArrayList<>();
    private ArrayList<Object> Info;
    protected String Line;
    protected String name;
    private int startIdx;
    private int endIdx;

    public MeasureLine(String s) {
        //each line string "s" starts with a position stamp to at what index it is in the root string
        // (the tablature string we are converting). So it is in the form [startIdx,endIdx]measureLineInfo
        Integer[] position = Patterns.getPositionFromStamp(s);
        this.startIdx = position[0];
        this.endIdx = position[1];

        s = Patterns.removePositionStamp(s);
        this.name = getNameOf(s);
        this.Line = removeNameOf(s);  //removing the line name from the line
        this.getNotes();

    }

    public void getNotes() {
        int dashCounter = 0;
        String noteString = null;
        for (int i = 1; i < Line.length(); i++) {

            if (Line.charAt(i) == '-') { //accounts for each instance of dash
                if(noteString != null){
                    this.notes.addAll(Note.from(noteString, dashCounter, this.name));
                    noteString = null;
                } dashCounter++;
            } else if (Line.charAt(i) == '|') { //accounts for each instance of vertical bar
                if(noteString != null){
                    this.notes.addAll(Note.from(noteString, dashCounter, this.name));
                    noteString = null;
                }
                dashCounter = 0; //reset dash counter
            } else {
                if(noteString == null){
                    noteString = "";
                }
                noteString += Line.charAt(i);
            }
        }
        if(noteString != null) {
            this.notes.addAll(Note.from(noteString, dashCounter, this.name));
            noteString = null;
        }
    }


    //this gets all possible measure names for all measure types
    public static List<String> getAllLineNames() {
        ArrayList<String> measureNames = new ArrayList<>();
        measureNames.addAll(GuitarMeasureLine.getLineNames());
        measureNames.addAll(DrumMeasureLine.getLineNames());
        return measureNames;
    }

    //gets the measure name of this particular measure
    public static String getNameOf(String line) {
        Patterns patterns = new Patterns();
        Pattern measureLineNamePttrn = Pattern.compile("^"+patterns.WhiteSpace+"*"+patterns.MeasureLineName);
        Matcher measureLineNameMatcher = measureLineNamePttrn.matcher(line);
        measureLineNameMatcher.find();
        return measureLineNameMatcher.group().strip();
    }

    //E|----------|------------|
    public static String removeNameOf(String line) {//throws InvalidParameterValueException{
        //if (line == null){
        //    throw new InvalidParameterValueException();
        //}
        Patterns patterns = new Patterns();
        String[] temp = line.split("^"+patterns.WhiteSpace+"*"+patterns.MeasureLineName+patterns.WhiteSpace+"*"+"\\|");
        if (temp.length>1) {
            line = temp[1]; //since i specified start of file, there can only be one match
        }else {
            line = temp[0];
        }
        return line.strip();
    }

    @Override
    public String toString() {
        return this.Line + "|";
    }

    public void setName(String e) {
        this.name = e;
    }

    public static boolean isDrum(String line) {
        line = Patterns.removePositionStamp(line);
        String name = MeasureLine.getNameOf(line);
        if (DrumMeasureLine.getLineNames().contains(name))
            return true;
        return false;
    }

    public static boolean isGuitar(String line) {
        line = Patterns.removePositionStamp(line);
        String name = MeasureLine.getNameOf(line);
        if (GuitarMeasureLine.getLineNames().contains(name))
            return true;
        return false;
    }

    public String getName() {
        return this.name;
    }
}
