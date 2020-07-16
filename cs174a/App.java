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
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	 private String bbn;
	 private Customer currentUser;
	 private int a = 0;
	 private String date = null;
	 
	 
	App(String bbn)
	{
		// TODO: Any actions you need.
		this.bbn = bbn;
	}
	
	public OracleConnection getConnection() {
		return _connection;
	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = _connection.createStatement() )
		{
		    try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables where owner = 'C##XINHAOXIAO'" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##xinhaoxiao";
		final String DB_PASSWORD = "4594529";

		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			_connection = (OracleConnection) ods.getConnection();

			// Get the JDBC driver name and version.
			DatabaseMetaData dbmd = _connection.getMetaData();
			System.out.println( "Driver Name: " + dbmd.getDriverName() );
			System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			System.out.println( "Database Username is: " + _connection.getUserName() );
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");  
			//LocalDateTime now = LocalDateTime.now();  
			//date = dtf.format(now);
			//System.out.println("Date: " + date);
			//System.out.println();

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

    /**
     * Example of one of the testable functions.
     */
    
    /**
     * Another example.
     */
	 
	 //setPin for currentUser
	public String setPin(String tin, int oldPin, int newPin) {
		 String FIND_PIN_SQL = "select * from Customer where pin = '" + oldPin + "'";
		  
		
		 boolean found = false;
		String tnum = null;
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement.executeQuery(FIND_PIN_SQL )) {
				while(rs.next()) {
					if(tin.equals(rs.getString("tnum").trim())) {
						//tnum = currentUser.getTID().trim();
						found = true;
					}
				}
			}
			if(found) {
				String UPDATE_PIN_SQL = "UPDATE Customer SET pin = '" + newPin+"' where tnum = '" +tin +"'";
				statement.executeUpdate(UPDATE_PIN_SQL);
			}else {
				System.out.println("Id number and old pin does not match. ");
				return "1";
			}
			
			return "0";
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		
		
	 
	 
	 }
	 
	 
	 
	 public String addOwner(String accountId, String tin, String name, String address) {
		 
		String FIND_CUSTOMER_SQL = "select * from Account where aid = '" + accountId + "'";
		boolean exist = false;
		boolean collide = false;
		String pri_owner = null;
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement.executeQuery(FIND_CUSTOMER_SQL)){
				while(rs.next()) {
					pri_owner = rs.getString("owner");
					if(pri_owner.trim().equals(name)) {
						collide = true;
					}
					exist = true;
				}
			}
			if(exist) {
				
				String FIND_COOWNER_SQL = "select * from  co_owner where tnum = '" + tin + "' AND aid = '" + accountId +"'";
				try (ResultSet rs = statement.executeQuery(FIND_COOWNER_SQL)){
					while(rs.next()) {
						collide = true;
					}
				}
				if(!collide) {
					String INSERT_COOWNER_SQL = "INSERT INTO co_owner(tnum,address,pin,aid,cname)" 
					    + "VALUES ('" + tin +"','" + address + "','" + 1717 + "','" + accountId + "','" + name + "')";
						
					statement.executeUpdate(INSERT_COOWNER_SQL);
					createCustomer( accountId, tin, name,address);
					
				}else {
					System.out.println("Owner Already added before. Failed to Add owner");
					return "1";
				}
				
			}else {
				System.out.println("Account does not exist. Failed to Add owner");
				return "1";
			}
			return "0";
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		 
		 
		
	 
	 
	 
	}
	 
	 
	 
    @Override
    public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
    {
		
		if(accountType == AccountType.STUDENT_CHECKING) {
			ArrayList<Customer> owner = new ArrayList<Customer>();
			
			student_checking_account a = new student_checking_account(id,bbn,"Student_Checking", "closed", initialBalance, tin, false, currentUser);
			
			insertAccount(id, bbn, a.getType(),a.getRate(), initialBalance,"open",tin);
			if(name != null) {
				createCustomer(id,tin,name,address);
			}
			deposit(id,initialBalance);
			owner.add(currentUser);
			currentUser.addAccountToList(id);
			
			
		}
		
		if(accountType == AccountType.INTEREST_CHECKING) {
			ArrayList<Customer> owner = new ArrayList<Customer>();
			
			interest_checking_account a = new interest_checking_account(id,bbn,"Interest_Checking", "closed", initialBalance, tin, false, currentUser);
			
			insertAccount(id, bbn, a.getType(),a.getRate(), initialBalance,"open",tin);
			//System.out.println(name);
			if(name != null) {
				createCustomer(id,tin,name,address);
			}	
			deposit(id,initialBalance);
			owner.add(currentUser);
			currentUser.addAccountToList(id);
		}
		
		if(accountType == AccountType.SAVINGS) {
			ArrayList<Customer> owner = new ArrayList<Customer>();
			
			saving_account a = new saving_account(id,bbn,"Savings", "closed", initialBalance, tin, false, currentUser);
			
			insertAccount(id, bbn, a.getType(),a.getRate(), initialBalance,"open",tin);
			if(name != null) {
				createCustomer(id,tin,name,address);
			}	
			deposit(id,initialBalance);
			owner.add(currentUser);
			currentUser.addAccountToList(id);
		}
		
		
		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
    }


    @Override
    public String dropTables() {
	
	String DROP_Transaction_SQL="DROP TABLE Transaction";
	String DROP_Account_SQL = "DROP TABLE Account";
	String DROP_Customer_SQL = "DROP TABLE Customer";
	String DROP_SavingAccount_SQL = "DROP TABLE SavingAccount";
	String DROP_CheckingAccount_SQL = "DROP TABLE CheckingAccount";
	String DROP_PocketAccount_SQL = "DROP TABLE PocketAccount";
	String DROP_AccountList_SQL = "DROP TABLE AccountList";
	String DROP_Chcek_SQL = "DROP TABLE CheckTable";
	String DROP_POCKETLINK_SQL = "DROP TABLE PocketLink";
	String DROP_COOWNER_SQL = "DROP TABLE Co_owner";
	String DROP_INTERESTRECORD_SQL = "DROP TABLE InterestRecord";
	String DROP_BALANCETABLE_SQL = "DROP TABLE BalanceTable";

	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(DROP_Transaction_SQL);
		statement.executeQuery(DROP_Customer_SQL);
		statement.executeQuery(DROP_COOWNER_SQL);
		statement.executeQuery(DROP_BALANCETABLE_SQL);
		statement.executeQuery(DROP_POCKETLINK_SQL);
		statement.executeQuery(DROP_INTERESTRECORD_SQL);
	    statement.executeQuery(DROP_AccountList_SQL);
		statement.executeQuery(DROP_Account_SQL);
	    //statement.executeQuery(DROP_SavingAccount_SQL);
		//statement.executeQuery(DROP_CheckingAccount_SQL);
		//statement.executeQuery(DROP_PocketAccount_SQL);
		statement.executeQuery(DROP_Chcek_SQL);
		
		
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
       
	return "0";

    }

    @Override
    public String createTables() {
	String r1 =createAccountTable();
	String r4 = createPocketLinkTable();
	String r5 = createCustomerTable();
	String r6 = createInterestRecordTable();
	String r2 = createCoownerTable();
	//String r4 = createSavingAccountTable();
	String r3 = createAccountListTable();
	//String r5 = createCheckingAccountTable();
	//String r6 = createPocketAccountTable();
	String r7 = createTransactionTable();
	String r8 = createCheckTable();
	String r9 = createBalanceTable();
	//String r9 = "";
	//System.out.println(r4);
	if(r1.equals(r2) && r1.equals(r3) &&  r1.equals(r4) && r1.equals(r5) && r1.equals(r6) && r1.equals(r7) && r1.equals(r8)&& r1.equals(r9) && r1.equals("0")) {
	    return "0";
	}else {
	    return "1";
	}
       
    }

    

    @Override
    public String createPocketAccount(String id, String linkedId, double initialTopUp, String tin) {
		ArrayList<Customer> owner = new ArrayList<Customer>();
			
		pocket_account a = new pocket_account(id,  bbn, "Pocket", "closed", initialTopUp,tin, false,currentUser);
			
		insertAccount(id, bbn, "Pocket",a.getRate(), initialTopUp,"open",tin);
		
		String sql  = "INSERT INTO PocketLink(linkedId,aid) VALUES ('" + linkedId + "','" + id + "'" + ")";
		try(Statement statement = _connection.createStatement()){
			statement.executeUpdate(sql);
			String INSERT_ACCOUNTLIST_SQL = "INSERT INTO AccountList (tnum, aid)"
				+ "VALUES (" + "'" + tin + "'" + "," + "'" + id +"'"  + ")";
			
				
			statement.executeUpdate(INSERT_ACCOUNTLIST_SQL);	
			String s = "select *from Account where aid = '" + id + "'";
			String name = null;
			boolean found = false;
			try (ResultSet rs = statement.executeQuery(s )) {
				while (rs.next() ){				
					String tid =  rs.getString("owner");
					if(tid == tin) {
						found = true;
					}
				}
			}
			
			if(found = true) {
				s = "select * from Customer where tnum = '" +tin + "'"; 
				try (ResultSet rs = statement.executeQuery(s )) {
					while (rs.next() ){				
						name =  rs.getString("cname");
					}
				}
			}else {
				s = "select * from co_owner where aid = '"  + id + "'"; 
				try (ResultSet rs = statement.executeQuery(s )) {
					while (rs.next() ){			
						String tid = rs.getString("tnum");
						if(tid == tin) {
							name =  rs.getString("cname");
						}
					}
				}
			
			}
			
			String address = null;
			ArrayList<String> account_list = new ArrayList<String>();
			Customer c = new Customer(name,tin,address,account_list);
			currentUser = c;
		
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		
		
		topUp(id, initialTopUp);
		owner.add(currentUser);
		currentUser.addAccountToList(id);
		return "0";
	
    }
	

    @Override
    public String createCustomer(String accountId, String tin, String name, String address) {
		boolean found = false;
		try(Statement statement = _connection.createStatement()){
			String sql = "select * from Customer where cname = '" + name + "'";
			try (ResultSet rs = statement
			.executeQuery(sql )) {
				while (rs.next() && !found){				
					found = true;
	  
				}
			}catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
		}catch(SQLException e) {
			return "1";
		}
		
		
		if(!found) {
			ArrayList<String> account_list = new ArrayList<String>();
			//account_list.add(accountId);
			Customer c = new Customer(name,tin,address,account_list);
			currentUser = c;
			
			insertCustomer(tin,address,c.getPIN(), name, accountId);
			return "0";
		}else {
			
			//System.out.println("enter");
			try(Statement statement = _connection.createStatement()){
				String INSERT_ACCOUNTLIST_SQL = "INSERT INTO AccountList (tnum, aid)"
			+ "VALUES (" + "'" + tin + "'" + "," + "'" + accountId +"'" +  ")";
				
				statement.executeUpdate(INSERT_ACCOUNTLIST_SQL);	
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			
			return "0";
		}
		//return "1";
		
    }
	
	@Override
    public String listClosedAccounts()
    {
		String FIND_CLOSEDACCOUNT_SQL = "select * from Account where closed = 'closed'";
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement.executeQuery(FIND_CLOSEDACCOUNT_SQL )) {
				
				System.out.println("List closed account:");
				while(rs.next()) {
					String aid = rs.getString("aid");
					double balance = rs.getDouble("balance");
					String status = rs.getString("closed");
					
					System.out.println(aid + "		" + balance + "		" + status);
				}
			 }catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			 
			 return "0";
				
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
	return "0 it works!";
    }
	
	
	
	@Override
    public String setDate(int year, int month, int day) {
		if(date == null) {
			date = month + "-" + day + "-" + year; 
		}else {
			String[] arr = date.split("-", 3);
			int m = Integer.parseInt(arr[0]);
			int y = Integer.parseInt(arr[2]);
			
			//String m1 = String.format("%02d", month+ "");
			if(month == m) {
				date = month + "-" + day + "-" + y; 
			}else {
				if(m== 2) {
					if(year%4 == 0) {
						date = m + "-29-" + y; 
					}else {
						date = m + "-28-" + y; 
					}
				}else if(m== 1 ||
							m== 3 ||
							m== 5 ||
							m== 7 ||
							m== 8 ||
							m== 10 ||
							m== 12 ){
								
					date = m+ "-31-" + y; 			
				
				}else {
					date = m + "-30-" + y; 
				}
			}
		}
		//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");  
		//LocalDateTime now = LocalDateTime.now();  
		//date = dtf.format(now);
		System.out.println("Current System Date: " + date);
		System.out.println();
		return "0";
    }


    @Override
    public String deposit(String accountId, double amount) {
		
		String FIND_ACCOUNT_SQL = "select * from Account where aid = '" + accountId + "'";
		double balance = 0;
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement.executeQuery(FIND_ACCOUNT_SQL )) {	
				while(rs.next()) {
					balance = rs.getDouble("balance");

				}

			}
			/////
			//UPDATE BALANCE TABLE SQL
			boolean found = false;
			String SEARCH_BALANCE_SQL = "select * from BalanceTable where aid = '" + accountId + "' AND d = '" + date + "'";
			try (ResultSet rs = statement.executeQuery(SEARCH_BALANCE_SQL )) {	
				while(rs.next()) {
					found = true;

				}

			}
			if(found) {
				String UPDATE_BALANCE_SQL =  "UPDATE BalanceTable SET balance = '" + (balance+amount) + "' where aid = '" + accountId + "'";
				statement.executeUpdate(UPDATE_BALANCE_SQL);
			}else {
				String INSERT_BALANCE_SQL = "INSERT INTO BalanceTable(d,aid,balance) VALUES ('" + date + "','" + accountId + "','" + (balance+amount) + "')";
				statement.executeUpdate(INSERT_BALANCE_SQL);
			}
			/////

			String UPDATE_SQL = "UPDATE Account SET balance = '" + (balance + amount ) + "' where aid = '" 
									+ accountId + "'";
			statement.executeQuery(UPDATE_SQL );
			String TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,t,d)" 
									+ "VALUES ('" + currentUser.getName().trim() +"','" + "deposits" + "','" + amount + "','" + accountId + "','" + accountId + "','" + date +"')";
			statement.executeQuery(TRANSACTION_SQL );
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		
		
	return "";
    }

    @Override
    public String showBalance(String accountId) {
		String FIND_BALANCE_SQL = "select balance from Account where aid = '"+accountId + "'";
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement.executeQuery(FIND_BALANCE_SQL )) {
				while(rs.next()) {
					double balance = rs.getDouble("balance");
					System.out.println(accountId + " information: ");
					System.out.println(accountId + "		" + balance );
				}
			 }catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			 
			 return "0";
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
		return "1";
    }

    @Override
    public String topUp(String accountId, double amount) {
		String aid = accountId;
		
		try(Statement statement = _connection.createStatement()){
			
			double balance = 0;
			String linkId = null;
			Account currentAccount = new Account(null,null,null,null,0,null,false,null);
			String name = currentUser.getName();
			//Look for linkId 
			String FIND_LINK_SQL = "select * from Account A, (select linkedId from PocketLink where aid = '" + aid +"') B where A.aid = B.linkedId";
			String FIND_LINKID_SQL = "select * from PocketLink where aid = '" + aid +"'";
			String FIND_POCKET_SQL = "select * from Account A where aid = '" + aid + "'";
			
			//System.out.println(FIND_LINKID_SQL);
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
			String sql = "select * from Customer where tnum = '" + linkId +"'";
			double b = 0;
			try (ResultSet r = statement
						.executeQuery(FIND_POCKET_SQL )) {
				while(r.next()) {
					//String aid = r.getString("aid");
					String bbn = r.getString("bbn");
					String closed = r.getString("closed");
					 b = r.getDouble("balance");
					String owner = r.getString("owner");
					
					currentAccount = new Account(aid, bbn, "Pocket", closed, b, owner, false, currentUser);
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			/*
			try (ResultSet r = statement
						.executeQuery(sql )) {
				while(r.next()) {
					name = r.getString("cname");
				
				}
			
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			
			*/
			//System.out.println(balance);
			if(balance > amount) {
				
				//String result = currentAccount.top_up(amount);
				//if(result.equals("0")) {
					String UPDATE_SQL1 = "UPDATE Account SET balance = '" + (balance - amount) + "' where aid = '" 
							+ linkId.trim() + "'";
					String UPDATE_SQL = "UPDATE Account SET balance = '" + (b + amount) + "' where aid = '" 
							+ aid + "'";
					String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,t,d)" 
							+ "VALUES ('" + name +"','" + "top_up" + "','" + amount + "','" + aid + "','" + linkId + "','" + aid + "','" + date + "')";
					statement.executeUpdate(UPDATE_SQL);
					statement.executeUpdate(UPDATE_SQL1);
					statement.executeUpdate(INSERT_TRANSACTION_SQL);
					
					boolean found1 = false;
					String SEARCH_BALANCE_SQL = "select * from BalanceTable where aid = '" + accountId + "' AND d = '" + date + "'";
					try (ResultSet rs = statement.executeQuery(SEARCH_BALANCE_SQL )) {	
						while(rs.next()) {
							found1= true;

						}
					}

					boolean found2 = false;
					SEARCH_BALANCE_SQL = "select * from BalanceTable where aid = '" + linkId.trim() + "' AND d = '" + date + "'";
					try (ResultSet rs = statement.executeQuery(SEARCH_BALANCE_SQL )) {	
						while(rs.next()) {
							found2 = true;

						}
					}
				
				if(found2) {
					String UPDATE_BALANCE_SQL =  "UPDATE BalanceTable SET balance = '" + (balance-amount) + "' where aid = '" +  linkId.trim()+ "'";
					statement.executeUpdate(UPDATE_BALANCE_SQL);
				}else {
					String INSERT_BALANCE_SQL = "INSERT INTO BalanceTable(d,aid,balance) VALUES ('" + date + "','" + linkId.trim() + "','" + (balance-amount) + "')";
					statement.executeUpdate(INSERT_BALANCE_SQL);
				}


				if(found1) {
					
					String UPDATE_BALANCE_SQL =  "UPDATE BalanceTable SET balance = '" + (b+amount) + "' where aid = '" + accountId + "'";
					statement.executeUpdate(UPDATE_BALANCE_SQL);
				}else {
					String INSERT_BALANCE_SQL = "INSERT INTO BalanceTable(d,aid,balance) VALUES ('" + date + "','" + accountId + "','" + (b+amount) + "')";
					statement.executeUpdate(INSERT_BALANCE_SQL);
					
				}
					//System.out.println("Top-up Successfully");
					//System.out.println("Record of this transaction:");
					//System.out.println( name.trim()+ " top-ups $" + amount + " from account " + aid +  " to account " + linkId.trim());
					check_if_closed((balance - amount), linkId);
					return "0";
				//}else {
					//System.out.println("Invalid Transaction");
					//return "1";
				//}
			}else {
				System.out.println("Invalid Transaction");
				return "1";
			}
			
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		
    }
	
	private void check_if_closed(double balance,String accountId) {
		if(balance <= 0) {
			
			//String sql = "select * from Account where aid = '" + currentAccount.getAid().trim() +"'";
			
			String UPDATE_SQL = "UPDATE Account SET closed = 'closed' where aid = '" + accountId +"'"; 
							
			//currentAccount.setStatus("closed");
			try(Statement statement = _connection.createStatement()){
				statement.executeUpdate(UPDATE_SQL);
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}	
		
			
		
		
		}
	}

    @Override
    public String payFriend(String from, String to, double amount) {
	return "";
    }


    //My own functions .....................
    //
    //
	
	public String getDate() {
		return date;
	}
	
	
	public String createCoownerTable() {
	String CREATE_TABLE_SQL="CREATE TABLE co_owner("
                    + "tnum CHAR(20) NOT NULL,"
                    + "cname CHAR(20) NOT NULL,"
                    + "address CHAR(20) NOT NULL,"
					+ "aid CHAR(20),"
                    + "pin INTEGER NOT NULL,"
					
                    + "PRIMARY KEY (tnum,pin,aid),"
					+ "FOREIGN KEY (aid) REFERENCES Account ON DELETE CASCADE)"
					;
					
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }
	
	
	public String setInterest(String aid , double i) {
		String FIND_ACCOUNT_SQL = "select * from Account where aid = '" + aid +"'";
		String UPDATE_RATE_SQL = "UPDATE Account SET interestRate = '" + i + "' where aid = '" 
						+ aid + "'";
		try(Statement statement = _connection.createStatement()){
			try (ResultSet r = statement.executeQuery(FIND_ACCOUNT_SQL )) {
				while(r.next()) {
					double rate = r.getDouble("interestRate");
					
					
				}
				
				
			}catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			statement.executeUpdate(UPDATE_RATE_SQL);
			
			String INSERT_RATE_SQL = "INSERT INTO InterestRecord(d,aid,interestRate) VALUES ('"+ date +"','"  + aid + "','" + i +"')";
			statement.executeUpdate(INSERT_RATE_SQL);
			return "0";
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		
		
		
		
		
		
		
		
		
		
	}
	
	
    public String createAccountListTable(){
	String CREATE_TABLE_SQL="CREATE TABLE AccountList("
					+ "tnum CHAR(20) ,"
                    + "aid CHAR(20) ,"
					+ "a INTEGER GENERATED BY DEFAULT ON NULL AS IDENTITY,"
					+ "PRIMARY KEY(a),"
					+ "FOREIGN KEY(aid) REFERENCES Account ON DELETE CASCADE)"
					
					;
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }
	
	public String createBalanceTable(){
	String CREATE_TABLE_SQL="CREATE TABLE BalanceTable("
        + "d CHAR(20) ,"
        + "aid CHAR(20) ,"
		+ "balance REAL ,"
		+ "a INTEGER GENERATED BY DEFAULT ON NULL AS IDENTITY,"
	    + "PRIMARY KEY (aid,a),"
		+ "FOREIGN KEY(aid) REFERENCES Account ON DELETE CASCADE)";
	    ;
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }


	 public String createCheckTable(){
	String CREATE_TABLE_SQL="CREATE TABLE CheckTable("
        + "checknum CHAR(20) ,"
        + "aid CHAR(20) ,"
		+ "balance REAL ,"
	    + "PRIMARY KEY (checknum))"
	    ;
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }
	
	
	public String createPocketLinkTable() {
		String CREATE_TABLE_SQL="CREATE TABLE PocketLink("
			+ "aid CHAR(20) ,"
			+ "linkedId CHAR(20) ,"
			+ "PRIMARY KEY (aid))"
			//+ "FOREIGN KEY (aid) REFERENCES Account ON DELETE CASCADE)"
			;
		try(Statement statement = _connection.createStatement()) {
			statement.executeQuery(CREATE_TABLE_SQL);	
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		return "0";
	
	
	
	}

    //Create Table named Account
    public String createAccountTable(){
	String CREATE_TABLE_SQL="CREATE TABLE Account("
                    + "aid CHAR(20) ,"
                    + "bbn CHAR(20) ,"
                    + "interestRate REAL ,"
					+ "accureInterest REAL,"
	    + "type CHAR(20) ,"
	    + "balance REAL ,"
		+ "init REAL,"
		+ "closed CHAR(20),"
		+ "added CHAR(20),"
		+ "owner CHAR(20),"
	    + "PRIMARY KEY (aid),"
	    + "CHECK (balance >= 0))";
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }
/*
    public String createCheckingAccountTable(){
	String CREATE_TABLE_SQL="CREATE TABLE CheckingAccount("
	    + "aid INTEGER NOT NULL,"
	    + "bbn CHAR(20) NOT NULL,"
	    + "interestRate REAL NOT NULL,"
	    + "balance REAL NOT NULL,"
	    + "PRIMARY KEY (aid),"
	    + "CHECK (balance >= 0))";
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }

    public String createSavingAccountTable(){
	String CREATE_TABLE_SQL="CREATE TABLE SavingAccount("
	    + "aid INTEGER ,"
	    + "bbn CHAR(20),"
	    + "interestRate REAL ,"
	    + "balance REAL ,"
	    + "PRIMARY KEY (aid),"
	    + "CHECK (balance >= 0))";

	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }

    public String createPocketAccountTable(){
	String CREATE_TABLE_SQL="CREATE TABLE PocketAccount("
	    + "aid INTEGER ,"
	    + "bbn CHAR(20),"
	    + "interestRate REAL ,"
	    + "balance REAL ,"
	    + "PRIMARY KEY (aid),"
	    + "CHECK (balance >= 0 AND interestRate = 0))";

	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }
*/

	public String createInterestRecordTable(){
	String CREATE_TABLE_SQL="CREATE TABLE InterestRecord("
                    + "d CHAR(20) ,"
                    + "aid CHAR(20) ,"
                    + "interestRate REAL ,"
					+ "a INTEGER GENERATED BY DEFAULT ON NULL AS IDENTITY,"
	    + "PRIMARY KEY (aid,a),"
		+ "FOREIGN KEY (aid) REFERENCES Account ON DELETE CASCADE)";
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }



    //Create Table named Customer
    public String createCustomerTable() {
	String CREATE_TABLE_SQL="CREATE TABLE Customer("
                    + "tnum CHAR(20) NOT NULL,"
                    + "cname CHAR(20) NOT NULL,"
                    + "address CHAR(20) NOT NULL,"
					+ "aid CHAR(20),"
                    + "pin INTEGER NOT NULL,"
					
                    + "PRIMARY KEY (tnum,pin))"
					//+ "FOREIGN KEY (aid) REFERENCES Account ON DELETE CASCADE)"
					;
					
	try(Statement statement = _connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }

    //Create Table named Transaction
	
    public  String createTransactionTable() {
	
	String CREATE_TABLE_SQL="CREATE TABLE Transaction("
                    + "cname CHAR(20),"
	    + "action CHAR(20),"
	    + "aid INTEGER ,"
		+ "amount REAL,"
	    + "d CHAR(20),"
	    + "f INTEGER ,"
	    + "t INTEGER,"
		+ "a INTEGER GENERATED BY DEFAULT ON NULL AS IDENTITY,"
	    + "PRIMARY KEY (cname,a))";

	try(Statement statement = _connection.createStatement()) {
	    statement.executeUpdate(CREATE_TABLE_SQL);	
	}catch(SQLException e) {
	    System.out.println(e.getMessage());
	    return "1";
	}
	return "0";
    }
	

	//INSERT FUNCTIONS
	
    public String insertCustomer(String tnum, String address, int pin, String cname,String accountId) {
		String INSERT_CUSTOMER_SQL = "INSERT INTO Customer (tnum,cname,address,pin,aid)"
			+ "VALUES (" + "'" + tnum + "'" + "," + "'" + cname + "'" + "," + "'" + address + "'" + "," + "'" + pin + "'" + "," + "'" + accountId + "'" +")";
		String INSERT_ACCOUNTLIST_SQL = "INSERT INTO AccountList (tnum, aid)"
			+ "VALUES (" + "'" + tnum + "'" + "," + "'" + accountId +"'" +  ")";
			a++;
		try(Statement statement = _connection.createStatement()) {
			statement.executeUpdate(INSERT_ACCOUNTLIST_SQL);
			statement.executeUpdate(INSERT_CUSTOMER_SQL);	
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		return "0";
    }
	
	public String insertAccount(String aid, String bbn, String type, double rate, double amount,String closed,String owner) {
		//System.out.println("insert");
		String INSERT_ACCOUNT_SQL = "INSERT INTO Account (aid,bbn,type,interestRate,balance,closed,owner,init,added)"
			+ "VALUES (" + "'" + aid + "'" + "," + "'" + bbn + "'" + "," + "'" + type + "'" + ","  + "'" + rate+"'" +"," + "'" + 0 +"','" +  closed+ "','" + owner  
			+ "','" + amount + "','" + "no" + "')";
		
		try(Statement statement = _connection.createStatement()) {
			statement.executeUpdate(INSERT_ACCOUNT_SQL);	
			setInterest(aid , rate);
			//deposit(aid,amount);
		}catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		return "0";
    }

    public String printCustomerTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from Customer" )) {
			 System.out.println("---------");
			 System.out.println("Customers");
			 System.out.println("---------");
			 System.out.println("SS#" + "   			"+"NAME"+"    		"+ "Address" + "    		" + "PIN" );
		while (rs.next()){
		    String tnum = rs.getString("tnum");
		    String cname = rs.getString("cname");
		    String address  = rs.getString("address");
		    int pin = rs.getInt("pin");
			String aid = rs.getString("aid");
			
			
			
			
			
		    System.out.println(tnum+"   "+cname+"    "+ address + "    "   + pin );     
		}
		System.out.println();
	    } catch(SQLException e) {
			System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }
	
	
	public String printCoownerTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from Co_owner" )) {
			 System.out.println("--------");
			 System.out.println("Co_owner");
			 System.out.println("--------");
			 System.out.println("SS#" + "   			"+"NAME"+"    		"+ "Address" + "    		"  + "AccountId");
		while (rs.next()){
		    String tnum = rs.getString("tnum");
		    String cname = rs.getString("cname");
		    String address  = rs.getString("address");
		    int pin = rs.getInt("pin");
			String aid = rs.getString("aid");
			
			
			
			
			
		    System.out.println(tnum+"   "+cname+"    "+ address + "    "   + aid);     
		}
		System.out.println();
	    } catch(SQLException e) {
			System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }
	
	

    public String printAccountTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	ArrayList<Account> a_list = new ArrayList<Account>();
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from Account" )) {
			 System.out.println("--------");
			 System.out.println("Accounts");
			 System.out.println("--------");
		System.out.println("ID" + "   			"+"BANK-BRANCH-NAME"+"       "+ "TYPE"  + "                  "+ "BALANCE" 
			+ "		" + "Status" + "			" + "Owner" );
		while (rs.next()){
		    String aid = rs.getString("aid");
		    String bbn = rs.getString("bbn");
			String type = rs.getString("type");
			double balance = rs.getDouble("balance");
			String status = rs.getString("closed");
			String pri_owner = rs.getString("owner");
			double init = rs.getDouble("init");
			a_list.add(new Account(aid,bbn,type,status,balance,pri_owner,false,currentUser));
			//System.out.println(aid+"   "+bbn+"    "+ type +"  "+ balance+"		" + status + "	" + pri_owner+ "	" + init);
	
		}
		
	
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
		
		for(int i = 0 ; i < a_list.size(); i++) {
			Account current = a_list.get(i);
			System.out.print(current.getAid()+"   "+current.getBbn()+"    "+ current.getType() +"  "+ current.getBalance()+"		" + current.getStatus() + "	" );
			String s = "select * from Customer where tnum = '" +current.getOwner().trim() +"'";
			try (ResultSet rs = statement.executeQuery(s )) {
				while (rs.next()){
					String name = rs.getString("cname");
					System.out.println(name);
	
				}
		
			}
			
			String sql = "select * from co_owner where aid = '" +current.getAid().trim() +"'";
			try (ResultSet rs = statement.executeQuery(sql )) {
				while (rs.next()){
					System.out.println("                                                                                                                " + rs.getString("cname"));
				}
		
			}
			System.out.println();
			
		}
		
		
		
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }

	public String printAccountListTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from AccountList" )) {
			 System.out.println("-----------");
			 System.out.println("AccountList");
			 System.out.println("-----------");
		System.out.println("TNUM" + "			"+"AID");
		while (rs.next()){
			int A = rs.getInt("a");
		    String aid = rs.getString("aid");
		    String tnum = rs.getString("tnum");
		
		    System.out.println(tnum+"   "+aid);
		    
		}
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }
	
	public String printTransactionTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from Transaction" )) {
			 System.out.println("-----------");
			 System.out.println("Transaction");
			 System.out.println("-----------");
		System.out.println("Date" + "				"+ "NAME" +  "			"+"Action" + "			"+ "Amount" + "	" + "From" + "	" + "To");
		while (rs.next()){
			String d = rs.getString("d");
			String name = rs.getString("cname");
			String aid = rs.getString("aid");
			String action = rs.getString("action");
			Double amount = rs.getDouble("amount");
			String f = rs.getString("f");
			String t = rs.getString("t");
		    System.out.println(d + "		" + name+ "   " + action+"   "+amount + "	" + f + "	" + t);
		    
		}
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }
	
	
	public String printCheckTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from CheckTable" )) {
			 System.out.println("-----------");
			 System.out.println("Check Table");
			 System.out.println("-----------");
		System.out.println("CHECKNUM" +  "	 "+"AID" + "   	"+ "AMOUNT" );
		while (rs.next()){
			String checknum = rs.getString("checknum");
			String aid = rs.getString("aid");
			Double amount = rs.getDouble("balance");
		    System.out.println(checknum.trim()+ "   		" + aid.trim()+"   "+amount );
		}
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }
	
	public String printPocketLinkTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from PocketLink" )) {
			 System.out.println("-----------");
			 System.out.println("Pocket Link");
			 System.out.println("-----------");
		System.out.println("LINKID" +  "			"+"AID"  );
		while (rs.next()){
			String linkedId = rs.getString("linkedId");
			String aid = rs.getString("aid");
			
		    System.out.println(linkedId+ "  " + aid );
		}
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }


	public String printBalanceTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from BalanceTable" )) {
			 System.out.println("-----------");
			 System.out.println("Balance Table");
			 System.out.println("-----------");
		System.out.println("DATE" +  "			"+"AID" + "			" + "BALANCE"  );
		while (rs.next()){
			String d = rs.getString("d");
			String aid = rs.getString("aid");
			double balance = rs.getDouble("balance");
		    System.out.println(d+ "  " + aid + "	" + balance);
		}
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }

	
	public String printInterestRecordTable()  {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from InterestRecord" )) {
			 System.out.println("---------------");
			 System.out.println("Interest Record");
			 System.out.println("---------------");
		System.out.println("DATE" +  "	 "+"AID" + "	" + "INTERESTRATE" );
		while (rs.next()){
			String d = rs.getString("d");
			String aid = rs.getString("aid");
			String rate = rs.getString("interestRate");
		    System.out.println(d.trim()+ "   		" + aid.trim() + "		"  + rate);
		}
	    } catch(SQLException e) {
		System.out.println(e.getMessage());
		return "1";
	    }
	}catch(SQLException e) {
	    return "1";
	}
	return "0";
    }
}
