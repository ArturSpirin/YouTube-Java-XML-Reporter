import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Artur on 5/14/2017.
 */
public class Reporter {

    private File file;
    private DocumentBuilder builder;

    public Reporter(File file) throws ParserConfigurationException {
        this.file = file;
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public void update(String suiteName, String testName, String testResult){

        try{

            Document doc;
            if(!file.exists()){
                Files.createDirectories(file.toPath().getParent());
                Files.createFile(file.toPath());
                doc = create_new_xml(suiteName, testName, testResult);
            }else doc = update_existing_xml(suiteName, testName, testResult);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        }catch (TransformerException | IOException | SAXException e){
            e.printStackTrace();
        }
    }

    private Document create_new_xml(String suiteName, String testName, String testResult){
        Document doc = builder.newDocument();

        Element testsuites = doc.createElement("testsuites");

        Element testsuite = doc.createElement("testsuite");
        testsuite.setAttribute("name", suiteName);
        testsuite.setAttribute("tests", "1");
        testsuite.setAttribute("failures", testResult.equals("FAIL") ? "1": "0");
        testsuite.setAttribute("pass", testResult.equals("PASS") ? "1": "0");

        Element testcase = doc.createElement("testcase");
        testcase.setAttribute("name", testName);
        testcase.setAttribute("status", testResult);

        if(testResult.equals("FAIL")){
            Element failure = doc.createElement("failure");
            testcase.appendChild(failure);
        }

        doc.appendChild(testsuites);
        testsuites.appendChild(testsuite);
        testsuite.appendChild(testcase);
        return doc;
    }

    private Document update_existing_xml(String suiteName, String testName, String testResult) throws IOException, SAXException {

        Document doc = builder.parse(file);
        Node testsuites = doc.getFirstChild();

        NodeList suites = doc.getElementsByTagName("testsuite");
        for(int index = 0; index < suites.getLength(); index++){
            Node suite = suites.item(index);
            String existitng_suite = suite.getAttributes().getNamedItem("name").getNodeValue();
            if(existitng_suite.equals(suiteName)){
                System.out.println("Suite already exists. Making changes to it...");
                Element testcase = doc.createElement("testcase");
                testcase.setAttribute("name", testName);
                testcase.setAttribute("status", testResult);

                Node test = suite.getAttributes().getNamedItem("tests");
                Node pass = suite.getAttributes().getNamedItem("pass");
                Node failures = suite.getAttributes().getNamedItem("failures");
                int t = Integer.valueOf(test.getNodeValue()) + 1;
                int p = Integer.valueOf(pass.getNodeValue());
                int f = Integer.valueOf(failures.getNodeValue());

                if (testResult.equals("FAIL")){
                    f++;
                    Element failure = doc.createElement("failure");
                    testcase.appendChild(failure);
                }else p++;

                test.setNodeValue(String.valueOf(t));
                failures.setNodeValue(String.valueOf(f));
                pass.setNodeValue(String.valueOf(p));
                suite.appendChild(testcase);
                return doc;
            }
        }

        Element testsuite = doc.createElement("testsuite");
        testsuite.setAttribute("name", suiteName);
        testsuite.setAttribute("tests", "1");
        testsuite.setAttribute("failures", testResult.equals("FAIL") ? "1": "0");
        testsuite.setAttribute("pass", testResult.equals("PASS") ? "1": "0");

        Element testcase = doc.createElement("testcase");
        testcase.setAttribute("name", testName);
        testcase.setAttribute("status", testResult);

        if(testResult.equals("FAIL")){
            Element failure = doc.createElement("failure");
            testcase.appendChild(failure);
        }
        testsuites.appendChild(testsuite);
        testsuite.appendChild(testcase);
        return doc;
    }
}
