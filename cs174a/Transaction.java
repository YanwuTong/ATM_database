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


interface Transaction {
	
	
	
	String deposit(double amount);
	
	String withdrawal(double amount);
	
	String transfer(double amount);
	
	String wire(double amount);
	
	String write_check(double amount);
	
	String top_up(double amount);
	
	String purchase(double amount);
	
	String collect(double amount);
	
	
	String pay_friend(double amount);
	
	/*
	void set_date(String date);
	
	void accure_interest(double amount, int customer);
	
	*/



}