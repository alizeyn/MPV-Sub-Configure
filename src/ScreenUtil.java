import java.awt.Toolkit;
import javafx.stage.Screen;

public class ScreenUtil {

    public static double getWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

    public static double getAppWidth() {
        return getWidth() / 4.4;
    }

    public static double getAppHeight() {
        return getHeight() / 2;
    }

}
