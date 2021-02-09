package converter.converter_TryVersion;

import converter.measure.GuitarMeasure;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AcousticGuitar {
    protected static int measureNumber;
    protected final double PAGE_WIDTH = 1100;
    protected final double PAGE_HEIGHT = 1700;
    protected ArrayList<AcousticGuitarMeasure> measures;
    protected ArrayList<String> barsPerline;

    public AcousticGuitar(){}

    //I assumed this class receives ArrayList has whole bar per line as String.
    //e.g) bars.get(0) = first line whole bar, like this. Whole one line is one element.
    //|---------|---3------2--|---2-8---|
    //|---------|---0---------|---------|
    //|---------|-------------|---7-----|
    //|---------|----------6--|-------0-|
    //|-------0-|-------------|-1-------|
    //|-------0-|---3---------|--10-----|
    public AcousticGuitar(ArrayList<String> barsPerline){
        this.barsPerline = barsPerline;
        for(String bar: barsPerline){
            AcousticGuitarMeasure o = new AcousticGuitarMeasure(bar);
            measures.add(o);
        }
    }

}
