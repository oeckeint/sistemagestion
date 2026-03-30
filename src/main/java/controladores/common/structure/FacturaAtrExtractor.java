package controladores.common.structure;

import common.publisher.common.XmlNodeKey;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FacturaAtrExtractor {

    private final Document doc;

    public FacturaAtrExtractor(Document doc) {
        this.doc = doc;
    }

    public String obtenerValor(XmlNodeKey nodeKey) {
        NodeList flowListFacturaATR = doc.getElementsByTagName("DatosFacturaATR");

        if (flowListFacturaATR.getLength() == 0) {
            return "NO_INFORMADO";
        }

        String target = nodeKey.value();
        NodeList childList = flowListFacturaATR.item(0).getChildNodes();

        for (int i = 0; i < childList.getLength(); i++) {
            Node childNode = childList.item(i);

            if (target.equals(childNode.getNodeName())) {
                String value = childNode.getTextContent();
                return value != null ? value.trim() : "NO_INFORMADO";
            }
        }

        return "NO_INFORMADO";
    }
}
