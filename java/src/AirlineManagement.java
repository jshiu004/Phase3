/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class AirlineManagement {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of AirlineManagement
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public AirlineManagement(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end AirlineManagement

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            AirlineManagement.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      AirlineManagement esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the AirlineManagement object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new AirlineManagement (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");

                switch(authorisedUser) {
                  case "management":
                     //**the following functionalities should only be able to be used by Management**
                     System.out.println("1. View Flights");
                     System.out.println("2. View Flight Seats");
                     System.out.println("3. View Flight Status");
                     System.out.println("4. View Flights of the day");  
                     System.out.println("5. View Full Order ID History");
                     System.out.println("6. Find all information on traveler on a reservation");
                     System.out.println("7. Get Plane information");
                     System.out.println("8. Get all repairs made by a technician");
                     System.out.println("9. Get all repairs made on a plane");
                     System.out.println("10. View flight statistics");
                     System.out.println("20. Log out");
                     switch(readChoice()) {
                        case 1: feature1(esql); break;
                        case 2: feature2(esql); break;
                        case 3: feature3(esql); break;
                        case 4: feature4(esql); break;
                        case 5: feature5(esql); break;
                        case 6: feature6(esql); break;
                        case 7: feature7(esql); break;
                        case 8: feature8(esql); break;
                        case 9: feature9(esql); break;
                        case 10: feature10(esql); break;
                        case 20: usermenu = false; break;
                        default : System.out.println("Unrecognized choice!"); break;
                     }
                     break;
                  case "customer":
                     //**the following functionalities should only be able to be used by customers**
                     System.out.println("11. Search Flights");
                     System.out.println("12. Search Flight cost");
                     System.out.println("13. Search flight airplane type");
                     System.out.println("14. Make a reservation");
                     System.out.println("20. Log out");
                     switch(readChoice()) {
                        case 11: feature11(esql); break;
                        case 12: feature12(esql); break;
                        case 13: feature13(esql); break;
                        case 14: feature14(esql); break;
                        case 20: usermenu = false; break;

                        default : System.out.println("Unrecognized choice!"); break;
                     }
                     break;
                  case "pilot":
                     //**the following functionalities should ony be able to be used by Pilots**
                     System.out.println("15. Maintenace Request");
                     System.out.println("20. Log out");
                     switch(readChoice()) {
                        case 15: createMaintenanceRequest(esql); break;
                        case 20: usermenu = false; break;

                        default : System.out.println("Unrecognized choice!"); break;
                     }
                     break;
                  case "technician":
                     //**the following functionalities should ony be able to be used by Technicians**
                     System.out.println("16. View Repairs for a Plane");
                     System.out.println("17. View Maintenance Request by Pilot");
                     System.out.println("18. Log Repair Entry");
                     System.out.println("20. Log out");
                     switch(readChoice()) {
                        case 16: viewRepairsForPlaneInRange(esql); break;
                        case 17: viewPilotMaintenanceRequests(esql); break;
                        case 18: logRepairEntry(esql); break;
                        case 20: usermenu = false; break;

                        default : System.out.println("Unrecognized choice!"); break;
                     }
                     break;
                }

               //  switch (readChoice()){
               //     case 1: feature1(esql); break;
               //     case 2: feature2(esql); break;
               //     case 3: feature3(esql); break;
               //     case 4: feature4(esql); break;
               //     case 5: feature5(esql); break;
               //     case 6: feature6(esql); break;
               //     case 7: feature7(esql); break;
               //     case 8: feature8(esql); break;
               //     case 9: feature9(esql); break;
               //     case 10: feature10(esql); break;
               //     case 11: feature11(esql); break;
               //     case 12: feature12(esql); break;
               //     case 13: feature13(esql); break;
               //     case 14: feature14(esql); break;
                   




               //     case 20: usermenu = false; break;
               //     default : System.out.println("Unrecognized choice!"); break;
               //  }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(AirlineManagement esql){
      System.out.println("Hello new user");
      System.out.println("Please indicate your position");
      System.out.println("1. Airline Management");
      System.out.println("2. Customer");
      System.out.println("3. Maintenance Staff");
      System.out.println("4. Pilot");
      System.out.println("Please enter a number from 1-4");

      int choice = readChoice();
      while(choice != 1 && choice != 2 && choice != 3 && choice != 4) {
         System.out.println("Your choice was invalid. Please enter a number from 1-4");
         choice = readChoice();
      }

      String role = null;
      switch(choice) {
         case 1:
            role = "management";
            break;
         case 2:
            role = "customer";
            break;
         case 3:
            role = "technician";
            break;
         case 4:
            role = "pilot";
            break;
      }
      try {
         String username = null;
         String password = null;
         boolean unique = false;
         int attempts = 0;
         while(!unique || attempts == 0) {
            System.out.print("Please enter a username: ");
            username = in.readLine();
            String query = "SELECT username FROM Users WHERE username = '";
            query = query + username + "'";
            int count = esql.executeQuery(query);
            if(count > 0) {
               System.out.println("Username already taken. Please input another one.");
            }
            else {
               unique = true;
            }
            attempts++;
         }
         System.out.print("Please enter a password: ");
         password = in.readLine();
         String query = "INSERT INTO Users(username, password, role) VALUES('";
         query = query + username + "','" + password + "','" + role + "')";
         esql.executeUpdate(query);
      }
      catch (IOException | SQLException e) {
        System.err.println("Error creating user: " + e.getMessage());
    }

   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(AirlineManagement esql){
      try {
         System.out.println("Hello!");
         String username, password;
         System.out.print("Input username: ");
         username = in.readLine();
         System.out.print("Input password: ");
         password = in.readLine();
         String query = "SELECT Users.role From Users Where username = '";
         query = query + username + "' AND password = '" + password + "'";
         int count = esql.executeQuery(query);
         if(count > 0) {
            List<List<String>> rolesList = new ArrayList<>();
            rolesList = esql.executeQueryAndReturnResult(query);
            return rolesList.get(0).get(0);
         }
         else {
            System.out.println("Username or password is incorrect, please try again");
            return null;
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }//end

// Rest of the functions definition go in here

   public static void feature1(AirlineManagement esql) {
      System.out.println("View flights");
      try{
         String query = "SELECT * FROM Schedule WHERE FlightNumber = '";
         System.out.print("\tEnter flight number: ");
         String input = in.readLine();
         query = query + input + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature2(AirlineManagement esql) {
      System.out.println("View flight seats");
      try{
         String query = "SELECT SeatsTotal - SeatsSold as SeatsAvailable, SeatsSold FROM FlightInstance Where FlightInstanceID = ";
         System.out.print("\tEnter flight: ");
         String inputFlight = in.readLine();
         query += inputFlight;
         System.out.print("\tEnter date (YYYY-MM-DD): ");
         String inputDate = in.readLine();
         query = query + "AND FlightDate = DATE '" + inputDate + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature3(AirlineManagement esql) {
      System.out.println("View flight status");
      try {
         String query = "Select CASE WHEN DepartedOnTime = 't' THEN 'true' ELSE 'false' END AS DepartedOnTime, CASE WHEN ArrivedOnTime = 't' THEN 'true' ELSE 'false' END AS ArrivedOnTime From FlightInstance Where FlightInstanceID = ";
         System.out.print("\tEnter FlightInstanceID: ");
         String input = in.readLine();
         System.out.print("\tEnter date (YYYY-MM-DD): ");
         String inputDate = in.readLine();
         query = query + input + "AND FlightDate = Date '" + inputDate + "'";
         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature4(AirlineManagement esql) {
      System.out.println("View flights of the day");
      try{
         String query = "SELECT * FROM FlightInstance WHERE FlightDate = ";
         System.out.print("\tEnter date (YYYY-MM-DD): ");
         String inputDate = in.readLine();
         query = query + "DATE '" + inputDate + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature5(AirlineManagement esql) {
      System.out.println("View full order id history");
      try{
         String query = "SELECT c.FirstName, c.LastName, r.Status FROM Customer c join Reservation r On c.CustomerID = r.customerID join FlightInstance f On f.FlightInstanceID = r.FlightInstanceID WHERE f.FlightInstanceID = ";
         System.out.print("\tEnter FlightInstanceID: ");
         String input = in.readLine();
         System.out.print("\tEnter date (YYYY-MM-DD): ");
         String inputDate = in.readLine();
         query = query + input + "AND f.FlightDate = DATE '" + inputDate + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature6(AirlineManagement esql) {
      try {
         System.out.print("\tEnter a reservation number: ");
         String reservationID = in.readLine();
         String query = "SELECT * FROM Customer c join Reservation r on c.CustomerID = r.customerID WHERE r.ReservationID = '" + reservationID + "'";
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature7(AirlineManagement esql) {
      try {
         String query = "SELECT Make, Model FROM Plane WHERE PlaneID = '";
         System.out.print("\t Enter PlaneID: ");
         String input = in.readLine();
         query = query + input + "'";
         int count = esql.executeQuery(query);
         if(count == 0) {
            System.out.println("No such plane, try again");
         }
         else {
            esql.executeQueryAndPrintResult(query);
         } 
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature8(AirlineManagement esql) {
      try {
         String query = "SELECT * FROM Repair r JOIN Technician t ON r.TechnicianID = t.TechnicianID WHERE t.TechnicianID = '";
         System.out.print("\t Enter TechnicianID: ");
         String input = in.readLine();
         query = query + input + "'";
         int count = esql.executeQuery(query);
         if(count == 0) {
            System.out.println("No such Technician, try again");
         }
         else {
            esql.executeQueryAndPrintResult(query);
         } 
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature9(AirlineManagement esql) {
      try {
         String query = "SELECT RepairDate, RepairCode FROM Repair WHERE PlaneID = '";
         System.out.print("\t Enter PlaneID: ");
         String plane = in.readLine();
         System.out.print("\t Enter start date for range (YYYY-MM-DD): ");
         String startDate = in.readLine();
         System.out.print("\t Enter end date for range (YYYY-MM-DD): ");
         String endDate = in.readLine();
         query = query + plane + "' AND RepairDate >= '" + startDate + "' AND RepairDate <= '" + endDate + "'";
         int count = esql.executeQuery(query);
         if(count == 0) {
            System.out.println("No such Technician, try again");
         }
         else {
            esql.executeQueryAndPrintResult(query);
         } 
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature10(AirlineManagement esql) {
      try{
         String query1 = "SELECT COUNT(*) as TotalOnTimeDepartures FROM FlightInstance Where FlightNumber = '";
         String query2 = "SELECT COUNT(*) as TotalOnTimeArrivals FROM FlightInstance Where FlightNumber = '";
         String query3 = "SELECT SUM(SeatsSold) as Total_Seats_Sold FROM FlightInstance Where FlightNumber = '";

         System.out.print("\tEnter flight: ");
         String inputFlight = in.readLine();
         System.out.print("\tEnter start date range (YYYY-MM-DD): ");
         String startDate = in.readLine();
         System.out.print("\tEnter end date range (YYYY-MM-DD): ");
         String endDate = in.readLine();

         query1 = query1 + inputFlight + "'  AND FlightDate >= '" + startDate + "'  AND FlightDate <= '" + endDate + "'  AND DepartedOnTime = TRUE";
         query2 = query2 + inputFlight + "'  AND FlightDate >= '" + startDate + "'  AND FlightDate <= '" + endDate + "'  AND ArrivedOnTime = TRUE";
         query3 = query3 + inputFlight + "'  AND FlightDate >= '" + startDate + "'  AND FlightDate <= '" + endDate + "'";
         esql.executeQueryAndPrintResult(query1);
         esql.executeQueryAndPrintResult(query2);
         esql.executeQueryAndPrintResult(query3);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature11(AirlineManagement esql) {
      try {
         System.out.print("\tEnter a destination city: ");
         String destinationCity = in.readLine();
         System.out.print("\tEnter a departure city: ");
         String departureCity = in.readLine();
         String query = "SELECT s.FlightNumber, s.DayOfWeek, s.DepartureTime, s.ArrivalTime, i.NumOfStops, COUNT(CASE WHEN ArrivedOnTime = True THEN 1 END) * 1.0 / COUNT(*) AS onTimePercentage FROM Schedule s join Flight f on s.FlightNumber = f.FlightNumber join FlightInstance i on i.FlightNumber = f.FlightNumber WHERE f.DepartureCity = '" + departureCity + "'  AND f.ArrivalCity = '" + destinationCity + "' GROUP BY s.FlightNumber, s.DayOfWeek, s.DepartureTime, s.ArrivalTime, i.NumOfStops";
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature12(AirlineManagement esql) {
      try {
         System.out.print("\tEnter a flight number: ");
         String flightNumber = in.readLine();
         String query = "SELECT FlightNumber, FlightDate, TicketCost FROM FlightInstance WHERE FlightNumber = '" + flightNumber + "'";
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature13(AirlineManagement esql) {
      try {
         System.out.print("\tEnter a flight number: ");
         String flightNumber = in.readLine();
         String query = "SELECT p.Make, p.Model FROM Flight f join Plane p on f.PlaneID = p.PlaneID WHERE f.FlightNumber = '" + flightNumber + "'";
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void feature14(AirlineManagement esql) {
      try {
         System.out.println("\tMaking a reservation");
         // System.out.println("\tEnter first name: ");
         // String firstName = in.readLine();
         // System.out.println("\tEnter last name");
         // String lastName = in.readLine();

         System.out.print("\tEnter customerID: ");
         String customerID = in.readLine();

         String numReservationsQuery = "SELECT COUNT(*) FROM RESERVATION";
         int numReservations = 0;
         List<List<String>> result = esql.executeQueryAndReturnResult(numReservationsQuery);
         numReservations = Integer.parseInt(result.get(0).get(0));
         numReservations++;
         String reservationID = String.format("R%04d", numReservations);

         System.out.print("\tEnter flight instance id: ");
         String flightInstanceID = in.readLine();

         String seatsSoldQuery = "SELECT SeatsSold FROM FlightInstance WHERE FlightInstanceID = '" + flightInstanceID + "'";
         int numSeatsSold = 0;
         List<List<String>> SeatsSold = new ArrayList<>();
         SeatsSold = esql.executeQueryAndReturnResult(seatsSoldQuery);
         numSeatsSold = Integer.parseInt(SeatsSold.get(0).get(0));

         String totalSeatsQuery = "SELECT SeatsTotal FROM FlightInstance WHERE FlightInstanceID = '" + flightInstanceID + "'";
         int numSeatsTotal = 0;
         List<List<String>> SeatsTotal = new ArrayList<>();
         SeatsTotal = esql.executeQueryAndReturnResult(totalSeatsQuery);
         numSeatsTotal = Integer.parseInt(SeatsTotal.get(0).get(0));

         String addReservation = null;
         if(numSeatsSold < numSeatsTotal) {
            addReservation = "INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status) VALUES ('" + reservationID + "', '" + customerID + "', '" + flightInstanceID + "', 'reserved');";
            esql.executeUpdate(addReservation);
            String updateTickets = "UPDATE FlightInstance SET SeatsSold = SeatsSold + 1, WHERE FlightInstanceID = '" + flightInstanceID + "'";
         }
         else {
            addReservation = "INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status) VALUES ('" + reservationID + "', '" + customerID + "', '" + flightInstanceID + "', 'waitlist');";
            esql.executeUpdate(addReservation);
         }
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

   public static void viewRepairsForPlaneInRange(AirlineManagement esql) {
      System.out.println("View all repairs for a plane within a date range");
      try {
         System.out.print("\tEnter Plane ID: ");
         String planeID = in.readLine();
         System.out.print("\tEnter start date (YYYY-MM-DD): ");
         String startDate = in.readLine();
         System.out.print("\tEnter end date (YYYY-MM-DD): ");
         String endDate = in.readLine();

         String query = "SELECT MaintenanceDate, RepairCode FROM Maintenance " +
                        "WHERE AircraftID = '" + planeID + "' " +
                        "AND MaintenanceDate BETWEEN DATE '" + startDate + "' AND DATE '" + endDate + "'";

         int rowCount = esql.executeQueryAndPrintResult(query);
         System.out.println("total row(s): " + rowCount);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
}

public static void viewPilotMaintenanceRequests(AirlineManagement esql) {
   System.out.println("View maintenance requests made by a pilot");
   try {
      System.out.print("\tEnter Pilot ID: ");
      String pilotID = in.readLine();

      String query = "SELECT PlaneID, RepairCode, RequestDate FROM MaintenanceRequest WHERE PilotID = '" + pilotID + "'";

      int rowCount = esql.executeQueryAndPrintResult(query);
      System.out.println("total row(s): " + rowCount);
   } catch (Exception e) {
      System.err.println(e.getMessage());
   }
}

public static void logRepairEntry(AirlineManagement esql) {
   System.out.println("Log a completed repair entry");
   try {
      System.out.print("\tEnter Plane ID: ");
      String planeID = in.readLine();
      System.out.print("\tEnter Repair Code: ");
      String repairCode = in.readLine();
      System.out.print("\tEnter Date of Repair (YYYY-MM-DD): ");
      String repairDate = in.readLine();

      String query = "INSERT INTO Maintenance (AircraftID, RepairCode, MaintenanceDate) VALUES " +
                     "('" + planeID + "', '" + repairCode + "', DATE '" + repairDate + "')";

      esql.executeUpdate(query);
      System.out.println("Repair entry logged successfully.");
   } catch (Exception e) {
      System.err.println(e.getMessage());
   }
}

public static void createMaintenanceRequest(AirlineManagement esql) {
   System.out.println("Create a maintenance request");
   try {
      System.out.print("\tEnter Pilot ID: ");
      String pilotID = in.readLine();
      System.out.print("\tEnter Plane ID: ");
      String planeID = in.readLine();
      System.out.print("\tEnter Repair Code: ");
      String repairCode = in.readLine();
      System.out.print("\tEnter Request Date (YYYY-MM-DD): ");
      String requestDate = in.readLine();

      String query = "INSERT INTO MaintenanceRequest (PilotID, PlaneID, RepairCode, RequestDate) VALUES " +
                     "('" + pilotID + "', '" + planeID + "', '" + repairCode + "', DATE '" + requestDate + "')";

      esql.executeUpdate(query);
      System.out.println("Maintenance request submitted.");
   } catch (Exception e) {
      System.err.println(e.getMessage());
   }
}


  


}//end AirlineManagement

