package com.utils;
import java.beans.XMLDecoder;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringUtils;
//import org.jdom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
//    public static String getChildrenText(List children) {
//        StringBuffer sb = new StringBuffer();
//        if(!children.isEmpty()) {
//            Iterator it = children.iterator();
//            while(it.hasNext()) {
//                Element e = (Element) it.next();
//                String name = e.getName();
//                String value = e.getTextNormalize();
//                List list = e.getChildren();
//                sb.append("<" + name + ">");
//                if(!list.isEmpty()) {
//                    sb.append(getChildrenText(list));
//                }
//                sb.append(value);
//                sb.append("</" + name + ">");
//            }
//        }
//
//        return sb.toString();
//    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        org.w3c.dom.Document document = newDocumentBuilder().newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }


    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = newDocumentBuilder().parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            throw ex;
        }

    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);

        return documentBuilderFactory.newDocumentBuilder();
    }

    public static String getXmlStringFileBetenn(String xmlFilePath,String open,String close){
        String str= null;
        try {
            File xmlFile=new File(xmlFilePath);
            if (xmlFile.isFile()  &&  xmlFile.exists()){
                String encoding = "utf-8";
                InputStreamReader read = new InputStreamReader(new FileInputStream(xmlFile), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                StringBuffer stringBuffer = new  StringBuffer();
                String lineStr = null; // 每次读取一行字符串
                while ((lineStr = bufferedReader.readLine()) != null) {
                    stringBuffer.append(lineStr);
                }
                str=StringUtils.substringBetween(stringBuffer.toString(),open,close);

            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return str;
    }


    /**
     * @Author Alistair.Chow
     * @Description XML序列化
     * @Date 18:48 2018/8/28
     * @Param [obj, xmlFilePath]
     * @return void
     **/
    public static <T> void serialize(T obj, String xmlFilePath) throws JAXBException, IOException {
//        FileWriter writer = null;
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
//        writer = new FileWriter(xmlFilePath);

        File file = new File(xmlFilePath);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8"
                )
        );
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        marshaller.marshal(obj, writer);
    }

    /**
     * @Author Alistair.Chow
     * @Description XML序列化
     * @Date 16:03 2018/8/29
     * @Param [obj]
     * @return java.lang.String
     **/
    public static <T> String serialize(T obj) throws JAXBException, IOException {
        StringWriter writer = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    /**
     * @Author Alistair.Chow
     * @Description XML反序列化
     * @Date 9:59 2018/8/29
     * @Param [xmlFilePath, clazz]
     * @return T
     **/
    @SuppressWarnings({ "unchecked", "rawtypes" })
//     public static <T extends Serializable> T deserialize(String xmlFilePath, Class clazz)
//             throws FileNotFoundException, JAXBException, UnsupportedEncodingException {
//        JAXBContext context = JAXBContext.newInstance(clazz);
//        Unmarshaller unmarshal = context.createUnmarshaller();
//        FileInputStream fis = new FileInputStream(xmlFilePath);
//        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//        return (T) unmarshal.unmarshal(isr);
//    }

    public static <T extends Serializable> T deserialize(String xml, Class clazz)
            throws FileNotFoundException, JAXBException, UnsupportedEncodingException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshal = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        return (T) unmarshal.unmarshal(reader);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parserXML(String xml) {
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
        decoder.close();
        return (T) decoder.readObject();
    }

}