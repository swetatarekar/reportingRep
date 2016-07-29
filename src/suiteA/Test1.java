package suiteA;

import listeners.ErrorUtil;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Test1 {

	@Test(dataProvider="getData")
	public void test1(String u, String p){
		try {
			Assert.assertEquals(u, "U4");
		} catch (Throwable t) {
			ErrorUtil.addVerificationFailure(t);
		}
		
		
	}
	
	@DataProvider
	public Object[][] getData(){
		
		Object data[][]= new Object[2][2];

		data[0][0]= "u1";
		data[0][1]= "p1";
		
		data[1][0]= "u2";
		data[1][1]= "p2";
		
		return data;
	}
}
