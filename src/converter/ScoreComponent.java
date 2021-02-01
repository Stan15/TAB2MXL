package converter;

public interface ScoreComponent {
    public static String positionStampPtrn = "\\[-?[0-9]+,-?[0-9]+\\]";
    public static String rootString = "";
    public abstract boolean validate();
}
