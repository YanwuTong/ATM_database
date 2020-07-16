package cs174a;                        

import cs174a.Testable.*;
import java.util.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

public class UI{

	public UI(App app) {
		String r = "";
		boolean quit = false;
		while(!quit) {
			System.out.println("Slect Interface:");
			System.out.println("(1)ATM-APP");
			System.out.println("(2)Bank Teller");
			System.out.println("(3)Set System Date");
			System.out.println("(q)Quit the System");
			Scanner s = new Scanner(System.in);	
			String input = s.nextLine(); 
			if(input.equals("q")) {
				quit = true;
			}else {
				switch(input) {
				
					case "1":
						//Enter ATM-APP
						//Ask for PIN
						boolean back = false;
						System.out.print("Enter your tax number (or enter 0 to back):");
						String tin = s.nextLine(); 
						if(!tin.equals("0")) {
							System.out.print("Enter your PIN:");
							String PIN = s.nextLine();
							
							atm _atm = new atm(app.getConnection(),tin,PIN,app);
							if(_atm.getCorrectPin()) {
								while(!back) {
									System.out.println("Please select the account:(Enter the number)");
									boolean select = false;
							
									_atm.printAccountList();
									String selection = s.nextLine();
									while(!select) {
										if(!selection.equals("q")) {
											try{
												_atm.setCurrentAccount(Integer.parseInt(selection));
												System.out.println("What do you want to do today:(Enter the number)");
												_atm.printAllowAction();
												String action = s.nextLine();
												if(!action.equals("b"))  {
													_atm.makeTransaction(Integer.parseInt(action),s);
												}else {
													select = true;
												}
												r = app.printTransactionTable()	;
											}catch(java.lang.NumberFormatException e) {
												System.out.println("Invalid input");
												select = true;
												
											}
										}else {
											select = true;
											back = true;
										}
									}
								}
							}
						}
						
						break;
						
					case "2": //Bank Teller T 
						//TODO:
						
						boolean b = false;
							BankTeller _bt = new BankTeller(app.getConnection(), app);
						
								while(!b) {
									System.out.println("Please select the Action:(Enter the number)");
									boolean select = false;
									System.out.println("(1)Enter Check Transaction");
									System.out.println("(2)DTER");
									System.out.println("(3)Customer Report:");
									System.out.println("(q)Quit the interface");
									String selection = s.nextLine();
									if(selection.equals("q")) {
										b = true;
									}else {
										_bt.makeSelection(s,selection);
									}
									
									
								}
							
						
						
						
						
						
						
					
						
						break;
						
					case "3":
						System.out.println("Enter year:");
						String y = s.nextLine();
						System.out.println("Enter month:");
						String m = s.nextLine();
						System.out.println("Enter date:");
						String d = s.nextLine();
						try{
							int year = Integer.parseInt(y);
							int month = Integer.parseInt(m);
							int day = Integer.parseInt(d);
							app.setDate(year,month,day);
						}catch(java.lang.NumberFormatException e) {
							System.out.println("Error. You might enter some invalid input. ");
						}
						break;
					default:
						System.out.println("Invalid option.");
				
				}
			}
		}
	
	
	
	
	}









}