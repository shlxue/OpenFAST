package org.openfast;

import org.openfast.test.ObjectMother;
import org.openfast.test.OpenFastTestCase;

public class ApplicationTypeDictionaryTest extends OpenFastTestCase {

	public void testLookup() {
		ObjectMother.allocationInstruction().setTypeReference("AllocationInstruction");
		ObjectMother.allocations().setTypeReference("Allocation");
		
		Context context = new Context();
		
		context.store("type", ObjectMother.allocationInstruction(), "ID", string("1234"));
		
		assertEquals(string("1234"), context.lookup("type", ObjectMother.allocationInstruction(), "ID"));
		assertEquals(ScalarValue.UNDEFINED, context.lookup("type", ObjectMother.allocations().getGroup(), "ID"));
	}

}
