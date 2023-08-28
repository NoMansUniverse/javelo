package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Robin Bochatay(329724)
 */

public class GpxGenerator {

    private GpxGenerator(){}

    public static Document createGpx(Route route, ElevationProfile profile){
        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        double position=0;
        int i =0;
        List<Double> pos = new ArrayList<Double>();
        for(Edge e: route.edges()){
            position+=e.length();
            pos.add(position);

        }
        for(PointCh point : route.points()){
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.valueOf(Math.toDegrees(point.lat())));
            rtept.setAttribute("lon", String.valueOf(Math.toDegrees(point.lon())));

            if(i<pos.size()){
                Element ele = doc.createElement("ele");
                rtept.appendChild(ele);
                ele.setTextContent(String.valueOf(profile.elevationAt(pos.get(i))));
                i++;
            }
        }

        return doc;
    }
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

    /**
     * method allowinfg to creat a GPX file
     * @param fileName name of the file
     * @param route route to be written in file
     * @param profile profile of the route
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile){
        try{
            Document doc = createGpx(route, profile);
            Writer w = new BufferedWriter(new FileWriter(fileName));

            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch(TransformerException e){
                throw new Error(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
