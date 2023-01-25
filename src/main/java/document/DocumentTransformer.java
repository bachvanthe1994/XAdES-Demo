package document;

import https.vkuzel_com.xades_demo.DocumentToSign;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.transform.OutputKeys.INDENT;

@SuppressWarnings("unused")
public class DocumentTransformer {

    public static String toString(Document document) {
        return new String(toBytes(document), UTF_8);
    }

    public static String toPrettyString(Document document) {
        try {
            Transformer transformer = TransformerFactory.newDefaultInstance().newTransformer();
            transformer.setOutputProperty(INDENT, "yes");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "2");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            return outputStream.toString(UTF_8);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] toBytes(Document document) {
        try {
            Transformer transformer = TransformerFactory.newDefaultInstance().newTransformer();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
            return outputStream.toByteArray();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document toDocument(JAXBElement<?> jaxbElement) {
        try {
            DocumentBuilder documentBuilder = createDocumentBuilder();
            Document document = documentBuilder.newDocument();

            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentToSign.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(jaxbElement, document);
            return document;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(JAXBElement<?> jaxbElement) {
        return new String(toBytes(jaxbElement), UTF_8);
    }

    public static byte[] toBytes(JAXBElement<?> jaxbElement) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentToSign.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            marshaller.marshal(jaxbElement, outputStream);
            return outputStream.toByteArray();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document fromString(String content) {
        return fromBytes(content.getBytes(UTF_8));
    }

    public static Document fromBytes(byte[] content) {
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            DocumentBuilder documentBuilder = createDocumentBuilder();
            return documentBuilder.parse(inputStream);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> JAXBElement<T> fromDocument(Document document, Class<T> type) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentToSign.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(document, type);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> JAXBElement<T> fromString(String content, Class<T> type) {
        return fromBytes(content.getBytes(UTF_8), type);
    }

    public static <T> JAXBElement<T> fromBytes(byte[] content, Class<T> type) {
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            Source source = new StreamSource(inputStream);
            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentToSign.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(source, type);
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
