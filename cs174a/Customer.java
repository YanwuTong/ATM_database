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


public class Customer {
	
	private int PIN = 1717;
	private String name;
	private String tax_num;
	private String address;
	private ArrayList<String> account_list;

	public Customer( String name, String tax_num, String address, ArrayList<String> account_list) {
		
		this.name = name;
		this.tax_num = tax_num;
		this.address = address;
		this.account_list = account_list;
	
	}
	
	public String getTID() {
		return tax_num;
	
	}
	public String getName() {
		return name;
	}
	 public ArrayList<String> getAccountList() {
		return this.account_list;
	 }
	 
	 public void addAccountToList(String a) {
		account_list.add(a);
	 }
	
	public boolean vertifyPin(int pin) {
		if(pin == PIN) {
			return true;
		}
		return false;
	}
	
	public void setPin(int pin1, int pin2) {
		if(pin1 == PIN) {
			PIN = pin2;
		}
	}
	public int getPIN() {
		return PIN;
	}
	
	






}