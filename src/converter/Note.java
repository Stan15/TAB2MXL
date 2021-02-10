package converter;

import java.util.ArrayList;
import java.util.List;

public abstract class Note implements ScoreComponent, Comparable {
    public int distanceToMeasureStart;
    public static void main(String[] args) {
        Note note = new GuitarNote("e", 13, 1, 5);
        System.out.println(note.toXML(true));
    }

    private int octave;
    private String key;
    private int duration;
    public int fret;
    public Note(String lineName, int fret, int duration, int distanceToMeasureStart) {
        this.fret = fret;
        int stringNumber = this.convertNameToNumber(lineName);
        this.octave = octave(stringNumber, fret);
        this.key = Note.key(stringNumber, fret);
        this.duration = duration;
        this.distanceToMeasureStart = distanceToMeasureStart;
    }

    public int convertNameToNumber(String lineName) {
        if (lineName.equals("e")) {
            return 1;
        } else if (lineName.equals("A")) {
            return 2;
        } else if (lineName.equals("D")) {
            return 3;
        } else if (lineName.equals("G")) {
            return 4;
        } else if (lineName.equals("B")) {
            return 5;
        } else if (lineName.equals("E")) {
            return 6;
        }
        return 0;
    }

    //I made only pitch part for now.
    //reference: https://theacousticguitarist.com/all-notes-on-guitar/
    //make script
    public String pitchScript() {
        String octaveString = "<octave>" + octave + "</octave>\n";
        String stepString;
        if(!key.contains("#")) {
            stepString = "<step>" + key + "</step>\n";
        }
        else {
            stepString = "<step>" + key.charAt(0) + "</step>\n"
                    + "<alter>" + 1 + "</alter>\n";
            //In musicxml, # is expressed as <alter>1</alter>
        }

        return "<pitch>\n"
                + stepString
                + octaveString
                + "</pitch>\n";
    }


    //decide octave of note
    private static int octave(int stringNumber, int fret) {
        int octave;
        if(stringNumber == 6) {
            if(fret >= 0 && fret <= 7) {
                octave = 2;
            }
            else {
                octave = 3;
            }
        }
        else if(stringNumber == 5) {
            if(fret >= 0 && fret <= 2) {
                octave = 2;
            }
            else if(fret >= 3 && fret <= 14) {
                octave = 3;
            }
            else {
                octave = 4;
            }
        }
        else if(stringNumber == 4) {
            if(fret >=0 && fret <= 9) {
                octave = 3;
            }
            else {
                octave = 4;
            }
        }
        else if(stringNumber == 3) {
            if(fret >= 0 && fret <= 4) {
                octave = 3;
            }
            else if(fret >= 5 && fret <= 16) {
                octave = 4;
            }
            else {
                octave = 5;
            }
        }
        else if(stringNumber == 2) {
            if(fret == 0) {
                octave = 3;
            }
            else if(fret >= 1 && fret <= 12) {
                octave = 4;
            }
            else {
                octave = 5;
            }
        }
        else {
            if(fret >= 0 && fret <= 7) {
                octave = 4;
            }
            else {
                octave = 5;
            }
        }
        return octave;
    }

    //decide key of note
    public static String key(int stringNumber, int fret) {
        String[] keys = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        if(stringNumber == 6) {
            return keys[(fret + 4) % 12];
        }
        else if(stringNumber == 5) {
            return keys[(fret + 9) % 12];
        }
        else if(stringNumber == 4) {
            return keys[(fret + 2) % 12];
        }
        else if(stringNumber == 3) {
            return keys[(fret + 7) % 12];
        }
        else if(stringNumber == 2) {
            return keys[(fret + 11) % 12];
        }
        else {
            return keys[(fret + 4) % 12];
        }
    }

    @Override
    public boolean validate() {
        return false;
    }

    public String toXML(boolean startsWithPrevious) {
        StringBuilder noteXML = new StringBuilder();
        noteXML.append("<note>\n");

        if (startsWithPrevious)
            noteXML.append("<chord/>\n");
        noteXML.append(pitchScript());
        noteXML.append("<duration>");
        noteXML.append(this.duration);
        noteXML.append("</duration>\n");

        noteXML.append("<note>\n");

        return noteXML.toString();
    }

    public void setTie() {
        return;
    }

    @Override
    public int compareTo(Object o) {
        Note other = (Note) o;
        return other.distanceToMeasureStart-this.distanceToMeasureStart;
    }

    //Creates a new arraylist of Note objects from stuff like 12h3 or (6\2) or (2)(7h1)
    public static List<Note> from(String noteString, int distanceFromMeasureStart, String lineName) {
        ArrayList<Note> noteList = new ArrayList<>();
        noteList.add(new GuitarNote(lineName, Integer.valueOf(noteString), 1, distanceFromMeasureStart));
        return noteList;
    }

}
