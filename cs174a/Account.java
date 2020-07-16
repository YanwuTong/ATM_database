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

public class Account implements Transaction{
	
	private String aid; 
	private String bbn; 
	private String type;
	private String closed;
	private double amount;
	
	private Customer currentUser;
					
	private String owner;
	private boolean add;
	
	
	public Account(String aid, String bbn, String type, String closed, double amount
					, String owner, boolean add, Customer currentUser) {
					
		this.aid = aid;
		this.type = type;
		this.bbn = bbn;
		this.closed = closed;
		this.amount = amount;
		this.currentUser = currentUser;
		this.owner = owner;
		this.add = add;
		
	
	}
	
	//HELPER FUNCTION
	//GETTER SETTER
	public String getStatus() {
		return closed;
	}
	public void setStatus(String closed) {
		this.closed = closed;
	}
	public String getAid() {
		return aid;
	}
	public String getBbn() {
		return bbn;
	}
	
	public double getBalance() {
		return amount;
	}
	
	public String getType() {
		return type;
	}
	
	public String getOwner() {
		return owner;
		
	}
	public void setBalance(double balance) {
		this.amount = balance;
	}

	
	
	

	@Override
	public String deposit(double amount) {
		if(!type.equals("Pocket")) {
			this.amount += amount;
			return "0";
		}
	
		return "1";
	}
	@Override
	public String withdrawal(double amount) {
		if(!type.equals("Pocket")) {
			if(this.amount >= amount) {
				this.amount -= amount;
				return "0";
			}
		}
		return "1";
	
	}
	
	@Override
	public String transfer(double amount) {
		
		
		
		if(!type.equals("Pocket") && amount <= 2000) {
			if(this.amount >= amount) {
				this.amount -= amount;
				return "0";
			}
			
		}
		return "1";
	
	}
	
	@Override
	public String wire(double amount ) {
		if(!type.equals("Pocket")) {
			if(this.amount >= amount*1.02) {
				this.amount -= amount*1.02;
				return "0";
			}
		}
		return "1";
	
	}
	@Override
	public String write_check(double amount) 
	{
		if(type.equals("Interest_Checking") || type.equals("Student_Checking")) {
		
			if(this.amount >= amount) {
			
				this.amount -= amount;
				return "0";
			}
			return "1";
		
		
		}
		return "1";
		
	}
	
	@Override
	public String top_up(double amount) {
		if(type.equals("Pocket")) {
			this.amount += amount;
			return "0";
		}
		return "1";
	
	}
	
	@Override
	public String purchase(double amount) {
		if(type.equals("Pocket")) {
			if(this.amount >= amount ) {
				this.amount -= amount;
				return "0";
			}
		
			return "1";
		}
		return "1";
	
	}
	
	
	
	
	
	
	
	@Override
	public String collect(double amount) {
		if(type.equals("Pocket")) {
		
			if(this.amount >= amount*1.02) {
				this.amount -= amount*1.02;
				return "0";
			}
			return "1";
		}
		return "1";
	}
	
	@Override
	public String pay_friend(double amount) {
		if(type.equals("Pocket") ) {
			if(this.amount >= amount) {
				this.amount -= amount;
				
				return "0";
				
			}
			return "1";
		}
		return "1";
	
	
	}
	
	/*
	
	
	
	
	public void set_date(String date);
	@Override
	public void accure_interest(double amount, int customer) {
	
	}
	
	*/	


}