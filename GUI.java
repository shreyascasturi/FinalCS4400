import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.*; // import the big package.
import java.time.LocalDate;
public class GUI {
    public static String userID = ""; // set the basic String.
    public static boolean isAdmin  = false; // set the boolean as false.
    public static void main(String[] args) throws Exception {
        while (true) {
            //Scanner ChoiceIntroPage = new Scanner(System.in);
            System.out.println("------------------------");
            System.out.println("WELCOME TO OUR DATABASE APP");
            System.out.println("");
            System.out.println("--------- OPTIONS ---------");
            System.out.println("CHOOSE 1 FOR LOGIN");
            System.out.println("CHOOSE 2 FOR REGISTER");
            System.out.println("CHOOSE 3 TO QUIT");
            System.out.print("ENTER CHOICE: ");
            Scanner ChoiceIntroPage = new Scanner(System.in); // read in the input; either 1, 2, or 3.
            int ChoiceIntroString = ChoiceIntroPage.nextInt(); // we assume that it is an int.
            if (ChoiceIntroString == 1) {
                System.out.println("GO BACK INTO IT, LOGIN"); // assume login.
                int resultofLogin = checkLogin(); // do login, and eventually go from there. This int is unnecessary unless
                // you decide to exit out of the login screen.
                if (resultofLogin == -1) { // if -1, we'll choose to break. Zeroes and other things continue the outer loop.
                    break; // this ends the app.
                }
            } else if (ChoiceIntroString == 2) { // assume register.
                System.out.println("GO BACK INTO IT, REGISTER");
                int resultofRegister = register(); // do login, and eventually go from there. This int is unnecessary unless
                // you decide to exit out of the login screen.
                if (resultofRegister == -1) { // if -1, we'll choose to break.
                    break;
                }
                // goto register.
            } else if (ChoiceIntroString == 3) { // assume exit solely chosen in the choice intro page
                System.out.println("Thank you for using our app.");
                ChoiceIntroPage.close(); // close the scanner.
                System.exit(1); // end immediately here.
            } else { // assume incorrect input.
                System.out.println("You entered an incorrect number. Try again. Or quit.");
                System.out.println("");
            }
        }
        System.out.println("Thank you for using our app."); // exit screen obtained from exiting from other places.
        System.exit(1); // to make sure this works.
    }

    public static Connection getConnection() throws Exception { // open a Connection to the database.

        Connection conn = null;
        // Properties connectionProps = new Properties();
        // connectionProps.put("user", this.userName);
        // connectionProps.put("password", this.password);

        Class.forName("org.mariadb.jdbc.Driver"); // test if the driver that allows this connection exists.
        System.out.println("Driver loaded"); // nice little printout message.
        conn = DriverManager.getConnection(
                                           "jdbc:" + "mysql" + "://" +
                                           "localhost" + "/TMB", "test", "testpwd"); // connect using a link.
        System.out.println("Connected to database"); // print out nice message.
        return conn; // return the connection back to checkLogin.
    }

