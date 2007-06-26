package org.openfast.codec;

import java.io.InputStream;

import junit.framework.TestCase;

import org.openfast.ByteUtil;
import org.openfast.Context;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.StringValue;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;

public class FastDecoderTest extends TestCase {

	public void testDecodeEmptyMessage()
	{
		Group messageTemplate = new MessageTemplate(null, new Field[] {});
		InputStream in = ByteUtil.createByteStream("11000000 11110001");
		Context context = new Context();
		context.registerTemplate(113, messageTemplate);
		
		GroupValue message = new FastDecoder(context, in).readMessage();
		assertEquals(113, message.getInteger(0));
	}
	
	public void testDecodeSequentialEmptyMessages()
	{
		Group messageTemplate = new MessageTemplate(null, new Field[] {});
		InputStream in = ByteUtil.createByteStream("11000000 11110001 10000000");
		Context context = new Context();
		context.registerTemplate(113, messageTemplate);
		
		FastDecoder decoder = new FastDecoder(context, in);
		GroupValue message = decoder.readMessage();
		GroupValue message2 = decoder.readMessage();
		assertEquals(113, message.getInteger(0));
		assertEquals(113, message2.getInteger(0));
	}
	
	public void testDecodeSimpleMessage()
	{
		MessageTemplate template = new MessageTemplate(null, 
				new Field[] {
					new Scalar("1", Type.UNSIGNED_INTEGER, Operator.COPY, false)
		});
		InputStream in = ByteUtil.createByteStream("11100000 11110001 10000001");
		Context context = new Context();
		context.registerTemplate(113, template);
		
		GroupValue message = new Message(template, 113);
		message.setInteger(1, 1);
		FastDecoder decoder = new FastDecoder(context, in);
		GroupValue readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
		assertEquals(readMessage, message);
	}
	
	public void testDecodeMessageWithAllFieldTypes()
	{
		
		//               --PMAP-- --TID--- ---#1--- -------#2-------- ------------#3------------ ---#4--- ------------#5------------ ---#6---
		String msgstr = "11111111 11110001 11001000 10000001 11111111 11111101 00001001 10110001 11111111 01100001 01100010 11100011 10000010";
		InputStream in = ByteUtil.createByteStream(msgstr);
		
		MessageTemplate template = new MessageTemplate(null, new Field[] {
				new Scalar("1", Type.STRING, Operator.COPY, false),
				new Scalar("2", Type.BYTE_VECTOR, Operator.COPY, false),
				new Scalar("3", Type.DECIMAL, Operator.COPY, false),
				new Scalar("4", Type.SIGNED_INTEGER, Operator.COPY, false),
				new Scalar("5", Type.STRING, Operator.COPY, false),
				new Scalar("6", Type.UNSIGNED_INTEGER, Operator.COPY, false),
		});
		Context context = new Context();
		context.registerTemplate(113, template);
		
		GroupValue message = new Message(template, 113);
		message.setString(1, "H");
		message.setByteVector(2, new byte[] { (byte) 0xFF });
		message.setDecimal(3, 1.201);
		message.setInteger(4, -1);
		message.setString(5, "abc");
		message.setInteger(6, 2);
		assertEquals(message, new FastDecoder(context, in).readMessage());
	}
	

	public void testDecodeMessageWithSignedIntegerFieldTypesAndAllOperators()
	{
		MessageTemplate template = new MessageTemplate(null, new Field[] {
				new Scalar("1", Type.SIGNED_INTEGER, Operator.COPY, false),
				new Scalar("2", Type.SIGNED_INTEGER, Operator.DELTA, false),
				new Scalar("3", Type.SIGNED_INTEGER, Operator.INCREMENT, new IntegerValue(10), false), 
				new Scalar("4", Type.SIGNED_INTEGER, Operator.INCREMENT, false), 
				new Scalar("5", Type.SIGNED_INTEGER, Operator.CONSTANT, new IntegerValue(1), false), /* NON-TRANSFERRABLE */
				new Scalar("6", Type.SIGNED_INTEGER, Operator.DEFAULT, new IntegerValue(2), false)
		});
		
		GroupValue message = new Message(template, 113);
		message.setInteger(1, 109);
		message.setInteger(2, 29470);
		message.setInteger(3, 10);
		message.setInteger(4, 3);
		message.setInteger(5, 1);
		message.setInteger(6, 2);
		
		//             --PMAP-- --TID--- --------#1------- ------------#2------------ ---#4---
		String msg1 = "11101000 11110001 00000000 11101101 00000001 01100110 10011110 10000011";
		
		//             --PMAP-- ---#2--- ---#6---
		String msg2 = "10000100 11111111 10000011";
		
		//             --PMAP-- --------#1------- --------#2------- ---#4--- ---#6---
		String msg3 = "10101100 00000000 11100000 00001000 10000111 10000001 10000011";

		InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2 + ' ' + msg3);
		Context context = new Context();
		context.registerTemplate(113, template);
		FastDecoder decoder = new FastDecoder(context, in);
		
