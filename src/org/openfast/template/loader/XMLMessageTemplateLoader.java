/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.template.loader;

import org.openfast.FastAlertSeverity;
import org.openfast.ScalarValue;

import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastConstants;

import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.TwinValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.operator.TwinOperator;
import org.openfast.template.type.Type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;


public class XMLMessageTemplateLoader implements MessageTemplateLoader {
    private static final List NON_FIELD_ELEMENTS = Arrays.asList(new String[] {
                "messageRef", "length"
            });
    private static final ErrorCode IO_ERROR = new ErrorCode(FastConstants.STATIC,
            -1, "IOERROR", "IO Error", FastAlertSeverity.ERROR);
    private static final ErrorCode XML_PARSING_ERROR = new ErrorCode(FastConstants.STATIC,
            -1, "XMLPARSEERR", "XML Parsing Error", FastAlertSeverity.ERROR);
    private ErrorHandler errorHandler = ErrorHandler.DEFAULT;

    public MessageTemplate[] load(InputStream source) {
        Document document = parseXml(source);

        if (document == null) {
            return new MessageTemplate[] {  };
        }

        Element root = document.getDocumentElement();
		if (root.getNodeName().equals("template")) {
        	return new MessageTemplate[] { new MessageTemplate(root.getAttribute("name"), parseFields(root)) };
        } else {
	        NodeList templateTags = root
	                                        .getElementsByTagName("template");
	        
	        MessageTemplate[] templates = new MessageTemplate[templateTags.getLength()];
	
	        for (int i = 0; i < templateTags.getLength(); i++) {
	            Element templateTag = (Element) templateTags.item(i);
	            templates[i] = new MessageTemplate(templateTag.getAttribute("name"),
	                    parseFields(templateTag));
	            templates[i].setMessageReference(getMessageReference(templateTag));
	        }
	        return templates;
        }

    }

    private String getMessageReference(Element templateTag) {
        String messageReference = null;
        NodeList messageReferenceTags = templateTag.getElementsByTagName(
                "messageRef");

        if (messageReferenceTags.getLength() > 0) {
            Element messageRef = (Element) messageReferenceTags.item(0);
            messageReference = getName(messageRef);
        }

        return messageReference;
    }

    private Field[] parseFields(Element template) {
        NodeList childNodes = template.getChildNodes();
        List fields = new ArrayList();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);

