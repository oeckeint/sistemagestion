package utileria.documentos;

import excepciones.nodos.NoCoincidenLosNodosEsperadosException;
import excepciones.nodos.energiaexcedentaria.autoconsumo.ExisteMasDeUnAutoconsumoException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utileria.texto.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class NodosUtil {

    public static NodeList getSingleNodeListByNameFromDocument(String nombreArchivo, Document document, String nodeName) throws ExisteMasDeUnAutoconsumoException {
        NodeList nodeList = document.getElementsByTagName(nodeName);
        if (nodeList.getLength() != 1) {
            throw new ExisteMasDeUnAutoconsumoException("Error: No se encontró el nodo " + nodeName + " en el archivo " + nombreArchivo + ".");
        }
        return nodeList;
    }

    public static NodeList getSingleNodeListByChainedNames(NodeList parentNode, String... nodeNames) throws NoCoincidenLosNodosEsperadosException {
        for (String nodeName : nodeNames) {
            parentNode = getAllNodesByNameWithSpecificExpectedNodes(parentNode, nodeName, 1);
        }
        return parentNode;
    }

    public static NodeList getAllNodesByNameWithSpecificExpectedNodes(NodeList childList, String nodeName, int expectedNodes) throws NoCoincidenLosNodosEsperadosException {
        if (expectedNodes < 1) {
            throw new RuntimeException("Error: Los nodos esperados no pueden ser menores a 1.");
        }

        List<Node> nodeList = new ArrayList<>();

        for (int i = 0; i < childList.getLength(); i++) {
            NodeList childNode = childList.item(i).getChildNodes();
            for (int j = 0; j < childNode.getLength(); j++) {
                Node childNode2 = childNode.item(j);
                if (nodeName.trim().equalsIgnoreCase(childNode2.getNodeName().trim())) {
                    nodeList.add(childNode2);
                }
            }
        }

        if (nodeList.size() != expectedNodes) {
            throw new NoCoincidenLosNodosEsperadosException(nodeName, expectedNodes, nodeList.size());
        }

        return new CustomNodeList(nodeList);
    }

    public static NodeList getSingleNodeListByName(NodeList parentNode, String nodeName) throws NoCoincidenLosNodosEsperadosException {
        return getAllNodesByNameWithSpecificExpectedNodes(parentNode, nodeName, 1);
    }

    public static List<String> getAllContentNodesAsStringList(NodeList parentNode, String nodeNameReference) {
        int contador = 0;
        List<String> textContentList = new ArrayList<>();
        for (int i = 0; i < parentNode.getLength(); i++) {
            NodeList nodeList = parentNode.item(i).getChildNodes();
            for (int j = 0; j < nodeList.getLength(); j++) {
                if (nodeNameReference.equals(nodeList.item(j).getNodeName())) {
                    textContentList.add(nodeList.item(j).getTextContent().trim());
                    contador++;
                }
            }
        }

        if (contador != parentNode.getLength()) {
            throw new RuntimeException("Error: La cantidad de datos de " + nodeNameReference + " encontrados no corresponden con exactamente 1 por cada " + parentNode.item(0).getNodeName() + ".");
        }

        return textContentList;
    }

    public static List<Double> getAllContentNodesAsDoubleList(NodeList parentNode, String nodeNameReference) {
        return StringUtils.parseStringListToDoubleList(getAllContentNodesAsStringList(parentNode, nodeNameReference));
    }

    public static String getSingleContentNodeAsString(NodeList parentNode, String nodeNameReference) {
        List<String> results = getAllContentNodesAsStringList(parentNode, nodeNameReference);

        if (results.isEmpty()) {
            throw new RuntimeException("Error: No se encontró ningún nodo con el nombre '" + nodeNameReference + "'.");
        }
        if (results.size() > 1) {
            throw new RuntimeException("Error: Se encontraron múltiples nodos con el nombre '" + nodeNameReference + "' cuando solo se esperaba uno.");
        }

        return results.get(0);
    }

    public static int getSingleContentNodeAsInt(NodeList parentNode, String nodeNameReference) {
        return StringUtils.parseStringToInt(getSingleContentNodeAsString(parentNode, nodeNameReference));
    }

    public static LocalDateTime getSingleContentNodeAsLocalDateTimeWithDefaultTime(NodeList parentNode, String nodeNameReference) {
        return StringUtils.parseStringToLocalDateTimeWithDefaultTime(getSingleContentNodeAsString(parentNode, nodeNameReference));
    }

    private static class CustomNodeList implements NodeList {
        private final List<Node> nodes;

        public CustomNodeList(List<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Node item(int index) {
            return nodes.get(index);
        }

        @Override
        public int getLength() {
            return nodes.size();
        }
    }

    private NodosUtil() {
        throw new IllegalStateException("Utility class");
    }

}
