import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.ProcessBuilder.Redirect.Type;
import java.sql.ResultSetMetaData;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Scanner;
import java.util.concurrent.Callable;

import javax.swing.text.Style;

public class StoredProceduresLab2 {
		static final String databasePrefix ="cs366-2217_flynnsg10";
		static final String netID ="flynnsg10"; // Please enter your netId
		static final String hostName ="washington.uww.edu"; //140.146.23.39 or washington.uww.edu
		static final String databaseURL ="jdbc:mariadb://"+hostName+"/"+databasePrefix;
		static final String password=""; // please enter your own password
					
		private Connection connection = null;
		private Statement statement = null;
		private ResultSet resultSet = null;

		public void Connection(){
			try {
				Class.forName("org.mariadb.jdbc.Driver");
				System.out.println("databaseURL"+ databaseURL);
				connection = DriverManager.getConnection(databaseURL, netID, password);
				System.out.println("Successfully connected to the database");
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		} // end of Connection

		public void storedProcedureFacultyCount(String spName) {
			try {
				statement = connection.createStatement();
				int total =0;
				CallableStatement myCallStmt = connection.prepareCall("{call "+spName+"(?)}");
				myCallStmt.registerOutParameter(1,Types.BIGINT);
				myCallStmt.execute();
				total = myCallStmt.getInt(1);
				System.out.println("Total number of faculty: "+ total);

			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		} // end of storedProcedureFacultyCount method
		
		public void storedProcedureStudentInfo(Scanner input){
			//Scanner input = new Scanner(System.in);

			System.out.println("Please enter the student id: ");
			//BigInteger snum = BigInteger.valueOf(Integer.parseInt(input.next()));
			String snum = input.next();
			//input.close();
			
			String spName = "getStudentInfo";
			try{
				statement = connection.createStatement();
				String sname = "",
				major = "",
				level = "";
				Integer age = 0;
				
				CallableStatement myCallStmt = connection.prepareCall("{call " + spName + "(" + snum + ",?,?,?,?,?)}");
				myCallStmt.registerOutParameter(1, Types.BIGINT);
				myCallStmt.registerOutParameter(2, Types.VARCHAR);
				myCallStmt.registerOutParameter(3, Types.VARCHAR);
				myCallStmt.registerOutParameter(4, Types.VARCHAR);
				myCallStmt.registerOutParameter(5, Types.INTEGER);
				myCallStmt.execute();
				BigDecimal o_snum = myCallStmt.getBigDecimal(1);
				sname = myCallStmt.getString(2);
				major = myCallStmt.getString(3);
				level = myCallStmt.getString(4);
				age = myCallStmt.getInt(5);
				
				System.out.println("\tStudent: " + sname);
				System.out.println("\tMajor: " + major);
				System.out.println("\tGrade Level: " + level);
				System.out.println("\tStudent Age: " + age);
				System.out.println("\tStudent Num: " + o_snum);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void cursorClassInfo(String spName) {

			try {
				statement = connection.createStatement();
				String listName;
				CallableStatement myCallStmt = connection.
						prepareCall("{call "+spName+"(?)}");
				myCallStmt.setString(1,"");
				myCallStmt.registerOutParameter(1,Types.VARCHAR);
				myCallStmt.execute();
				listName = myCallStmt.getString(1);
				System.out.println("The student names are : \n"+listName);
		
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public void queryClassInfo(String sqlQuery) {
	    
	    	try {
	    		statement = connection.createStatement();
	    		resultSet = statement.executeQuery(sqlQuery);

	    		ResultSetMetaData metaData = resultSet.getMetaData();
	    		int columns = metaData.getColumnCount();

	    		for (int i=1; i<= columns; i++) {
	    			System.out.print(metaData.getColumnName(i)+"\t");
	    		}

	    		System.out.println();

	    		while (resultSet.next()) {
	       
	    			for (int i=1; i<= columns; i++) {
	    				System.out.print(resultSet.getObject(i)+"\t\t");
	    			}
	    			System.out.println();
	    		}
	    	}
	    	catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    } // end of simpleQuery method

	public static void main(String args[]) {
		
		StoredProceduresLab2 demoObj = new StoredProceduresLab2();
		demoObj.Connection();
		Scanner input = new Scanner(System.in);
		System.out.println("Which Option Would You Like?");
		System.out.println("1. Get Total Faculty Count\n2. Display Information About a Student");
		System.out.println("3. Info On All Classes Using Stored Procedure\n4. Info On All Classes Using SQL Statement");
		System.out.println("Please Enter In An Option Number: ");

		Integer choice = Integer.parseInt(input.next());
		
		if(choice == 1){
			String spName ="getTotalFaculty";
			demoObj.storedProcedureFacultyCount(spName);
		}
		else if(choice == 2){
			demoObj.storedProcedureStudentInfo(input);
		}
		else if(choice == 3){
			String spName ="getClassInfo";
			demoObj.cursorClassInfo(spName);
		}
		else if (choice == 4){
			String sqlQuery ="select * from Class;";
	    	demoObj.queryClassInfo(sqlQuery);
		}
		else{
			System.out.println("Not a valid option.");
		}
		input.close();
	}
}

/*
delimiter $$
drop procedure if exists getTotalFaculty;
create procedure getTotalFaculty(INOUT count INT)
begin
	SELECT COUNT(*) INTO count
	FROM Faculty;
end $$
delimiter ;



delimiter $$
drop procedure if exists getStudentInfo;
create procedure getStudentInfo(IN in_snum BIGINT, OUT o_snum BIGINT, OUT o_sname VARCHAR(255), OUT o_major VARCHAR(255), OUT o_level VARCHAR(255), OUT o_age INT)
begin
      SELECT snum, sname, major, level, age INTO o_snum, o_sname, o_major, o_level, o_age
      FROM Student
      WHERE snum=in_snum;
end $$
delimiter ;



delimiter $$
drop procedure if exists getClassInfo;
create procedure getClassInfo(INOUT classList varchar(4000))
begin
declare isDone integer default 0;
declare currentClass varchar(255) default "";
declare currentMeet varchar(255) default "";
declare currentRoom varchar(255) default "";

declare classCursor cursor for
select cname, meets_at, room from Class;

declare continue handler
for not found set isDone = 1;

open classCursor;
getList: loop
fetch classCursor into currentClass, currentMeet, currentRoom;
if isDone = 1 then
leave getList;
end if;
   
set classList = concat("","\n", classList);
set classList = concat(currentRoom,"\t", classList);
set classList = concat(currentMeet,"\t", classList);
set classList = concat(currentClass,"\t\t", classList);

end loop getList;

close classCursor;
end $$
delimiter ;
*/