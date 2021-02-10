package converter.converter_TryVersion;

import converter.measure.GuitarMeasure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class AcousticGuitar implements ScriptComponents{
    protected static int measureNumber;
    protected final double PAGE_WIDTH = 1100;
    protected final double PAGE_HEIGHT = 1700;
    protected ArrayList<AcousticGuitarMeasure> measures;
    protected ArrayList<String> barsPerline;

    public AcousticGuitar(){}

    //I assumed this class receives ArrayList has whole bar per line as String.
    //e.g) bars.get(0) = first whole bar line, like this. Whole one line is one element.
    //|---------|---3------2--|---2-8--------|
    //|---------|---0---------|--------------|
    //|---------|-------------|---7----------|
    //|---------|----------6--|------------0-|
    //|-------0-|-------------|-1------------|
    //|-------0-|---3---------|--10----------|

    //pre condition: each Whole bar should has the same number of measures.
    public AcousticGuitar(ArrayList<String> WholeBars){
        this.barsPerline = WholeBars;
        AcousticGuitar.measureNumber = 0;
    }

    //pre condition: eachStringsInfo array should has 6 elements because guitar has 6 strings.
    //HashMap<measure number, each strings' info(dashes, notes)
    private HashMap<Integer, ArrayList<String>> measureInfo(String[] eachStringsInfo){
        ArrayList<String[]> temp = new ArrayList<>();
        for(int i = 0; i < eachStringsInfo.length; i++){
            temp.add(eachStringsInfo[i].split("[|]"));
        }
        int totalMeasureNum = temp.get(0).length;

        HashMap<Integer, ArrayList<String>> measuresWithNum = new HashMap<>();

        int measureCount = 0;

        while(measureCount < totalMeasureNum){
            ArrayList<String> resultInfo = new ArrayList<>();
            for(int i = 0; i < eachStringsInfo.length;i++){
                resultInfo.add(temp.get(i)[measureCount]);
            }
            measureNumber++;
            measuresWithNum.put(measureNumber, resultInfo);
            measureCount++;
        }

        return measuresWithNum;
    }

    @Override
    public String partwiseInfo() {
        return null;
    }

    @Override
    public String defaultInfo() {
        return null;
    }

    @Override
    public String partlistInfo() {
        return null;
    }

    @Override
    public String partInfo() {
        return null;
    }

    @Override
    public String mergeAllscripts() {
        return null;
    }
}
