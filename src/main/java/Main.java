import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

/**
 * Created by Artur on 5/14/2017.
 */
public class Main {

    public static void main(String[] agrs) throws ParserConfigurationException {
        Reporter reporter = new Reporter(
                new File("C:\\Users\\Artur\\Desktop\\Development Projects\\xmlDemo\\src\\main\\resources\\results.xml"));
        reporter.update("Suite 1", "Test 1", "PASS");
        reporter.update("Suite 1", "Test 2", "FAIL");
        reporter.update("Suite 1", "Test 3", "FAIL");
        reporter.update("Suite 2", "Test 1", "PASS");
        reporter.update("Suite 2", "Test 2", "FAIL");
    }
}
