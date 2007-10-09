package org.openfast.session;

import java.util.HashMap;
import java.util.Map;

import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.MessageHandler;
import org.openfast.ScalarValue;
import org.openfast.SequenceValue;
import org.openfast.codec.Coder;
import org.openfast.error.ErrorCode;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.StaticTemplateReference;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class SessionControlProtocol_1_1 extends AbstractSessionControlProtocol {
		
    private static final Map/*<MessageTemplate, SessionMessageHandler>*/ messageHandlers = new HashMap();
	public SessionControlProtocol_1_1() {
    	messageHandlers.put(FAST_ALERT_TEMPLATE, ALERT_HANDLER);
    	messageHandlers.put(TEMPLATE_DEFINITION, new SessionMessageHandler() {
    		public void handleMessage(Session session, Message message) {
    			MessageTemplate template = createTemplateFromMessage(message);
				session.addDynamicTemplateDefinition(template);
    			if (message.isDefined("TemplateId"))
    				session.registerDynamicTemplate(template.getName(), message.getInt("TemplateId"));
    		}});
    	messageHandlers.put(TEMPLATE_DECLARATION, new SessionMessageHandler() {
    		public void handleMessage(Session session, Message message) {
    			session.registerDynamicTemplate(message.getString("Name"), message.getInt("TemplateId"));
    		}});
	}
	public void configureSession(Session session) {
		registerSessionTemplates(session.in);
		registerSessionTemplates(session.out);
		
		session.in.addMessageHandler(FAST_RESET_TEMPLATE, RESET_HANDLER);
		session.out.addMessageHandler(FAST_RESET_TEMPLATE, RESET_HANDLER);
	}

	public void registerSessionTemplates(TemplateRegistry registry) {
        registry.registerTemplate(FAST_HELLO_TEMPLATE_ID, FAST_HELLO_TEMPLATE);
        registry.registerTemplate(FAST_ALERT_TEMPLATE_ID, FAST_ALERT_TEMPLATE);
        registry.registerTemplate(FAST_RESET_TEMPLATE_ID, FAST_RESET_TEMPLATE);
        registry.registerTemplate(TEMPLATE_DECL_ID, TEMPLATE_DECLARATION);
        registry.registerTemplate(TEMPLATE_DEF_ID, TEMPLATE_DEFINITION);
        registry.registerTemplate(INT32_INSTR_ID, INT32_INSTR);
        registry.registerTemplate(UINT32_INSTR_ID, UINT32_INSTR);
        registry.registerTemplate(INT64_INSTR_ID, INT64_INSTR);
        registry.registerTemplate(UINT64_INSTR_ID, UINT64_INSTR);
        registry.registerTemplate(DECIMAL_INSTR_ID, DECIMAL_INSTR);
//        registry.registerTemplate(COMP_DECIMAL_INSTR_ID, COMP_DECIMAL_INSTR);
        registry.registerTemplate(ASCII_INSTR_ID, ASCII_INSTR);
        registry.registerTemplate(UNICODE_INSTR_ID, UNICODE_INSTR);
        registry.registerTemplate(BYTE_VECTOR_INSTR_ID, BYTE_VECTOR_INSTR);
//        registry.registerTemplate(STAT_TEMP_REF_INSTR_ID, STAT_TEMP_REF_INSTR);
//        registry.registerTemplate(DYN_TEMP_REF_INSTR_ID, DYN_TEMP_REF_INSTR);
        registry.registerTemplate(SEQUENCE_INSTR_ID, SEQUENCE_INSTR);
        registry.registerTemplate(GROUP_INSTR_ID, GROUP_INSTR);
        registry.registerTemplate(CONSTANT_OP_ID, CONSTANT_OP);
        registry.registerTemplate(DEFAULT_OP_ID, DEFAULT_OP);
        registry.registerTemplate(COPY_OP_ID, COPY_OP);
        registry.registerTemplate(INCREMENT_OP_ID, INCREMENT_OP);
        registry.registerTemplate(DELTA_OP_ID, DELTA_OP);
        registry.registerTemplate(TAIL_OP_ID, TAIL_OP);
//        registry.registerTemplate(FOREIGN_INSTR_ID, FOREIGN_INSTR);
        registry.registerTemplate(ELEMENT_ID, ELEMENT);
//        registry.registerTemplate(TEXT_ID, TEXT);
	}

	public Session connect(String senderName, Connection connection) {
		Session session = new Session(connection, this);
		session.out.writeMessage(createHelloMessage(senderName));
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		Message message = session.in.readMessage();
		String serverName = message.getString(1);
		String vendorId = message.isDefined(2) ? message.getString(2) : "unknown";
		session.setClient(new BasicClient(serverName, vendorId));
        return session;
	}

	public void onError(Session session, ErrorCode code, String message) {
		session.out.writeMessage(createFastAlertMessage(code));
	}

	public Session onNewConnection(String serverName, Connection connection) {
		Session session = new Session(connection, this);
		Message message = session.in.readMessage();
		
		String clientName = message.getString(1);
		String vendorId = message.isDefined(2) ? message.getString(2) : "unknown";
		session.setClient(new BasicClient(clientName, vendorId));
		
		session.out.writeMessage(createHelloMessage(serverName));
        return session;
	}
        
    public Message createHelloMessage(String senderName) {
        Message message = new Message(FAST_HELLO_TEMPLATE);
        message.setString(1, senderName);
        message.setString(2, SessionConstants.VENDOR_ID);
        return message;
    }

    public static Message createFastAlertMessage(ErrorCode code) {
        Message alert = new Message(FAST_ALERT_TEMPLATE);
        alert.setInteger(1, code.getSeverity().getCode());
        alert.setInteger(2, code.getCode());
        alert.setString(4, code.getDescription());

        return alert;
    }

	public void handleMessage(Session session, Message message) {
		if (!messageHandlers.containsKey(message.getTemplate())) return;
		((SessionMessageHandler) messageHandlers.get(message.getTemplate())).handleMessage(session, message);
	}

	public boolean isProtocolMessage(Message message) {
		if (message == null) return false;
		return messageHandlers.containsKey(message.getTemplate());
	}

	public boolean supportsTemplateExchange() {
		return true;
	}

	public Message createTemplateDeclarationMessage(MessageTemplate messageTemplate, int templateId) {
		Message declaration = new Message(TEMPLATE_DECLARATION);
		setName(messageTemplate, declaration);
		declaration.setInteger("TemplateId", templateId);
		return declaration;
	}
	private void setName(Field field, GroupValue declaration) {
		declaration.setString("Name", field.getName());
		declaration.setString("Ns", "");
	}

	public MessageTemplate createTemplateFromMessage(Message templateDef) {
		String name = templateDef.getString("Name");
		Field[] fields = parseFieldInstructions(templateDef);
		return new MessageTemplate(name, fields);
	}
	
	private Field createGroup(GroupValue fieldDef) {
		String name = fieldDef.getString("Name");
		Field[] fields = parseFieldInstructions(fieldDef);
		boolean optional = fieldDef.getBool("Optional");
		return new Group(name, fields, optional);
	}

	private Field createSequence(GroupValue fieldDef) {
		String name = fieldDef.getString("Name");
		Field[] fields = parseFieldInstructions(fieldDef);
		boolean optional = fieldDef.getBool("Optional");
		return new Sequence(name, fields, optional);
	}

	private Field[] parseFieldInstructions(GroupValue templateDef) {
		SequenceValue instructions = templateDef.getSequence("Instructions");
		Field[] fields = new Field[instructions.getLength()];
		for (int i=0; i<fields.length; i++) {
			GroupValue fieldDef = instructions.get(i).getGroup(0);
			if (isScalar(fieldDef.getGroup()))
				fields[i] = createScalar(fieldDef);
			else if (fieldDef.getGroup().equals(GROUP_INSTR))
				fields[i] = createGroup(fieldDef);
			else if (fieldDef.getGroup().equals(SEQUENCE_INSTR))
				fields[i] = createSequence(fieldDef);
			else
				throw new IllegalStateException("Encountered unknown group " + fieldDef.getGroup() + "while processing field instructions " + templateDef.getGroup());
		}
		return fields;
	}

	private boolean isScalar(Group group) {
		return TYPE_TEMPLATE_MAP.values().contains(group);
	}

	private Field createScalar(GroupValue field) {
		Type type = (Type) TEMPLATE_TYPE_MAP.get(field.getGroup());
		Operator operator = (Operator) TEMPLATE_OPERATOR_MAP.get(field.getGroup("Operator").getGroup(0).getGroup());
		boolean optional = field.getBool("Optional");
		return new Scalar(field.getString("Name"), type, operator, null, optional);
	}

	public Message createTemplateDefinitionMessage(MessageTemplate messageTemplate) {
		Message templateDefinition = createGroup(messageTemplate, new Message(TEMPLATE_DEFINITION));
		templateDefinition.setInteger("Reset", 0);
		return templateDefinition;
	}
	
	private Message createGroup(Group group, Message groupMsg) {
		setName(group, groupMsg);
		SequenceValue instructions = new SequenceValue(TEMPLATE_DEFINITION.getSequence("Instructions"));
		int i = group instanceof MessageTemplate ? 1 : 0;
		for (; i<group.getFieldCount(); i++) {
			Field field = group.getField(i);
			FieldValue value = null; 
			if (field instanceof Scalar) {
				value = createScalar((Scalar) field);
			} else if (field instanceof Group) {
				value = createGroup((Group) field, new Message(GROUP_INSTR));
			} else if (field instanceof Sequence) {
				value = createSequence((Sequence) field);
			}
			instructions.add(new FieldValue[] { value });
		}
		groupMsg.setFieldValue("Instructions", instructions);
		return groupMsg;
	}

	private Message createSequence(Sequence sequence) {
		Message seqDef = createGroup(sequence.getGroup(), new Message(SEQUENCE_INSTR));
		if (!sequence.isImplicitLength()) {
			GroupValue seqLenDef = new GroupValue(SEQUENCE_INSTR.getGroup("Length"));
			Scalar length = sequence.getLength();
			GroupValue lengthName = new GroupValue(SEQUENCE_INSTR.getGroup("Length").getGroup("Name"));
			lengthName.setString("Name", length.getName());
			setName(length, lengthName);
			seqLenDef.setFieldValue("Name", lengthName);
			seqDef.setFieldValue("Length", seqLenDef);
		}
		return seqDef;
	}

	private GroupValue createScalar(Scalar scalar) {
		MessageTemplate scalarTemplate = (MessageTemplate) TYPE_TEMPLATE_MAP.get(scalar.getType());
		Message scalarMsg = new Message(scalarTemplate);
		setName(scalar, scalarMsg);
		scalarMsg.setInteger("Optional", scalar.isOptional() ? 1 : 0);
		scalarMsg.setFieldValue("Operator", new GroupValue(scalarTemplate.getGroup("Operator"), new FieldValue[] { createOperator(scalar) }));
		return scalarMsg;
	}

	private GroupValue createOperator(Scalar scalar) {
		if (!OPERATOR_TEMPLATE_MAP.containsKey(scalar.getOperator()))
			return null;
		GroupValue operatorMessage = new Message((MessageTemplate) OPERATOR_TEMPLATE_MAP.get(scalar.getOperator()));
		return operatorMessage;
	}

	public static final int FAST_RESET_TEMPLATE_ID = 120;
	public static final int FAST_HELLO_TEMPLATE_ID = 16003;
    public static final int FAST_ALERT_TEMPLATE_ID = 16004;
    public static final int TEMPLATE_DECL_ID       = 16010;
    public static final int TEMPLATE_DEF_ID        = 16011;
    public static final int INT32_INSTR_ID         = 16012;
    public static final int UINT32_INSTR_ID        = 16013;
    public static final int INT64_INSTR_ID         = 16014;
    public static final int UINT64_INSTR_ID        = 16015;
    public static final int DECIMAL_INSTR_ID       = 16016;
    public static final int COMP_DECIMAL_INSTR_ID  = 16017;
    public static final int ASCII_INSTR_ID         = 16018;
    public static final int UNICODE_INSTR_ID       = 16019;
    public static final int BYTE_VECTOR_INSTR_ID   = 16020;
    public static final int STAT_TEMP_REF_INSTR_ID = 16021;
    public static final int DYN_TEMP_REF_INSTR_ID  = 16022;
    public static final int SEQUENCE_INSTR_ID      = 16023;
    public static final int GROUP_INSTR_ID         = 16024;
    public static final int CONSTANT_OP_ID         = 16025;
    public static final int DEFAULT_OP_ID          = 16026;
    public static final int COPY_OP_ID             = 16027;
    public static final int INCREMENT_OP_ID        = 16028;
    public static final int DELTA_OP_ID            = 16029;
    public static final int TAIL_OP_ID             = 16030;
    public static final int FOREIGN_INSTR_ID       = 16031;
    public static final int ELEMENT_ID             = 16032;
    public static final int TEXT_ID                = 16033;
    
    public final static MessageTemplate FAST_ALERT_TEMPLATE = new MessageTemplate("",
            new Field[] {
                new Scalar("Severity", Type.U32, Operator.NONE,
                    ScalarValue.UNDEFINED, false),
                new Scalar("Code", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false),
                new Scalar("Value", Type.U32, Operator.NONE,
                    ScalarValue.UNDEFINED, true),
                new Scalar("Description", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, false),
            });
    public final static MessageTemplate FAST_HELLO_TEMPLATE = new MessageTemplate("",
            new Field[] {
                new Scalar("SenderName", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, false),
                new Scalar("VendorId", Type.ASCII, Operator.NONE, ScalarValue.UNDEFINED, true)
            });
    public static final Message RESET = new Message(FAST_RESET_TEMPLATE) {
            private static final long serialVersionUID = 1L;

			public void setFieldValue(int fieldIndex, FieldValue value) {
                throw new IllegalStateException(
                    "Cannot set values on a fast reserved message.");
            }
        };
    
    /************************** MESSAGE HANDLERS **********************************************/
    private static final MessageHandler RESET_HANDLER = new MessageHandler() {
            public void handleMessage(Message readMessage, Context context, Coder coder) {
                coder.reset();
            }
        };
        
    private static final SessionMessageHandler ALERT_HANDLER = new SessionMessageHandler() {
		public void handleMessage(Session session, Message message) {
			session.getErrorHandler().error(ErrorCode.getAlertCode(message), message.getString(4));
		}
    };


    /*************************** MESSAGE TEMPLATES *******************************************/
    private static final MessageTemplate ATTRIBUTE = new MessageTemplate("Attribute", new Field[] {
    		dict("Ns", Type.UNICODE, true, "template"),
    		unicode("Name"),
    		unicode("Value")
    });
    
    private static final MessageTemplate ELEMENT = new MessageTemplate("Element", new Field[] {
    		dict("Ns", Type.UNICODE, true, "template"),
    		unicode("Name"),
    		new Sequence("Attributes", new Field[] {
    				new StaticTemplateReference(ATTRIBUTE)
    		}, false),
    		new Sequence("Content", new Field[] {
    				new DynamicTemplateReference()
    		}, false)
    });
    
    private static final MessageTemplate OTHER = new MessageTemplate("Other", new Field[] {
    		new Sequence("ForeignAttributes", new Field[]{
    				new StaticTemplateReference(ATTRIBUTE)
    		}, true),
    		new Sequence("ForeignElements", new Field[]{
    				new StaticTemplateReference(ELEMENT)
    		}, true)
    });
    
    private static final MessageTemplate TEMPLATE_NAME = new MessageTemplate("TemplateName", new Field[] {
    		new Scalar("Ns", Type.UNICODE, Operator.COPY, null, false),
    		new Scalar("Name", Type.UNICODE, Operator.NONE, null, false)
    });
    
    private static final MessageTemplate NS_NAME = new MessageTemplate("NsName", new Field[] {
    		dict("Ns", Type.UNICODE, false, "template"),
    		new Scalar("Name", Type.UNICODE, Operator.NONE, null, false)
    });
	
	private static final MessageTemplate NS_NAME_WITH_AUX_ID = new MessageTemplate("NsNameWithAuxId", new Field[] {
		new StaticTemplateReference(NS_NAME),
		new Scalar("AuxId", Type.UNICODE, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate FIELD_BASE = new MessageTemplate("PrimFieldBase", new Field[] {
		new StaticTemplateReference(NS_NAME_WITH_AUX_ID),
		new Scalar("Optional", Type.U32, Operator.NONE, null, false),
		new StaticTemplateReference(OTHER)
	});
	
	private static final MessageTemplate PRIM_FIELD_BASE = new MessageTemplate("PrimFieldBase", new Field[] {
		new StaticTemplateReference(FIELD_BASE),
		new Group("Operator", new Field[] {
			new DynamicTemplateReference()
		}, true)
	});
	
	private static final MessageTemplate LENGTH_PREAMBLE = new MessageTemplate("LengthPreamble", new Field[] {
		new StaticTemplateReference(NS_NAME_WITH_AUX_ID),
		new StaticTemplateReference(OTHER)
	});
	
	private static final MessageTemplate PRIM_FIELD_BASE_WITH_LENGTH = new MessageTemplate("PrimFieldBaseWithLength", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Group("Length", new Field[] {
			new StaticTemplateReference(LENGTH_PREAMBLE)
		}, true)
	});
	
	private static final MessageTemplate INT32_INSTR = new MessageTemplate("Int32Instr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.I32, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate UINT32_INSTR = new MessageTemplate("UInt32Instr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.U32, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate INT64_INSTR = new MessageTemplate("Int64Instr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.I64, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate UINT64_INSTR = new MessageTemplate("UInt64Instr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.U64, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate DECIMAL_INSTR = new MessageTemplate("DecimalInstr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.DECIMAL, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate UNICODE_INSTR = new MessageTemplate("UnicodeInstr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE_WITH_LENGTH),
		new Scalar("InitialValue", Type.UNICODE, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate ASCII_INSTR = new MessageTemplate("AsciiInstr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.ASCII, Operator.NONE, null, true)
	});
	
	private static final MessageTemplate BYTE_VECTOR_INSTR = new MessageTemplate("ByteVectorInstr", new Field[] {
		new StaticTemplateReference(PRIM_FIELD_BASE),
		new Scalar("InitialValue", Type.BYTE_VECTOR, Operator.NONE, null, true)
	});
    
    private static final MessageTemplate TYPE_REF = new MessageTemplate("TypeRef", new Field[] {
    		new Group("TypeRef", new Field[] {
    				new StaticTemplateReference(NS_NAME),
    				new StaticTemplateReference(OTHER)
    		}, true)
    });
    
    private static final MessageTemplate TEMPLATE_DECLARATION = new MessageTemplate("TemplateDecl", new Field[] {
    		new StaticTemplateReference(TEMPLATE_NAME),
    		u32("TemplateId")
    });
    
	private static final MessageTemplate TEMPLATE_DEFINITION = new MessageTemplate("TemplateDef", new Field[] {
			new StaticTemplateReference(TEMPLATE_NAME),
			unicodeopt("AuxId"),
			u32opt("TemplateId"),
			new StaticTemplateReference(TYPE_REF),
			u32("Reset"),
			new StaticTemplateReference(OTHER),
			new Sequence("Instructions", new Field[] {
					new DynamicTemplateReference()
			}, false)
	});
	
	private static final MessageTemplate OP_BASE = new MessageTemplate("OpBase", new Field[] {
			unicodeopt("Dictionary"),
			new Group("Key", new Field[] {
				new StaticTemplateReference(NS_NAME)
			}, true),
			new StaticTemplateReference(OTHER)
	});
	
	private static final MessageTemplate CONSTANT_OP = new MessageTemplate("ConstantOp", new Field[] {
		new StaticTemplateReference(OP_BASE)
	});
	
	private static final MessageTemplate DEFAULT_OP = new MessageTemplate("DefaultOp", new Field[] {
		new StaticTemplateReference(OP_BASE)
	});
	
	private static final MessageTemplate COPY_OP = new MessageTemplate("CopyOp", new Field[] {
		new StaticTemplateReference(OP_BASE)
	});
	
	private static final MessageTemplate INCREMENT_OP = new MessageTemplate("IncrementOp", new Field[] {
		new StaticTemplateReference(OP_BASE)
	});
	
	private static final MessageTemplate DELTA_OP = new MessageTemplate("DeltaOp", new Field[] {
		new StaticTemplateReference(OP_BASE)
	});
	
	private static final MessageTemplate TAIL_OP = new MessageTemplate("TailOp", new Field[] {
		new StaticTemplateReference(OP_BASE)
	});
	
	private static final MessageTemplate GROUP_INSTR = new MessageTemplate("GroupInstr", new Field[]{
		new StaticTemplateReference(FIELD_BASE),
		new StaticTemplateReference(TYPE_REF),
		new Sequence("Instructions", new Field[] {
			new DynamicTemplateReference()
		}, false)
	});
	
	private static final MessageTemplate SEQUENCE_INSTR = new MessageTemplate("SequenceInstr", new Field[]{
		new StaticTemplateReference(FIELD_BASE),
		new StaticTemplateReference(TYPE_REF),
		new Group("Length", new Field[] {
			new Group("Name", new Field[] {
				new StaticTemplateReference(NS_NAME_WITH_AUX_ID)
			}, true),
			new Group("Operator", new Field[] {
				new DynamicTemplateReference()
			}, true),
			new Scalar("InitialValue", Type.U32, Operator.NONE, null, true),
			new StaticTemplateReference(OTHER),
		}, true),
		new Sequence("Instructions", new Field[] {
			new DynamicTemplateReference()
		}, false)
	});
    
	private static Field u32(String name) {
		return new Scalar(name, Type.U32, Operator.NONE, null, false);
	}
	
	private static Field dict(String name, Type type, boolean optional, String dictionary) {
		Scalar scalar = new Scalar(name, Type.UNICODE, Operator.COPY, null, optional);
		scalar.setDictionary(dictionary);
		return scalar;
	}

	private static Field unicodeopt(String name) {
		return new Scalar(name, Type.UNICODE, Operator.NONE, null, true);
	}
	
	private static Field unicode(String name) {
		return new Scalar(name, Type.UNICODE, Operator.NONE, null, false);
	}

	private static Field u32opt(String name) {
		return new Scalar(name, Type.U32, Operator.NONE, null, true);
	}
	
	private static final Map/*<Type, MessageTemplate>*/ TYPE_TEMPLATE_MAP = new HashMap();
	private static final Map/*<Type, MessageTemplate>*/ TEMPLATE_TYPE_MAP = new HashMap();
	private static final Map/*<Operator, MessageTemplate>*/ OPERATOR_TEMPLATE_MAP = new HashMap();
	private static final Map/*<Operator, MessageTemplate>*/ TEMPLATE_OPERATOR_MAP = new HashMap();
	
	static {
		TYPE_TEMPLATE_MAP.put(Type.I32,         INT32_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.U32,         UINT32_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.I64,         INT64_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.U64,         UINT64_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.DECIMAL,     DECIMAL_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.UNICODE,     UNICODE_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.ASCII,       ASCII_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.STRING,      ASCII_INSTR);
		TYPE_TEMPLATE_MAP.put(Type.BYTE_VECTOR, BYTE_VECTOR_INSTR);
		
		TEMPLATE_TYPE_MAP.put(INT32_INSTR,       Type.I32);
		TEMPLATE_TYPE_MAP.put(UINT32_INSTR,      Type.U32);
		TEMPLATE_TYPE_MAP.put(INT64_INSTR,       Type.I64);
		TEMPLATE_TYPE_MAP.put(UINT64_INSTR,      Type.U64);
		TEMPLATE_TYPE_MAP.put(DECIMAL_INSTR,     Type.DECIMAL);
		TEMPLATE_TYPE_MAP.put(UNICODE_INSTR,     Type.UNICODE);
		TEMPLATE_TYPE_MAP.put(ASCII_INSTR,		 Type.ASCII);
		TEMPLATE_TYPE_MAP.put(BYTE_VECTOR_INSTR, Type.BYTE_VECTOR);
		
		OPERATOR_TEMPLATE_MAP.put(Operator.CONSTANT,  CONSTANT_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.DEFAULT,   DEFAULT_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.COPY,      COPY_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.INCREMENT, INCREMENT_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.DELTA,     DELTA_OP);
		OPERATOR_TEMPLATE_MAP.put(Operator.TAIL,      TAIL_OP);
		
		TEMPLATE_OPERATOR_MAP.put(CONSTANT_OP,  Operator.CONSTANT);
		TEMPLATE_OPERATOR_MAP.put(DEFAULT_OP,   Operator.DEFAULT);
		TEMPLATE_OPERATOR_MAP.put(COPY_OP,      Operator.COPY);
		TEMPLATE_OPERATOR_MAP.put(INCREMENT_OP, Operator.INCREMENT);
		TEMPLATE_OPERATOR_MAP.put(DELTA_OP,     Operator.DELTA);
		TEMPLATE_OPERATOR_MAP.put(TAIL_OP,      Operator.TAIL);
    }
}
