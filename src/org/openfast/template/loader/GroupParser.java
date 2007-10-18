package org.openfast.template.loader;

import java.util.ArrayList;
import java.util.List;

import org.openfast.error.FastConstants;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GroupParser extends AbstractFieldParser {

	public GroupParser() {
		super("group");
	}
	
    /**
     * Creates a Group object from the dom group element
     * @param group The dom element object
     * @param isOptional Determines if the Field is required or not for the data
     * @return Returns a newly created Group object
     */
	protected Field parse(Element groupElement, boolean optional, ParsingContext context) {
        Group group = new Group(context.getName(), parseFields(groupElement, context), optional);
        parseMore(groupElement, group, context);
		return group;
	}
	
	protected static void parseMore(Element groupElement, Group group, ParsingContext context) {
        group.setChildNamespace(context.getNamespace());
        if (groupElement.hasAttribute("id"))
    		group.setId(groupElement.getAttribute("id"));
        group.setTypeReference(getTypeReference(groupElement));
		parseExternalAttributes(groupElement, group);
	}

	/**
     * Places the nodes of the passed element into an array
     * @param template The dom element object
     * @return Returns a Field array of the parsed nodes of the dom element 
     */
    protected static Field[] parseFields(Element template, ParsingContext context) {
        NodeList childNodes = template.getChildNodes();
        List fields = new ArrayList();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);

            if (isElement(item)) {
            	if ("typeRef".equals(item.getNodeName()) || "length".equals(item.getNodeName()))
            		continue;
            	Element element = (Element) item;
				FieldParser fieldParser = context.getFieldParser(element);
				if (fieldParser == null)
					context.getErrorHandler().error(FastConstants.PARSE_ERROR, "No parser registered for " + element.getNodeName());
				fields.add(fieldParser.parse(element, context));
            }
        }

        return (Field[]) fields.toArray(new Field[] {  });
    }

	/**
	 * Finds the typeReference tag in the passed dom element object
	 * @param templateTag The dom element object 
	 * @return Returns a string of the TypeReference from the passed element dom object
	 */
    protected static String getTypeReference(Element templateTag) {
        String typeReference = null;
        NodeList typeReferenceTags = templateTag.getElementsByTagName("typeRef");

        if (typeReferenceTags.getLength() > 0) {
            Element messageRef = (Element) typeReferenceTags.item(0);
            typeReference = messageRef.getAttribute("name");
        }

        return typeReference;
    }

}
