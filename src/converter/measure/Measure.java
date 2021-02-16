package converter.measure;

import converter.measure_line.DrumMeasureLine;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;
import converter.note.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Measure {
    public static int MEASURE_COUNT = 0;
    int beats = 4;
    int beatType = 4;
    List<String> lines;
    List<String> lineNames;
    public int lineCount;
    List<Integer> positions;
    List<MeasureLine> measureLineList;
    boolean isFirstMeasure;

    public Measure(List<String> lines, List<String> lineNames, List<Integer> linePositions, boolean isFirstMeasure) {
        this.lines = lines;
        this.lineCount = this.lines.size();
        this.lineNames = lineNames;
        this.positions = linePositions;
        this.measureLineList = this.createMeasureLineList(this.lines, this.lineNames, this.positions);
    }

    /**
     * Creates a List of MeasureLine objects from the provided string representation of a Measure.
     * These MeasureLine objects are not guaranteed to be valid. you can find out if all the Measure
     * objects in this MeasureGroup are actually valid by calling the Measure().validate() method.
     * @param lines a List of Strings where each String represents a line of the measure. It is a parallel list with lineNames and linePositions
     * @param lineNames a List of Strings where each String represents the name of a line of the measure. It is a parallel list with lines and linePositions
     * @param linePositions a List of Strings where each String represents the starting index of a line of the measure,
     *                      where a starting index of a line is the index where the line can be found in the root string,
     *                      Score.ROOT_STRING, from where it was derived. It is a parallel list with lineNames and lines
     * @return A list of MeasureLine objects. The concrete class type of these MeasureLine objects is determined
     * from the input String lists(lines and lineNames), and they are not guaranteed to all be of the same type.
     */
    private List<MeasureLine> createMeasureLineList(List<String> lines, List<String> lineNames, List<Integer> linePositions) {
        List<MeasureLine> measureLineList = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            String name = lineNames.get(i);
            int position = linePositions.get(i);
            measureLineList.add(MeasureLine.from(line, name, position));
        }
        return measureLineList;
    }

    /**
     * Creates an instance of the abstract Measure class whose concrete type is either GuitarMeasure or DrumMeasure, depending
     * on if the features of the input String Lists resemble a drum measure or a Guitar measure(this is determined by the
     * MeasureLine.isGuitar() and MeasureLine.isDrum() methods). If its features could not be deciphered or it has features
     * of both guitar and drum features, it defaults to creating a GuitarMeasure object and further error checking can
     * be done by calling GuitarMeasure().validate() on the object.
     * @param lineList A list of the insides of each measure lines that makes up this measure
     * @param lineNameList A list of the names of each the measure lines that makes up this measure
     * @param linePositionList A list of the positions of the insides of each of the measure lines that make up this
     *                         measure, where a line's position is the index at which the line is located in the root
     *                         String from which it was derived (Score.ROOT_STRING)
     *
     * @return A Measure object which is either of type GuitarMeasure if the measure was understood to be a guitar
     * measure, or of type DrumMeasure if the measure was understood to be of type DrumMeasure
     */
    public static Measure from(List<String> lineList, List<String> lineNameList, List<Integer> linePositionList, boolean isFirstMeasure) {
        boolean isGuitarMeasure = true;
        boolean isDrumMeasure = true;
        for (int i=0; i<lineList.size(); i++) {
            String line = lineList.get(i);
            String name = lineNameList.get(i);
            isGuitarMeasure &= MeasureLine.isGuitar(line, name);
            isDrumMeasure &= MeasureLine.isDrum(line, name);
        }
        if (isDrumMeasure && !isGuitarMeasure)
            return new DrumMeasure(lineList, lineNameList, linePositionList, isFirstMeasure);
        else if(isGuitarMeasure && !isDrumMeasure)
            return new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasure);
        else
            return new GuitarMeasure(lineList, lineNameList, linePositionList, isFirstMeasure); //default value if any of the above is not true (i.e when the measure type can't be understood or has components belonging to both instruments)
    }

    /**
     * Validates if all MeasureLine objects which this Measure object aggregates areinstances of the same concrete
     * MeasureLine Class (i.e they're all GuitarMeasureLine instances or all DrumMeasureLine objects). It does not
     * validate its aggregated objects. That job is left up to its concrete classes (this is an abstract class)
     * @return a HashMap<String, String> that maps the value "success" to "true" if validation is successful and "false"
     * if not. If not successful, the HashMap also contains mappings "message" -> the error message, "priority" -> the
     * priority level of the error, and "positions" -> the indices at which each line pertaining to the error can be
     * found in the root string from which it was derived (i.e Score.ROOT_STRING).
     * This value is formatted as such: "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public HashMap<String, String> validate() {
        HashMap<String, String> result = new HashMap<>();

        boolean hasGuitarMeasureLines = true;
        boolean hasDrumMeasureLines = true;
        for (MeasureLine measureLine : this.measureLineList) {
            hasGuitarMeasureLines &= measureLine instanceof GuitarMeasureLine;
            hasDrumMeasureLines &= measureLine instanceof DrumMeasureLine;
        }
        if (!(hasGuitarMeasureLines|| hasDrumMeasureLines)) {
            result.put("success", "false");
            result.put("message", "All measure lines in a measure must be of the same type (i.e. all guitar measure lines or all drum measure lines)");
            result.put("positions", this.getLinePositions());
            result.put("priority", "1");
            return result;
        }
        result.put("success", "true");
        return result;
    }

    /**
     * Creates a string representation of the index position range of each line making up this Measure instance,
     * where each index position range describes the location where the lines of this Measure can be found in the
     * root string from which it was derived (i.e Score.ROOT_STRING)
     * @return a String representing the index range of each line in this Measure, formatted as follows:
     * "[startIndex,endIndex];[startIndex,endIndex];[startInde..."
     */
    public String getLinePositions() {
        StringBuilder linePositions = new StringBuilder();
        for (int i=0; i<this.lines.size(); i++) {
            int startIdx = this.positions.get(i);
            int endIdx = startIdx+this.lines.get(i).length();
            if (!linePositions.isEmpty())
                linePositions.append(";");
            linePositions.append("["+startIdx+","+endIdx+"]");
        }
        return linePositions.toString();
    }


    public String toXML() {
        Measure.MEASURE_COUNT++;
        StringBuilder measureXML = new StringBuilder();
        measureXML.append("<measure number=\""+Measure.MEASURE_COUNT+"\">\n");
        // TODO much later on, check the notes in all the measure lines for notes with the same duration and make a chord out of them. then
        if (this.isFirstMeasure)
            this.addAttributesXML(measureXML);
        this.addNotesXML(measureXML);
        measureXML.append("</measure>\n");
        return measureXML.toString();
    }

    private StringBuilder addAttributesXML(StringBuilder measureXML) {
        measureXML.append("<attributes>\n");
        measureXML.append("<divisions>");
        measureXML.append(this.beatType/4);
        measureXML.append("</divisions>\n");

        measureXML.append("<key>\n");

        measureXML.append("<fifths>");
        measureXML.append(0);
        measureXML.append("</fifths>\n");

        measureXML.append("</key>\n");

        measureXML.append("<time>\n");

        measureXML.append("<beats>");
        measureXML.append(this.beats);
        measureXML.append("</beats>\n");

        measureXML.append("<beat-type>");
        measureXML.append(this.beatType);
        measureXML.append("</beat-type>\n");

        measureXML.append("</time>\n");

        measureXML.append("<clef>\n");

        measureXML.append("<sign>");
        measureXML.append("G");
        measureXML.append("</sign>\n");

        measureXML.append("<line>");
        measureXML.append(2);
        measureXML.append("</line>\n");

        measureXML.append("</clef>\n");

        measureXML.append("</attributes>\n");
        return measureXML;
    }

    private StringBuilder addNotesXML(StringBuilder measureXML) {
        //remove all the other notes that make up the chord and place the chord in the appropriate location
        PriorityQueue<Note> noteQueue = this.getNoteQueue();
        while(!noteQueue.isEmpty()) {

            //notes of the same distance from the start of their measure are a chord, and are collected in the below array
            List<Note> currentChord = new ArrayList<>();
            Note previousNote;
            do {
                Note note = noteQueue.poll();
                currentChord.add(note);
                previousNote = note;
            }while(!noteQueue.isEmpty() && noteQueue.peek().distance==previousNote.distance);

            //adding all chord notes to the measureXML
            for(int i=0; i<currentChord.size(); i++) {
                Note note = currentChord.get(i);
                boolean startWithPrevious = false;
                if (i>1)
                    startWithPrevious = true;
                measureXML.append(note.toXML(startWithPrevious));
            }
        }
        return measureXML;
    }

    private PriorityQueue<Note> getNoteQueue() {
        PriorityQueue<Note> noteQueue = new PriorityQueue<>();
        for (MeasureLine line : this.measureLineList) {
            GuitarMeasureLine guitarMline = (GuitarMeasureLine) line;
            noteQueue.addAll(guitarMline.noteList);
        }
        return noteQueue;
    }
}
