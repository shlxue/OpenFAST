package org.openfast.template.loader;

import org.openfast.QName;
import org.openfast.template.Field;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractFieldParser implements FieldParser {
	
	private String[] parseableNodeNames;

	protected AbstractFieldParser(String nodeName) {
		this(new String[] { nodeName });
	}
	
	protected AbstractFieldParser(String[] nodeNames) {
		this.parseableNodeNames = nodeNames;
	}
	
	public boolean canParse(Element element, ParsingContext context) {
		for (int i=0; i<parseableNodeNames.length; i++)
			if (parseableNodeNames[i].equals(element.getNodeName()))
				return true;
		return false;
	}
	
	public final Field parse(Element fieldNode, ParsingContext parent) {
		boolean optional = "optional".equals(fieldNode.getAttribute("presence"));
		return parse(fieldNode, optional, new ParsingContext(fieldNode, parent));
	}
	
	protected abstract Field parse(Element fieldNode, boolean optional, ParsingContext context);

	protected static void parseExternalAttributes(Element element, Field field) {
		NamedNodeMap attributes = element.getAttributes();
		for (int i=0; i<attributes.getLength(); i++) {
			Attr attribute = (Attr) attributes.item(i);
			if (attribute.getNamespaceURI() == null || attribute.getNamespaceURI().equals("") || attribute.getNamespaceURI().equals(XMLMessageTemplateLoader.TEMPLATE_DEFINITION_NS))
				continue;
			field.addAttribute(new QName(attribute.getLocalName(), attribute.getNamespaceURI()), attribute.getValue());
		}
	}
	
    /**
     * Find the first element item within the passed Element objects child nodes
     * @param fieldNode The dom element object
     * @return Returns the first element of the child nodes of the passed element, otherwise returns null
     */
    protected static Element getElement(Element fieldNode, int elementIndex) {
        NodeList children = fieldNode.getChildNodes();
        int elemIndex = 0;
        for (int i=0; i<children.getLength(); i++) {
        	Node item = children.item(i);
            if (isElement(item)) {
            	elemIndex++;
            	if (elemIndex == elementIndex)
            		return ((Element) item);
            }
        }

        return null;
    }

    /**
     * Determines if the passed Node is of type element
     * @param item The Node that is being checked to see if its of type element
     * @return Returns true if passed Node type is type element, false otherwise
     */
    protected static boolean isElement(Node item) {
        return item.getNodeType() == Node.ELEMENT_NODE;
    }
}
