package suiteA;

import listeners.ErrorUtil;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Test2 {

	@Test
	public void test2(){
		try {
			Assert.assertEquals("A", "B");
		} catch (Throwable t) {
			ErrorUtil.addVerificationFailure(t);
		}
		
		try {
			Assert.assertEquals("A", "D");
		} catch (Throwable t) {
			ErrorUtil.addVerificationFailure(t);
		}
		
		try {
			Assert.assertEquals("A", "G");
		} catch (Throwable t) {
			ErrorUtil.addVerificationFailure(t);
		}
		
		Assert.assertEquals("A", "H");
		
	}
}
