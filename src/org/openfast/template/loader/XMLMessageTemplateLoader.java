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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.error.FastAlertSeverity;
import org.openfast.error.FastConstants;
import org.openfast.template.DelegatingTemplateRepository;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.StaticTemplateReference;
import org.openfast.template.TemplateRepository;
import org.openfast.template.TwinValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.operator.TwinOperatorCodec;
import org.openfast.template.type.Type;
import org.openfast.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XMLMessageTemplateLoader implements MessageTemplateLoader {
    private static final List NON_FIELD_ELEMENTS = Arrays.asList(new String[] {
                "typeRef", "length", "templateRef"
            });
    private static final ErrorCode IO_ERROR = new ErrorCode(FastConstants.STATIC,
            -1, "IOERROR", "IO Error", FastAlertSeverity.ERROR);
    private static final ErrorCode XML_PARSING_ERROR = new ErrorCode(FastConstants.STATIC,
            -1, "XMLPARSEERR", "XML Parsing Error", FastAlertSeverity.ERROR);
	private static final ErrorCode INVALID_TYPE = new ErrorCode(FastConstants.STATIC, -1, "INVALIDTYPE", "Invalid Type", FastAlertSeverity.ERROR);
    
    private final DelegatingTemplateRepository templateRepository = new DelegatingTemplateRepository(TemplateRepository.NULL);
    private ErrorHandler errorHandler = ErrorHandler.DEFAULT;
    private Map typeMap = Type.getRegisteredTypeMap();

    /**
     * Parses the XML stream and creates an array of the elements
     * @param source The inputStream object to load
     */
    public MessageTemplate[] load(InputStream source) {
        Document document = parseXml(source);

        if (document == null) {
            return new MessageTemplate[] { };
        }

        Element root = document.getDocumentElement();
		if (root.getNodeName().equals("template")) {
			return new MessageTemplate[] { parseTemplate(root) };
        } else {
	        NodeList templateTags = root.getElementsByTagName("template");
	        MessageTemplate[] templates = new MessageTemplate[templateTags.getLength()];
	        for (int i = 0; i < templateTags.getLength(); i++) {
	            Element templateTag = (Element) templateTags.item(i);
	            templates[i] = parseTemplate(templateTag);
	        }
	        return templates;
        }

    }

    /**
     * Creates a Group object from the dom group element
     * @param group The dom element object
     * @param isOptional Determines if the Field is required or not for the data
     * @return Returns a newly created Group object
     */
    private Group parseGroup(Element groupElement, boolean isOptional, String dictionary) {
    	if (groupElement.hasAttribute("dictionary"))
    		dictionary = groupElement.getAttribute("dictionary");
        Group group = new Group(getName(groupElement), parseFields(groupElement, dictionary), isOptional);
        if (groupElement.hasAttribute("id"))
    		group.setId(groupElement.getAttribute("id"));
        group.setTypeReference(getTypeReference(groupElement));
		return group;
    }
    
    /**
     * Creates a MessageTemplate object from the dom template element
     * @param templateElement The dom element object
     * @return Returns a newly created MessageTemplate object
     */
	private MessageTemplate parseTemplate(Element templateElement) {
		String dictionary = "global";
		if (templateElement.hasAttribute("dictionary"))
			dictionary = templateElement.getAttribute("dictionary");
		MessageTemplate messageTemplate = new MessageTemplate(templateElement.getAttribute("name"), parseFields(templateElement, dictionary));
		if (templateElement.hasAttribute("id"))
    		messageTemplate.setId(templateElement.getAttribute("id"));
		messageTemplate.setTypeReference(getTypeReference(templateElement));
		add(messageTemplate);
		return messageTemplate;
	}

	/**
	 * Finds the typeReference tag in the passed dom element object
	 * @param templateTag The dom element object 
	 * @return Returns a string of the TypeReference from the passed element dom object
	 */
    private String getTypeReference(Element templateTag) {
        String typeReference = null;
        NodeList typeReferenceTags = templateTag.getElementsByTagName(
                "typeRef");

        if (typeReferenceTags.getLength() > 0) {
            Element messageRef = (Element) typeReferenceTags.item(0);
            typeReference = getName(messageRef);
        }

        return typeReference;
    }

    /**
     * Places the nodes of the passed element into an array
     * @param template The dom element object
     * @return Returns a Field array of the parsed nodes of the dom element 
     */
    private Field[] parseFields(Element template, String dictionary) {
        NodeList childNodes = template.getChildNodes();
        List fields = new ArrayList();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);

            if (isElement(item)) {
            	if (isMessageFieldElement(item))
            		fields.add(parseField((Element) item, dictionary));
            	else if (item.getNodeName().equals("templateRef"))
            		fields.add(parseTemplateRef((Element) item));
            }
        }

        return (Field[]) fields.toArray(new Field[] {  });
    }

    private Field parseTemplateRef(Element element) {
    	if (element.hasAttribute("name")) {
    		String templateName = element.getAttribute("name");
    		if (hasTemplate(templateName))
    			return new StaticTemplateReference(getTemplate(templateName));
    		else {
    			errorHandler.error(FastConstants.D8_TEMPLATE_NOT_EXIST, "The template \"" + templateName + "\" was not found.");
    			return null;
    		}
    	} else {
    		return new DynamicTemplateReference();
    	}
	}

	/**
     * This method checks what the type of the supplied element to determine how to parse it.
     * Once that is determined, it will parse it accordingly and return a new Scalar object of the 
     * passed data.
     * @param fieldNode The dom element object
     * @return Returns a new Scalar object of the parsed data. 
     */
    private Field parseField(Element fieldNode, String dictionary) {
        String name = fieldNode.getAttribute("name");
        String type = fieldNode.getNodeName();
        boolean optional = false;

        optional = "optional".equals(fieldNode.getAttribute("presence"));

        if ("sequence".equals(type)) {
            return parseSequence(fieldNode, optional, dictionary);
        } else if ("group".equals(type)) {
            return parseGroup(fieldNode, optional, dictionary);
        } else if ("string".equals(type)) {
        	if (fieldNode.hasAttribute("charset"))
        		type = fieldNode.getAttribute("charset");
        	else
        		type = "ascii";
        } else if ("decimal".equals(type)) {
            // Check for "decimal" special case where there are two separate operators for the mantissa and exponent
            NodeList fieldChildren = fieldNode.getChildNodes();
            Node mantissaNode = null;
            Node exponentNode = null;

            for (int i = 0; i < fieldChildren.getLength(); i++) {
                if ("mantissa".equals(fieldChildren.item(i).getNodeName())) {
                    mantissaNode = fieldChildren.item(i);
                } else if ("exponent".equals(fieldChildren.item(i).getNodeName())) {
                    exponentNode = fieldChildren.item(i);
                }
            }

            if ((mantissaNode != null) || (exponentNode != null)) {
                return createTwinFieldDecimal(fieldNode, name, optional,
                    mantissaNode, exponentNode, dictionary);
            }
        }

        return createScalar(fieldNode, name, optional, type, dictionary);
    }

    /**
     * Create a new Scalar object with a new TwinValue and a new TwinOperator with the mantissa and exponent nodes.
     * If there are nodes or child nodes within the passed Nodes, those values are stored as well
     * @param fieldNode The dom element object
     * @param name The name of the create Scalar object
     * @param optional Determines if the Field is required or not for the data
     * @param mantissaNode The passed mantissaNode
     * @param exponentNode The passed exponentNode
     * @return Returns a new Scalar object with the newly create TwinValue object and TwinOperator object.
     */
    private Field createTwinFieldDecimal(Element fieldNode, String name,
        boolean optional, Node mantissaNode, Node exponentNode, String dictionary) {
        String mantissaOperator = "none";
        String exponentOperator = "none";
        ScalarValue mantissaDefaultValue = ScalarValue.UNDEFINED;
        ScalarValue exponentDefaultValue = ScalarValue.UNDEFINED;
        
        if (fieldNode.hasAttribute("dictionary"))
        	dictionary = fieldNode.getAttribute("dictionary");
        
        if ((mantissaNode != null) && mantissaNode.hasChildNodes()) {
            Node operatorNode = mantissaNode.getChildNodes().item(0);
            mantissaOperator = operatorNode.getNodeName();

            String value = ((Element) operatorNode).getAttribute("value");

            if ((value != null) && !value.equals("")) {
                mantissaDefaultValue = Type.U32.getValue(value);
            }
        }

        if ((exponentNode != null) && exponentNode.hasChildNodes()) {
            Node operatorNode = exponentNode.getChildNodes().item(0);
            exponentOperator = operatorNode.getNodeName();

            String value = ((Element) operatorNode).getAttribute("value");

            if ((value != null) && !value.equals("")) {
                exponentDefaultValue = Type.U32.getValue(value);
            }
        }

        Scalar scalar = new Scalar(name, Type.DECIMAL,
		            new TwinOperatorCodec(Operator.getOperator(exponentOperator), Operator.getOperator(mantissaOperator)),
		            new TwinValue(exponentDefaultValue, mantissaDefaultValue), optional);
        if (fieldNode.hasAttribute("id"))
    		scalar.setId(fieldNode.getAttribute("id"));
        scalar.setDictionary(dictionary);
		return scalar;
    }

    /**
     * Create a new Scalar object with the passed information
     * @param fieldNode the dom element object
     * @param name The name of the new Scalar
     * @param optional Determines if the Scalar is required or not for the data
     * @param typeName The typeName of the new Scalar
     * @return Returns a new scalar with the passed information
     */
    private Scalar createScalar(Element fieldNode, String name, boolean optional, String typeName, String dictionary) {
    	Operator operator = Operator.NONE;
    	String defaultValue = null;
    	String key = null;
    	if (fieldNode.hasAttribute("dictionary"))
    		dictionary = fieldNode.getAttribute("dictionary");
        Element operatorElement = getOperatorElement(fieldNode);
        if (operatorElement != null) {
	        if (operatorElement.hasAttribute("value"))
	            defaultValue = operatorElement.getAttribute("value");
	        operator = Operator.getOperator(operatorElement.getNodeName());
	        if (operatorElement.hasAttribute("key"))
	        	key = operatorElement.getAttribute("key");
        }
        if (!typeMap.containsKey(typeName)) {
        	errorHandler.error(INVALID_TYPE, "The type " + typeName + " is not defined.  Possible types: " + Util.collectionToString(typeMap.keySet(), ", "));
        }
        Type type = (Type) typeMap.get(typeName);
		Scalar scalar = new Scalar(name, type, operator, type.getValue(defaultValue), optional);
		if (fieldNode.hasAttribute("id"))
    		scalar.setId(fieldNode.getAttribute("id"));
        if (key != null)
        	scalar.setKey(key);
        scalar.setDictionary(dictionary);
		return scalar;
    }

    /**
     * Find the first element item within the passed Element objects child nodes
     * @param fieldNode The dom element object
     * @return Returns the first element of the child nodes of the passed element, otherwise returns null
     */
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

    /**
     * Creates a sequence object from the dom sequence element
     * @param sequence The dom element object
     * @param optional Determines if the Sequence is required or not for the data
     * @return Returns a new Sequence object created out of the sequence dom element
     */
    private Sequence parseSequence(Element sequenceElement, boolean optional, String dictionary) {
    	if (sequenceElement.hasAttribute("dictionary"))
    		dictionary = sequenceElement.getAttribute("dictionary");
        String name = getName(sequenceElement);
		Sequence sequence = new Sequence(name,
		            parseSequenceLengthField(sequenceElement, name, optional, dictionary),
		            parseFields(sequenceElement, dictionary), optional);
        sequence.setTypeReference(getTypeReference(sequenceElement));
        if (sequenceElement.hasAttribute("id"))
    		sequence.setId(sequenceElement.getAttribute("id"));
		return sequence;
    }

    /**
     * 
     * @param sequence The dom element object
     * @param sequenceName Name of the sequence to which this lenght field belongs
     * @param optional Determines if the Scalar is required or not for the data
     * @return Returns null if there are no elements by the tag length, otherwise 
     */
    private Scalar parseSequenceLengthField(Element sequence, String sequenceName, boolean optional, String dictionary) {
        NodeList lengthElements = sequence.getElementsByTagName("length");

        if (lengthElements.getLength() == 0) {
            return null;
        }

        Element length = (Element) lengthElements.item(0);
        if (length.hasAttribute("dictionary"))
        	dictionary = length.getAttribute("dictionary");
        String name = length.hasAttribute("name") ? length.getAttribute("name")
                                                  : Global.createImplicitName(sequenceName);

        return createScalar(length, name, optional, Type.U32.getName(), dictionary);
    }

    /**
     * Obtain the name of passed Element object as a string
     * @param sequence The Element that is being checked
     * @return Returns a string of the name of the Element sequence
     */
    private String getName(Element sequence) {
        return sequence.getAttribute("name");
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
            builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
				public void error(SAXParseException exception) throws SAXException {
					errorHandler.error(XML_PARSING_ERROR, "ERROR: " + exception.getMessage(), exception);
				}
				public void fatalError(SAXParseException exception) throws SAXException {
					errorHandler.error(XML_PARSING_ERROR, "FATAL: " + exception.getMessage(), exception);
				}
				public void warning(SAXParseException exception) throws SAXException {
					errorHandler.error(XML_PARSING_ERROR, "WARNING: " + exception.getMessage(), exception);
				}});

            return builder.parse(templateStream);
        } catch (IOException e) {
            errorHandler.error(IO_ERROR,
                "Error occurred while trying to read xml template: " + e.getMessage(), e);
        } catch (Exception e) {
            errorHandler.error(XML_PARSING_ERROR, "Error occurred while parsing xml template: " + e.getMessage(), e);
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

	public void setTemplateRepository(TemplateRepository templateRepository) {
		this.templateRepository.setRepository(templateRepository);
	}

	public void add(MessageTemplate template) {
		templateRepository.add(template);
	}

	public MessageTemplate getTemplate(String name) {
		return templateRepository.getTemplate(name);
	}

	public MessageTemplate getTemplate(int id) {
		return templateRepository.getTemplate(id);
	}

	public boolean hasTemplate(String name) {
		return templateRepository.hasTemplate(name);
	}

	public boolean hasTemplate(int id) {
		return templateRepository.hasTemplate(id);
	}

	public MessageTemplate[] toArray() {
		return templateRepository.toArray();
	}

	public void setTypeMap(Map typeMap) {
		this.typeMap = typeMap;
	}
}
