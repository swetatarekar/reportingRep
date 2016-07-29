package listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.testng.internal.Utils;

public class CustomListener extends TestListenerAdapter implements IInvokedMethodListener,ISuiteListener{

	public static Hashtable<String, String> resultsTable = null;
	public static String resultFolderName = null;
	public static String resultFilePath = null;
	public static ArrayList<String> keys= null;
	
	public void onTestSuccess(ITestResult tr) {
		report(tr.getName(), "Pass");
	}
	public void onTestFailure(ITestResult tr) {
		
		List<Throwable> verificationFailures = ErrorUtil.getVerificationFailures();
		String errMsg = "";
		
		for (int i = 0; i < verificationFailures.size(); i++) {
			errMsg = errMsg +"[ "+ verificationFailures.get(i).getMessage()+"] -- ";
		}
		
		report(tr.getName(), errMsg);
	}
	public void onTestSkipped(ITestResult tr) {
		report(tr.getName(), tr.getThrowable().getMessage());
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult result) {
		Reporter.setCurrentTestResult(result);

		if (method.isTestMethod()) {
			List<Throwable> verificationFailures = ErrorUtil.getVerificationFailures();
			//if there are verification failures...
			if (verificationFailures.size() != 0) {
				//set the test to failed
				result.setStatus(ITestResult.FAILURE);
				
				//if there is an assertion failure add it to verificationFailures
				if (result.getThrowable() != null) {
					verificationFailures.add(result.getThrowable());
				}
 
				int size = verificationFailures.size();
				//if there's only one failure just set that
				if (size == 1) {
					result.setThrowable(verificationFailures.get(0));
				} else {
					//create a failure message with all failures and stack traces (except last failure)
					StringBuffer failureMessage = new StringBuffer("Multiple failures (").append(size).append("):nn");
					for (int i = 0; i < size-1; i++) {
						failureMessage.append("Failure ").append(i+1).append(" of ").append(size).append(":n");
						Throwable t = verificationFailures.get(i);
						String fullStackTrace = Utils.stackTrace(t, false)[1];
						failureMessage.append(fullStackTrace).append("nn");
					}
 
					//final failure
					Throwable last = verificationFailures.get(size-1);
					failureMessage.append("Failure ").append(size).append(" of ").append(size).append(":n");
					failureMessage.append(last.toString());
 
					//set merged throwable
					Throwable merged = new Throwable(failureMessage.toString());
					merged.setStackTrace(last.getStackTrace());
 
					result.setThrowable(merged);
					
				}
			}
		
		}
		
	}

	@Override
	public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStart(ISuite suite) {
		System.out.println("Starting Suite -> "+suite.getName());
		resultsTable = new Hashtable<String, String>();
		keys = new ArrayList<String>();
		
		if (resultFolderName == null) {
			Date d = new Date();
			resultFolderName = d.toString().replace(":", "_");
			File f = new File(System.getProperty("user.dir")+"\\target\\reports\\"+resultFolderName);
			f.mkdir();
			
			//make changes if required
			resultFilePath = System.getProperty("user.dir")+"\\target\\reports\\"+resultFolderName+"\\Report.xls";
			
			File src = new File(System.getProperty("user.dir")+"\\target\\reports\\reportTemplate.xls");
			File dest = new File(resultFilePath);
			
			try {
				FileUtils.copyFile(src, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onFinish(ISuite suite) {
		System.out.println("Finishing Suite -> "+suite.getName());
		System.out.println(resultsTable);
		System.out.println(keys);
		
		//write results in xls files:
		//here we done need to create a file for the testng suite as we are creating separate sheets for each suite, 
		// the foll if statement will prevent from creatting that:
		// make changes in the name if rewuired:
		if (!(suite.getName().equals("reporting example New"))) {
			Xls_Reader xls = new Xls_Reader(resultFilePath);
			xls.addSheet(suite.getName());
			//create the col names:
			xls.setCellData(suite.getName(), 0, 1, "Test Cases");
			xls.setCellData(suite.getName(), 1, 1, "Results");
			
			for (int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				String result = resultsTable.get(key);
				
				xls.setCellData(suite.getName(), 0, i+2, key);
				xls.setCellData(suite.getName(), 1, i+2, result);
				
			}
			
			//now add results to the sheet
			
		}
		
		resultsTable= null;
		keys = null;
	}

	public void report(String testName, String testResult) {
		
		int iteration_Number = 1;
		
		while (resultsTable.containsKey(testName+" iteration "+iteration_Number)) {
			iteration_Number++;
		}
		
		keys.add(testName+" iteration "+iteration_Number);
		resultsTable.put(testName+" iteration "+iteration_Number, testResult);
	}
}
