package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.util.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

public class sampleData {

	public sampleData(App app) {
		String r= app.dropTables();
	    r = app.createTables();
		r = app.setDate(2011,03,01);
		r = app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING,"17431",5000,"344151573","Joe Pepsi","3210 State St");
		r = app.setPin("344151573",1717,3692);
		r = app.addOwner("17431","412231856","Cindy Laughter","700 Hollister");
		r = app.setPin("412231856",1717,3764);
		r = app.addOwner("17431","322175130","Ivan Lendme","1235 Johnson Dr");
		r = app.setPin("322175130",1717,8471);
		
		r = app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING,"54321",5000,"212431965","Hurryson Ford","678 State St");
		r = app.setPin("212431965",1717,3532);
		r = app.addOwner("54321","412231856","Cindy Laughter","700 Hollister");
		//r = app.setPin("412231856",1717,3764);
		r = app.addOwner("54321","122219876","Elizabeth Sailor","4321 State St");
		r = app.setPin("122219876",1717,3856);
		r = app.addOwner("54321","203491209","Nam-Hoi Chung","1997 People St HK");
		r = app.setPin("203491209",1717,5340);
		
		r = app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING,"12121",5000,"207843218","David Copperfill","1357 State St");
		r = app.setPin("207843218",1717,8582);
		
		r = app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING,"41725",5000,"201674933","George Brush","5346 Foothill Av");
		r = app.setPin("201674933",1717,9824);
		r = app.addOwner("41725","401605312","Fatal Castro","3756 La Cumbre Plaza");
		r = app.setPin("401605312",1717,8193);
		r = app.addOwner("41725","231403227","Billy Clinton","5777 Hollistor");
		r = app.setPin("231403227",1717,8193);
		
		r = app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING,"76543",5000,"212116070","Li Kung","2 People Rd Beijing");
		r = app.setPin("212116070",1717,9173);
		r = app.addOwner("76543","188212217","Magic Jordon","3852 Court Rd");
		r = app.setPin("188212217",1717,7351);
		
		r = app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING,"93156",5000,"209378521","Kelvin Costner","Santa Cruz #3579");
		r = app.setPin("209378521",1717,4659);
		r = app.addOwner("93156","188212217","Magic Jordon","3852 Court Rd");
		//r = app.setPin("188212217"1717,7351);
		r = app.addOwner("93156","210389768","Oliver Stoner","6689 El Colegio #151");
		r = app.setPin("210389768",1717,8452);
		r = app.addOwner("93156","122219876","Elizabeth Sailor","4321 State St");
		//r = app.setPin(1717,3856);
		r = app.addOwner("93156","203491209","Nam-Hoi Chung","1997 People St HK");
		//r = app.setPin(1717,5340);
		
		r = app.createCheckingSavingsAccount(AccountType.SAVINGS,"43942",5000,"361721022","Alfred Hitchcook","6667 El Colegio #40");
		r = app.setPin("361721022",1717,1234);
		r = app.addOwner("43942","400651982","Pit Wilson","911 State St");
		r = app.setPin("400651982",1717,1821);
		r = app.addOwner("43942","212431965","Hurryson Ford","678 State St");
		//r = app.setPin(1717,3532);
		r = app.addOwner("43942","322175130","Ivan Lendme","1235 Johnson Dr");
		//r = app.setPin(1717,8471);
		
		
		r = app.createCheckingSavingsAccount(AccountType.SAVINGS,"29107",5000,"209378521","Kelvin Costner","Santa Cruz #3579");
		//r = app.setPin(1717,4659);
		r = app.addOwner("29107","212116070","Li Kung","2 People Rd Beijing");
		//r = app.setPin(1717,9173);
		r = app.addOwner("29107","210389768","Oliver Stoner","6689 El Colegio #151");
		//r = app.setPin(1717,8452);
		
		r = app.createCheckingSavingsAccount(AccountType.SAVINGS,"19023",5000,"412231856","Cindy Laughter","700 Hollister");
		//r = app.setPin(1717,3764);
		r = app.addOwner("19023","201674933","George Brush","5346 Foothill Av");
		//r = app.setPin(1717,9824);
		r = app.addOwner("19023","401605312","Fatal Castro","3756 La Cumbre Plaza");
		//r = app.setPin(1717,8193);
		
		
		r = app.createCheckingSavingsAccount(AccountType.SAVINGS,"32156",5000,"188212217","Magic Jordon","3852 Court Rd");
		//r = app.setPin(1717,7351);
		r = app.addOwner("32156","207843218","David Copperfill","1357 State St");
		//r = app.setPin(1717,8582);
		r = app.addOwner("32156","122219876","Elizabeth Sailor","4321 State St");
		//r = app.setPin(1717,3856);
		r = app.addOwner("32156","344151573","Joe Pepsi","3210 State St");
		//r = app.setPin(1717,3692);
		r = app.addOwner("32156","203491209","Nam-Hoi Chung","1997 People St HK");
		//r = app.setPin(1717,5340);
		r = app.addOwner("32156","210389768","Oliver Stoner","6689 El Colegio #151");
		//r = app.setPin(1717,8452);
		
		r = app.createPocketAccount("43947","29107",1000,"212116070");
		r = app.createPocketAccount("53027","12121",1000,"207843218");
		r = app.createPocketAccount("60413","43942",1000,"400651982");
		r = app.createPocketAccount("67521","19023",1000,"401605312");
		
		
		
		
	}







}