    public static int checkLogin() throws Exception {
        Connection newConnect = getConnection(); // check getConnection;
        Scanner userpwd = new Scanner(System.in); // login username/pwd scanner.        
        while (true) { // this keeps repeating if your username is wrong/doesn't exist in the database.
            System.out.println("----------- LOGIN SCREEN -------------");
            System.out.print("ENTER YOUR USERNAME: ");
            String username = userpwd.nextLine(); // get the username. // test "chal68";
            if (username.equalsIgnoreCase("exit")) {
                System.out.println("You exited the checkLogin at User, going back to the main welcome screen. Good-bye.");
                return 0; // go to the main welcome screen.
            } else if (username.equalsIgnoreCase("exitfull")) {
                System.out.println("You exited the checkLogin at User, basically ending your usage of the app. Good-bye.");
                return -1; // quit the app.
            } else {
                //System.out.println(""); // need to go down.
                Statement usercheck = newConnect.createStatement(); // we create a statement to be used.
                String userQuery = "SELECT ID FROM User WHERE ID = '" + username + "'"; // create the query and then execute. QUERY TO CHECK USERNAME VALIDITY
                ResultSet userSet = usercheck.executeQuery(userQuery); // see? Get the results as a ResultSet.
                if (!(userSet.isBeforeFirst())) { // i'm not gonna explain this one... but... if the set has no rows, then the set is empty.
                    System.out.println("you failed. Username is incorrect. Try again."); // if the set is empty... then the username is wrong.
                    System.out.println("------- RETURNING TO LOGIN SCREEN, USERNAME DOESN'T EXIST --------");
                } else {
                    while (true) { // an inner while-true loop, that only executes repeatedly if you fail your password login.
                        System.out.print("ENTER YOUR PASSWORD: "); // enter the password. test "eightchar".
                        String password = userpwd.nextLine();
                        if (password.equalsIgnoreCase("exit")) {
                            System.out.println("You exited the checkLogin at Password to go to the main welcome screen. Good-bye.");
                            return 0; // goes to the main welcome screen
                        } else if (password.equalsIgnoreCase("exitfull")) {
                            System.out.println("You exited the checkLogin at password to quit the app. Good-bye.");
                            return -1; // quits the app
                        } else {
                            System.out.println("");
                            Statement pwdcheck = newConnect.createStatement();
                            String passQuery = "SELECT password FROM User WHERE ID = '" + username + "' AND password = '" + password + "'"; // QUERY TO CHECK USERNAME + PWD VALIDITY
                            ResultSet pwdSet = pwdcheck.executeQuery(passQuery);
                            if (!(pwdSet.isBeforeFirst())) {
                                System.out.println("you entered password wrong. try again.");
                                System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");
                            } else {
                                System.out.println("Congrats, you're now logged in.");
                                System.out.println("Checking admin now...");
                                userID = username; // set the static variable as username.
                                Statement adminCheck = newConnect.createStatement(); // create new statement for admin check.
                                String adminQuery = "SELECT ID From Admin WHERE ID = '" + userID + "'"; // admin query creation 
                                ResultSet adminSet = adminCheck.executeQuery(adminQuery); //
                                if (!(adminSet.isBeforeFirst())) {
                                  while(true) {
                                    int passengerGUIresult = MainPassengerGUI(); // check how this works
                                    if (passengerGUIresult == 3) {
                                      int cardSuccess = buyCard(userID);
                                      System.out.println();
                                    }
                                    if (passengerGUIresult == 7) { // goto the login screen.
                                        break; // this should break out of the inner loop about the password filling in and go straight to the login screen.
                                    } else if (passengerGUIresult == 8) {
                                        return 0; // this takes us to the welcome screen.
                                    } else if (passengerGUIresult == 9) {
                                        return -1; // this quits the app and gives us the quit screen.
                                    } else {
                                        System.out.println("This should never be reached at all. This is in the passengerGui check in checkLogin.");
                                        System.out.println("Crash app completely.");
                                        System.exit(1); // this breaks the app with no quit screen other than "crash app completely."
                                    }                                
                                  }    
                                } else {
                                    System.out.println("admin motherfucker");
                                    isAdmin = true; // set the admin.                                    
                                    System.exit(1);                                    
                                }                                                                
                            }
                        }
                    }
                }
            }
        }
    }

