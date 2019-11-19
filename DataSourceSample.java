/* Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.*/
/*
   DESCRIPTION    
   The code sample shows how to use the DataSource API to establish a connection
   to the Database. You can specify properties with "setConnectionProperties".
   This is the recommended way to create connections to the Database.

   Note that an instance of oracle.jdbc.pool.OracleDataSource doesn't provide
   any connection pooling. It's just a connection factory. A connection pool,
   such as Universal Connection Pool (UCP), can be configured to use an
   instance of oracle.jdbc.pool.OracleDataSource to create connections and 
   then cache them.
    
    Step 1: Enter the Database details in this file. 
            DB_USER, DB_PASSWORD and DB_URL are required
    Step 2: Run the sample with "ant DataSourceSample"
  
   NOTES
    Use JDK 1.7 and above

   MODIFIED    (MM/DD/YY)
    nbsundar    02/17/15 - Creation 
 */

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

public class DataSourceSample {  
  // The recommended format of a connection URL is the long format with the
  // connection descriptor.
  final static String DB_URL= "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
  // For ATP and ADW - use the TNS Alias name along with the TNS_ADMIN when using 18.3 JDBC driver
  // final static String DB_URL="jdbc:oracle:thin:@wallet_dbname?TNS_ADMIN=/Users/test/wallet_dbname";
  // In case of windows, use the following URL 
  // final static String DB_URL="jdbc:oracle:thin:@wallet_dbname?TNS_ADMIN=C:\\Users\\test\\wallet_dbname";
  final static String DB_USER = "c##xinhaoxiao";
  final static String DB_PASSWORD = "4594529";

 /*
  * The method gets a database connection using 
  * oracle.jdbc.pool.OracleDataSource. It also sets some connection 
  * level properties, such as,
  * OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH,
  * OracleConnection.CONNECTION_PROPERTY_THIN_NET_CHECKSUM_TYPES, etc.,
  * There are many other connection related properties. Refer to 
  * the OracleConnection interface to find more. 
  */
  public static void main(String args[]) throws SQLException {
    Properties info = new Properties();     
    info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
    info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);          
    info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");    
  

    OracleDataSource ods = new OracleDataSource();
    ods.setURL(DB_URL);    
    ods.setConnectionProperties(info);

    // With AutoCloseable, the connection is closed automatically.
    try (OracleConnection connection = (OracleConnection) ods.getConnection()) {
      // Get the JDBC driver name and version 
      DatabaseMetaData dbmd = connection.getMetaData();       
      System.out.println("Driver Name: " + dbmd.getDriverName());
      System.out.println("Driver Version: " + dbmd.getDriverVersion());
      // Print some connection properties
      System.out.println("Default Row Prefetch Value is: " + 
         connection.getDefaultRowPrefetch());
      System.out.println("Database Username is: " + connection.getUserName());
      System.out.println();
      // Perform a database operation
      //createAccountTable(connection);
      //createCustomerTable(connection);
      deleteTable(connection);
      createTransactionTable(connection);
    
      //printAccountTable(connection);
      printEmployees(connection);
    }   
  }

    //Create Table named Account
    public static void createAccountTable(Connection connection) throws SQLException{
	String CREATE_TABLE_SQL="CREATE TABLE Account("
                    + "aid INTEGER NOT NULL,"
                    + "bbn CHAR(20) NOT NULL,"
                    + "type CHAR(20) NOT NULL,"
                    + "interestRate REAL NOT NULL,"
                    + "PRIMARY KEY (aid))";
	try(Statement statement = connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}
    }

    //Create Table named Customer
    public static void createCustomerTable(Connection connection) throws SQLException{
	String CREATE_TABLE_SQL="CREATE TABLE Customer("
                    + "tid INTEGER NOT NULL,"
                    + "cname CHAR(20) NOT NULL,"
                    + "address CHAR(20) NOT NULL,"
                    + "pin INTEGER NOT NULL,"
                    + "PRIMARY KEY (tid,pin))";
	try(Statement statement = connection.createStatement()) {
	    statement.executeQuery(CREATE_TABLE_SQL);	
	}
    }

    //Create Table named Transaction
    public static void createTransactionTable(Connection connection) throws SQLException{
	
	String CREATE_TABLE_SQL="CREATE TABLE Transaction("
                    + "cname CHAR(20) NOT NULL,"
	    + "t_type CHAR(20) NOT NULL,"
	    + "aid INTEGER NOT NULL,"
	    + "d CHAR(20) NOT NULL,"
	    + "f INTEGER NOT NULL,"
	    + "t INTEGER NOT NULL,"
	    + "PRIMARY KEY (aid,cname),"
	    + "FOREIGN KEY (aid) REFERENCES Account(aid))";

	try(Statement statement = connection.createStatement()) {
	    statement.executeUpdate(CREATE_TABLE_SQL);	
	}
    }
    public static void deleteTable(Connection connection) throws SQLException {
	String DROP_TABLE_SQL="DROP TABLE Transaction";
	try(Statement statement = connection.createStatement()) {
	    statement.executeUpdate(DROP_TABLE_SQL);	
	}


    }

    
    public static void printAccountTable(Connection connection) throws SQLException {
	// Statement and ResultSet are AutoCloseable and closed automatically. 
	try (Statement statement = connection.createStatement()) {      
	    try (ResultSet rs = statement
		 .executeQuery("select * from Account" )) {
		while (rs.next()){
		    int aid = rs.getInt("aid");
		    String bbn = rs.getString("bbn");
		    String type  = rs.getString("type");
		    double rate = rs.getDouble("interestRate");
		    System.out.println(aid+"   "+bbn+"    "+type);     
		}
	    }   
	}
    }

  
  
 /*
  * Displays first_name and last_name from the employees table.
  */
  public static void printEmployees(Connection connection) throws SQLException {
    // Statement and ResultSet are AutoCloseable and closed automatically. 
    try (Statement statement = connection.createStatement()) {      
      try (ResultSet resultSet = statement
          .executeQuery("select owner,table_name from all_tables")) {
        while (resultSet.next())
          System.out.println(resultSet.getString(1) + " "
              + resultSet.getString(2) + " ");       
      }
    }   
  } 
}

