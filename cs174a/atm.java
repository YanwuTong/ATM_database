package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.util.Date;
import java.util.*;

public class atm {
	
	private OracleConnection _connection;
	private Customer currentUser;
	private ArrayList<Account> a_list = new ArrayList<Account>();
	private Account currentAccount;
	private boolean correctPin = false;
	private App app;
	public atm(OracleConnection _connection,String tin, String PIN, App app) {
		this._connection = _connection;
		this.app = app;
		String accountId = null;
		String FIND_CUSTOMER_SQL= "select * from Customer where tnum = '" + tin + "'";
		
		
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement
						.executeQuery(FIND_CUSTOMER_SQL )) {
				
				while (rs.next() && !correctPin){
					
					String tnum = rs.getString("tnum");
					String cname = rs.getString("cname");
					String address  = rs.getString("address");
					int pin = rs.getInt("pin");
					String aid = rs.getString("aid");
					//System.out.println(tnum + "" + pin);
					ArrayList<String> account_list = new ArrayList<String>();
					//account_list.add(aid);
					Customer c = new Customer(cname,tnum,address,account_list);
					c.setPin(1717,pin);
					if(c.vertifyPin(Integer.parseInt(PIN))) {
						
						correctPin = true;
						System.out.println("-------");
						System.out.println("Welcome");
						System.out.println("-------");
						currentUser = c;
						
						//accountId = aid;
					}

				}
			}catch(SQLException e) {
				System.out.println(e.getMessage());
						
			}
			if(correctPin) {
				String t = currentUser.getTID();
				t = t. replaceAll("\\s","");
				String sql = "select * from Account A, (select aid from AccountList where tnum = '" + t +"') R " 
							+ "where A.aid = R.aid";
				//System.out.println(sql);
				try (ResultSet r = statement
						.executeQuery(sql )) {
							//System.out.println("enter");
					while(r.next()) {
						
						
						
							String id = r.getString("aid");
							String bbn = r.getString("bbn");
							String type = r.getString("type");
							double balance = r.getDouble("balance");
							String status = r.getString("closed");
							ArrayList<Customer> owner = new ArrayList<Customer>();
							owner.add(currentUser);
							//System.out.println(id + " " + bbn + " " + type + " ");
							type = type. replaceAll("\\s","");
							if(type.equals("Student_Checking")) {
								//System.out.println(id + " " + bbn + " " + type + " " );
								student_checking_account a = new student_checking_account(id,bbn,"Student_Checking", status, balance, currentUser.getTID().trim(), false, currentUser);
								a_list.add(a);
							}
							
							if(type.equals("Interest_Checking")) {
									//System.out.println(id + " " + bbn + " " + type + " ");
								interest_checking_account a = new interest_checking_account(id,bbn,"Interest_Checking", status, balance, currentUser.getTID().trim(), false, currentUser);
								a_list.add(a);
							}
							if(type.equals("Savings")) {
									//System.out.println(id + " " + bbn + " " + type + " ");
								saving_account a = new saving_account(id,bbn,"Savings", status, balance, currentUser.getTID().trim(), false, currentUser);
								a_list.add(a);
							}
							if(type.equals("Pocket")) {
									//System.out.println(id + " " + bbn + " " + type + " ");
								String q = "select * from Pocket_Link where aid = '" + id +"'";
								
								
	
								pocket_account a = new pocket_account(id,bbn,"Pocket", status, balance, currentUser.getTID().trim(), false, currentUser);
								a_list.add(a);
							}
							
						}
						//System.out.println(a_list.size());
						
						
						
						
					}catch(SQLException e) {
					System.out.println(e.getMessage());
							
				}
			
			}else {
				System.out.println("Invalid ID or PIN.");
			}			
					
					
						
				
				
			
			
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		
	}
	public boolean getCorrectPin() {
		return correctPin;
	}
	
	public void printAccountList() {
		int i = 0;
		for(; i < a_list.size(); i++) {
		
			System.out.println("(" + (i+1) +")" + a_list.get(i).getAid() + "  " + a_list.get(i).getType() + " Account");
		
		
		}
		
		System.out.println("(q)Quit the interface " );
	
	
	
	}
	
	public void printAccountInformation() {
		String sql = "select * from Account where aid = '" + currentAccount.getAid().trim()+"'";
		try(Statement statement = _connection.createStatement()){
			try (ResultSet rs = statement.executeQuery(sql)) {
				System.out.println("-------------");
				System.out.println("Your Accounts");
				System.out.println("-------------");
				System.out.println("ID" + "   			"+"BANK-BRANCH-NAME"+"    		"+ "TYPE"  + "  "+ "BALANCE" + "	" + "Status");
				while(rs.next()) {
					String aid = rs.getString("aid");
					String bbn = rs.getString("bbn");
					String type = rs.getString("type");
					double balance = rs.getDouble("balance");
					String status = rs.getString("closed");
				
					System.out.println(aid+"   "+bbn+"    "+ type +"  "+ balance+"  " + status);
					
				}
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	
	
	
	
	}
	
	
	public void setCurrentAccount(int selection) {
		currentAccount = a_list.get(selection-1);
		
	}
	
	public void printAllowAction() {
		
		String type = currentAccount.getType();
		printAccountInformation();
		switch(type) {
		
			case "Student_Checking":
				System.out.println("(1)Deposit");
				System.out.println("(2)Withdrawal");
				System.out.println("(3)Transfer");
				System.out.println("(4)Wire");
				System.out.println("(5)Write-check");
				System.out.println("(b)Back to Account Selection");
			
			
				break;
			
			case "Interest_Checking":
				System.out.println("(1)Deposit");
				System.out.println("(2)Withdrawal");
				System.out.println("(3)Transfer");
				System.out.println("(4)Wire");
				System.out.println("(5)Write-check");
				//System.out.println("(6)Accure-interest");
				System.out.println("(b)Back to Account Selection");
				break;
			
			case "Savings": 
				System.out.println("(1)Deposit");
				System.out.println("(2)Withdrawal");
				System.out.println("(3)Transfer");
				System.out.println("(4)Wire");
				//System.out.println("(5)Accure-interest");
				System.out.println("(b)Back to Account Selection");
				break;
			case "Pocket":
				System.out.println("(1)Top_up");
				System.out.println("(2)Purchase");
				System.out.println("(3)Collect");
				System.out.println("(4)Pay_Friend");
				System.out.println("(b)Back to Account Selection");
				break;
		
		}
	
	}


	public void makeTransaction(int action,Scanner s) {
		String type = currentAccount.getType();
		String status = currentAccount.getStatus();
		//System.out.println("'" + status.trim() + "'");
		if(status.trim().equals("open")) {
			if(type.equals("Interest_Checking") || type.equals("Student_Checking")) {
				switch(action) {
				
				case 1:
					//Deposit
					deposit_action(s);
				
					break;
				case 2:
					//withdrawal
					withdrawal_action(s);
					break;
				case 3:
					transfer_action(s);
					break;
				case 4:
					wire_action(s);
					break;
				case 5:
					write_check_action(s);
					break;
				
				}
			
			}else if(type.equals("Savings")){
				switch(action) {
				
				case 1:
					//Deposit
					deposit_action(s);
				
					break;
				case 2:
					//withdrawal
					withdrawal_action(s);
					break;
				case 3:
					transfer_action(s);
					break;
				case 4:
					wire_action(s);
					break;
				
				}
			
			
			}else {
			//POCKET ACCOUNT
				switch(action) {
				
				case 1:
					//Deposit
					top_up_action(s);
				
					break;
				case 2:
					purchase_action(s);
					break;
				
				case 3:
					collect_action(s);
					break;
					
				case 4:
					pay_friend_action(s);
					break;
			
				}
			
			
			}
		}else {
			System.out.println("This account is currently closed. Can't make any transactions.");
		}
	
	}
	
	//Transaction action:
	
	private void update_balance_single(String aid, double balance) {
		/////
		
						//UPDATE BALANCE TABLE SQL
					try(Statement statement = _connection.createStatement()){
						boolean found = false;
						String SEARCH_BALANCE_SQL = "select * from BalanceTable where aid = '" + aid.trim() + "' AND d = '" + app.getDate().trim() + "'";
						try (ResultSet rs = statement.executeQuery(SEARCH_BALANCE_SQL )) {	
							while(rs.next()) {
								found = true;

							}

						}
						if(found) {
							String UPDATE_BALANCE_SQL =  "UPDATE BalanceTable SET balance = '" + balance + "' where aid = '" + aid.trim() + "'";
							statement.executeUpdate(UPDATE_BALANCE_SQL);
						}else {
							String INSERT_BALANCE_SQL = "INSERT INTO BalanceTable(d,aid,balance) VALUES ('" + app.getDate().trim() + "','" + aid.trim() + "','" + balance + "')";
							statement.executeUpdate(INSERT_BALANCE_SQL);
						}
					}catch(SQLException e) {
						System.out.println(e.getMessage());
					}
						/////
	}

	private void update_balance_Double(String aid1, double balance1,String aid2, double balance2) {
				try(Statement statement = _connection.createStatement()){
					boolean found1 = false;
					String SEARCH_BALANCE_SQL = "select * from BalanceTable where aid = '" + aid1.trim() + "' AND d = '" + app.getDate().trim() + "'";
					try (ResultSet rs = statement.executeQuery(SEARCH_BALANCE_SQL )) {	
						while(rs.next()) {
							found1= true;

						}
					}

					boolean found2 = false;
					SEARCH_BALANCE_SQL = "select * from BalanceTable where aid = '" + aid2.trim() + "' AND d = '" + app.getDate().trim() + "'";
					try (ResultSet rs = statement.executeQuery(SEARCH_BALANCE_SQL )) {	
						while(rs.next()) {
							found2 = true;

						}
					}
				
				if(found2) {
					String UPDATE_BALANCE_SQL =  "UPDATE BalanceTable SET balance = '" + balance2 + "' where aid = '" + aid2.trim() + "'";
					statement.executeUpdate(UPDATE_BALANCE_SQL);
				}else {
					String INSERT_BALANCE_SQL = "INSERT INTO BalanceTable(d,aid,balance) VALUES ('" + app.getDate().trim() + "','" + aid2.trim() + "','" + balance2 + "')";
					statement.executeUpdate(INSERT_BALANCE_SQL);
				}


				if(found1) {
					
					String UPDATE_BALANCE_SQL =  "UPDATE BalanceTable SET balance = '" + balance1 + "' where aid = '" + aid1.trim() + "'";
					statement.executeUpdate(UPDATE_BALANCE_SQL);
				}else {
					String INSERT_BALANCE_SQL = "INSERT INTO BalanceTable(d,aid,balance) VALUES ('" + app.getDate().trim() + "','" + aid1.trim()+ "','" + balance1 + "')";
					statement.executeUpdate(INSERT_BALANCE_SQL);
					
				}
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
	}


	private void deposit_action(Scanner s) {
				System.out.println("Enter the amount: ");
				String amount = s.nextLine();
				
				String result = currentAccount.deposit(Double.parseDouble(amount));
				if(result.equals("0")) {
					String name = currentUser.getName();
					//name = name. replaceAll("\\t","");
					
					
					String aid = currentAccount.getAid();
					aid = aid. replaceAll("\\s","");
					
					String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
									+ aid + "'";
									//System.out.println(UPDATE_SQL);
					String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,t,d)" 
									+ "VALUES ('" + name +"','" + "deposits" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + app.getDate() +"')";
					try(Statement statement = _connection.createStatement()){
						statement.executeUpdate(UPDATE_SQL);
						statement.executeUpdate(INSERT_TRANSACTION_SQL);
						System.out.println("Deposit Successfully");
						System.out.println("Record of this transaction:");
						System.out.println( name.trim()+ " deposits $" + amount + " to account " + currentAccount.getAid());
						check_if_closed(currentAccount.getBalance());
						update_balance_single(aid,currentAccount.getBalance());
					}catch(SQLException e) {
						System.out.println(e.getMessage());
					}
					
					

					
				}else {
					System.out.println("Invalid transaction");
				}
	}
	
	
	
	private void withdrawal_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
		
		String result = currentAccount.withdrawal(Double.parseDouble(amount));
		if(result.equals("0")) {
			
			String name = currentUser.getName();
			name = name.trim();
			
			String aid = currentAccount.getAid();
			aid = aid. replaceAll("\\s","");
			String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
									+ aid + "'";
									//System.out.println(UPDATE_SQL);
			String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,d)" 
									+ "VALUES ('" + name +"','" + "withdrawals" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid +  "','" + app.getDate()+"')";
			try(Statement statement = _connection.createStatement()){
				statement.executeUpdate(UPDATE_SQL);
				statement.executeUpdate(INSERT_TRANSACTION_SQL);
			update_balance_single(aid,currentAccount.getBalance());





			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			System.out.println("Withdrawals Successfully");
			System.out.println("Record of this transaction:");
			System.out.println( name+ " withdrawals $" + amount + " from account " + currentAccount.getAid());
			check_if_closed(currentAccount.getBalance());
		





		}else {
			System.out.println("Invalid transaction");
		}
		
	}
	
	private void transfer_action(Scanner s) {
		
		System.out.println("Select the account id which you want to transfer to: ");
		int count = 1;
		ArrayList<Account> list = new ArrayList<Account>();
		for(int i = 0;i < a_list.size(); i++) {
			if(!a_list.get(i).getType().equals("Pocket") && !a_list.get(i).getAid().trim().equals(currentAccount.getAid().trim())) {
				System.out.println("(" + (count) +")" + a_list.get(i).getAid() + "  " + a_list.get(i).getType() + " Account");
				list.add(a_list.get(i));
				count++;
			}
		
		}
	
		String sel = s.nextLine();
		int select = Integer.parseInt(sel);
		
		if(select> 0 && select <=count-1 ){
			String target = list.get(select-1).getAid().trim();
			
			System.out.println("Enter the amount: ");
			String amount = s.nextLine();
			
			for(int i = 0 ; i < a_list.size(); i++) {
				if(a_list.get(i).getAid().trim().equals(target.trim()) && !a_list.get(i).getAid().trim().equals(currentAccount.getAid().trim())) {
					if(!check_closed(target)) {
					String result = currentAccount.transfer(Double.parseDouble(amount));
					if(result.equals("0")) {
						String name = currentUser.getName();
						String tname = "";
							
							
						String aid = currentAccount.getAid();
						String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
										+ aid.trim() + "'";
										//System.out.println(UPDATE_SQL);
						String UPDATE_SQL1 = "UPDATE Account SET balance = '" + (a_list.get(i).getBalance() + Double.parseDouble(amount)) + "' where aid = '" 
										+ target.trim() + "'";
										//System.out.println(UPDATE_SQL1);
						String FIND_NAME_SQL = "select * from Customer C, (select owner from Account where aid = '" + target.trim() + "') B where C.tnum = B.owner";
						
						try(Statement statement = _connection.createStatement()){
							try (ResultSet rs = statement
								.executeQuery(FIND_NAME_SQL )) {
					
								while(rs.next()) {
								tname = rs.getString("cname");
							
								}
							}
							statement.executeUpdate(UPDATE_SQL);
							statement.executeUpdate(UPDATE_SQL1);
							a_list.get(i).setBalance(a_list.get(i).getBalance() + Double.parseDouble(amount));
							String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
										+ "VALUES ('" + name +"','" + "transfers" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + target +  "','" + app.getDate()+"')";
							String INSERT_TRANSACTION_SQL1 = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
										+ "VALUES ('" + tname +"','" + "transfers_from" + "','" + Double.parseDouble(amount) + "','" + target + "','" + target + "','" + aid +  "','" + app.getDate()+"')";
							statement.executeUpdate(INSERT_TRANSACTION_SQL);
							statement.executeUpdate(INSERT_TRANSACTION_SQL1);
							
						}catch(SQLException e) {
							System.out.println(e.getMessage() );
						}
						update_balance_Double(aid,(currentAccount.getBalance() ),target,(a_list.get(i).getBalance() + Double.parseDouble(amount)) );
						System.out.println("Transfers Successfully");
						System.out.println("Record of this transaction:");
						System.out.println( name.trim()+ " transfers $" + Double.parseDouble(amount) + " from account " + currentAccount.getAid().trim() +  " to Account " + target);
						check_if_closed(currentAccount.getBalance());
					}else {
						System.out.println("Invalid Transaction");
					}
					}else {
						System.out.println("Target Account currently closed.");
						System.out.println("Transaction Failed");
					}
				}
			}
	
		}
	
	}
	
	private void wire_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
		System.out.println("Enter the account id which you want to wire to: ");
		String target = s.nextLine();
		
		//TODO: CHECK IF AID IS POCKET OR IF AID IS THE SAME OWNER OR IF AID IS THE SAME ACCOUNT 
		boolean collide = false;
		for(int i = 0 ; i < a_list.size();i++) {
			if(a_list.get(i).getAid().trim().equals(target)) {
				collide = true;
			}
		}
		String find = "select * from Account where aid = '" + target.trim() + "'";
		Account a = new Account(null,null,null,null,0,null,false,null);
		try(Statement statement = _connection.createStatement()){
				try (ResultSet rs = statement
							.executeQuery(find )) {
				while(rs.next()) {
					String aid = rs.getString("aid");
					String type = rs.getString("type");
					if(type.trim().equals("Pocket")) {
						collide = true;
					}
					
					
					
				}
			}
		}catch(SQLException e) {
			System.out.println();
		}
		
		
		
		
		
		if(!check_closed(target.trim())) {
		String result = "1";
		if(!collide) {
			result = currentAccount.wire(Double.parseDouble(amount));
		}
		
		if(result.equals("0")) {
		
			String name = currentUser.getName();
			//name = name. replaceAll("\\t","");
		
			
			String FIND_ACCOUNT_SQL = "select * from Account where aid = '" + target.trim() + "'";
			String FIND_NAME_SQL = "select * from Customer C, (select owner from Account where aid = '" + target.trim() + "') B where C.tnum = B.owner";
			try(Statement statement = _connection.createStatement()){
				double balance = 0;
				String tname = "";
				String aid = currentAccount.getAid();
					aid = aid. replaceAll("\\s","");
				try (ResultSet rs = statement
							.executeQuery(FIND_ACCOUNT_SQL )) {
				
				while(rs.next()) {
					
					balance = rs.getDouble("balance");
				}
					
					String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
							+ aid + "'";
									//System.out.println(UPDATE_SQL);
			
					String UPDATE_SQL1 = "UPDATE Account SET balance = '" + (balance + Double.parseDouble(amount)) + "' where aid = '" 
							+ target.trim() + "'";
									//System.out.println(UPDATE_SQL1);
			
					statement.executeUpdate(UPDATE_SQL);
					statement.executeUpdate(UPDATE_SQL1);
					
				}
				try (ResultSet rs = statement
							.executeQuery(FIND_NAME_SQL )) {
				
				while(rs.next()) {
					tname = rs.getString("cname");
					
				}
					
					String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
									+ "VALUES ('" + name +"','" + "wires" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + target +  "','" + app.getDate()+"')";
					String INSERT_TRANSACTION_SQL1 = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
									+ "VALUES ('" + tname +"','" + "wires_from" + "','" + Double.parseDouble(amount) + "','" + target + "','" + target + "','" + aid +  "','" + app.getDate()+"')";
					statement.executeUpdate(INSERT_TRANSACTION_SQL);
					statement.executeUpdate(INSERT_TRANSACTION_SQL1);
				}
				
				update_balance_Double(aid,(currentAccount.getBalance() ),target,(balance + Double.parseDouble(amount)));
				
				System.out.println("Wires Successfully");
				System.out.println("Record of this transaction:");
				System.out.println( name.trim()+ " wires $" + amount + " from account " + currentAccount.getAid() + " to Account " + target.trim() );
				check_if_closed(currentAccount.getBalance());
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}

		}else {
			System.out.println("Invalid transaction");
		}
		
		}else {
			System.out.println("Target Account currently closed.");
			System.out.println("Transaction Failed");
		}

	
	}
	
	
	private void write_check_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
				
		String result = currentAccount.write_check(Double.parseDouble(amount));
		if(result.equals("0")) {		
			String name = currentUser.getName();
			//name = name. replaceAll("\\t","");
					
					
			String aid = currentAccount.getAid();
			aid = aid. replaceAll("\\s","");
					
			String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
						+ aid + "'";
			System.out.println(UPDATE_SQL);
			String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,d)" 
						+ "VALUES ('" + name +"','" + "write_check" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + app.getDate()+ "')";
			
			String sql = "select * from CheckTable";
			try(Statement statement = _connection.createStatement()){
				statement.executeUpdate(UPDATE_SQL);
				statement.executeUpdate(INSERT_TRANSACTION_SQL);
					String checknum = null;
				try (ResultSet r = statement
							.executeQuery(sql )) {
				
					while(r.next()) {
						checknum = r.getString("checknum");
					}
					
					if(checknum != null) {
						System.out.println("here: " + Integer.parseInt(checknum.trim()));
						checknum = (Integer.parseInt(checknum.trim())+1) + "";
						
					}
					if (checknum == null){
						checknum = "0";
					}
				
				}catch(SQLException e) {
					System.out.println(e.getMessage());
				}
				String INSERT_CHECK_SQL = "INSERT INTO CheckTable (checknum,aid,balance) VALUES ('" + checknum + "','" +aid + "','" + amount + "')";
				statement.executeUpdate(INSERT_CHECK_SQL);
				System.out.println("Deposit Successfully");
				System.out.println("Record of this transaction:");
				System.out.println( name.trim()+ " writes a check in the amount of $" + amount + " from account " + currentAccount.getAid());
				check_if_closed(currentAccount.getBalance());

				update_balance_single(aid,(currentAccount.getBalance() ) );


			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}else {
			System.out.println("Invalid transaction");
		}
	
	
	}

	private void top_up_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
				
		
				
		String name = currentUser.getName();
		//name = name. replaceAll("\\t","");
		
		
				
		String aid = currentAccount.getAid();
		aid = aid. replaceAll("\\s","");
				
		
						//System.out.println(UPDATE_SQL);
		
		String FIND_LINK_SQL = "select * from Account A, (select linkedId from PocketLink where aid = '" + aid +"') B where A.aid = B.linkedId";
		String FIND_LINKID_SQL = "select * from PocketLink where aid = '" + aid +"'";
		System.out.println(FIND_LINKID_SQL);
		try(Statement statement = _connection.createStatement()){
			
			double balance = 0;
			String linkId = null;
			//Look for linkId 
			try (ResultSet r = statement
						.executeQuery(FIND_LINK_SQL )) {
				while(r.next()) {
					balance = r.getDouble("balance");
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			try (ResultSet r = statement
						.executeQuery(FIND_LINKID_SQL )) {
				while(r.next()) {
					linkId = r.getString("linkedId");
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			if(!check_closed(linkId)) {
			if(balance > Double.parseDouble(amount)) {
				
				String result = currentAccount.top_up(Double.parseDouble(amount));
				if(result.equals("0")) {
					String UPDATE_SQL1 = "UPDATE Account SET balance = '" + (balance - Double.parseDouble(amount)) + "' where aid = '" 
							+ linkId.trim() + "'";
					String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
							+ aid + "'";
					String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
							+ "VALUES ('" + name +"','" + "top_up" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + linkId + "','" + aid + "','" + app.getDate()+"')";
					statement.executeUpdate(UPDATE_SQL);
					statement.executeUpdate(UPDATE_SQL1);
					statement.executeUpdate(INSERT_TRANSACTION_SQL);
					System.out.println("Top-up Successfully");
					System.out.println("Record of this transaction:");
					System.out.println( name.trim()+ " top-ups $" + amount + " from account " + aid +  " to account " + linkId.trim());
					check_if_closed(currentAccount.getBalance());

					update_balance_Double(aid,(currentAccount.getBalance() ),linkId,(balance - Double.parseDouble(amount)) );

				}else {
					System.out.println("Invalid Transaction");
				}
			}else {
				System.out.println("Link account doesn't have enough balance to top-up.");
				System.out.println("Transaction failed. ");
			}
			}else {
				System.out.println("Target Account currently closed.");
				System.out.println("Transaction Failed");
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		 
		 
	}

	private void purchase_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
				
		
				
		String name = currentUser.getName();
		//name = name. replaceAll("\\t","");
		
		
		String result = currentAccount.purchase(Double.parseDouble(amount));
		if(result.equals("0")) {
			String aid = currentAccount.getAid();
			aid = aid. replaceAll("\\s","");
			
			String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
							+ aid + "'";
						//System.out.println(UPDATE_SQL);
			String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,d)" 
							+ "VALUES ('" + name +"','" + "purchases" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + app.getDate()+"')";
			try(Statement statement = _connection.createStatement()){
				statement.executeUpdate(UPDATE_SQL);
				statement.executeUpdate(INSERT_TRANSACTION_SQL);
				System.out.println("Deposit Successfully");
				System.out.println("Record of this transaction:");
				System.out.println( name.trim()+ " purchases $" + amount + " from account " + currentAccount.getAid());
				check_if_closed(currentAccount.getBalance());
				update_balance_single(aid,(currentAccount.getBalance() ));


			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}else {
		
			System.out.println("Invalid Transaction");
		}
				
	
	
	}

	private void collect_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
				
		
				
		String name = currentUser.getName();
		//name = name. replaceAll("\\t","");
		
		
				
		String aid = currentAccount.getAid();
		aid = aid. replaceAll("\\s","");
				
		
						//System.out.println(UPDATE_SQL);
		
		String FIND_LINK_SQL = "select * from Account A, (select linkedId from PocketLink where aid = '" + aid +"') B where A.aid = B.linkedId";
		String FIND_LINKID_SQL = "select * from PocketLink where aid = '" + aid +"'";
		System.out.println(FIND_LINKID_SQL);
		try(Statement statement = _connection.createStatement()){
			
			double balance = 0;
			String linkId = null;
			//Look for linkId 
			try (ResultSet r = statement
						.executeQuery(FIND_LINK_SQL )) {
				while(r.next()) {
					balance = r.getDouble("balance");
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			try (ResultSet r = statement
						.executeQuery(FIND_LINKID_SQL )) {
				while(r.next()) {
					linkId = r.getString("linkedId");
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			//System.out.println(linkId);
			if(!check_closed(linkId)) {
			String result = currentAccount.collect(Double.parseDouble(amount));
			if(result.equals("0")) {
				String UPDATE_SQL1 = "UPDATE Account SET balance = '" + (balance + Double.parseDouble(amount)) + "' where aid = '" 
						+ linkId.trim() + "'";
				String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
						+ aid + "'";
				String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
						+ "VALUES ('" + name +"','" + "collect" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + linkId + "','" + aid + "','" + app.getDate()+"')";
				statement.executeUpdate(UPDATE_SQL);
				statement.executeUpdate(UPDATE_SQL1);
				statement.executeUpdate(INSERT_TRANSACTION_SQL);
				System.out.println("Top-up Successfully");
				System.out.println("Record of this transaction:");
				System.out.println( name.trim()+ " collects $" + amount + " from account " + aid +  " to account " + linkId.trim());
				check_if_closed(currentAccount.getBalance());
				update_balance_Double(aid,(currentAccount.getBalance() ),linkId,(balance + Double.parseDouble(amount)));
				
			}else {
				System.out.println("Invalid transaction");
			}
			}else {
				System.out.println("Target Account currently closed.");
				System.out.println("Transaction Failed");
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void pay_friend_action(Scanner s) {
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
		System.out.println("Enter the account id which you want to wire to: ");
		String target = s.nextLine();
		
		String aid = currentAccount.getAid();
		aid = aid. replaceAll("\\s","");
		String name = currentUser.getName();
		
		//Check if target id exist and is pocket account
		String type = null;
		double balance = 0;
		String FIND_TARGETACCOUNT_SQL = "select * from Account where aid = '" + target.trim() +"'";
		try(Statement statement = _connection.createStatement()){
			try (ResultSet rs = statement.executeQuery(FIND_TARGETACCOUNT_SQL)) {
				while(rs.next()) {
					
					type = rs.getString("type");
					balance = rs.getDouble("balance");
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			if(!check_closed(target.trim())) {
			if(type.trim().equals("Pocket")) {
			
				String result = currentAccount.pay_friend(Double.parseDouble(amount));
				if(result.equals("0")) {
					String UPDATE_SQL = "UPDATE Account SET balance = '" + (currentAccount.getBalance() ) + "' where aid = '" 
							+ aid + "'";
									//System.out.println(UPDATE_SQL);
			
					String UPDATE_SQL1 = "UPDATE Account SET balance = '" + (balance + Double.parseDouble(amount)) + "' where aid = '" 
							+ target.trim() + "'";
									//System.out.println(UPDATE_SQL1);
					statement.executeUpdate(UPDATE_SQL);
					statement.executeUpdate(UPDATE_SQL1);
					String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
									+ "VALUES ('" + name +"','" + "pay_friend" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + target + "','" + app.getDate()+ "')";
					statement.executeUpdate(INSERT_TRANSACTION_SQL);
					System.out.println("Wires Successfully");
					System.out.println("Record of this transaction:");
					System.out.println( name.trim()+ " pay-friends $" + amount + " from account " + aid.trim() + " to Account " + target.trim() );
					check_if_closed(currentAccount.getBalance());
					update_balance_Double(aid,(currentAccount.getBalance() ),target,(balance + Double.parseDouble(amount)));
				}else {
					System.out.println("Invalid Transaction");
				}
			
			}
			}else {
			System.out.println("Target Account currently closed.");
			System.out.println("Transaction Failed");
		}
		
		
		
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
		
	
	}

	private boolean check_closed(String aid) {
		String sql = "select * from Account where aid = '" + aid.trim() + "'"; 
		try(Statement statement = _connection.createStatement()){
				try(ResultSet rs = statement.executeQuery(sql)) {
					while(rs.next()) {
						String closed = rs.getString("closed");
						if(closed.trim().equals("closed")) {
							return true;
						}
					}
				}
				return false;
			}catch(SQLException e) {
				System.out.println(e.getMessage());
				return false;
			}	
	}


	private void check_if_closed(double balance) {
		if(balance <= 0) {
			
			//String sql = "select * from Account where aid = '" + currentAccount.getAid().trim() +"'";
			
			String UPDATE_SQL = "UPDATE Account SET closed = 'closed' where aid = '" + currentAccount.getAid().trim() +"'"; 
							
			currentAccount.setStatus("closed");
			try(Statement statement = _connection.createStatement()){
				statement.executeUpdate(UPDATE_SQL);
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}	
		
			
		
		
		}
	}
}