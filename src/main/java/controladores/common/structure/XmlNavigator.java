package controladores.common.structure;

import common.publisher.common.XmlNodeKey;
import lombok.NonNull;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlNavigator {

    private XmlNavigator() {
        // Private constructor to prevent instantiation
    }

    public static boolean containerHasNode(@NonNull NodeList parentNode, @NonNull XmlNodeKey nodeKey) {
        String target = nodeKey.value();

        for (int i = 0; i < parentNode.getLength(); i++) {
            Node node = parentNode.item(i);

            if (target.equals(node.getNodeName())) {
                return true;
            }

            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                if (target.equals(children.item(j).getNodeName())) {
                    return true;
                }
            }
        }

        return false;
    }

}