    public static int MainPassengerGUI() throws Exception { // after login on the user is done.
        Connection newConnect = getConnection();
        Scanner choosePassengerGUI = new Scanner(System.in);
        while(true) {
            System.out.println("------------ MAIN PASSENGER 'GUI' ---------------------");
            System.out.printf("CHOOSE A NUMBER TO EXPLORE DIFFERENT OPTIONS. %n 1. Leave Review %n 2. View Reviews %n 3. Buy Card %n 4. Go On Trip %n 5. View Trips %n"
                              + " 6. Edit Profile %n 7. Goto Login %n 8. Goto Welcome Screen %n 9. Quit Fully %n");
            System.out.print("CHOOSE AN OPTION: ");
            int choosePassengerInt = choosePassengerGUI.nextInt(); // same old, basically.
            if (choosePassengerInt == 1) {
                leaveReview(newConnect);
                break; //return 0; // leave review
            } else if (choosePassengerInt == 2) {
                break; //return 0; // view review
            } else if (choosePassengerInt == 3) {
                return 3; //return 3(Go to card purchase screen);
            } else if (choosePassengerInt == 4) {
                break; //return 0;
            } else if (choosePassengerInt == 5) { // View Trip
                break; //return 0;
            } else if (choosePassengerInt == 6) { // edit profile
                break; //return 0;
            } else if (choosePassengerInt == 7) { // goto login screen. this means logout.
                System.out.println("------------LOGGING OUT GOTO LOGIN SCREEN------------");
                System.out.println("");
                userID = ""; // reset as empty.
                isAdmin = false; // reset as false even though it is false.
                return 7;
            } else if (choosePassengerInt == 8) { // goto welcome screen // means we must logout.
                System.out.println("------------LOGGING OUT GOTO WELCOME SCREEN------------");
                System.out.println("");
                userID = "";
                isAdmin = false;
                return 8;
            } else if (choosePassengerInt == 9) { // quit fully. full logout as well.
                System.out.println("------------LOGGING OUT EXIT FULLY------------");
                System.out.println("");
                userID = "";
                isAdmin = false;
                return 9;
            } else {
                System.out.println("You chose an incorrect number. Try again.");
            }
        }
        return 0;
         // breaks out of the big while-loop, will hit if exit at password.
    }

