import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alizeyn on 7/13/17.
 */
public class TermianlReader implements Runnable {

    private InputStream is;

    public TermianlReader(InputStream is) {
        this.is = is;

    }

    @Override
    public void run() {
        byte[] b = new byte[1024];
            try {
                while ( is.read(b) != -1) {
                    System.out.println(new String(b));
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
