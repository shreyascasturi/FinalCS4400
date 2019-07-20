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
            if (ChoiceIntroString == 1) { // assume login.
                int resultofLogin = checkLogin(); // do login, and eventually go from there. This int is unnecessary unless
                // you decide to exit out of the login screen.
                if (resultofLogin == -1) { // if -1, we'll choose to break. Zeroes and other things continue the outer loop.
                    break; // this ends the app.
                }
            } else if (ChoiceIntroString == 2) { // assume register.
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
                                System.out.println("Checking admin now...");
                                userID = username; // set the static variable as username.
                                Statement adminCheck = newConnect.createStatement(); // create new statement for admin check.
                                String adminQuery = "SELECT ID From Admin WHERE ID = '" + userID + "'"; // admin query creation
                                ResultSet adminSet = adminCheck.executeQuery(adminQuery); //
                                if (!(adminSet.isBeforeFirst())) {
                                    System.out.println("Congrats, you're now logged in.");
                                    while(true) {                                     
                                        int passengerGUIresult = MainPassengerGUI(); // check how this works
                                        if (passengerGUIresult == 1) {
                                            int leaveReview = leaveReview(userID, newConnect); // LEAVE REVIEW
                                            System.out.println(); // shouldn't do anything really.
                                        } else if (passengerGUIresult == 2) { // VIEW REVIEWS
                                            int viewResults = viewReviews(userID, newConnect, "REGULAR");
                                            System.out.println();
                                        } else if (passengerGUIresult == 3) {
                                            int cardSuccess = buyCard(userID); // BUYING CARD
                                            System.out.println();
                                        } else if (passengerGUIresult == 4) { // GO ON TRIP
                                            int tripCreatesuccess = goOnTrip(userID);
                                            System.out.println();  
                                        } else if (passengerGUIresult == 5) { // VIEW TRIP
                                            int viewTripSuccess = viewTrip(userID, "card_type");
                                            System.out.println();
                                        } else if (passengerGUIresult == 6) { // EDITING USER PROFILE
                                            int editSuccess = editUser(userID);
                                            if (editSuccess == -1) {
                                                return 0; // get back to the welcoming screen.
                                            }
                                            System.out.println();
                                        } else if (passengerGUIresult == 7) { // GOTO LOGIN SCREEN
                                            break; // this should break out of the inner loop about the password filling in and go straight to the login screen.
                                        } else if (passengerGUIresult == 8) { // GOTO WELCOME SCREEN
                                            return 0; // this takes us to the welcome screen.
                                        } else if (passengerGUIresult == 9) { // QUIT FULLY
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
            System.out.println("------------ MAIN PAGE ---------------------");
            System.out.println();
            System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.           
            System.out.printf("CHOOSE A NUMBER TO EXPLORE DIFFERENT OPTIONS. %n 1. Leave Review %n 2. View Reviews %n 3. Buy Card %n 4. Go On Trip %n 5. View Trips %n"
                              + " 6. Edit Profile %n 7. Goto Login %n 8. Goto Welcome Screen %n 9. Quit Fully %n");
            System.out.print("CHOOSE AN OPTION: ");
            int choosePassengerInt = choosePassengerGUI.nextInt(); // same old, basically.
            if (choosePassengerInt == 1) {
                // leaveReview(newConnect);
                // break; //return 0; // leave review
                System.out.println("------------LEAVE REVIEW------------"); // LEAVE REVIEW
                System.out.println("");
                return 1;
            } else if (choosePassengerInt == 2) {
                System.out.println("------------VIEW REVIEWS------------");// view review
                System.out.println("");
                return 2;
            } else if (choosePassengerInt == 3) {
                System.out.println("------------PURCHASE CARD------------");
                System.out.println("");
                return 3; //return 3(Go to card purchase screen);
            } else if (choosePassengerInt == 4) { // Create Trip
                System.out.println("------------PLAN TRIP------------");
                System.out.println("");
                return 4; //return 4(Go to Go On Trip screen)
            } else if (choosePassengerInt == 5) { // View Trip
                System.out.println("--------------VIEWING TRIPS----------");
                return 5;                
            } else if (choosePassengerInt == 6) { // edit profile
                System.out.println("------------EDIT USER INFORMATION------------");
                System.out.println("");
                return 6; //return 0;
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
                return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
            } else {
                if (firstName.length() == 0 || firstName.equals("NULL")) {
                    System.out.println("Type a first name please");
                } else {
                    while (true) {
                        System.out.println("ENTER MIDDLE INITIAL: ");
                        String middleI = registration.nextLine();
                        mi = middleI.length() == 0 ? "NULL" : middleI;
                        if (mi.equalsIgnoreCase("exit")) {
                            System.out.println("You exited the registration at Middle Initial. Good-bye.");
                            return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
                        } else {
                            if (mi.length() > 1 && !mi.equals("NULL")) {
                                System.out.println("Middle initial should be one character.");
                            } else {
                                while (true) {
                                    System.out.println("ENTER LAST NAME: ");
                                    lastName = registration.nextLine();
                                    if (lastName.equalsIgnoreCase("exit")) {
                                        System.out.println("You exited the registration at LastName. Good-bye.");
                                        return 0;
                                    } else {
                                        if (lastName.length() == 0 || lastName.equals("NULL")) {
                                            System.out.println("Type a last name please");
                                        } else {
                                            while (true) {
                                                System.out.println("ENTER EMAIL: ");
                                                email = registration.nextLine();
                                                if (email.equalsIgnoreCase("exit")) {
                                                    System.out.println("You exited the registration at Email. Good-bye.");
                                                    return 0;
                                                } else {
                                                    if (email.length() == 0 || email.equals("NULL")) {
                                                        System.out.println("Type an email please");
                                                    } else {
                                                        while (true) {
                                                            System.out.println("ENTER USERID: ");
                                                            userID = registration.nextLine();
                                                            if (userID.equalsIgnoreCase("exit")) {
                                                                System.out.println("You exited the registration at UserID. Good-bye.");
                                                                return 0;
                                                            } else {
                                                                Statement rgstrcheck = newConnect.createStatement();
                                                                String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                ResultSet testSet = rgstrcheck.executeQuery(testQuery);
                                                                boolean unique = true;
                                                                if (userID.length() == 0 || userID.equals("NULL")) {
                                                                    System.out.println("Type a UserID please");
                                                                }  else if (testSet.isBeforeFirst()) {
                                                                    System.out.println("Choose a unique UserID pls");
                                                                } else {
                                                                    while (true) {
                                                                        System.out.println("ENTER PASSWORD: ");
                                                                        pass1 = registration.nextLine();
                                                                        if (pass1.equalsIgnoreCase("exit")) {
                                                                            System.out.println("You exited the registration at Password. Good-bye.");
                                                                            return 0;
                                                                        } else {
                                                                            if (pass1.length() < 8) {
                                                                                System.out.println("Type a password of length 8 or greater please");
                                                                            } else {
                                                                                while (true) {
                                                                                    System.out.println("RE-ENTER PASSWORD: ");
                                                                                    pass2 = registration.nextLine();
                                                                                    if (pass2.equalsIgnoreCase("exit")) {
                                                                                        System.out.println("You exited the registration at Password. Good-bye.");
                                                                                        return 0;
                                                                                    } else {
                                                                                        if (!pass1.equals(pass2)) {
                                                                                            System.out.print("Make sure the passwords match");
                                                                                        } else {
                                                                                            System.out.println("");

                                                                                            mi = mi.equals("NULL") ? mi : "'" + mi + "'";


                                                                                            String passQuery = "INSERT INTO User VALUES ('" + userID + "', '" + firstName + "', " + mi + ", '" + lastName + "', '" + pass1 + "', '" + email + "')";
                                                                                            ResultSet rgstrSet = rgstrcheck.executeQuery(passQuery);
                                                                                            // String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                                            // ResultSet testSet = rgstrcheck.executeQuery(testQuery);

                                                                                            // if (!(testSet.isBeforeFirst())) {
                                                                                            //     System.out.println("username is not unique. try again.");
                                                                                            //     System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");
                                                                                            // } else {
                                                                                                System.out.println("Congrats, you're now registered.");
                                                                                                return 0; // break completely;
                                                                                            // }
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
    }

    public static int leaveReview(String userID, Connection connection) throws Exception { // leave reviews, USER/ADMIN
         Connection newConnection = connection; // pass the connection in.
         // be able to list all the stations
         Statement getAllStations = newConnection.createStatement();
         Scanner chooseStation = new Scanner(System.in); // you're gonna use this to get the station to work with.

         int shoppingRating = -1; // initialization of ratings stuff.
         int connectionRating = -1;
         String nameOfStation = null;
         String commentLeft = null;               
         // We want the stations that are admin-approved AND are on admin-approved lines.
         String getStationsQuery = "SELECT name FROM Station ORDER BY name"; // order by name, assume we only have to get from stations.
         String getStationQueryNum = "SELECT COUNT(name) FROM Station"; // get the count of names.nnn
         // String getStationsQuery = "SELECT name FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name ORDER BY"; // this gets us the actual table of names.
         // String getStationQueryNum = "SELECT COUNT(name) FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name"; // this gets us the count.
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

         System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
         
         String actualArray = Arrays.toString(arrStations); // turn this into a printable thing. finally, fucking use ARRAYS.

         while(true) { // a massive loop because fuck
             System.out.println("ARRAY FOR STATIONS: " + Arrays.toString(arrStations)); // print array.
             System.out.print("CHOOSE BY INDEX YOUR STATION; 0 BEING FIRST, N - 1 BEING THE NTH: "); // choose by INDEX from the array.
             int getInt = chooseStation.nextInt();
             if (getInt < 0 || getInt >= getNum) {
                 System.out.println("Pick a number greater than 0 and less than or equal to n-1."); // check invalid INDEX
                 System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
             } else {
                 shoppingRating = -1; // assume we did get valid index, set shopping and connection ratings.
                 connectionRating = -1;
                 while(true) { // these smaller and smaller infinite loops restrict our errors, so making one error forces us to solve that
                     // one error before moving on, input-wise.
                     System.out.print("CHOOSE A SHOPPING RATING FROM 0 TO 5: ");
                     shoppingRating = chooseStation.nextInt();
                     if (shoppingRating < 0 || shoppingRating > 5) {
                         System.out.println("Choose a valid rating."); // valid check on shopping rating.
                         System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
                     } else {
                         while(true) {
                             System.out.print("CHOOSE A CONNECTION RATING FROM 0 TO 5: "); // valid check on
                             connectionRating = chooseStation.nextInt();
                             if (connectionRating < 0 || connectionRating > 5) {
                                 System.out.println("Choose a valid number");
                                 System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
                             } else {
                                 System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user. // throwing identification everywhere so jackson doesn't get mad.
                                 Scanner captureReview = new Scanner(System.in);
                                 System.out.print("LEAVE A COMMENT ABOUT THE STATION (OPTIONAL): ");
                                 commentLeft = captureReview.nextLine();
                                 if (commentLeft.length() == 0 || commentLeft.equals("NULL")) {
                                     commentLeft = "NULL";
                                 } 
                                 ResultSet r = getAllStations.executeQuery("SELECT COUNT(rid) AS rowcount FROM Review WHERE passenger_ID='" + userID + "'");
                                 r.next();
                                 int count = r.getInt("rowcount") + 1;
                                 r.close();

                                 String passQuery = "INSERT INTO Review VALUES ('" + userID + "', " + count + ", " + shoppingRating + ", " + connectionRating +", '" + commentLeft +  "', NULL, 'pending', NULL, '" + arrStations[getInt] + "')";
                                 ResultSet rgstrSet = getAllStations.executeQuery(passQuery);
                                 System.out.println("You left a review.");
                                 return 0;
                                 // TODO: OBTAIN THE NEW RID BY GETTING ALL REVIEWS BY A PARTICULAR USER AND GETTING THE MAX
                                 // NUMBER OF THOSE RIDS, ADD 1 TO GET THE NEW RID - IT'S A NEW REVIEW.
                                 // TODO: WRITE THE QUERY TO ADD EVERYTHING AS A REVIEW TUPLE.
                                 // TODO: TEST AND SEE IF THIS WORKS
                                 // edit timestamp should be null
                             }
                         }
                     }
                 }

             }

         }

         //System.exit(1); // exit for now.
     }

    public static int buyCard(String userID) throws Exception {
        while(true) {                   
            System.out.println();
            System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user. // random identifying nonsense
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
                return 0;
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

    public static int editUser(String ID) throws Exception {
        Connection newConnect = getConnection();
        Scanner editor = new Scanner(System.in);

        while (true) {
            System.out.println("CHOOSE 1 TO UPDATE ACCOUNT");
            System.out.println("CHOOSE 2 TO DELETE ACCOUNT");
            System.out.println("CHOOSE 3 TO QUIT TO MAIN GUI"); 
            System.out.print("ENTER CHOICE: ");

            Scanner ChoiceEditType = new Scanner(System.in);
            int choiceEditString = ChoiceEditType.nextInt();

            if (choiceEditString == 1) {
                String firstName;
                String mi;
                String lastName;
                String email;
                String userID;
                String pass1;
                String pass2;

                while(true) {
                    System.out.print("ENTER FIRST NAME: ");
                    firstName = editor.nextLine();
                    if (firstName.equalsIgnoreCase("exit")) {
                        System.out.println("You exited the editor at FirstName. Good-bye.");
                        return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
                    } else {
                        if (firstName.length() == 0) {
                            System.out.println("Type a first name please");
                        } else {
                            while (true) {
                                System.out.println("ENTER MIDDLE INITIAL: ");
                                String middleI = editor.nextLine();
                                mi = middleI.length() == 0 ? "NULL" : middleI;
                                if (mi.equalsIgnoreCase("exit")) {
                                    System.out.println("You exited the editor at Middle Initial. Good-bye.");
                                    return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
                                } else {
                                    while (true) {
                                        System.out.println("ENTER LAST NAME: ");
                                        lastName = editor.nextLine();
                                        if (lastName.equalsIgnoreCase("exit")) {
                                            System.out.println("You exited the editor at LastName. Good-bye.");
                                            return 0;
                                        } else {
                                            if (lastName.length() == 0) {
                                                System.out.println("Type a last name please");
                                            } else {
                                                while (true) {
                                                    System.out.println("ENTER EMAIL: ");
                                                    email = editor.nextLine();
                                                    if (email.equalsIgnoreCase("exit")) {
                                                        System.out.println("You exited the editor at Email. Good-bye.");
                                                        return 0;
                                                    } else {
                                                        if (email.length() == 0) {
                                                            System.out.println("Type an email please");
                                                        } else {
                                                            while (true) {
                                                                System.out.println("ENTER USERID: ");
                                                                userID = editor.nextLine();
                                                                if (userID.equalsIgnoreCase("exit")) {
                                                                    System.out.println("You exited the editor at UserID. Good-bye.");
                                                                    return 0;
                                                                } else {
                                                                    if (userID.length() == 0) {
                                                                        System.out.println("Type a UserID please");
                                                                    } else {
                                                                        while (true) {
                                                                            System.out.println("ENTER PASSWORD: ");
                                                                            pass1 = editor.nextLine();
                                                                            if (pass1.equalsIgnoreCase("exit")) {
                                                                                System.out.println("You exited the editor at Password. Good-bye.");
                                                                                return 0;
                                                                            } else {
                                                                                if (pass1.length() == 0) {
                                                                                    System.out.println("Type a password please");
                                                                                } else {
                                                                                    while (true) {
                                                                                        System.out.println("RE-ENTER PASSWORD: ");
                                                                                        pass2 = editor.nextLine();
                                                                                        if (pass2.equalsIgnoreCase("exit")) {
                                                                                            System.out.println("You exited the editor at Password. Good-bye.");
                                                                                            return 0;
                                                                                        } else {
                                                                                            if (pass2.length() == 0) {
                                                                                                System.out.println("Type a Password please");
                                                                                            } else if (!pass1.equals(pass2)) {
                                                                                                System.out.print("Make sure the passwords match");
                                                                                            } else {
                                                                                                System.out.println("");
                                                                                                Statement rgstrcheck = newConnect.createStatement();
                                                                                                String passQuery = "UPDATE User SET ID = '" + userID + "', first_name = '" + firstName + "', minit = '" + mi + "', last_name = '" + lastName + "', password = '" + pass1 + "', passenger_email = '" + email + "' WHERE ID='" + ID + "'"; // YOU HAVE TO CHANGE WHERE IT'S ID.
                                                                                                //String passQuery = "INSERT INTO User VALUES ('" + userID + "', '" + firstName + "', '" + mi + "', '" + lastName + "', '" + pass1 + "', '" + email + "')";
                                                                                                ResultSet rgstrSet = rgstrcheck.executeQuery(passQuery);
                                                                                                String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                                                ResultSet testSet = rgstrcheck.executeQuery(testQuery);
                                                                                                if (!testSet.isBeforeFirst()) { // if the set is empty, then it means
                                                                                                    System.out.println("information edit failed.");
                                                                                                    System.out.println("-------- RETURNING TO LOGIN SCREEN, EDIT FAILED ---------");
                                                                                                    return -1;
                                                                                                } else {
                                                                                                    System.out.println("Congrats, your account has been edited. We'll need to return you to the login screen.");
                                                                                                    return -1;               
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
                // System.out.println("HITTING LINE OUTSIDE BIG BLOCK, DONE WHEN EXIT TYPED IN USERNAME CHECK");
                // return 0; // this should never hit.
            } else if (choiceEditString == 2) {
                Statement editcheck = newConnect.createStatement();
                String passQuery = "DELETE FROM User WHERE ID='" + ID + "'";
                ResultSet editset = editcheck.executeQuery(passQuery);

                String testQuery = "SELECT * FROM User WHERE ID='" + ID + "'";
                ResultSet testSet = editcheck.executeQuery(testQuery);

                if (!(testSet.isBeforeFirst())) {
                    System.out.println("Congrats you deleted your account!");
                    System.out.println("------- RETURNING TO WELCOME SCREEN ------- ");
                    return -1;
                } else {
                    System.out.println("Delete failed");
                    System.out.println("-------- RETURNING TO MAIN GUI, DELETE DIDN'T WORK ---------");                                        
                    return 1;
                }
            } else if (choiceEditString == 3) {
                System.out.println("You exited while editing user info. Goodbye");
                return 0;
            } else {
                System.out.println("You entered an incorrect number. Try again. Or quit.");
                System.out.println("");
            }
        }
    }

    public static int goOnTrip(String userID) throws Exception {
        Connection newConnect = getConnection();
        Scanner editor = new Scanner(System.in);

        Statement getAllStations = newConnect.createStatement();

        String getStationsQuery = "SELECT name FROM Station ORDER BY name"; // order by name, assume we only have to get from stations.
        String getStationQueryNum = "SELECT COUNT(name) FROM Station"; // get the count of names.nnn
        // String getStationsQuery = "SELECT name FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name ORDER BY"; // this gets us the actual table of names.
        // String getStationQueryNum = "SELECT COUNT(name) FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name"; // this gets us the count.
        ResultSet getStations = getAllStations.executeQuery(getStationsQuery); // get the stations.
        ResultSet getNumStations = getAllStations.executeQuery(getStationQueryNum); // get the count of stations.
        int stationCount = -5; // set random num
        while(getNumStations.next()) {
            stationCount = Integer.parseInt(getNumStations.getString("COUNT(name)")); // getNum = count of stations.
        }
        String[] arrStations = new String[stationCount]; // create string array to hold the names of the stations
        int fillIndex = 0; // simple loop to go through ResultSet getStations.
        while (getStations.next()) {
            arrStations[fillIndex] = getStations.getString("name"); // get the value out of the column "name"
            fillIndex++;
        }
        //String actualArray = Arrays.toString(arrStations); // turn this into a printable thing. finally, fucking use ARRAYS.
        System.out.println("ARRAY FOR STATIONS: " + Arrays.toString(arrStations)); // print array.

        while(true) { // classic repeating loop
            System.out.println("----------- TRIP PLAN -------------");
            System.out.print("CHOOSE BY INDEX YOUR STARTING STATION; 0 BEING FIRST, N - 1 BEING THE NTH OR -1 TO QUIT: "); // choose by INDEX from the array.
            int stationInt = editor.nextInt();
            if (stationInt < -1 || stationInt >= stationCount) {
                System.out.println("Pick a number greater than 0 and less than or equal to n-1 or -1 to quit."); // check invalid INDEX
            } else if (stationInt == -1) {
                System.out.println("Returning to main menu");
                return 0;
            } else {

                Statement getAllCards = newConnect.createStatement();

                while (true) {
                    String getCardsQuery = "SELECT type_of_card, purchase_date_time, uses_left, expiration_date  FROM Card WHERE user_ID='" + userID + "'"; // order by name, assume we only have to get from Cards.
                    String getCardQueryNum = "SELECT COUNT(purchase_date_time) FROM Card WHERE user_ID='" + userID + "'"; // get the count of names.nnn
                    // String getCardsQuery = "SELECT name FROM Card JOIN Card_On_Line WHERE Card_On_Line.Card_name = Card.name ORDER BY"; // this gets us the actual table of names.
                    // String getCardQueryNum = "SELECT COUNT(name) FROM Card JOIN Card_On_Line WHERE Card_On_Line.Card_name = Card.name"; // this gets us the count.
                    ResultSet getCards = getAllCards.executeQuery(getCardsQuery); // get the Cards.
                    ResultSet getNumCards = getAllCards.executeQuery(getCardQueryNum); // get the count of Cards.
                    int cardCount = -5; // set random num
                    while(getNumCards.next()) {
                        cardCount = Integer.parseInt(getNumCards.getString("COUNT(purchase_date_time)")); // getNum = count of Cards.
                    }
                    String[] arrCards = new String[cardCount];
                    String[] arrCardTypes = new String[cardCount]; // create string array to hold the types of the Cards
                    String[] arrCardDate = new String[cardCount];
                    String[] arrUses = new String[cardCount];
                    String[] arrExpCardDate = new String[cardCount];
                    fillIndex = 0; // simple loop to go through ResultSet getCards.
                    while (getCards.next()) {
                        arrCardTypes[fillIndex] = getCards.getString("type_of_card");
                        arrCardDate[fillIndex] = getCards.getString("purchase_date_time"); // get the value out of the column "purchase_date_time"
                        arrExpCardDate[fillIndex] = getCards.getString("expiration_date");
                        arrUses[fillIndex] = getCards.getString("uses_left");
                        String usesLeft = arrUses[fillIndex] == null ? "UNLIMITED" : arrUses[fillIndex];
                        arrCards[fillIndex] = arrCardTypes[fillIndex] + " USES LEFT: " + usesLeft;
                        fillIndex++;
                    }

                    if (arrCards.length == 0) {
                        System.out.println("You must purchase a card to go on a trip, returning to main menu");
                        return 0;
                    }



                    String actualCardArray = Arrays.toString(arrCards); // turn this into a printable thing. finally, fucking use ARRAYS.
                    System.out.println("ARRAY FOR CARDS: " + Arrays.toString(arrCards)); // print array.

                    System.out.print("CHOOSE BY INDEX THE CARD YOU WISH TO USE; 0 BEING FIRST, N - 1 BEING THE NTH OR -1 TO QUIT: ");



                    LocalDate localDate = LocalDate.now();
                    int curYear = localDate.getYear();
                    int curMonth = localDate.getMonthValue();
                    int curDay = localDate.getDayOfMonth();

                    int cardInt = editor.nextInt();

                    if (cardInt == -1) {
                        System.out.println("Returning to main menu");
                        return 0;
                    }

                    if (cardInt < 0 || cardInt >= cardCount) {
                        System.out.println("Pick a number greater than 0 and less than or equal to n-1."); // check invalid INDEX
                    } else {

                        String expYear = "9999";
                        String expMonth = "99";
                        String expDay = "99";
                        if (!(arrExpCardDate[cardInt] == null)) {
                            expYear = arrExpCardDate[cardInt].substring(0, 4);
                            expMonth = arrExpCardDate[cardInt].substring(5, 7);
                            expDay = arrExpCardDate[cardInt].substring(8, arrExpCardDate[cardInt].length());
                        }

                        if ((!(arrUses[cardInt] == null) && arrUses[cardInt].equals("0")) || ((!(arrExpCardDate[cardInt] == null)) && (expYear.compareTo(String.valueOf(curYear)) < 0 || (expYear.equals(String.valueOf(curYear)) && expMonth.compareTo(curMonth < 10 ? "0" + curMonth : String.valueOf(curMonth)) < 0) || (expYear.equals(String.valueOf(curYear)) && expMonth.equals(curMonth < 10 ? "0" + curMonth : String.valueOf(curMonth)) && expDay.compareTo(curDay < 10 ? "0" + curDay : String.valueOf(curDay)) < 0)))) {

                            System.out.println("Choose a valid card");
                        } else {
                            System.out.println("YOU HAVE CHOSEN A TRIP STARTING FROM: " + arrStations[stationInt] + " AND USING CARD: " + arrCards[cardInt]);

                            System.out.println("CHOOSE 1 TO CONFIRM");
                            System.out.println("CHOOSE ANYTHING ELSE TO CANCEL AND QUIT");
                            System.out.print("ENTER CHOICE: ");

                            Scanner ChoiceEditType = new Scanner(System.in);
                            int choiceEditString = ChoiceEditType.nextInt();

                            if (choiceEditString == 1) {



                                System.out.println("");
                                Statement tripcheck = newConnect.createStatement();
                                String passQuery = "INSERT INTO Trip VALUES ('" + userID + "', '" + arrCardTypes[cardInt] + "', '" + arrCardDate[cardInt] + "', sysdate(), NULL, '" + arrStations[stationInt] + "', NULL)";

                                //String passQuery = "UPDATE User SET ID = '" + userID + "', first_name = '" + firstName + "', minit = '" + mi + "', last_name = '" + lastName + "', password = '" + pass1 + "', passenger_email = '" + email + "' WHERE ID='" + userID + "'";
                                //String passQuery = "INSERT INTO User VALUES ('" + userID + "', '" + firstName + "', '" + mi + "', '" + lastName + "', '" + pass1 + "', '" + email + "')";
                                ResultSet tripSet = tripcheck.executeQuery(passQuery);
                                if (!arrCardTypes[cardInt].equals("T-mes") || !arrCardTypes[cardInt].equals("T-jove")) {
                                    String updateQuery = "UPDATE Card SET uses_left=uses_left - 1 WHERE user_ID='" + userID + "' AND type_of_card='" + arrCardTypes[cardInt] + "' AND purchase_date_time='" + arrCardDate[cardInt] + "'";
                                    tripcheck.executeQuery(updateQuery);
                                }
                                // String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                // ResultSet testSet = rgstrcheck.executeQuery(testQuery);
                                System.out.println("You have embarked on a trip");
                                return 0;

                            } else {
                                System.out.println("You have cancelled your trip and are returning to main menu");
                                return 0;
                            }
                        }
                    }
                }
            }
        }
    }

    public static int viewTrip(String userID, String sortCond) throws Exception {
        Connection newConnect = getConnection();
        Scanner viewer = new Scanner(System.in);

        Statement getAllTrips = newConnect.createStatement();

        String getTripsQuery = "SELECT * FROM Trip WHERE user_ID='" + userID + "' ORDER BY " + sortCond; // gets all trips
        String getTripQueryNum = "SELECT COUNT(start_date_time) FROM Trip WHERE user_ID='" + userID + "'"; // get the count of user's trips
        ResultSet getTrips = getAllTrips.executeQuery(getTripsQuery); // get the stations.
        ResultSet getNumTrips = getAllTrips.executeQuery(getTripQueryNum); // get the count of Trips.
        int tripCount = -5; // set random num
        while(getNumTrips.next()) {
            tripCount = Integer.parseInt(getNumTrips.getString("COUNT(start_date_time)")); // getNum = count of Trips.
        }
        String[] arrTrips = new String[tripCount]; // create string array to hold the names of the Trips
        String[] arrCards = new String[tripCount];
        String[] arrCardDates = new String[tripCount];
        String[] arrStartDates = new String[tripCount];
        String[] arrEndDates = new String[tripCount];
        String[] arrFroms = new String[tripCount];
        String[] arrTos = new String[tripCount];

        int fillIndex = 0; // simple loop to go through ResultSet getTrips.
        int fmax = 4;
        int tmax = 2;

        while (getTrips.next()) {
            arrCards[fillIndex] = getTrips.getString("card_type"); // get the value out of the column "name"
            arrStartDates[fillIndex] = getTrips.getString("start_date_time");
            arrEndDates[fillIndex] = getTrips.getString("end_date_time") == null ? "XXXX-XX-XX XX:XX:XX.X" : getTrips.getString("end_date_time");
            arrFroms[fillIndex] = getTrips.getString("from_station_name");
            arrCardDates[fillIndex] = getTrips.getString("card_purchase_date_time");
            arrTos[fillIndex] = getTrips.getString("to_station_name") == null ? "NULL" : getTrips.getString("to_station_name");
            if (arrFroms[fillIndex].length() > fmax)
                fmax = arrFroms[fillIndex].length();
            if (arrTos[fillIndex].length() > tmax)
                tmax = arrTos[fillIndex].length();
            fillIndex++;
        }
        fmax += 2;
        tmax += 2;
        System.out.println(fmax);
        System.out.println(tmax);

        String lFrom = "";
        String rFrom = "";
        String lTo = "";
        String rTo = "";
        for (int i = 0; i < (fmax - 4) / 2; i++) {
            lFrom = lFrom + " ";
            rFrom = rFrom + " ";
        }

        if (!(fmax % 2 == 0))
            rFrom = rFrom + " ";

        for (int i = 0; i < (tmax - 2) / 2; i++) {
            lTo = lTo + " ";
            rTo = rTo + " ";
        }

        if (!(tmax % 2 == 0))
            rTo = rTo + " ";

        //String actualArray = Arrays.toString(arrTrips); // turn this into a printable thing. finally, fucking use ARRAYS.
        //System.out.println("ARRAY FOR TRIPS: " + Arrays.toString(arrTrips)); // print array.
        System.out.println("--------------------------------ALL TRIPS--------------------------------");
        System.out.println();
        System.out.println(" |    Start Date/Time    |     End Date/Time     | Card Used |" + lFrom + "From" + rFrom + "|" + lTo + "To" + rTo + "|");

        for (int i = 0; i < fillIndex; i++) {
            String lf = "";
            String rf = "";
            String lt = "";
            String rt = "";

            for (int j = 0; j < (fmax - arrFroms[i].length()) / 2; j++) {
                lf = lf + " ";
            }
            for (int j = 0; j < (fmax - arrFroms[i].length()) / 2; j++) {
                rf = rf + " ";
            }
            if (arrFroms[i].length() % 2 != fmax % 2) {
                rf = rf + " ";
            }

            for (int j = 0; j < (tmax - arrTos[i].length()) / 2; j++) {
                lt = lt + " ";
            }
            for (int j = 0; j < (tmax - arrTos[i].length()) / 2; j++) {
                rt = rt + " ";
            }
            if (arrTos[i].length() % 2 != tmax % 2) {
                rt = rt + " ";
            }

            String lc = arrCards[i].equals("T-50/30") || arrCards[i].equals("T-jove") ? "  " : "   ";
            String rc = arrCards[i].equals("T-mes") || arrCards[i].equals("T-jove") ? "   " : arrCards[i].equals("T-10") ? "    " : "  ";

            System.out.println(" | " + arrStartDates[i] + " | " + arrEndDates[i] + " |" + lc + arrCards[i] + rc + "|" + lf + arrFroms[i] + rf + "|" + lt + arrTos[i] + rt + "|");
        }
        System.out.println();
        while (true) {
            System.out.println("CHOOSE 1 TO SORT TRIPS");
            System.out.println("CHOOSE 2 TO UPDATE A TRIP");
            System.out.println("CHOOSE 3 TO QUIT");
            System.out.print("ENTER CHOICE: ");

            Scanner ChoiceEditType = new Scanner(System.in);
            int choiceEditString = ChoiceEditType.nextInt();

            if (choiceEditString == 1) {
                while (true) {
                    System.out.println("HOW WOULD YOU LIKE TO SORT?");
                    System.out.println("(1): START DATE/TIME");
                    System.out.println("(2): END DATE/TIME");
                    System.out.println("(3): CARD USED");
                    System.out.println("(4): STARTING STATION");
                    System.out.println("(5): END STATION");
                    System.out.println("(6): CANCEL");
                    System.out.print("ENTER NUMBER OF CHOICE: ");

                    int choiceSortString = ChoiceEditType.nextInt();
                    if (choiceSortString == 1) {
                        int sorted = viewTrip(userID, "start_date_time");
                        break;
                    } else if (choiceSortString == 2) {
                        int sorted = viewTrip(userID, "end_date_time");
                        break;
                    } else if (choiceSortString == 3) {
                        int sorted = viewTrip(userID, "card_type");
                        break;
                    } else if (choiceSortString == 4) {
                        int sorted = viewTrip(userID, "from_station_name");
                        break;
                    } else if (choiceSortString == 5) {
                        int sorted = viewTrip(userID, "to_station_name");
                        break;
                    } else if (choiceSortString == 6) {
                        break;
                    } else {
                        System.out.println("Enter a valid number");
                    }
                }
            } else if (choiceEditString == 3) {
                return 0;
            } else if (choiceEditString == 2) {
                System.out.println();
                while (true) {
                    System.out.println("----------- UPDATE TRIP -------------");
                    System.out.print("CHOOSE BY INDEX THE TRIP YOU WISH TO EDIT; 0 BEING FIRST, N - 1 BEING THE NTH OR -1 TO QUIT: "); // choose by INDEX from the array.
                    int upTripInt = viewer.nextInt();

                    if (upTripInt < -1 || upTripInt >= tripCount) {
                        System.out.println("Pick a number greater than 0 and less than or equal to n-1 or -1 to quit."); // check invalid INDEX
                    } else if (upTripInt == -1) {
                        System.out.println("Returning to main menu");
                        return 0;
                    } else {

                        String upTripStart = arrStartDates[upTripInt];
                        String upTripFrom = arrFroms[upTripInt];
                        String upTripCardType = arrCards[upTripInt];
                        String upTripCardDate = arrCardDates[upTripInt];

                        System.out.println("---TRIP TO BE UDPATED---");
                        System.out.println("STARTING FROM: " + upTripFrom);
                        System.out.println("USING CARD: " + upTripCardType + " " + upTripCardDate);

                        Statement getAllStations = newConnect.createStatement();

                        String getStationsQuery = "SELECT name FROM Station ORDER BY name"; // order by name, assume we only have to get from stations.
                        String getStationQueryNum = "SELECT COUNT(name) FROM Station"; // get the count of names.nnn
                        // String getStationsQuery = "SELECT name FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name ORDER BY"; // this gets us the actual table of names.
                        // String getStationQueryNum = "SELECT COUNT(name) FROM Station JOIN Station_On_Line WHERE Station_On_Line.station_name = Station.name"; // this gets us the count.
                        ResultSet getStations = getAllStations.executeQuery(getStationsQuery); // get the stations.
                        ResultSet getNumStations = getAllStations.executeQuery(getStationQueryNum); // get the count of stations.
                        int stationCount = -5; // set random num
                        while(getNumStations.next()) {
                            stationCount = Integer.parseInt(getNumStations.getString("COUNT(name)")); // getNum = count of stations.
                        }
                        String[] arrStations = new String[stationCount]; // create string array to hold the names of the stations
                        int fillerIndex = 0; // simple loop to go through ResultSet getStations.
                        while (getStations.next()) {
                            arrStations[fillerIndex] = getStations.getString("name"); // get the value out of the column "name"
                            fillerIndex++;
                        }
                        //String actualArray = Arrays.toString(arrStations); // turn this into a printable thing. finally, fucking use ARRAYS.
                        System.out.println("ARRAY FOR STATIONS: " + Arrays.toString(arrStations)); // print array.

                        while(true) { // classic repeating loop
                            System.out.print("CHOOSE BY INDEX YOUR ENDING STATION; 0 BEING FIRST, N - 1 BEING THE NTH OR -1 TO QUIT: "); // choose by INDEX from the array.
                            int stationInt = viewer.nextInt();
                            if (stationInt < -1 || stationInt >= stationCount) {
                                System.out.println("Pick a number greater than 0 and less than or equal to n-1 or -1 to quit."); // check invalid INDEX
                            } else if (stationInt == -1) {
                                System.out.println("Returning to main menu");
                                return 0;
                            } else {
                                Statement tripcheck = newConnect.createStatement();
                                String passQuery = "UPDATE Trip SET end_date_time = sysdate(), to_station_name = '" + arrStations[stationInt] + "' WHERE user_ID='" + userID + "' AND card_type='" + upTripCardType + "' AND card_purchase_date_time='" + upTripCardDate + "' AND start_date_time='" + upTripStart + "'";
                                ResultSet tripSet = tripcheck.executeQuery(passQuery);
                                System.out.println("Trip updated");
                                break;
                            }
                        }
                        break;
                    }
                }
                int tripUpdateSuccess = viewTrip(userID, "start_date_time");
                break;
            } else {
                System.out.print("Enter a valid number");
            }
        }
        return 0;
    }

    public static int viewReviews(String userID, Connection newConnect, String endingString) throws Exception {
        Connection gatherData = newConnect; // get the connection;
        String idToUse = userID; // get the id, even though it's static you shouldn't need it.
        int numOfQueries = -1;
        String[][] displayArr;
        String[] actualArrChoice = new String[]{"rid", "station_name", "shopping", "connection_speed", "approval_status"};
        String gatherQuery;

        ArrayList<String> arrChoice = new ArrayList<>(Arrays.asList(actualArrChoice)); // we gotta check this.
        Scanner getReviewData = new Scanner(System.in);

        while(true) {
            System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
            System.out.println();
            System.out.println("-----------------REVIEWS TO VIEW-----------------");
            if (!endingString.equals("REGULAR")) {
                gatherQuery = "SELECT rid, station_name, shopping, connection_speed, comment, approval_status FROM Review WHERE passenger_ID='" + idToUse + "'" + " ORDER BY " + endingString;
                System.out.println("GATHER QUERY: " + gatherQuery);
            } else {
                gatherQuery = "SELECT rid, station_name, shopping, connection_speed, comment, approval_status FROM Review WHERE passenger_ID='" + idToUse + "'";
                System.out.println("GATHER QUERY: " + gatherQuery);
            }
            System.out.println("GOT PAST QUERY SELECTION, LINE 720");
            Statement gatherAllReviews = newConnect.createStatement();
            String gatherNumQuery = "SELECT COUNT(rid) FROM Review WHERE passenger_id='" + idToUse + "'";
            ResultSet reviewInfo = gatherAllReviews.executeQuery(gatherQuery); // this gets us all the info of the reviews.
            ResultSet reviewNum = gatherAllReviews.executeQuery(gatherNumQuery); // this gets us the numerical stuff.
            // SELECT rid, station_name, shopping, connection_speed, comment, approval_status FROM Station WHERE userID = passenger_ID;
            System.out.println("CHECKING WHETHER EMPTY ARRAY OR ACTUAL ARRAY EXISTS.");
            if (!(reviewNum.isBeforeFirst())) {
                System.out.println("THERE ARE NO REVIEWS PUBLISHED. RETURN TO THE MAIN GUI.");
                return 0;
            } else {
                System.out.println("THE ARRAY OF REVIEWS: ");
                System.out.println();
                while (reviewNum.next()) {
                    numOfQueries = Integer.parseInt(reviewNum.getString("COUNT(rid)"));
                }
                displayArr = new String[numOfQueries][6];
                displayArr[0][0] = "RID"; // fill in the top part of the multi dim array with the categories.
                displayArr[0][1] = "          STATION";
                displayArr[0][2] = "          SHOPPING_NUM";
                displayArr[0][3] = "          CONN_SPEED";
                displayArr[0][4] = "          COMMENT_TEXT";
                displayArr[0][5] = "          APPROVAL_STATUS";
                int rowInt = 1;
                int colInt = 1;
                System.out.println("ARRAY BEING FILLED IN.");
                while (reviewInfo.next()) {
                    if (rowInt == numOfQueries) {break;}
                    while(colInt <= 6) {
                        displayArr[rowInt][colInt - 1] = colInt == 1 ? reviewInfo.getString(colInt) : "                   " + reviewInfo.getString(colInt);                    
                        colInt++;   
                    }
                    rowInt++;
                    colInt = 1;                    
                }

                System.out.println(Arrays.deepToString(displayArr).replace("], ", "]\n\n"));

                // be able to choose various things, such as the review and the station. Focus on this now.
                System.out.println();

                System.out.println("----------IMPORTANT INFORMATION TO READ--------------");
                System.out.println("THERE ARE TWO THINGS YOU CAN DO - EITHER CHOOSE A REVIEW"
                                   + " TO EDIT OR STATION TO LEARN MORE ABOUT, OR SORT/ORDER ALL COLUMNS EXCEPT FOR COMMENT.");
                System.out.println();
                //rid, station_name, shopping, connection_speed, comment, approval_status
                System.out.println("If you want to choose a station or review, type in the indices of the array, where 00 refers to the first review with RID 1. 150 would be a review with RID 15.");
                System.out.println();
                System.out.println();
                System.out.println("Or, if you want to sort columns, here are your column choices: rid, station_name, shopping, connection_speed, comment, approval_status");
                System.out.println();
                System.out.println();
                System.out.println("To sort a column in regular order, type in: SORT rid ASC, or SORT connection_speed ASC");
                System.out.println();
                System.out.println();
                System.out.println("Use SORT rid DESC to order rids in reverse, descending order.");
                System.out.println();
                System.out.println();
                System.out.println("If you'd like to quit the page and be taken to the main GUI, type in EXIT.");
                System.out.println();
                System.out.println();
                System.out.print("ENTER YOUR CHOICE NOW: ");
                

                String whatChoice = getReviewData.nextLine(); // get the result of what they want.
                if (whatChoice.length() < 2) {
                    System.out.println("INCORRECT ENTRY. TRY AGAIN.");
                } else if (whatChoice.equalsIgnoreCase("exit")) {
                    return 0;
                } else if (checkIfNumeric(whatChoice)) {
                    int checkInt = Integer.parseInt(whatChoice); // if this is a number, we have to do some checks.
                    if (checkInt > (numOfQueries * 10 + 1) || (checkInt < 9) || (checkInt % 10 > 1)) { // some insane fuckery is happening here.
                        System.out.println("These are unacceptable numerical choices. Pick again.");
                    } else {
                        int secondArrIndex = checkInt % 10;
                        int firstArrIndex = checkInt / 10;
                        System.out.println("This is the result of the index picked: " + displayArr[firstArrIndex][secondArrIndex]);
                        System.out.println("EXITING.");
                        System.exit(1); // if done correctly, this will allow us to pick either a review to edit or a station to look at.
                    }
                } else {
                    String[] breakArr = whatChoice.split(" "); // assume we have a non-numeric string. We have to figure out how to actually order by.
                    if (breakArr.length != 3) {
                        System.out.println("You typed in an incorrect sorting string. Type again.");
                    } else if (!arrChoice.contains(breakArr[1])) {
                        System.out.println("You mistyped what can be sorted. Choose again from the list given. Follow exactly.");
                    } else {
                        String sendOut = breakArr[1] + " " + breakArr[2]; // we're sending, say "RID ASCENDING", or "RID DESCENDING." Watch.
                        System.out.println("THIS IS THE SENDOUT: " + sendOut);
                        return viewReviews(idToUse, gatherData, sendOut); // this completes the thing. It's fucking recursive.
                    }
                    
                }                                                                               
            }
        }        
    }

    public static boolean checkIfNumeric(String checkString) {
        try {
            Integer.parseInt(checkString);
            return true; // assumes that it did work.
        } catch (NumberFormatException e) {
            return false; // not numeric at all.
        }
    }


    public static int stationDisplay(String stationName, Connection newConnect) throws Exception {
        int approvedReviewsForStation = -1;
        Connection getReviewsAddressEtc = newConnect;
        String nameStation = stationName;
        Statement connectOnStation = newConnect.createStatement();
        String address = null;
        String avgShopping = "AVERAGE SHOPPING: ";
        String avgConnSpeed = "AVG CONN SPEED: ";

        while(true) {
            ArrayList<String> LinesList = new ArrayList<>();
            String queryToGetStationInfo = "SELECT address, status FROM Station WHERE name='" + nameStation + "'";
            String getLinesForStation = "SELECT line_name FROM Station_On_Line WHERE station_name='" + nameStation + "'";
            String getReviewsForStation = "SELECT user, shopping, connection_speed, comment FROM Review WHERE station_name='" + nameStation +"' AND approval_status='pending'";
            String getCountReviews = "SELECT COUNT(user) FROM Review WHERE station_name='" + nameStation +"' AND approval_status='pending'";
            ResultSet StationInfoAddrStat = connectOnStation.executeQuery(queryToGetStationInfo);
            ResultSet StationLines = connectOnStation.executeQuery(getLinesForStation);
            ResultSet ReviewsForStationAppr = connectOnStation.executeQuery(getReviewsForStation);
            // ResultSet;
            while(StationLines.next()) {
                LinesList.add(StationLines.getString("line_name")); // add to LinesList;
            }

            System.exit(1);
            
        }
        
    }
    
    public static int lineDisplay(String line, Connection newConnect) throws Exception {
        int numOfStops = -1; // set the numOfStops impossible
        String nameLine = "Line NUMBER/NAME: "; // prep string
        Connection lineConn = newConnect; // bring connection in
        Statement stateLine = newConnect.createStatement(); // create statement
        String[][] displayArr; // the array that'll be displayed.
        String queryToExec = "SELECT station_name, order_number FROM Station_On_Line WHERE line_name='" + line + "'"; // query to execute.
        String countQuery = "SELECT COUNT(station_name) FROM Station_On_Line WHERE line_name='" + line + "'"; // get the count.
        ResultSet getStations = stateLine.executeQuery(queryToExec); // get the damn set.
        ResultSet getCountStations = stateLine.executeQuery(countQuery); // get the count.
        while(getCountStations.next()) {
            numOfStops = Integer.parseInt(getCountStations.getString("COUNT(station_name)"));
        }
        displayArr = new String[numOfStops + 1][2]; // ADD 1 because we start 1 row below.
        displayArr[0][0] = "STATION"; // you know what this is, basic setup.
        displayArr[0][1] = "ORDER";


        int rowInt = 1;
        int colInt = 1;
        System.out.println("ARRAY BEING FILLED IN.");
        while (getStations.next()) {
            if (rowInt == numOfStops) {break;}
            while(colInt <= 2) {
                displayArr[rowInt][colInt - 1] =  getStations.getString(colInt);
                colInt++;   
            }
            rowInt++;
            colInt = 1;                    
        }

        System.out.println(Arrays.deepToString(displayArr).replace("], ", "]\n\n"));


        // be able to choose various things, such as the review and the station. Focus on this now.
        System.out.println();

        return 0;        
        // SELECT station_name, order_number FROM Station_On_Line WHERE line_name='name';
    }
}
