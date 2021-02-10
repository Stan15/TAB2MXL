package converter.measure;

import converter.Note;
import converter.measure_line.GuitarMeasureLine;
import converter.measure_line.MeasureLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

public class GuitarMeasure extends Measure {
    private List<MeasureLine> measureLines;
    public int beats;
    public int beatType;

    private GuitarMeasure(List<String> lines, int startIdx, int  endIdx, String rootStr, boolean isFirstMeasure) {
        super(lines, startIdx, endIdx, rootStr, isFirstMeasure);
        this.beats = 4;
        this.beatType = 4;
        this.measureLines = this.getMeasureLines();
    }

    public static GuitarMeasure getInstance(List<String> lines, int startIdx, int  endIdx, String rootStr, boolean isFirstMeasure) {
        if (Measure.isGuitar(lines)) {
            return new GuitarMeasure(lines, startIdx, endIdx, rootStr, isFirstMeasure);
        }else {
            return null;
        }
    }

    @Override
    public boolean validate() {
        //validate that each of the measure lines are valid and belong to a guitar,
        // and that they are placed in the right order (e, B, G, D, A, E).

        //check if in order or reverse order. If in reverse order, rearrange it. If not in order at all, it is an error
        //that we prompt the user ot fix
        for (int i=0; i<this.measureLines.size(); i++) {
            if (true) {
                continue;
            }
        }
        MeasureLine prevMsurLine = null;
        for (int i=0; i<this.measureLines.size(); i++) {
            MeasureLine currMsurLine = this.measureLines.get(i);
            String lineName = currMsurLine.getName();
            if (prevMsurLine!=null && prevMsurLine.getName().toLowerCase()=="e" ) {
                if (lineName=="B")
                    prevMsurLine.setName("e");
            }
        }
        return false;
    }

    @Override
    public String toXML() {
        Measure.measureNum++;
        StringBuilder measureXML = new StringBuilder();
        measureXML.append("<measure number=\""+Measure.measureNum+"\">\n");
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
            List<Note> currentChord = new ArrayList<Note>();
            Note previousNote;
            do {
                Note note = noteQueue.poll();
                currentChord.add(note);
                previousNote = note;
            }while(!noteQueue.isEmpty() && noteQueue.peek().distanceToMeasureStart==previousNote.distanceToMeasureStart);

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
        for (MeasureLine line : this.measureLines) {
            GuitarMeasureLine guitarMline = (GuitarMeasureLine) line;
            noteQueue.addAll(guitarMline.notes);
        }
        return noteQueue;
    }
}
