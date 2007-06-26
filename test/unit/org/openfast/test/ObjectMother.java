package org.openfast.test;

import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.StringValue;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class ObjectMother {

	private static MessageTemplate quoteTemplate;
	private static MessageTemplate allocationInstruction;
	private static Group instrument;
	private static Sequence allocations;
	public static final int QUOTE_TEMPLATE_ID = 10;
	public static final int ALLOC_INSTRCTN_TEMPLATE_ID = 25;

	public static MessageTemplate quoteTemplate() {
		if (quoteTemplate == null) {
			quoteTemplate = new MessageTemplate(null, new Field[] {
					new Scalar("bid", Type.DECIMAL, Operator.DELTA, false),
					new Scalar("ask", Type.DECIMAL, Operator.DELTA, false)
			});
		}
		return quoteTemplate;
	}

	public static Message quote(double bid, double ask) {
		Message quote = new Message(quoteTemplate(), QUOTE_TEMPLATE_ID);
		quote.setDecimal(1, bid);
		quote.setDecimal(2, ask);
		return quote;
	}

	public static Message newAllocInstrctn(String id, int side, double quantity, double averagePrice, GroupValue instrument, SequenceValue allocations) {
		Message allocInstrctn = new Message(allocationInstruction(), ALLOC_INSTRCTN_TEMPLATE_ID);
		allocInstrctn.setFieldValue(1, allocations);
		allocInstrctn.setFieldValue(2, instrument);
		allocInstrctn.setFieldValue(3, new StringValue(id));
		allocInstrctn.setFieldValue(4, new IntegerValue(side));
		allocInstrctn.setFieldValue(5, new DecimalValue(quantity));
		allocInstrctn.setFieldValue(6, new DecimalValue(averagePrice));
		return allocInstrctn;
	}
	
	public static MessageTemplate allocationInstruction() {
		if (allocationInstruction == null) {
			allocationInstruction = new MessageTemplate("AllocInstrctn", new Field[] {
					allocations(),
					instrument(),
					new Scalar("ID", Type.STRING, Operator.DELTA, false),
					new Scalar("Side", Type.UNSIGNED_INTEGER, Operator.COPY, false),
					new Scalar("Quantity", Type.DECIMAL, Operator.DELTA, false),
					new Scalar("Average Price", Type.DECIMAL, Operator.DELTA, false)
			});
		}
		return allocationInstruction;
	}
	
	private static Sequence allocations() {
		if (allocations == null) {
			allocations = new Sequence("Allocations", new Field[] {
					new Scalar("Account", Type.STRING, Operator.COPY, false),
					new Scalar("Price", Type.DECIMAL, Operator.DELTA, false),
					new Scalar("Quantity", Type.DECIMAL, Operator.DELTA, false),
					new Scalar("Average Price", Type.DECIMAL, Operator.DELTA, false)
			}, false);
		}
		return allocations;
	}

	private static Group instrument() {
		if (instrument == null) {
			instrument = new Group("Instrmt", new Field[] {
					new Scalar("Symbol", Type.STRING, Operator.COPY, false),
					new Scalar("MMY", Type.STRING, Operator.DELTA, false),
			}, false);
		}
		return instrument;
	}

	public static GroupValue newInstrument(String symbol, String mmy) {
		return new GroupValue(instrument(), new FieldValue[] {
			new StringValue(symbol),
			new StringValue(mmy)
		});
	}

	public static GroupValue newAllocation(String account, double price, double quantity) {
		return new GroupValue(allocations().getGroup(), new FieldValue[] {new StringValue(account), new DecimalValue(price), new DecimalValue(quantity), new DecimalValue(0.0)});
	}
}
