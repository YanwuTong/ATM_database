package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.util.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{
		App app = new App("Goleta");                        // We need the default constructor of your App implementation.  Make sure such
													// constructor exists.
		String r = app.initializeSystem();          // We'll always call this function before testing your system.
	
		//r= app.dropTables();
	    //r = app.createTables();
		
		sampleData sample = new sampleData(app);
		/*
		r= app.dropTables();
	    r = app.createTables();
		
		r = app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING,"76543",6000,"212116070","Li Kung","2 People Rd Beijing");
		
		r = app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING,"17431",2000,"344151573","Joe Pepsi","3210 State St");
		r = app.createCheckingSavingsAccount(AccountType.SAVINGS,"29107",2000,"212116070","Li Kung","2 people Rd Beijing");
		r = app.createPocketAccount("43947","29107",100,"212116070");
		r = app.createPocketAccount("65556","29107",100,"212116070");
		*/
		r = app.printCustomerTable();
		r = app.printAccountTable();
		r = app.printAccountListTable() ;
		r = app.printPocketLinkTable() ;
		r =  app.printCoownerTable();
		//r = app.setInterest("76543", 8);
		r = app.printBalanceTable() ;
		
		UI ui = new UI(app);
		
		System.out.println("After Action: ");
		
		r = app.printCustomerTable();
		r = app.printAccountTable();
		r = app.printTransactionTable()	;
		r = app.printCheckTable();
		r = app.listClosedAccounts();
		r = app.printInterestRecordTable();
		//r = app.insertCustomer();
		r = app.printBalanceTable() ;
		//r = app.printSavingAccountTable();
		app.exampleAccessToDB();
        
	}
	//!### FINALIZAMOS
}