            if (isElement(item) && isMessageFieldElement(item)) {
                fields.add(parseField((Element) item));
            }
        }

        return (Field[]) fields.toArray(new Field[] {  });
    }

    private Field parseField(Element fieldNode) {
        String name = fieldNode.getAttribute("name");
        String type = fieldNode.getNodeName();
        boolean optional = false;

        if (fieldNode.hasAttribute("presence")) {
            optional = fieldNode.getAttribute("presence").equals("optional");
        }

        if (type.equals("sequence")) {
            return parseSequence(fieldNode, optional);
        } else if (type.equals("group")) {
            return parseGroup(fieldNode, optional);
        } else if (type.equals("string")) {
        	if (fieldNode.hasAttribute("charset"))
        		type = fieldNode.getAttribute("charset");
        	else
        		type = "ascii";
        } else if (type.equals("decimal")) {
            // Check for "decimal" special case where there are two separate operators for the mantissa and exponent
            NodeList fieldChildren = fieldNode.getChildNodes();
            Node mantissaNode = null;
            Node exponentNode = null;

            for (int i = 0; i < fieldChildren.getLength(); i++) {
                if (fieldChildren.item(i).getNodeName().equals("mantissa")) {
                    mantissaNode = fieldChildren.item(i);
                } else if (fieldChildren.item(i).getNodeName().equals("exponent")) {
                    exponentNode = fieldChildren.item(i);
                }
            }

            if ((mantissaNode != null) || (exponentNode != null)) {
                return createTwinFieldDecimal(fieldNode, name, optional,
                    mantissaNode, exponentNode);
            }
        }

        return createScalar(fieldNode, name, optional, type);
    }

    private Field createTwinFieldDecimal(Element fieldNode, String name,
        boolean optional, Node mantissaNode, Node exponentNode) {
        String mantissaOperator = "none";
        String exponentOperator = "none";
        ScalarValue mantissaDefaultValue = ScalarValue.UNDEFINED;
        ScalarValue exponentDefaultValue = ScalarValue.UNDEFINED;

        if ((mantissaNode != null) && mantissaNode.hasChildNodes()) {
            Node operatorNode = mantissaNode.getChildNodes().item(0);
            mantissaOperator = operatorNode.getNodeName();

            String value = ((Element) operatorNode).getAttribute("value");

            if ((value != null) && !value.equals("")) {
                mantissaDefaultValue = ScalarValue.getValue(Type.U32,
                        value);
            }
        }

        if ((exponentNode != null) && exponentNode.hasChildNodes()) {
            Node operatorNode = exponentNode.getChildNodes().item(0);
            exponentOperator = operatorNode.getNodeName();

            String value = ((Element) operatorNode).getAttribute("value");

            if ((value != null) && !value.equals("")) {
                exponentDefaultValue = ScalarValue.getValue(Type.U32,
                        value);
            }
        }

        return new Scalar(name, Type.DECIMAL,
            new TwinOperator(exponentOperator, mantissaOperator),
            new TwinValue(exponentDefaultValue, mantissaDefaultValue), optional);
    }

    private Scalar createScalar(Element fieldNode, String name, boolean optional, String typeName) {
    	String operator = Operator.NONE;
    	String defaultValue = null;
    	String key = null;
        Element operatorElement = getOperatorElement(fieldNode);
        if (operatorElement != null) {
	        if (operatorElement.hasAttribute("value"))
	            defaultValue = operatorElement.getAttribute("value");
	        operator = operatorElement.getNodeName();
	        if (operatorElement.hasAttribute("key"))
	        	key = operatorElement.getAttribute("key");
        }
        Scalar scalar = new Scalar(name, typeName, operator, defaultValue, optional);
        if (key != null)
        	scalar.setKey(key);
		return scalar;
    }

    private Element getOperatorElement(Element fieldNode) {
        NodeList children = fieldNode.getChildNodes();
        int index = 0;

        for (Node item = children.item(0); index < children.getLength();
                item = children.item(index++)) {
            if (isElement(item)) {
                return ((Element) item);
            }
        }

        return null;
    }

    private Group parseGroup(Element group, boolean isOptional) {
        return new Group(getName(group), parseFields(group), isOptional);
    }

    private Sequence parseSequence(Element sequence, boolean optional) {
        return new Sequence(getName(sequence),
            parseSequenceLengthField(sequence, optional),
            parseFields(sequence), optional);
    }

    private Scalar parseSequenceLengthField(Element sequence, boolean optional) {
        NodeList lengthElements = sequence.getElementsByTagName("length");

        if (lengthElements.getLength() == 0) {
            return null;
        }

        Element length = (Element) lengthElements.item(0);
        String name = length.hasAttribute("name") ? length.getAttribute("name")
                                                  : Sequence.createUniqueName();

        return createScalar(length, name, optional, "u32");
    }

    /**
     * Obtain the name of passed Element object as a string
     * @param sequence The Element that is being checked
     * @return Returns a string of the name of the Element sequence
     */
    private String getName(Element sequence) {
        return sequence.getAttributeNode("name").getNodeValue();
    }

    /**
     * Determines if the passed Node is of type element
     * @param item The Node that is being checked to see if its of type element
     * @return Returns true if passed Node type is type element, false otherwise
     */
    private boolean isElement(Node item) {
        return item.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * Determines if the passed node is within the message field element
     * @param item
     * @return Returns true if the passed Node is contained in the MessageFieldElement, false otherwise
     */
    private boolean isMessageFieldElement(Node item) {
        return !NON_FIELD_ELEMENTS.contains(item.getNodeName());
    }

    /**
     * Parse an XML file from an inputStream, returns a DOM org.w3c.dom.Document object.
     * @param templateStream The inputStream to be parsed
     * @return Returns a DOM org.w3c.dom.Document object, returns null if there are exceptions caught
     */
    private Document parseXml(InputStream templateStream) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();

            return builder.parse(templateStream);
        } catch (ParserConfigurationException e) {
            errorHandler.error(XML_PARSING_ERROR,
                "Error occurred while parsing xml template.", e);
        } catch (FactoryConfigurationError e) {
            errorHandler.error(XML_PARSING_ERROR,
                "Error occurred while parsing xml template.", e);
        } catch (SAXException e) {
            errorHandler.error(XML_PARSING_ERROR,
                "Error occurred while parsing xml template.", e);
        } catch (IOException e) {
            errorHandler.error(IO_ERROR,
                "Error occurred while trying to read xml template.", e);
        }

        return null;
    }

    /**
     * Sets the errorHandler object to a method
     * @param errorHandler The errorHandler that is being set
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
