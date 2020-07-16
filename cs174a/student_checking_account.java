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

public class student_checking_account extends Account {
	
	private double rate = 0;
	public student_checking_account(String aid, String bbn, String type, String closed, double amount,
					 String owner, boolean add,Customer currentUser) {
		super(aid,bbn,type,closed,amount,owner,add,currentUser);
		
		
		
	}
	

		public double getRate() {
		return rate;
	}
}