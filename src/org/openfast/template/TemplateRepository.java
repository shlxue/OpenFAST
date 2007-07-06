package org.openfast.template;


public interface TemplateRepository {
	public MessageTemplate getTemplate(String name);
	public MessageTemplate getTemplate(int id);
	public boolean hasTemplate(String name);
	public boolean hasTemplate(int id);
	public void add(MessageTemplate template);
	public MessageTemplate[] toArray();
	
	TemplateRepository NULL = new TemplateRepository() {
		public MessageTemplate getTemplate(String name) {
			return null;
		}
		public MessageTemplate getTemplate(int id) {
			return null;
		}
		public boolean hasTemplate(String name) {
			return false;
		}
		public boolean hasTemplate(int id) {
			return false;
		}
		public void add(MessageTemplate template) {
			throw new UnsupportedOperationException();
		}
		public MessageTemplate[] toArray() {
			return new MessageTemplate[] {};
		}};
		
}
