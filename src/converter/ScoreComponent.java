package converter;

public interface ScoreComponent {
    String positionStampPtrn = "^\\[-?[0-9]+,-?[0-9]+\\]";
    String rootString = "";
    boolean validate();
    String toXML();
}
