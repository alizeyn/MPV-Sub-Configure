import javafx.scene.control.Label;
import javafx.scene.text.Font;
/**
 * Created by alizeyn on 7/10/17.
 */
public class FontAwesomeLabel  extends Label {
    private Font fontAwesome;
    public FontAwesomeLabel() {
        fontAwesome =  Font.loadFont(FontAwesomeLabel.class.getResource("fontawesome-webfont.ttf").toExternalForm(), 33);
        this.setFont(fontAwesome);
    }

    public FontAwesomeLabel(double fontSize) {
        fontAwesome =  Font.loadFont(FontAwesomeLabel.class.getResource("fontawesome-webfont.ttf").toExternalForm(), fontSize);
        this.setFont(fontAwesome);
    }


}
