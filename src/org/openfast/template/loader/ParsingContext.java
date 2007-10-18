package org.openfast.template.loader;

import java.util.List;
import java.util.Map;

import org.openfast.QName;
import org.openfast.error.ErrorHandler;
import org.openfast.template.TemplateRegistry;
import org.w3c.dom.Element;

class ParsingContext {

	static final ParsingContext NULL = new ParsingContext();
	
	static {
		NULL.setDictionary("global");
		NULL.setNamespace("");
		NULL.setTemplateNamespace("");
	}

	private final ParsingContext parent;
	
	private String templateNamespace = null;
	private String namespace = null;
	private String dictionary = null;
	private ErrorHandler errorHandler;
	private TemplateRegistry templateRegistry;
	private Map typeMap;
	private List fieldParsers;

	private QName name;

	public ParsingContext() {
		this(NULL);
	}
	
	public ParsingContext(ParsingContext parent) {
		this.parent = parent;
	}
	
	public ParsingContext(Element node, ParsingContext parent) {
		this.parent = parent;
		if (node.hasAttribute("templateNs"))
			setTemplateNamespace(node.getAttribute("templateNs"));
		if (node.hasAttribute("ns"))
			setNamespace(node.getAttribute("ns"));
		if (node.hasAttribute("dictionary"))
			setDictionary(node.getAttribute("dictionary"));
		if (node.hasAttribute("name"))
			setName(new QName(node.getAttribute("name"), getNamespace()));
	}

	private void setName(QName name) {
		this.name = name;
	}

	public void setTemplateNamespace(String templateNS) {
		this.templateNamespace = templateNS;
	}

	public String getTemplateNamespace() {
		if (templateNamespace == null)
			return parent.getTemplateNamespace();
		return templateNamespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		if (namespace == null)
			return parent.getNamespace();
		return namespace;
	}

	public void setDictionary(String dictionary) {
		this.dictionary = dictionary;
	}

	public String getDictionary() {
		if (dictionary == null)
			return parent.getDictionary();
		return dictionary;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public ErrorHandler getErrorHandler() {
		if (errorHandler == null)
			return parent.getErrorHandler();
		return errorHandler;
	}

	public TemplateRegistry getTemplateRegistry() {
		if (templateRegistry == null)
			return parent.getTemplateRegistry();
		return templateRegistry;
	}

	public void setTemplateRegistry(TemplateRegistry templateRegistry) {
		this.templateRegistry = templateRegistry;
	}

	public void setTypeMap(Map typeMap) {
		this.typeMap = typeMap;
	}

	public Map getTypeMap() {
		if (typeMap == null)
			return parent.getTypeMap();
		return typeMap;
	}

	public List getFieldParsers() {
		if (fieldParsers == null)
			return parent.getFieldParsers();
		return fieldParsers;
	}

	public void setFieldParsers(List list) {
		this.fieldParsers = list;
	}

	public FieldParser getFieldParser(Element element) {
		List parsers = getFieldParsers();
		for(int i=parsers.size()-1; i>=0; i--) {
			FieldParser fieldParser = ((FieldParser)parsers.get(i));
			if (fieldParser.canParse(element, this))
				return fieldParser;
		}
		return null;
	}

	public ParsingContext getParent() {
		return parent;
	}

	public QName getName() {
		return name;
	}
}
