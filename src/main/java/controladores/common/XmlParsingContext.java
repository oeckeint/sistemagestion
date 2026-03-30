package controladores.common;

import common.publisher.common.XmlNodeKey;
import controladores.common.structure.FacturaAtrExtractor;
import controladores.common.structure.XmlNavigator;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Getter
public class XmlParsingContext {

    private final Document document;

    // Lazy-loaded extractors (opcional)
    private FacturaAtrExtractor facturaAtrExtractor;

    public XmlParsingContext(Document document) {
        this.document = document;
    }

    // ---- NAVIGATION ----
    public boolean containerHasNode(NodeList parent, XmlNodeKey key) {
        return XmlNavigator.containerHasNode(parent, key);
    }

    // ---- EXTRACTORS ----
    public FacturaAtrExtractor facturaAtr() {
        if (facturaAtrExtractor == null) {
            facturaAtrExtractor = new FacturaAtrExtractor(document);
        }
        return facturaAtrExtractor;
    }
}
