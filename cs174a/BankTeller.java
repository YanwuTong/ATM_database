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
import cs174a.Testable.*;


public class BankTeller {

	private OracleConnection _connection;
	private App app;	

	public BankTeller(OracleConnection _connection, App app){
		this.app = app;
		this._connection = _connection;
	}
	
	public void makeSelection(Scanner s, String selection) {
	
		switch(selection) {
		
			case "1":
				enterCheckTransaction(s);
				break;
			case "2":
				dter();
				break;
			case "3":
				System.out.println("Enter the tin of the customer:");
				String tin = s.nextLine();
				customerReport(tin);
				break;
			default:
		
		
		}
		
			
			
			
			
		
	}

	public String initializeSystem(){
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##y_tong";
		final String DB_PASSWORD = "5988142";

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
			// System.out.println( "Driver Name: " + dbmd.getDriverName() );
			// System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			// System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			// System.out.println( "Database Username is: " + _connection.getUserName() );
			System.out.println();

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	public String enterCheckTransaction(Scanner s){
		//need a check number and then we can add amount to the target amount
		String aid = "";
		String type = "";
		String status = "";
		String bbn = "";
		String pri_owner = "";
		String name = "";
		Double balance = 0.0;
		Double init = 0.0;
		System.out.println("Enter the account Id");
		String account = s.nextLine();
		// System.out.println(account);


		
		try (Statement statement = _connection.createStatement()){
			try (ResultSet rs = statement.executeQuery("select * from Account where aid = " + account)){
				while (rs.next()){
					type = rs.getString("type");
					type = type.replaceAll("\\s","");
					status = rs.getString("closed");
					status = status.replaceAll("\\s","");
					// System.out.println(status);
					if(!type.equals("Interest_Checking")&&!type.equals("Student_Checking")){
						System.out.println("this account is not a checking account");
						return "1";
					}
					if(!status.equals("open")){
						System.out.println("this account is already closed");
						return "1";
					}
					// System.out.println(account);
					
					aid = account. replaceAll("\\s","");
			    	bbn = rs.getString("bbn");
			    	bbn = bbn.replaceAll("\\s","");
					balance = rs.getDouble("balance");
					pri_owner = rs.getString("owner");
					pri_owner = pri_owner.replaceAll("\\s","");
					init = rs.getDouble("init");
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
	    	}

	    	try (ResultSet rs1 = statement.executeQuery("select * from Customer where aid = " + account)){
	    		while (rs1.next()){
	    			name = rs1.getString("cname");
	    			
	    		}
	    	}
	    	catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
		}
		catch(SQLException e) {
	    	return "1";
		}

		// System.out.println(balance);
		System.out.println("Current valid balance is: "+balance);
		System.out.println("Enter the amount: ");
		String amount = s.nextLine();
		Double amo = Double.parseDouble(amount);

		if(amo <= 0){
			System.out.println("amount should bigger than 0");
			return "1";
		}
		if(amo > balance){
			System.out.println("current balance is: "+balance);
			System.out.println("amount should be smaller or equal than balance");
			return "1";
		}

		balance = balance - amo;
		String UPDATE_SQL = "UPDATE Account SET balance = " + balance + " where aid = " + aid;
		// System.out.println(UPDATE_SQL);

		String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (cname,action,amount,aid,f,d)" 
		+ "VALUES ('" + name +"','" + "write_check" + "','" + Double.parseDouble(amount) + "','" + aid + "','" + aid + "','" + app.getDate()+ "')";
		// System.out.println(INSERT_TRANSACTION_SQL);
		


		String CHANGE_CLOSED_SQL = "UPDATE Account SET closed = 'closed' where aid = " + aid;




		String sql = "select * from CheckTable";
		try(Statement statement1 = _connection.createStatement()){
			// System.out.println("connection success");
			statement1.executeUpdate(UPDATE_SQL);
			if(balance == 0){
				statement1.executeUpdate(CHANGE_CLOSED_SQL);
			}
			statement1.executeUpdate(INSERT_TRANSACTION_SQL);
			String checknum = null;
			try (ResultSet r = statement1.executeQuery(sql )) {
				
				while(r.next()) {
					checknum = r.getString("checknum");
				}

				if(checknum != null) {
					// System.out.println("here: " + Integer.parseInt(checknum.trim()));
					checknum = (Integer.parseInt(checknum.trim())+1) + "";

				}
				if (checknum == null){
					checknum = "0";
				}
				
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
			String INSERT_CHECK_SQL = "INSERT INTO CheckTable (checknum,aid,balance) VALUES ('" + checknum + "','" +aid + "','" + amount + "')";
			statement1.executeUpdate(INSERT_CHECK_SQL);
			System.out.println("Deposit Successfully");
			System.out.println("Record of this transaction:");
			System.out.println( name.trim()+ " writes a check in the amount of $" + amount + " from account " + aid);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}
		return "0";
	}

	public String generateMonthlyStatement(Scanner s){
		System.out.println("Please enter the tax identification number for the customer :");
		String tin = s.nextLine();
		ArrayList<String> idList = new ArrayList<String>();
		String name = "";
		String address = "";

		try(Statement statement = _connection.createStatement()){
			String FIND_AID_SQL = "select * from Account where owner = '"+tin+"'";
			String FIND_CUSTOMER_SQL = "select * from Customer where tnum = '"+tin+"'";
			// System.out.println(FIND_AID_SQL);

			//find the information for the customer with input tin
			try (ResultSet rs2 = statement.executeQuery(FIND_CUSTOMER_SQL)){
				while(rs2.next()){
					name = rs2.getString("cname");
					name = name.trim();
					address = rs2.getString("address");
					address = address.trim();
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
			System.out.println(name + "," + address);

			//find all account that owned as primary
			try (ResultSet rs = statement.executeQuery(FIND_AID_SQL)){
				while(rs.next()){
					String aid = rs.getString("aid");
					aid = aid.trim();
					// System.out.println(aid);
					idList.add(aid);
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}

			//find all account that owned as coowner
			try (ResultSet rs1 = statement.executeQuery("select * from Co_owner where tnum = '"+tin+"'")){
				while(rs1.next()){
					String aid1 = rs1.getString("aid");
					aid1 = aid1.trim();
					idList.add(aid1);
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
			System.out.println("ID list: ");
			System.out.println(idList);

			for(int i = 0; i < idList.size(); i++){
				System.out.println("Account: "+idList.get(i));
				ArrayList<String> ownerList = new ArrayList<String>();

				//add pri owner with current account
				try (ResultSet rs3 = statement.executeQuery("select * from Account where aid = '"+idList.get(i).trim()+"'")){
					while(rs3.next()){
						String pri_owner = rs3.getString("owner");
						pri_owner = pri_owner.trim();
						ownerList.add(pri_owner);
					}	
				}		
				catch(SQLException e) {
					System.out.println(e.getMessage());
					return "1";
				}

				//add add co owner with current account
				try (ResultSet rs4 = statement.executeQuery("select * from co_owner where aid = '"+idList.get(i).trim()+"'")){
					while(rs4.next()){
						String co_owner = rs4.getString("tnum");
						co_owner = co_owner.trim();
						ownerList.add(co_owner);
					}	
				}		
				catch(SQLException e) {
					System.out.println(e.getMessage());
					return "1";
				}

				//print all owner's information
				System.out.println("      Owners:");
				// System.out.println(ownerList.size());
				for(int j = 0; j < ownerList.size(); j++){
					try (ResultSet rs5 = statement.executeQuery("select * from Customer where tnum = '"+ownerList.get(j).trim()+"'")){
						String pname = "";
						String paddress = "";
						while(rs5.next()){
							pname = rs5.getString("cname");
							pname = pname.trim();
							paddress = rs5.getString("address");
							paddress = paddress.trim();
							System.out.println("          "+pname + ", " + paddress);
						}
						//TODO: need to change so that can print all people
						
					}		
					catch(SQLException e) {
						System.out.println(e.getMessage());
						return "1";
					}
				}

				//print balance information
				System.out.println("      Balance:");
				try (ResultSet rs6 = statement.executeQuery("select * from Account where aid = '"+idList.get(i).trim()+"'")){
					Double init = 0.0;
					Double balance = 0.0;
					while(rs6.next()){
						init = rs6.getDouble("init");
						balance = rs6.getDouble("balance");
						System.out.println("          initial: "+ init);
						System.out.println("          Final: "+ balance);
					}
					//TODO: need to change so that can print all people
					
				}		
				catch(SQLException e) {
					System.out.println(e.getMessage());
					return "1";
				}

				//print transaction
				System.out.println("      Transaction:");
				try (ResultSet rs7 = statement.executeQuery("select * from Transaction where aid = '"+idList.get(i).trim()+"'")){
					while(rs7.next()){
						String td = rs7.getString("d");
						String tname = rs7.getString("cname");
						String taid = rs7.getString("aid");
						String taction = rs7.getString("action");
						Double tamount = rs7.getDouble("amount");
						String f = rs7.getString("f");
						String t = rs7.getString("t");
					    System.out.println("          "+td.trim() + "		" + tname.trim()+ "   " + taction.trim()+"   "+tamount + "	" + f + "	" + t);

					}
					//TODO: need to change so that can print all people
					
				}		
				catch(SQLException e) {
					System.out.println(e.getMessage());
					return "1";
				}

				// System.out.println(ownerList);
				System.out.println("");
			}


		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
		}

		return "0";
	}

	public String listClosedAccounts(){
		app.listClosedAccounts();
		return "";
	}

	public String dter(){
		ArrayList<String> nameList = new ArrayList<String>();
		Double amount = 0.0;
		ArrayList<String> namel = new ArrayList<String>();


		try (Statement statement = _connection.createStatement()){
			try (ResultSet rs1 = statement.executeQuery("select distinct aid from Transaction")){
				while(rs1.next()){
					String customer = rs1.getString("aid");
					customer = customer.trim();
					namel.add(customer);
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
			System.out.println("enter");
			// System.out.println(namel);
			for(int i = 0; i < namel.size(); i++){
				String GET_INFO_SQL = "select * from Transaction where aid = '"+namel.get(i)+"' and (action = 'transfers_from' or action = 'wires_from' or action = 'deposits')";
				// System.out.println(GET_INFO_SQL);
				try (ResultSet rs = statement.executeQuery(GET_INFO_SQL)){
					while(rs.next()){
						
						String name = rs.getString("cname");
						amount = amount + rs.getDouble("amount");
						String action = rs.getString("action");
						name = name.trim();
						// nameList.add(name);
						// System.out.println(name);
						// System.out.println(amount);
						// System.out.println(action);
					}
			 	}
			 	catch(SQLException e) {
					System.out.println(e.getMessage());
					return "1";
		    	}
		    	if(amount >= 10000){
		    		nameList.add(namel.get(i));
		    	}
		    	amount = 0.0;

		    }
	    	System.out.println("Customer List for DTER: ");
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0 ; i < nameList.size(); i++) {
				String PRI_SQL = "select * from Customer C, (select owner from Account where aid = '" + nameList.get(i) + "') B where C.tnum = B.owner";
				try (ResultSet rs = statement.executeQuery(PRI_SQL)){
					while(rs.next()){
						
						String name = rs.getString("cname");
						list.add(name.trim());
					}
			 	}
				String CO_SQL = "select * from co_owner where aid = '" + nameList.get(i) + "'";
				try (ResultSet rs = statement.executeQuery(CO_SQL)){
					while(rs.next()){
						
						String name = rs.getString("cname");
						list.add(name.trim());
					}
			 	}
				
			}
	    	System.out.println(list);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return "1";
	    }
		return "0";
	}

	public String customerReport(String tin){
		try (Statement statement = _connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from Account A, (select distinct aid from AccountList where tnum = '"+tin+"')B where A.aid = B.aid" )) {
			 System.out.println("---------");
			 System.out.println("customerReport");
			 System.out.println("---------");
			 System.out.println("current user tax identification number: \n" + tin);
			 System.out.println("ID" + "   		  "+"BANK-BRANCH-NAME"+"    	       "+ "TYPE"  + "                 "+ "BALANCE" + "   " + "Status");
		while (rs.next()){
		    String aid = rs.getString("aid");
		    String bbn = rs.getString("bbn");
			String type = rs.getString("type");
			double balance = rs.getDouble("balance");
			String status = rs.getString("closed");
			String pri_owner = rs.getString("owner");
			System.out.println(aid+"   "+bbn+"    "+ type +"  "+ balance+"    " + status);
		    
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


	


	public String addInterest(){
		// Vector<Pair<Integer,Double>> v =  new Vector<Pair<Integer,Double>>(100000);
		Vector<Double> v = new Vector<Double>(100000);
		Vector<Integer> v2 = new Vector<Integer>(100000);
		try (Statement statement = _connection.createStatement()) {      
			try (ResultSet rs = statement
				.executeQuery("select * from Account " )) {
				while (rs.next()){
					String id = rs.getString("aid");
					String inter = rs.getString("interestRate");
					String balance = rs.getString("balance");

					// System.out.println("this is information for add interest");
					// System.out.println("before add interest: "+balance);
					double did = Double.valueOf(id);
					int iid = (int)did;
					double temp = Double.valueOf(inter);
					double temp2 = Double.valueOf(balance);
					temp2 = temp2 + temp2*temp/100;
					
					v2.add(iid);
					v.add(temp2);
					// Pair<Integer,Double> p = new Pair<Integer,Double> (iid, temp2);
					// v.add(p);
					// System.out.println("Vector for account is: " + v2);
					System.out.println("after add interest: " + temp2);
					// String com = "UPDATE account SET balance = " + balance +"WHERE aid = " + id;
				 // 	statement.executeUpdate(com);

				}
			} catch(SQLException e) {
				System.out.println(e.getMessage());
				return "1";
			}
		}catch(SQLException e) {
			return "1";
		}

		try (Statement statement1 = _connection.createStatement()){
			for(int i = 0; i < v.size(); i++){
				String UPDATE_TABLE_DQL ="UPDATE account SET balance = " + v.get(i) +"WHERE aid = " + v2.get(i);
				statement1.executeUpdate(UPDATE_TABLE_DQL);
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			return "1";
		}
		
		return "0";
	}

	public String createAccount(Scanner s){
		String id, initial, tin, name, address, linkedId;
		Double ini;
		System.out.println("which kind of account do you want to create? please select:");
		System.out.println("[1]student_checking");
		System.out.println("[2]interest_checking");
		System.out.println("[3]saving");
		System.out.println("[4]pocket");
		String option = s.nextLine();
		switch(option){
			case "1":
				System.out.println("Please enter your account id");
				id = s.nextLine();
				System.out.println("initial deposit");
				initial = s.nextLine();
				ini = Double.parseDouble(initial);
				System.out.println("tax identification number");
				tin = s.nextLine();
				System.out.println("name");
				name = s.nextLine();
				System.out.println("address");
				address = s.nextLine();
				app.createCheckingSavingsAccount(AccountType.STUDENT_CHECKING, id, ini, tin, name, address);
				break;
				
			case "2":
				System.out.println("Please enter your account id");
				id = s.nextLine();
				System.out.println("initial deposit");
				initial = s.nextLine();
				ini = Double.parseDouble(initial);
				System.out.println("tax identification number");
				tin = s.nextLine();
				System.out.println("name");
				name = s.nextLine();
				System.out.println("address");
				address = s.nextLine();
				app.createCheckingSavingsAccount(AccountType.INTEREST_CHECKING, id, ini, tin, name, address);
				break;
			case "3":
				System.out.println("Please enter your account id");
				id = s.nextLine();
				System.out.println("initial deposit");
				initial = s.nextLine();
				ini = Double.parseDouble(initial);
				System.out.println("tax identification number");
				tin = s.nextLine();
				System.out.println("name");
				name = s.nextLine();
				System.out.println("address");
				address = s.nextLine();
				app.createCheckingSavingsAccount(AccountType.SAVINGS, id, ini, tin, name, address);
				break;
			case "4":
				System.out.println("Please enter your account id");
				id = s.nextLine();
				System.out.println("linked Id");
				linkedId = s.nextLine();
				System.out.println("InitialTopup");
				initial = s.nextLine();
				ini = Double.parseDouble(initial);
				System.out.println("tax identification number");
				tin = s.nextLine();
				app.createPocketAccount(id, linkedId, ini, tin);
				break;
			default :
				System.out.println("invalid selection");	

		}
		return "";
	}

	public String deleteClosed(){
		try(Statement statement = _connection.createStatement()){

			//delete all account which are closed
			try(ResultSet rs = statement.executeQuery("delete from Account where closed = 'closed'")){
				System.out.println("Delete all closed account successful");
				
			}
			catch(SQLException e) {
	    		System.out.println(e.getMessage());
	    		return "1";
	    	}

	    	//delete all customer who does not own any account
	    	try(ResultSet rs1 = statement.executeQuery("delete  from customer where tnum "+
	    		                                        "not in (select tnum from accountlist)")){
	    		System.out.println("Delete all customers with no open account successful");
				
			}
			catch(SQLException e) {
	    		System.out.println(e.getMessage());
	    		return "1";
	    	}
		}
		catch(SQLException e) {
	    	System.out.println(e.getMessage());
	    	return "1";
	    }
		return "0";
	}

	public String deleteTransactions(){
		String DROP_Transaction_SQL="DROP TABLE Transaction";
		try(Statement statement = _connection.createStatement()){
			statement.executeQuery(DROP_Transaction_SQL);
			//System.out.println("delete transaction successful");
		}
		catch(SQLException e) {
	    	System.out.println(e.getMessage());
	    	return "1";
		}
		String r = app.createTransactionTable();
		return "0";
	}
}