    public static int register() throws Exception {
        Connection newConnect = getConnection();
        Scanner registration = new Scanner(System.in);

        String firstName;
        String mi;
        String lastName;
        String email;
        String userID;
        String pass1;
        String pass2;

        while(true) {
            System.out.print("ENTER FIRST NAME: ");
            firstName = registration.nextLine();
            if (firstName.equalsIgnoreCase("exit")) {
                System.out.println("You exited the registration at FirstName. Good-bye.");
                break; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
            } else {
                if (firstName.length() == 0) {
                    System.out.println("Type a first name please");
                } else {
                    while (true) {
                        System.out.println("ENTER MIDDLE INITIAL: ");
                        String middleI = registration.nextLine();
                        mi = middleI.length() == 0 ? "NULL" : middleI;
                        if (mi.equalsIgnoreCase("exit")) {
                            System.out.println("You exited the registration at Middle Initial. Good-bye.");
                            break; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
                        } else {
                            while (true) {
                                System.out.println("ENTER LAST NAME: ");
                                lastName = registration.nextLine();
                                if (lastName.equalsIgnoreCase("exit")) {
                                    System.out.println("You exited the registration at LastName. Good-bye.");
                                    break;
                                } else {
                                    if (lastName.length() == 0) {
                                        System.out.println("Type a last name please");
                                    } else {
                                        while (true) {
                                            System.out.println("ENTER EMAIL: ");
                                            email = registration.nextLine();
                                            if (email.equalsIgnoreCase("exit")) {
                                                System.out.println("You exited the registration at Email. Good-bye.");
                                                break;
                                            } else {
                                                if (email.length() == 0) {
                                                    System.out.println("Type an email please");
                                                } else {
                                                    while (true) {
                                                        System.out.println("ENTER USERID: ");
                                                        userID = registration.nextLine();
                                                        if (userID.equalsIgnoreCase("exit")) {
                                                            System.out.println("You exited the registration at UserID. Good-bye.");
                                                            break;
                                                        } else {
                                                            if (userID.length() == 0) {
                                                                System.out.println("Type a UserID please");
                                                            } else {
                                                                while (true) {
                                                                    System.out.println("ENTER PASSWORD: ");
                                                                    pass1 = registration.nextLine();
                                                                    if (pass1.equalsIgnoreCase("exit")) {
                                                                        System.out.println("You exited the registration at Password. Good-bye.");
                                                                        break;
                                                                    } else {
                                                                        if (pass1.length() == 0) {
                                                                            System.out.println("Type a password please");
                                                                        } else {
                                                                            while (true) {
                                                                                System.out.println("RE-ENTER PASSWORD: ");
                                                                                pass2 = registration.nextLine();
                                                                                if (pass2.equalsIgnoreCase("exit")) {
                                                                                    System.out.println("You exited the registration at Password. Good-bye.");
                                                                                    break;
                                                                                } else {
                                                                                    if (pass2.length() == 0) {
                                                                                        System.out.println("Type a Password please");
                                                                                    } else if (!pass1.equals(pass2)) {
                                                                                        System.out.print("Make sure the passwords match");
                                                                                    } else {
                                                                                        System.out.println("");
                                                                                        Statement rgstrcheck = newConnect.createStatement();
                                                                                        String passQuery = "INSERT INTO User VALUES ('" + userID + "', '" + firstName + "', '" + mi + "', '" + lastName + "', '" + pass1 + "', '" + email + "')";
                                                                                        ResultSet rgstrSet = rgstrcheck.executeQuery(passQuery);
                                                                                        String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                                        ResultSet testSet = rgstrcheck.executeQuery(testQuery);

                                                                                        if (!(testSet.isBeforeFirst())) {
                                                                                            System.out.println("you entered password wrong. try again.");
                                                                                            System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");
                                                                                        } else {
                                                                                            System.out.println("Congrats, you're now logged in.");
                                                                                            System.exit(1); // break completely;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("HITTING LINE OUTSIDE BIG BLOCK, DONE WHEN EXIT TYPED IN USERNAME CHECK");
        return 0; // this should never hit.
    }

    public static void leaveReview(Connection connection) throws Exception { // leave reviews, USER/ADMIN
         Connection newConnection = connection; // pass the connection in.
         // be able to list all the stations
         Statement getAllStations = newConnection.createStatement();

         // We want the stations that are admin-approved AND are on admin-approved lines.
         String getStationsQuery = "SELECT name FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name"; // this gets us the actual table of names.
         String getStationQueryNum = "SELECT COUNT(name) FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name"; // this gets us the count.
         ResultSet getStations = getAllStations.executeQuery(getStationsQuery); // get the stations.
         ResultSet getNumStations = getAllStations.executeQuery(getStationQueryNum); // get the count of stations.
         int getNum = -5; // set random num
         while(getNumStations.next()) {
             getNum = Integer.parseInt(getNumStations.getString("COUNT(name)")); // getNum = count of stations.
         }
         String[] arrStations = new String[getNum]; // create string array to hold the names of the stations
         int fillIndex = 0; // simple loop to go through ResultSet getStations.
         while (getStations.next()) {
             arrStations[fillIndex] = getStations.getString("name"); // get the value out of the column "name"
             fillIndex++;
         }
         String actualArray = Arrays.toString(arrStations); // turn this into a printable thing. finally, fucking use ARRAYS.
         System.out.println("This is the array of stations: " + Arrays.toString(arrStations)); // print array.
         System.exit(1);
     }

    public static int buyCard(String userID) throws Exception {
        while(true) {
            System.out.println();
            System.out.println("CHOOSE 1 FOR T-mes");
            System.out.println("CHOOSE 2 FOR T-10");
            System.out.println("CHOOSE 3 FOR T-50/30");
            System.out.println("CHOOSE 4 FOR T-jove");
            System.out.println("CHOOSE 5 TO QUIT");
            System.out.print("ENTER CHOICE: ");

            Scanner ChoiceCardType = new Scanner(System.in); // read in the input; either 1, 2, 3, 4, or 5
            int ChoiceCardString = ChoiceCardType.nextInt();

            int resultOfPurchase;

            if (ChoiceCardString == 1) {
                 System.out.println("GO BACK INTO IT, PURCHASE T-mes"); // assume T-mes
                 return insertCard(userID, "T-mes", "NULL");

                // Statement cardcheck = newConnect.createStatement();
                // String passQuery = "INSERT INTO Card VALUES ('" + userID + "', 'T-mes', '" + curDate + "', 'N/A', '" + ((curMonth + 1) / 12) + "/" + curDay + "/" + curYear + "')";
                // ResultSet cardSet = rgstrcheck.executeQuery(passQuery);
                // String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                // ResultSet testSet = rgstrcheck.executeQuery(testQuery);

                // // you decide to exit out of the login screen.
                // if (resultofLogin == 1) { // if 1, we'll choose to break.
                //     break;
                // }
            } else if (ChoiceCardString == 2) { // assume T-10.
                System.out.println("GO BACK INTO IT, PURCHASE T-10");

                return insertCard(userID, "T-10", "'10'");

            } else if (ChoiceCardString == 3) { // assume T-50/30.
                System.out.println("GO BACK INTO IT, PURCHASE T-50/30");

                return insertCard(userID, "T-50/30", "'50'");

            } else if (ChoiceCardString == 4) { //assume T-jove
                System.out.println("GO BACK INTO IT, PURCHASE T-jove");

                return insertCard(userID, "T-jove", "NULL");

            }  else if (ChoiceCardString == 5) { //assume exit
                System.out.println("You exited during card purchase. Goodbye");
                System.exit(1);
            } else { // assume incorrect input.
                System.out.println("You entered an incorrect number. Try again. Or quit.");
                System.out.println("");
            }
        }

    }

    public static int insertCard(String userID, String type, String uses) throws Exception{
        Connection newConnect = getConnection();

        LocalDate localDate = LocalDate.now();
        int curYear = localDate.getYear();
        int curMonth = localDate.getMonthValue();
        int curDay = localDate.getDayOfMonth();
        int realYear = curYear;
        String realDay = String.valueOf(curDay);
        if ((type.equals("T-jove") && curMonth + 3 > 12) || curMonth + 1 > 12) { //checks if expiration date would push year over
            realYear = curYear + 1;
        }
        if (curDay < 10) { //checks if day is single digit so sql will accept it
            realDay = "0" + realDay;
        }

        String expDate = type.equals("T-10") ? "NULL" : type.equals("T-jove") ? "'" + realYear + "-" + ((curMonth + 3) % 12) + "-" + realDay + "'" : "'" + realYear + "-" + ((curMonth + 1) % 12) + "-" + realDay + "'";//sets expiration date accomodating for edge cases
        //String expDate = type.equals("T-10") ? "NULL" : type.equals("T-jove") ? "DATEADD(CAST (sysdate() AS date), INTERVAL 3 month)" : "DATEADD(month, 1, CAST (sysdate() AS date) )";
        //String expDate = "2020-10-03";

        Statement cardcheck = newConnect.createStatement();

        String passQuery = "INSERT INTO Card VALUES ('" + userID + "', '" + type + "', " + "sysdate()" + ", " + uses + ", " + expDate + ")";//Query to put new card in Card
        ResultSet cardSet = cardcheck.executeQuery(passQuery);
        // String testQuery = "SELECT * FROM Card WHERE ID='" + userID + "' AND type_of_card='" + type + "' AND purchase_date_time='" + curDate + "'";
        // ResultSet testSet = cardcheck.executeQuery(testQuery);

        // if (!(testSet.isBeforeFirst())) {
        //     System.out.println("Purchase failed");
        //     System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");
        // } else {
            System.out.println("Congrats, you have a card.");
            return 1;
            //System.exit(1); // break completely;
        // }
    }
}
