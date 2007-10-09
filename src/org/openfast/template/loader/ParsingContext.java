package org.openfast.template.loader;

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

	public ParsingContext() {
		this(NULL);
	}
	
	public ParsingContext(ParsingContext parent) {
		this.parent = parent;
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
	
}
