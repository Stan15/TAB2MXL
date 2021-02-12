package converter.converter_TryVersion;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class AcousticGuitarMeasure extends AcousticGuitar{

    private int measureNum;
    private ArrayList<String> measureInfo;
    private ArrayList<AcousticGuitarNote> notes;
    private String[][] notesBox = new String[6][16];
    private final int DEFAULT_DIVISION = 4;
    private final int DEFAULT_BEATS = 4;
    private final int DEFAULT_BEAT_TYPE = 4;

    public AcousticGuitarMeasure(int measureNum, ArrayList<String> measureInfo){
        this.measureNum = measureNum;
        this.measureInfo = measureInfo;
        this.notes = new ArrayList<>();
        for(int i = 0; i < measureInfo.size(); i++){
            String eachStringLine = measureInfo.get(i);
            storeNotes(eachStringLine, i);
        }
    }

    public static void main(String[] args) {
        String bbb = "------23---3---";
        System.out.println(bbb.indexOf("23"));
        String[] aa = bbb.split("[-]");
        for(String a : aa){
            System.out.println(a);
        }
    }

    private void storeNotes(String eachStringLine, int stringNum){
        int totalLength = eachStringLine.length();
        String[] splitChar = eachStringLine.split("[-]");
        ArrayList<String> notations = new ArrayList<>();
        for(String notation : splitChar){
            if(!notation.equals("")){
                notations.add(notation);
            }
        }
        for(String notation : notations){
            int index = eachStringLine.indexOf(notation);
            double position = index + 1 / totalLength;
            for(int i = 0; i < 16; i++){
                if(index <= i + 1 / 16) {
                    notesBox[stringNum][i] = notation;
                    break;
                }
            }
        }
    }

    private String makeAttributes(){
        String attributes = "<attributes>\n" +
                "<divisions>" + DEFAULT_DIVISION + "</divisions>\n" +
                "<key>\n" +
                "<fifths>0</fifths>\n" +
                "</key>\n" +
                "<time>\n" +
                "<beats>" + DEFAULT_BEATS + "</beats>\n" +
                "<beat-type>" + DEFAULT_BEAT_TYPE + "</beat-type>\n" +
                "</time>\n" +
                "<clef>\n" +
                "<sign>TAB</sign>\n" +
                "<line>5</line>\n" +
                "</clef>\n" +
                "<staff-details>\n" +
                "<staff-lines>6</staff-lines>\n" +
                "<staff-tuning line=\"1\">\n" +
                "<tuning-step>E</tuning-step>\n" +
                "<tuning-octave>2</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"2\">\n" +
                "<tuning-step>A</tuning-step>\n" +
                "<tuning-octave>2</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"3\">\n" +
                "<tuning-step>D</tuning-step>\n" +
                "<tuning-octave>3</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"4\">\n" +
                "<tuning-step>G</tuning-step>\n" +
                "<tuning-octave>3</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"5\">\n" +
                "<tuning-step>B</tuning-step>\n" +
                "<tuning-octave>3</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "<staff-tuning line=\"6\">\n" +
                "<tuning-step>E</tuning-step>\n" +
                "<tuning-octave>4</tuning-octave>\n" +
                "</staff-tuning>\n" +
                "</staff-details>\n" +
                "</attributes>\n";
        return attributes;
    }

    public String makeScript(){
        String script = "<measure number=\"" + measureNum + "\">\n";
        if(measureNum == 1){
            script += makeAttributes();
        }

        if(measureNum == lastMeasureNumber){
            script += "<barline location=\"right\">\n" +
                    "<bar-style>light-heavy</bar-style>\n" +
                    "</barline>\n";
        }
        script += "</measure>\n";
        return script;
    }
}