		Message readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
		
		message.setInteger(2, 29469);
		message.setInteger(3, 11);
		message.setInteger(4, 4);
		message.setInteger(6, 3);

		readMessage = decoder.readMessage();
		assertEquals(message, readMessage);

		message.setInteger(1, 96);
		message.setInteger(2, 30500);
		message.setInteger(3, 12);
		message.setInteger(4, 1);

		readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
	}
	
	public void testDecodeMessageWithUnsignedIntegerFieldTypesAndAllOperators()
	{
		MessageTemplate template = new MessageTemplate(null, new Field[] {
				new Scalar("1", Type.UNSIGNED_INTEGER, Operator.COPY, false),
				new Scalar("2", Type.UNSIGNED_INTEGER, Operator.DELTA, false),
				new Scalar("3", Type.UNSIGNED_INTEGER, Operator.INCREMENT, new IntegerValue(10), false), 
				new Scalar("4", Type.UNSIGNED_INTEGER, Operator.INCREMENT, false), 
				new Scalar("5", Type.UNSIGNED_INTEGER, Operator.CONSTANT, new IntegerValue(1), false), /* NON-TRANSFERRABLE */
				new Scalar("6", Type.UNSIGNED_INTEGER, Operator.DEFAULT, new IntegerValue(2), false)
		});
		
		GroupValue message = new Message(template, 113);
		message.setInteger(1, 109);
		message.setInteger(2, 29470);
		message.setInteger(3, 10);
		message.setInteger(4, 3);
		message.setInteger(5, 1);
		message.setInteger(6, 2);
		
		//             --PMAP-- --TID--- ---#1--- ------------#2------------ ---#4---
		String msg1 = "11101000 11110001 11101101 00000001 01100110 10011110 10000011";
		
		//             --PMAP-- ---#2--- ---#6---
		String msg2 = "10000100 11111111 10000011";
		
		//             --PMAP-- ---#1--- --------#2------- ---#4--- ---#6---
		String msg3 = "10101100 11100000 00001000 10000111 10000001 10000011";

		InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2 + ' ' + msg3);
		Context context = new Context();
		context.registerTemplate(113, template);
		FastDecoder decoder = new FastDecoder(context, in);
		
		Message readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
		
		message.setInteger(2, 29469);
		message.setInteger(3, 11);
		message.setInteger(4, 4);
		message.setInteger(6, 3);

		readMessage = decoder.readMessage();
		assertEquals(message, readMessage);

		message.setInteger(1, 96);
		message.setInteger(2, 30500);
		message.setInteger(3, 12);
		message.setInteger(4, 1);

		readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
	}
	
	public void testDecodeMessageWithStringFieldTypesAndAllOperators()
	{
		MessageTemplate template = new MessageTemplate(null, new Field[] {
				new Scalar("1", Type.STRING, Operator.COPY, false),
				new Scalar("2", Type.STRING, Operator.DELTA, false),
				new Scalar("3", Type.STRING, Operator.CONSTANT, new StringValue("e"), false), /* NON-TRANSFERRABLE */
				new Scalar("4", Type.STRING, Operator.DEFAULT, new StringValue("long"), false)
		});
		
		Message message = new Message(template, 113);
		message.setString(1, "on");
		message.setString(2, "DCB32");
		message.setString(3, "e");
		message.setString(4, "long");
		
		//             --PMAP-- --TID--- --------#1------- ---------------------#2---------------------
		String msg1 = "11100000 11110001 01101111 11101110 10000000 01000100 01000011 01000010 00110011 10110010";
		
		//             --PMAP-- ------------#2------------ ---------------------#4---------------------
		String msg2 = "10010000 10000010 00110001 10110110 01110011 01101000 01101111 01110010 11110100";

		InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2);
		Context context = new Context();
		context.registerTemplate(113, template);
		FastDecoder decoder = new FastDecoder(context, in);
		
		Message readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
		
		message.setString(2, "DCB16");
		message.setString(4, "short");

		readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
	}
}
