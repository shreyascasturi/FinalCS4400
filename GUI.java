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
            String ChoiceIntroString = ChoiceIntroPage.nextLine(); // we assume that it is an int.
            if (checkIfNumeric(ChoiceIntroString) && Integer.parseInt(ChoiceIntroString) == 1) { // assume login.
                int resultofLogin = checkLogin(); // do login, and eventually go from there. This int is unnecessary unless
                // you decide to exit out of the login screen.
                if (resultofLogin == -1) { // if -1, we'll choose to break. Zeroes and other things continue the outer loop.
                    break; // this ends the app.
                }
            } else if (checkIfNumeric(ChoiceIntroString) && Integer.parseInt(ChoiceIntroString) == 2) { // assume register.
                int resultofRegister = register(); // do login, and eventually go from there. This int is unnecessary unless
                // you decide to exit out of the login screen.
                if (resultofRegister == -1) { // if -1, we'll choose to break.

                    break;
                }
                // goto register.
            } else if (checkIfNumeric(ChoiceIntroString) && Integer.parseInt(ChoiceIntroString) == 3) { // assume exit solely chosen in the choice intro page
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
                                    break;
                                } else {
                                    System.out.println("LOGGED IN AS ADMIN..."); // TIME TO DO THIS AS ADMIN FUCK YES
                                    isAdmin = true; // set the admin.
                                    while(true) {
                                        int adminGUIresult = MainAdminGUI(); // check how this works
                                        if (adminGUIresult == 1) { // VIEW TRIPS
                                            int viewTripSuccess = viewTrip(userID, "card_type");
                                            System.out.println();
                                        } else if (adminGUIresult == 2) { // BUY CARD
                                            int cardSuccess = buyCard(userID);
                                            System.out.println();
                                        } else if (adminGUIresult == 3) { // GO ON TRIP
                                            int tripCreateSuccess = goOnTrip(userID);
                                            System.out.println();
                                        } else if (adminGUIresult == 4) { // REVIEW PASSENGER REVIEWS
                                            int viewReviews = reviewPassengerReviewsADMIN(newConnect);
                                            System.out.println();
                                        } else if (adminGUIresult == 5) { // EDIT PROFILE
                                            int editSuccess = editAdmin(userID);
                                            System.out.println();
                                        } else if (adminGUIresult == 6) { // ADD STATION
                                            int stationAddSuccess = addStation();
                                            System.out.println();
                                        } else if (adminGUIresult == 7) { // GOTO LOGIN SCREEN
                                            int lineAddSuccess = addLine();
                                            System.out.println();
                                            break; // this should break out of the inner loop about the password filling in and go straight to the login screen.
                                        } else if (adminGUIresult == 8) {
                                            break;// GOTO LOGIN
                                        } else if (adminGUIresult == 9) { // QUIT TO WELCOME
                                            return 0;
                                        } else if (adminGUIresult == 10) { // QUIT FULLY.
                                            return -1;
                                        } else {
                                            System.out.println("This should never be reached at all. This is in the passengerGui check in checkLogin.");
                                            System.out.println("Crash app completely.");
                                            System.exit(1); // this breaks the app with no quit screen other than "crash app completely."
                                        }
                                    }
                                    break; // this is necessary to go back to the login screen. using only one break, as in the conditional for adminGui == 8, will go back to enter password.
                                }
                            }
                        }
                    }
                }
            }
        }
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
                                                                                            System.out.println("Make sure the passwords match");
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


    public static int MainPassengerGUI() throws Exception { // after login on the user is done.
        Connection newConnect = getConnection();
        Scanner choosePassengerGUI = new Scanner(System.in);

        String nameUser = "SELECT first_name, minit, last_name FROM User WHERE ID='" + userID + "'";

        Statement nameUserStat = newConnect.createStatement();
        ResultSet getNameUser = nameUserStat.executeQuery(nameUser);

        getNameUser.next();

        String firstname = getNameUser.getString("first_name");
        String midinit = getNameUser.getString("minit");
        String lastname = getNameUser.getString("last_name");


        while(true) {
            System.out.println("------------ MAIN PAGE ---------------------");
            System.out.println();
            System.out.println("Welcome " + firstname + " " + midinit + " " + lastname);
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
            String getInt = chooseStation.nextLine();
            if (!checkIfNumeric(getInt) || Integer.parseInt(getInt) < 0 || Integer.parseInt(getInt) >= getNum) {
                System.out.println("Pick a number greater than 0 and less than or equal to n-1."); // check invalid INDEX
                System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
            } else {
                shoppingRating = -1; // assume we did get valid index, set shopping and connection ratings.
                connectionRating = -1;
                while(true) { // these smaller and smaller infinite loops restrict our errors, so making one error forces us to solve that
                    // one error before moving on, input-wise.
                    System.out.print("CHOOSE A SHOPPING RATING FROM 0 TO 5: ");
                    String shopRating = chooseStation.nextLine();
                    shoppingRating = checkIfNumeric(shopRating) ? Integer.parseInt(shopRating) : -1;
                    if (shoppingRating < 0 || shoppingRating > 5) {
                        System.out.println("Choose a valid rating."); // valid check on shopping rating.
                        System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
                    } else {
                        while(true) {
                            System.out.print("CHOOSE A CONNECTION RATING FROM 0 TO 5: "); // valid check on
                            String connRating = chooseStation.nextLine();
                            connectionRating = checkIfNumeric(connRating) ? Integer.parseInt(connRating) : -1;
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

                                String passQuery = "INSERT INTO Review VALUES ('" + userID + "', " + count + ", " + shoppingRating + ", " + connectionRating +", '" + commentLeft +  "', NULL, 'pending', NULL, '" + arrStations[Integer.parseInt(getInt)] + "')";
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


    public static int viewReviews(String userID, Connection newConnect, String endingString) throws Exception {
        Connection gatherData = newConnect; // get the connection;
        String idToUse = userID; // get the id, even though it's static you shouldn't need it.
        int numOfQueries = -1;
        String[][] displayArr;
        String[] actualArrChoice = new String[]{"rid", "station_name", "shopping", "connection_speed", "approval_status"};
        String gatherQuery;

        String nameUser = "SELECT first_name, minit, last_name FROM User WHERE ID='" + userID + "'";

        Statement nameUserStat = newConnect.createStatement();
        ResultSet getNameUser = nameUserStat.executeQuery(nameUser);

        getNameUser.next();

        String firstname = getNameUser.getString("first_name");
        String midinit = getNameUser.getString("minit");
        String lastname = getNameUser.getString("last_name");


        ArrayList<String> arrChoice = new ArrayList<>(Arrays.asList(actualArrChoice)); // we gotta check this.
        Scanner getReviewData = new Scanner(System.in);

        while(true) {
            System.out.println("Welcome " + firstname + " " + midinit + " " + lastname);
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
                    System.out.println("NUMBER OF REVIEWS: " + numOfQueries);
                }
                displayArr = new String[numOfQueries + 1][6];
                displayArr[0][0] = "RID"; // fill in the top part of the multi dim array with the categories.
                displayArr[0][1] = "STATION";
                displayArr[0][2] = "          SHOPPING_NUM";
                displayArr[0][3] = "          CONN_SPEED";
                displayArr[0][4] = "          COMMENT_TEXT";
                displayArr[0][5] = "          APPROVAL_STATUS";
                int rowInt = 1;
                int colInt = 1;
                System.out.println("ARRAY BEING FILLED IN.");
                while (reviewInfo.next()) {
                    if (rowInt > numOfQueries) {break;}
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
                System.out.println("If you want to choose a station or review, type in the indices of the array, where 10 refers to the first review with RID 1. 150 would be a review with RID 15.");
                System.out.println();
                System.out.println();
                System.out.println("Or, if you want to sort columns, here are your column choices: rid, station_name, shopping, connection_speed, approval_status");
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
                        if (secondArrIndex == 1) {
                            stationDisplay(displayArr[firstArrIndex][secondArrIndex], gatherData); // the station and line displays.
                        } else {
                            int editingSuccess = editReview(displayArr[firstArrIndex][secondArrIndex]);

                            System.out.println("EDITED REVIEW");
                        }

                        //System.out.println("EXITING.");
                        //System.exit(1); // if done correctly, this will allow us to pick either a review to edit or a station to look at.
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

    public static int editReview(String rid) throws Exception {
        Connection newConnect = getConnection();
        Scanner editor = new Scanner(System.in);

        Statement theReview = newConnect.createStatement();



        String getReviewQuery = "SELECT shopping, connection_speed, comment, approval_status, station_name  FROM Review WHERE passenger_ID='" + userID + "' AND rid='" + rid + "'";

        ResultSet getReview = theReview.executeQuery(getReviewQuery);

        String shoppingRate = "";

        getReview.next();

        for (int i = 0; i < Integer.parseInt(getReview.getString("shopping")); i++) {
            shoppingRate = shoppingRate + "* ";
        }

        String connectionRate = "";

        for (int i = 0; i < Integer.parseInt(getReview.getString("connection_speed")); i++) {
            connectionRate = connectionRate + "* ";
        }

        System.out.println("REVIEW FOR: " + getReview.getString("station_name"));
        System.out.println("APPROVAL STATUS: " + getReview.getString("approval_status"));
        System.out.println("REVIEW ID: " + rid);
        System.out.println("SHOPPING RATING: " + shoppingRate);
        System.out.println("CONNECTION RATING: " + connectionRate);
        System.out.println("COMMENT: " + getReview.getString("comment"));
        System.out.println();

        while (true) {
            System.out.println("CHOOSE 1 TO EDIT REVIEW");
            System.out.println("CHOOSE 2 TO DELETE REVIEW");
            System.out.println("CHOOSE 3 TO RETURN TO VIEW REVIEWS");
            System.out.print("ENTER CHOICE: ");

            Scanner chooseOption = new Scanner(System.in);
            int choiceEditString = chooseOption.nextInt();

            if (choiceEditString == 1) {
                int shoppingRating;
                int connectionRating;
                String commentLeft;

                shoppingRating = -1; // assume we did get valid index, set shopping and connection ratings.
                connectionRating = -1;
                while(true) { // these smaller and smaller infinite loops restrict our errors, so making one error forces us to solve that
                    // one error before moving on, input-wise.
                    System.out.print("CHOOSE A SHOPPING RATING FROM 0 TO 5: ");
                    shoppingRating = chooseOption.nextInt();
                    if (shoppingRating < 0 || shoppingRating > 5) {
                        System.out.println("Choose a valid rating."); // valid check on shopping rating.
                        System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
                    } else {
                        while(true) {
                            System.out.print("CHOOSE A CONNECTION RATING FROM 0 TO 5: "); // valid check on
                            connectionRating = chooseOption.nextInt();
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
                                String passQuery = "UPDATE Review SET shopping='" + shoppingRating + "', connection_speed='" + connectionRating + "', comment='" + commentLeft + "' WHERE passenger_ID='" + userID + "' AND rid='" + rid + "'";
                                //String passQuery = "INSERT INTO Review VALUES ('" + userID + "', " + count + ", " + shoppingRating + ", " + connectionRating +", '" + commentLeft +  "', NULL, 'pending', NULL, '" + arrStations[getInt] + "')";
                                ResultSet rgstrSet = theReview.executeQuery(passQuery);
                                System.out.println("You updated a review.");
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
                // System.out.println("HITTING LINE OUTSIDE BIG BLOCK, DONE WHEN EXIT TYPED IN USERNAME CHECK");
                // return 0; // this should never hit.
            } else if (choiceEditString == 2) {
                Statement editcheck = newConnect.createStatement();
                String passQuery = "DELETE FROM Review WHERE passenger_ID='" + userID + "' AND rid='" + rid + "'";
                ResultSet editset = editcheck.executeQuery(passQuery);

                String testQuery = "SELECT * FROM Review WHERE passenger_ID='" + userID + "' AND rid='" + rid + "'";
                ResultSet testSet = editcheck.executeQuery(testQuery);

                if (!(testSet.isBeforeFirst())) {
                    System.out.println("Congrats you deleted your review!");
                    System.out.println("------- RETURNING TO VIEW REVIEWS ------- ");
                    return 0;
                } else {
                    System.out.println("Delete failed");
                    System.out.println("-------- RETURNING TO MAIN GUI, DELETE DIDN'T WORK ---------");
                    return 1;
                }
            } else if (choiceEditString == 3) {
                System.out.println("You exited while editing review. Goodbye");
                return 0;
            } else {
                System.out.println("You entered an incorrect number. Try again. Or quit.");
                System.out.println("");
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
        double avgShop = 0.0;
        double avgConn = 0.0;
        Connection getReviewsAddressEtc = newConnect;
        String nameStation = stationName.trim();
        Statement connectOnStation = newConnect.createStatement();
        String[] twoArr = new String[2];
        String address = null;
        String status = null;
        String avgShopping = "AVERAGE SHOPPING: ";
        String avgConnSpeed = "AVG CONN SPEED: ";
        String[][] reviewArr;
        Scanner answerQuestion = new Scanner(System.in);

        while(true) {
            ArrayList<String> LinesList = new ArrayList<>(); // creates an array list to add names of lines.
            String queryToGetStationInfo = "SELECT address, status FROM Station WHERE name='" + nameStation + "'";
            String getLinesForStation = "SELECT line_name FROM Station_On_Line WHERE station_name='" + nameStation + "'";
            String getReviewsForStation = "SELECT first_name, minit, last_name, shopping, connection_speed, comment FROM Review JOIN User WHERE passenger_ID = User.ID AND approval_status = 'approved' AND station_name='" + nameStation +"'";
            String getCountReviews = "SELECT COUNT(connection_speed) FROM Review WHERE station_name='" + nameStation +"' AND approval_status='approved'";
            String getAvgShoppingConn = "SELECT AVG(shopping), AVG(connection_speed) FROM Review WHERE station_name='" + nameStation + "' AND approval_status='approved'";
            ResultSet StationInfoAddrStat = connectOnStation.executeQuery(queryToGetStationInfo); // address and info
            ResultSet StationLines = connectOnStation.executeQuery(getLinesForStation); // get the line names
            ResultSet ReviewsForStationAppr = connectOnStation.executeQuery(getReviewsForStation); // get the review info
            ResultSet CountReviews = connectOnStation.executeQuery(getCountReviews); // get the counts
            ResultSet Averages = connectOnStation.executeQuery(getAvgShoppingConn); // get the averages of conn and shopping


                        // ResultSet;
            while(StationLines.next()) {
                LinesList.add(StationLines.getString("line_name")); // add to LinesList all the lines.
            }
            while(StationInfoAddrStat.next()) { // get the address and status.
                address = StationInfoAddrStat.getString("address");
                status = StationInfoAddrStat.getString("status");
            }
            while (CountReviews.next()) { // get the counts of the reviews so that we can actually create the array.
                approvedReviewsForStation = Integer.parseInt(CountReviews.getString("COUNT(connection_speed)"));
            }

            if (approvedReviewsForStation == 0) { // we'll have an empty array.
                reviewArr = new String[approvedReviewsForStation + 1][6]; // create the array.
                reviewArr[0][0] = "FIRST_NAME";
                reviewArr[0][1] = "M.I.";
                reviewArr[0][2] = "LAST_NAME";
                reviewArr[0][3] = "SHOPPING"; // labeling categories.
                reviewArr[0][4] = "CONNECTION_SPEED";
                reviewArr[0][5] = "COMMENT";


                int rowInt = 1;
                int colInt = 1;
                while (ReviewsForStationAppr.next()) {
                    if (rowInt > approvedReviewsForStation) {break;}
                    while(colInt <= 6) {
                        reviewArr[rowInt][colInt - 1] = ReviewsForStationAppr.getString(colInt);
                        colInt++;
                    }
                    rowInt++;
                    colInt = 1;
                }

                System.out.println("---------- LINE INFO ----------");
                System.out.println("STATION NAME: " + nameStation); // print station name
                System.out.println("STATUS: " + status); // print the status

                System.out.println("ADDRESS: " + address); // print the address


                System.out.println("LINES: " + LinesList.toString()); // print the regular lines

                System.out.printf("%n%n"); // big new line
                System.out.println("---------- REVIEWS ---------");

                System.out.println(Arrays.deepToString(reviewArr).replace("], ", "]\n\n"));

                System.out.printf("%n%n");

                //System.out.println("TYPE IN 'EXIT', WITHOUT QUOTES, BECAUSE EVERYTHING IS EMPTY: ");

                int lengthOfList = LinesList.size();

                System.out.println("You can choose an index from the lines array, or type in EXIT to go back to the Reviews page.");
                System.out.printf("MAKE A CHOICE: ");
                String answerToQuest = answerQuestion.nextLine();
                if (answerToQuest.equalsIgnoreCase("exit")) {
                    System.out.println("EXITING STATION DISPLAY");
                    return 0;
                } else {
                    if (checkIfNumeric(answerToQuest) && Integer.parseInt(answerToQuest) >= 0 && Integer.parseInt(answerToQuest) < lengthOfList) { // assuming the answerToQuest is a valid int to pick from list.
                        lineDisplay(LinesList.get((int)(Integer.parseInt(answerToQuest))), newConnect, "REGULAR"); // goto line display.
                    } else {
                        System.out.println("WRONG CHOICE. PICK ANOTHER.");
                    }
                }

                // if (!answerQuestion.nextLine().equalsIgnoreCase("exit")) {
                //     System.out.println("Pick again.");
                // } else {
                //     return 0;
                // }


            } else { // ASSUME A NON EMPTY ARRAY
                reviewArr = new String[approvedReviewsForStation + 1][6]; // create the array.
                reviewArr[0][0] = "FIRST_NAME";
                reviewArr[0][1] = "M.I.";
                reviewArr[0][2] = "LAST_NAME";
                reviewArr[0][3] = "SHOPPING"; // labeling categories.
                reviewArr[0][4] = "CONNECTION_SPEED";
                reviewArr[0][5] = "COMMENT";


                int rowInt = 1;
                int colInt = 1;
                while (ReviewsForStationAppr.next()) {
                    if (rowInt > approvedReviewsForStation) {break;}
                    while(colInt <= 6) {
                        reviewArr[rowInt][colInt - 1] = ReviewsForStationAppr.getString(colInt);
                        colInt++;
                    }
                    rowInt++;
                    colInt = 1;
                }

                while (Averages.next()) {
                    avgShop = Double.parseDouble(Averages.getString("AVG(shopping)")); // GET THE AVERAGE SHOPPING ASSUMING APPROVED REVIEWS ONLY
                    avgConn = Double.parseDouble(Averages.getString("AVG(connection_speed)")); // GET THE AVG CONNECTION_SPEED
                }


                System.out.println("STATION NAME: " + nameStation); // Station name
                System.out.println("STATUS: " + status); // status to print out




                System.out.println("LINES: " + LinesList.toString()); // available lines


                System.out.printf("%n%n%n");
                System.out.println(avgShopping + avgShop); // print the strings out
                System.out.println(avgConnSpeed + avgConn); // print the strings out for avg connection and shopping


                System.out.println(Arrays.deepToString(reviewArr).replace("], ", "]\n\n"));
                System.out.printf("%n%n%n");

                int lengthOfList = LinesList.size();

                System.out.println("You can choose an index from the lines array, or type in EXIT to go back to the Reviews page.");
                System.out.printf("MAKE A CHOICE: ");
                String answerToQuest = answerQuestion.nextLine();
                if (answerToQuest.equalsIgnoreCase("exit")) {
                    System.out.println("EXITING STATION DISPLAY");
                    return 0;
                } else {
                    if (checkIfNumeric(answerToQuest) && Integer.parseInt(answerToQuest) >= 0 && Integer.parseInt(answerToQuest) < lengthOfList) { // assuming the answerToQuest is a valid int to pick from list.
                        lineDisplay(LinesList.get((int)(Integer.parseInt(answerToQuest))), newConnect, "REGULAR"); // goto line display.
                    } else {
                        System.out.println("WRONG CHOICE. PICK ANOTHER.");
                    }
                }

            }

        }
    }



    public static int lineDisplay(String line, Connection newConnect, String addition) throws Exception {

        System.out.printf("%n%n");
        System.out.println("---------- LINE INFO ----------");
        int numOfStops = -1; // set the numOfStops impossible
        String nameLine = "Line NUMBER/NAME: "; // prep string
        System.out.printf("%n%n");
        System.out.println(nameLine + line);
        Connection lineConn = newConnect; // bring connection in
        Statement stateLine = newConnect.createStatement(); // create statement
        String[][] displayArr; // the array that'll be displayed.
        //String[] arrChoiceSort = new String[]{"station_name, order_number"};
        Scanner anotherScan = new Scanner(System.in);
        String queryToExec = null;
        //ArrayList<String> checkChoiceList = new ArrayList<>(Arrays.asList(arrChoiceSort));

        while(true) {
            if (!(addition.equals("REGULAR"))) {
                queryToExec = "SELECT station_name, order_number FROM Station_On_Line WHERE line_name='" + line + "' ORDER BY " + addition; // query to execute.
                System.out.println("QUERY: " + queryToExec);
            } else {
                queryToExec = "SELECT station_name, order_number FROM Station_On_Line WHERE line_name='" + line + "'"; // query to execute.
            }
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
            System.out.println("---------- ARRAY ----------");
            while (getStations.next()) {
                if (rowInt > numOfStops) {break;}
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
            System.out.println("NUMBER OF STOPS: " + numOfStops);
            System.out.println();

            System.out.printf("Type in EXIT to get out of this page and back to the previous page, or specify sorting by typing SORT <category> ASC/DESC, respectively. <category> = station_name || order_number. CHOOSE: ");

            String lineChoice = anotherScan.nextLine();
            if (lineChoice.equalsIgnoreCase("EXIT")) {
                return 0;
            } else {
                String[] choiceArr = lineChoice.split(" ");
                if (choiceArr[0].trim().equalsIgnoreCase("sort") && (choiceArr[1].equals("station_name") || choiceArr[1].equals("order_number")) && (choiceArr[2].trim().equalsIgnoreCase("asc") || choiceArr[2].trim().equalsIgnoreCase("desc"))) {
                    String choiceMake = choiceArr[1] + " " + choiceArr[2]; // i'm not even going to check whether they typed the right thing

                    return lineDisplay(line, newConnect, choiceMake); // make this recursive to sort.
                } else {
                    System.out.println("Type correctly next time.");
                }

            }

        }
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
            String choiceOfCard = ChoiceCardType.nextLine();
            int ChoiceCardString = checkIfNumeric(choiceOfCard) ? Integer.parseInt(choiceOfCard) : -1;


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

            String theStation = editor.nextLine();

            int stationInt = checkIfNumeric(theStation) ? Integer.parseInt(theStation) : -2;
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

                    String cardSelection = editor.nextLine();
                    int cardInt = checkIfNumeric(cardSelection) ? Integer.parseInt(cardSelection) : -2;

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

                        if ((!(arrUses[cardInt] == null) && arrUses[cardInt].equals("0"))
                            || ((!(arrExpCardDate[cardInt].equals("NULL"))) && (expYear.compareTo(String.valueOf(curYear)) < 0
                                || (expYear.equals(String.valueOf(curYear)) && expMonth.compareTo(curMonth < 10 ? "0" + curMonth : String.valueOf(curMonth)) < 0)
                                || (expYear.equals(String.valueOf(curYear)) && expMonth.equals(curMonth < 10 ? "0" + curMonth : String.valueOf(curMonth)) && expDay.compareTo(curDay < 10 ? "0" + curDay : String.valueOf(curDay)) < 0)))) {

                            System.out.println("Choose a valid card");
                        } else {
                            System.out.println("YOU HAVE CHOSEN A TRIP STARTING FROM: " + arrStations[stationInt] + " AND USING CARD: " + arrCards[cardInt]);

                            System.out.println("CHOOSE 1 TO CONFIRM");
                            System.out.println("CHOOSE ANYTHING ELSE TO CANCEL AND QUIT");
                            System.out.print("ENTER CHOICE: ");

                            Scanner ChoiceEditType = new Scanner(System.in);

                            String makeDecision = ChoiceEditType.nextLine();
                            int choiceEditString = checkIfNumeric(makeDecision) ? Integer.parseInt(makeDecision) : -2;

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
        int fmax = 4; // longest length of a FROM STATION spacing purposes.
        int tmax = 2; // longest length of a TO STATION spacing purposes.

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

            String tripChoice = ChoiceEditType.nextLine();
            int choiceEditString = checkIfNumeric(tripChoice) ? Integer.parseInt(tripChoice) : -2;

            if (choiceEditString == 1) {
                while (true) {
                    System.out.println("HOW WOULD YOU LIKE TO SORT?");
                    System.out.println("(1): START DATE/TIME ASCENDING");
                    System.out.println("(2): START DATE/TIME DESCENDING");
                    System.out.println("(3): END DATE/TIME ASCENDING");
                    System.out.println("(4): END DATE/TIME DESCENDING");
                    System.out.println("(5): CARD USED ASCENDING");
                    System.out.println("(6): CARD USED DESCENDING");
                    System.out.println("(7): STARTING STATION ASCENDING");
                    System.out.println("(8): STARTING STATION DESCENDING");
                    System.out.println("(9): END STATION ASCENDING");
                    System.out.println("(10): END STATION DESCENDING");
                    System.out.println("(11): CANCEL");
                    System.out.print("ENTER NUMBER OF CHOICE: ");

                    String chooseSorting = ChoiceEditType.nextLine();
                    int choiceSortString = checkIfNumeric(chooseSorting) ? Integer.parseInt(chooseSorting) : -2;
                    if (choiceSortString == 1) {
                        int sorted = viewTrip(userID, "start_date_time ASC");
                        break;
                    } else if (choiceSortString == 2) {
                        int sorted = viewTrip(userID, "start_date_time DESC");
                        break;
                    } else if (choiceSortString == 3) {
                        int sorted = viewTrip(userID, "end_date_time ASC");
                        break;
                    } else if (choiceSortString == 4) {
                        int sorted = viewTrip(userID, "end_date_time DESC");
                        break;
                    } else if (choiceSortString == 5) {
                        int sorted = viewTrip(userID, "card_type ASC");
                        break;
                    } else if (choiceSortString == 6) {
                        int sorted = viewTrip(userID, "card_type DESC");
                        break;
                    } else if (choiceSortString == 7) {
                        int sorted = viewTrip(userID, "from_station_name ASC");
                        break;
                    } else if (choiceSortString == 8) {
                        int sorted = viewTrip(userID, "from_station_name DESC");
                        break;
                    } else if (choiceSortString == 9) {
                        int sorted = viewTrip(userID, "to_station_name ASC");
                        break;
                    } else if (choiceSortString == 10) {
                        int sorted = viewTrip(userID, "to_station_name DESC");
                        break;
                    } else if (choiceSortString == 11) {
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

                    String upTripString = viewer.nextLine();
                    int upTripInt = checkIfNumeric(upTripString) ? Integer.parseInt(upTripString) : -2;

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
                            String stationChoiceInt = viewer.nextLine();
                            int stationInt = checkIfNumeric(stationChoiceInt) ? Integer.parseInt(stationChoiceInt) : -2;
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
            break;
        }
        return 0;
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
                        if (firstName.length() == 0 || firstName.equals("NULL")) {
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
                                    if (mi.length() > 1 && !mi.equals("NULL")) {
                                        System.out.println("Middle initial should be one character.");
                                    } else {
                                        while (true) {
                                            System.out.println("ENTER LAST NAME: ");
                                            lastName = editor.nextLine();
                                            if (lastName.equalsIgnoreCase("exit")) {
                                                System.out.println("You exited the editor at LastName. Good-bye.");
                                                return 0;
                                            } else {
                                                if (lastName.length() == 0 || lastName.equals("NULL")) {
                                                    System.out.println("Type a last name please");
                                                } else {
                                                    while (true) {
                                                        System.out.println("ENTER EMAIL: ");
                                                        email = editor.nextLine();
                                                        if (email.equalsIgnoreCase("exit")) {
                                                            System.out.println("You exited the editor at Email. Good-bye.");
                                                            return 0;
                                                        } else {
                                                            if (email.length() == 0 || email.equals("NULL")) {
                                                                System.out.println("Type an email please");
                                                            } else {
                                                                while (true) {
                                                                    System.out.println("ENTER USERID: ");
                                                                    userID = editor.nextLine();
                                                                    if (userID.equalsIgnoreCase("exit")) {
                                                                        System.out.println("You exited the editor at UserID. Good-bye.");
                                                                        return 0;
                                                                    } else {
                                                                        Statement usrcheck = newConnect.createStatement();
                                                                        String usrchkQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                        ResultSet testingSet = usrcheck.executeQuery(usrchkQuery);
                                                                        boolean unique = true;
                                                                        if (userID.length() == 0 || userID.equals("NULL")) {
                                                                            System.out.println("Type a UserID please");
                                                                        } else if (testingSet.isBeforeFirst()){
                                                                            System.out.println("Choose a unique UserID pls");
                                                                        } else {
                                                                            while (true) {
                                                                                System.out.println("ENTER PASSWORD: ");
                                                                                pass1 = editor.nextLine();
                                                                                if (pass1.equalsIgnoreCase("exit")) {
                                                                                    System.out.println("You exited the editor at Password. Good-bye.");
                                                                                    return 0;
                                                                                } else {
                                                                                    if (pass1.length() == 0 || pass1.equals("NULL")) {
                                                                                        System.out.println("Type a password please");
                                                                                    } else {
                                                                                        while (true) {
                                                                                            System.out.println("RE-ENTER PASSWORD: ");
                                                                                            pass2 = editor.nextLine();
                                                                                            if (pass2.equalsIgnoreCase("exit")) {
                                                                                                System.out.println("You exited the editor at Password. Good-bye.");
                                                                                                return 0;
                                                                                            } else {
                                                                                                if (!pass1.equals(pass2)) {
                                                                                                    System.out.print("Make sure the passwords match");
                                                                                                } else {

                                                                                                    mi = mi.equals("NULL") ? mi : "'" + mi + "'";

                                                                                                    System.out.println("");
                                                                                                    Statement rgstrcheck = newConnect.createStatement();
                                                                                                    String passQuery = "UPDATE User SET ID = '" + userID + "', first_name = '" + firstName + "', minit = " + mi + ", last_name = '" + lastName + "', password = '" + pass1 + "', passenger_email = '" + email + "' WHERE ID='" + ID + "'"; // YOU HAVE TO CHANGE WHERE IT'S ID.
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



    /**
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
       TRANSITIONING INTO ADMIN AREAS TRANSITIONING INTO ADMIN AREAS
    **/



    public static int MainAdminGUI() throws Exception {
        Connection newConnect = getConnection();
        Scanner chooseAdminGUI = new Scanner(System.in);

        String nameUser = "SELECT first_name, minit, last_name FROM User WHERE ID='" + userID + "'";

        Statement nameUserStat = newConnect.createStatement();
        ResultSet getNameUser = nameUserStat.executeQuery(nameUser);

        getNameUser.next();

        String firstname = getNameUser.getString("first_name");
        String midinit = getNameUser.getString("minit");
        String lastname = getNameUser.getString("last_name");


        while(true) {
            System.out.println("------------ MAIN PAGE ---------------------");
            System.out.println();
            System.out.println("Welcome " + firstname + " " + midinit + " " + lastname);
            System.out.println("userID: " + userID + " ROLE: " + (isAdmin ? "ADMIN" : "PASSENGER")); // this is a string that tells us the userID and passenger/admin role for a given user.
            System.out.printf("CHOOSE A NUMBER TO EXPLORE DIFFERENT OPTIONS. %n 1. View Trips %n 2. Buy Card %n 3. Go On Trip %n 4. Review Passenger Reviews %n 5. Edit Profile %n"
                              + " 6. Add Station %n 7. Add Line %n 8. Goto Login %n 9. Goto Welcome Screen %n 10. Quit Fully %n");
            System.out.print("CHOOSE AN OPTION: ");
            int chooseAdminInt = chooseAdminGUI.nextInt(); // same old, basically.
            if (chooseAdminInt == 1) {
                // leaveReview(newConnect);
                // break; //return 0; // leave review
                System.out.println("------------VIEW TRIPS------------"); // VIEW TRIPS
                System.out.println("");
                return 1;
            } else if (chooseAdminInt == 2) {
                System.out.println("------------BUY CARD------------");// VIEW REVIEWS
                System.out.println("");
                return 2;
            } else if (chooseAdminInt == 3) {
                System.out.println("------------GO ON TRIP------------"); // GO ON TRIP
                System.out.println("");
                return 3; //return 3(Go to card purchase screen);
            } else if (chooseAdminInt == 4) { // REVIEW PASSENGER REVIEWS
                System.out.println("------------REVIEW PASSENGER REVIEWS------------");
                System.out.println("");
                return 4; //return 4(Go to Go On Trip screen)
            } else if (chooseAdminInt == 5) { // EDIT PROFILE
                System.out.println("--------------EDIT PROFILE----------");
                return 5;
            } else if (chooseAdminInt == 6) { // ADD STATION
                System.out.println("------------ADD STATION------------");
                System.out.println("");
                return 6; //return 0;
            } else if (chooseAdminInt == 7) { // ADD LINE
                System.out.println("------------ADD LINE------------");
                System.out.println("");
                userID = ""; // reset as empty.
                isAdmin = false; // reset as false even though it is false.
                return 7;
            } else if (chooseAdminInt == 8) { // goto LOGIN SCREEN
                System.out.println("------------LOGGING OUT GOTO LOGIN------------");
                System.out.println("");
                userID = "";
                isAdmin = false;
                return 8;
            } else if (chooseAdminInt == 9) { // GOTO WELCOME SCREEN
                System.out.println("------------LOGGING OUT GOTO WELCOME SCREEN ------------");
                System.out.println("");
                userID = "";
                isAdmin = false;
                return 9;
            } else if (chooseAdminInt == 10) { // GOTO QUIT FULLY
                System.out.println("------------LOGGING OUT EXIT FULLY ------------");
                System.out.println("");
                userID = "";
                isAdmin = false;
                return 10;
            } else {
                System.out.println("You chose an incorrect number. Try again.");
            }
        }

    }

    public static int reviewPassengerReviewsADMIN(Connection newConnect) throws Exception {
        while(true) {
            Connection getStationReviews = newConnect; // label the conn.
            Statement getReviewQuery = newConnect.createStatement(); // label the statement
            int countOfReviews = -1;
            String[][] displayArr;
            Scanner reviewAdminScan = new Scanner(System.in); // create the scanner.
            System.out.println("-------ENTERING REVIEW AREA AS ADMIN ---------");
            System.out.printf("%n%n");

            String getReviews = "SELECT passenger_id, station_name, shopping, connection_speed, comment FROM Review WHERE approval_status='" + "pending" + "'"; // get the stuff for the array.
            String getCountReviews = "SELECT COUNT(passenger_id) FROM Review WHERE approval_status='" + "pending" + "'"; // get the count for the array


            ResultSet getReviewData = getReviewQuery.executeQuery(getReviews); // get the review data.
            ResultSet getCountOfReviews = getReviewQuery.executeQuery(getCountReviews); // get the count of reviews.

            while (getCountOfReviews.next()) {
                countOfReviews = Integer.parseInt(getCountOfReviews.getString("COUNT(passenger_id)")); // get the int
            }

            displayArr = new String[countOfReviews + 1][5]; // initializing the array.
            displayArr[0][0] = "PASSENGER_ID";
            displayArr[0][1] = "STATION_NAME";
            displayArr[0][2] = "SHOPPING";
            displayArr[0][3] = "CONNECTION_SPEED";
            displayArr[0][4] = "COMMENT";
            int rowInt = 1;
            int colInt = 1;

            System.out.println("---------- PENDING REVIEWS ----------");
            System.out.println();
            while (getReviewData.next()) {
                if (rowInt > countOfReviews) {break;}
                while(colInt <= 5) {
                    displayArr[rowInt][colInt - 1] =  getReviewData.getString(colInt);
                    colInt++;
                }
                rowInt++;
                colInt = 1;
            }

            System.out.println(Arrays.deepToString(displayArr).replace("], ", "]\n\n"));

            System.out.printf("%n%n%n%n%n");

            System.out.println("YOU CAN CHOOSE A STATION USING <NUM REVIEW><1>. SO 11 is the index of the Station of the first review.");

            System.out.println("");

            System.out.println("ELSE, YOU CAN APPROVE OR REJECT REVIEWS, based on <a,r><NUM REVIEW>. To approve the first review, type in a1.");

            System.out.println("");

            System.out.println("OR, YOU CAN JUST TYPE IN EXIT TO QUIT AND BE TAKEN TO THE ADMIN GUI.");

            System.out.println("");

            System.out.print("MAKE A CHOICE: ");

            String choiceMade = reviewAdminScan.nextLine();
            if (choiceMade.equalsIgnoreCase("exit")) {
                return 0;
            } else if (checkIfNumeric(choiceMade)) { // this assumes they choose a station
                if (Integer.parseInt(choiceMade) <= 5 || Integer.parseInt(choiceMade) % 10 != 1 || Integer.parseInt(choiceMade) > ((10 * countOfReviews) + 1)) {
                    System.out.println("You have picked an incorrect number for the station. pick again.");
                } else {
                    int theStationChosen = Integer.parseInt(choiceMade); // int for station chosen.
                    System.out.println("This is the station chosen: " + displayArr[theStationChosen / 10][theStationChosen % 10]);
                    stationAdminDisplay(displayArr[theStationChosen / 10][theStationChosen % 10], getStationReviews); // go to the station display.
                }

            } else { // assume rejection/approval of a review
                if (choiceMade.length() != 2) {
                    System.out.println("YOU DIDN'T TYPE IN EXIT OR A NUMBER SO THIS IS WRONG. CHOOSE AGAIN.");
                }
                String[] splitChoiceAdmin = choiceMade.split(""); // split into two items.
                if (!(splitChoiceAdmin[0].equalsIgnoreCase("a") || splitChoiceAdmin[0].equalsIgnoreCase("r")) || (Integer.parseInt(splitChoiceAdmin[1]) > countOfReviews)) {
                    System.out.println("FRICKING PISSING ME OFF WITH THESE INCORRECT QUERIES PICK AGAIN AND DO IT RIGHT THIS TIME");
                    System.out.println("shopp");
                    System.out.println("--------RESTARTING THE DISPLAYING OF REVIEWS ---------");
                } else {
                    String userName = displayArr[Integer.parseInt(splitChoiceAdmin[1])][0]; // this gives us the user name for the review.
                    String stationParticularReview = displayArr[Integer.parseInt(splitChoiceAdmin[1])][1]; // this gives us the station.
                    String shoppingInt = displayArr[Integer.parseInt(splitChoiceAdmin[1])][2]; // GET THE INT SHOPPING
                    String connInt = displayArr[Integer.parseInt(splitChoiceAdmin[1])][3]; // GET THE CONN INT
                    String commentReview = displayArr[Integer.parseInt(splitChoiceAdmin[1])][4]; // GET THE COMMENT

                    System.out.println("I'M UPDATING/DELETING THIS REVIEW: " + "userName: " + userName + ", station: " + stationParticularReview + ", shoppingInt: " + shoppingInt + ", connInt: " + connInt + ", commentReview: " + commentReview);
                    System.out.println("%n%n%n%n");
                    String updateQuery = "UPDATE Review SET approval_status='" + (splitChoiceAdmin[0].equalsIgnoreCase("a") ? "approved" : "rejected") + "', approver_ID ='" + userID + "' WHERE passenger_id='" + userName + "' AND station_name='" + stationParticularReview + "' AND shopping=" + Integer.parseInt(shoppingInt)
                        + " AND connection_speed=" + Integer.parseInt(connInt) + " AND comment='" + commentReview + "'";
                    System.out.println("update query is: " + updateQuery);

                    Statement updateQueryRejectAccept = getStationReviews.createStatement();
                    ResultSet someSet = updateQueryRejectAccept.executeQuery(updateQuery);
                }
            }

        }
    }

    public static int stationAdminDisplay(String stationID, Connection newConnect) throws Exception {
        int approvedReviewsForStation = -1; // get approved reviews.
        double avgShop = 0.0; // get avgShop prep
        double avgConn = 0.0; // get avgConn prep
        Connection getReviewsAddressEtc = newConnect; // the connection
        String nameStation = stationID.trim(); // stationName.
        Statement connectOnStation = newConnect.createStatement(); // the connection itself being created
        String[] twoArr = new String[2]; // i forget why this is here.
        String address = null; // address exists.
        String status = null; // status exists.
        String avgShopping = "AVERAGE SHOPPING: "; // avg shopping.
        String avgConnSpeed = "AVG CONN SPEED: "; // avg connection speed.
        String[][] reviewArr; // the arr.
        Scanner answerQuestion = new Scanner(System.in);

        while(true) {

            System.out.println("----- TRANSITIONING INTO STATION DISPLAY ------");
            System.out.printf("%n%n%n");
            ArrayList<String> LinesList = new ArrayList<>(); // creates an array list to add names of lines.
            String queryToGetStationInfo = "SELECT address, status FROM Station WHERE name='" + nameStation + "'";
            String getLinesForStation = "SELECT line_name FROM Station_On_Line WHERE station_name='" + nameStation + "'";
            String getReviewsForStation = "SELECT passenger_ID, shopping, connection_speed, comment FROM Review WHERE station_name='" + nameStation +"' AND approval_status='approved'";
            String getCountReviews = "SELECT COUNT(passenger_ID) FROM Review WHERE station_name='" + nameStation +"' AND approval_status='approved'";
            String getAvgShoppingConn = "SELECT AVG(shopping), AVG(connection_speed) FROM Review WHERE station_name='" + nameStation + "' AND approval_status='approved'";
            ResultSet StationInfoAddrStat = connectOnStation.executeQuery(queryToGetStationInfo); // address and info
            ResultSet StationLines = connectOnStation.executeQuery(getLinesForStation); // get the line names
            ResultSet ReviewsForStationAppr = connectOnStation.executeQuery(getReviewsForStation); // get the review info
            ResultSet CountReviews = connectOnStation.executeQuery(getCountReviews); // get the counts
            ResultSet Averages = connectOnStation.executeQuery(getAvgShoppingConn); // get the averages of conn and shopping.
            // ResultSet;
            while(StationLines.next()) {
                LinesList.add(StationLines.getString("line_name")); // add to LinesList all the lines.
            }
            while(StationInfoAddrStat.next()) { // get the address and status.
                address = StationInfoAddrStat.getString("address");
                status = StationInfoAddrStat.getString("status");
            }
            while (CountReviews.next()) { // get the counts of the reviews so that we can actually create the array.
                approvedReviewsForStation = Integer.parseInt(CountReviews.getString("COUNT(passenger_ID)"));
            }

            if (approvedReviewsForStation == 0) { // we'll have an empty array.
                reviewArr = new String[approvedReviewsForStation + 1][4]; // create the array.
                reviewArr[0][0] = "USER";
                reviewArr[0][1] = "SHOPPING";
                reviewArr[0][2] = "CONNECTION_SPEED";
                reviewArr[0][3] = "COMMENT"; // labeling categories.

                int rowInt = 1;
                int colInt = 1;
                while (ReviewsForStationAppr.next()) {
                    if (rowInt > approvedReviewsForStation) {break;}
                    while(colInt <= 4) {
                        reviewArr[rowInt][colInt - 1] = ReviewsForStationAppr.getString(colInt);
                        colInt++;
                    }
                    rowInt++;
                    colInt = 1;
                }

                System.out.println("STATION NAME: " + nameStation);
                System.out.println("STATUS: " + status);


                System.out.println("LINES: " + LinesList.toString());

                System.out.printf("%n%n%n%n");

                System.out.println(Arrays.deepToString(reviewArr).replace("], ", "]\n\n"));

                int lengthOfList = LinesList.size();

                System.out.println("You can choose an index from the lines array, or type in EXIT to go back to the Reviews page.");
                System.out.println("Additionally, you can type in s1, s2, or s3 in order to change the status to open, closed, half-open, respectively.");
                System.out.printf("MAKE A CHOICE: ");

                String answerToQuest = answerQuestion.nextLine();
                if (answerToQuest.equalsIgnoreCase("exit")) {
                    System.out.println("EXITING STATION DISPLAY TO GOTO REVIEWS PAGE");
                    return 0;
                } else if (!checkIfNumeric(answerToQuest) && (answerToQuest.equals("s1") || answerToQuest.equals("s2") || answerToQuest.equals("s3"))) {
                    String updateQuery = "UPDATE Station SET status='" + (answerToQuest.split("")[1].equals("1") ? "open"
                                                                          : answerToQuest.split("")[1].equals("2") ? "closed" : "half-open") + "' WHERE "
                        + "address='" + address + "' AND name='" + nameStation + "'";
                    Statement updateStatusQuery = getReviewsAddressEtc.createStatement();
                    ResultSet whateverUpdateStatus = updateStatusQuery.executeQuery(updateQuery);
                } else if (checkIfNumeric(answerToQuest) && Integer.parseInt(answerToQuest) >= 0 && Integer.parseInt(answerToQuest) < lengthOfList) {
                    System.out.println("This is the line chosen: " + (LinesList.get((int)(Integer.parseInt(answerToQuest)))));
                    int testesestt = lineAdminDisplay(LinesList.get((int)(Integer.parseInt(answerToQuest))), newConnect, "REGULAR");
                } else {
                    System.out.println("PICK A CORRECT THING MAN");
                    System.out.printf("%n%n%n%n%n");
                }

            } else {
                reviewArr = new String[approvedReviewsForStation + 1][4]; // create the array.
                reviewArr[0][0] = "USER";
                reviewArr[0][1] = "SHOPPING";
                reviewArr[0][2] = "CONNECTION_SPEED";
                reviewArr[0][3] = "COMMENT";


                int rowInt = 1;
                int colInt = 1;
                while (ReviewsForStationAppr.next()) {
                    if (rowInt > approvedReviewsForStation) {break;}
                    while(colInt <= 4) {
                        reviewArr[rowInt][colInt - 1] = ReviewsForStationAppr.getString(colInt);
                        colInt++;
                    }
                    rowInt++;
                    colInt = 1;
                }

                while (Averages.next()) {
                    avgShop = Double.parseDouble(Averages.getString("AVG(shopping)"));
                    avgConn = Double.parseDouble(Averages.getString("AVG(connection_speed)"));
                }


                System.out.println("STATION NAME: " + nameStation);
                System.out.println("STATUS: " + status);


                System.out.println("ADDRESS: " + address);


                System.out.println("LINES: " + LinesList.toString());


                System.out.printf("%n%n%n");
                System.out.println(avgShopping + avgShop);
                System.out.println(avgConnSpeed + avgConn);


                System.out.println(Arrays.deepToString(reviewArr).replace("], ", "]\n\n"));
                System.out.printf("%n%n%n");

                int lengthOfList = LinesList.size();

                System.out.println("You can choose an index from the lines array, or type in EXIT to go back to the Reviews page.");
                System.out.println("Additionally, you can type in s1, s2, or s3 in order to change the status to open, closed, half-open, respectively.");
                System.out.printf("MAKE A CHOICE: ");

                String answerToQuest = answerQuestion.nextLine();
                if (answerToQuest.equalsIgnoreCase("exit")) {
                    System.out.println("EXITING STATION DISPLAY TO GOTO REVIEWS PAGE");
                    return 0;
                } else if (!checkIfNumeric(answerToQuest) && (answerToQuest.equals("s1") || answerToQuest.equals("s2") || answerToQuest.equals("s3"))) {
                    String updateQuery = "UPDATE Station SET status='" + (answerToQuest.split("")[1].equals("1") ? "open"
                                                                          : answerToQuest.split("")[1].equals("2") ? "closed" : "half-open") + "' WHERE "
                        + "address='" + address + "' AND name='" + nameStation + "'";
                    Statement updateStatusQuery = getReviewsAddressEtc.createStatement();
                    ResultSet whateverUpdateStatus = updateStatusQuery.executeQuery(updateQuery);
                } else if (checkIfNumeric(answerToQuest) && Integer.parseInt(answerToQuest) >= 0 && Integer.parseInt(answerToQuest) < lengthOfList) {
                    System.out.println("This is the line chosen: " + (LinesList.get((int)(Integer.parseInt(answerToQuest)))));
                    int memem = lineAdminDisplay(LinesList.get((int)(Integer.parseInt(answerToQuest))), newConnect, "REGULAR");
                } else {
                    System.out.println("PICK A CORRECT THING MAN");
                    System.out.printf("%n%n%n%n%n");
                }

            }

        }


    }

    public static int lineAdminDisplay (String line, Connection newConnect, String addition) throws Exception {
        int numOfStops = -1; // set the numOfStops impossible
        String nameLine = "Line NUMBER/NAME: "; // prep string
        Connection lineConn = newConnect; // bring connection in
        Statement stateLine = newConnect.createStatement(); // create statement
        String[][] displayArr; // the array that'll be displayed.
        //String[] arrChoiceSort = new String[]{"station_name, order_number"};
        Scanner anotherScan = new Scanner(System.in);
        String queryToExec = null;
        //ArrayList<String> checkChoiceList = new ArrayList<>(Arrays.asList(arrChoiceSort));

        while(true) {
            if (!(addition.equals("REGULAR"))) {
                queryToExec = "SELECT station_name, order_number FROM Station_On_Line WHERE line_name='" + line + "' ORDER BY " + addition; // query to execute.
                System.out.println("QUERY: " + queryToExec);
            } else {
                queryToExec = "SELECT station_name, order_number FROM Station_On_Line WHERE line_name='" + line + "'"; // query to execute.
            }
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
                if (rowInt > numOfStops) {break;}
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
            System.out.println("NUMBER OF STOPS: " + numOfStops);
            System.out.println();

            System.out.println("In order to choose a station to look at, pick the appropriate index. The 15th station would be picked by 150. The 4th station would be picked by 40.");
            System.out.println("");
            System.out.println("In order to choose an order number to change, pick the appropriate index. To change the 15th station's order, you'd type in 151, for example.");
            System.out.println("");
            System.out.println("If you want to delete a station, type in d<station number>. In order to delete the sixth station, type in d6.");
            System.out.println("");
            System.out.printf("Type in EXIT to get out of this page and back to the previous page, or specify sorting by typing SORT <category> ASC/DESC, respectively. <category> = station_name || order_number. CHOOSE: ");

            String lineChoice = anotherScan.nextLine();
            if (lineChoice.equalsIgnoreCase("exit")) {
                return 0;
            } else if (checkIfNumeric(lineChoice)) {
                int choosy = Integer.parseInt(lineChoice);
                if (choosy >= 10 && choosy <= numOfStops * 10 && choosy % 10 == 0) {
                    stationAdminDisplay(displayArr[choosy / 10][0], newConnect); //Going to station admin display
                } else if (choosy >= 10 && choosy <= numOfStops * 10 + 1 && choosy % 10 == 1) {
                    String[] orderNums = new String[numOfStops];
                    for (int i = 1; i < numOfStops + 1; i++) {
                        orderNums[i - 1] = displayArr[i][1];
                    }
                    while (true) {
                        System.out.println("ENTER NEW ORDER NUMBER:");
                        String newNum = anotherScan.nextLine();
                        if (checkIfNumeric(newNum)) {
                            boolean isTaken = false;
                            for (int i = 0; i < orderNums.length; i++) {
                                if (newNum.equals(orderNums[i]))
                                    isTaken = true;
                            }
                            if (isTaken) {
                                System.out.println("sike thats a conflicting numba ohhohohohohohohohoh type another one pls");
                            } else {
                                String changeTheNumba = "UPDATE Station_On_Line SET order_number = '" + newNum + "' WHERE station_name = '" + displayArr[choosy / 10][0] + "' AND line_name = '" + line + "'";
                                Statement numberChangeStat = newConnect.createStatement();
                                ResultSet numberChangeSet = numberChangeStat.executeQuery(changeTheNumba);
                                break;
                            }
                        } else {
                            System.out.println("You entered a non numeric order number");
                        }
                    }
                } else {
                    System.out.println("Type a valid coordinate");
                }
            } else if (lineChoice.charAt(0) == 'd') {
                String sallal = lineChoice.substring(1, lineChoice.length());
                if (checkIfNumeric(sallal) && Integer.parseInt(sallal) > 0 && Integer.parseInt(sallal) <= numOfStops) {
                    while (true) {
                        System.out.println("You have selected station: " + displayArr[Integer.parseInt(sallal)][0]);
                        System.out.println("ARE YOU SURE YOU WOULD LIKE TO DELETE?");
                        System.out.println("Enter YES or NO");

                        String deleteChoice = anotherScan.nextLine();
                        if (deleteChoice.equalsIgnoreCase("YES")) {
                            String deleteQuery = "DELETE FROM Station_On_Line WHERE station_name='" + displayArr[Integer.parseInt(sallal)][0] + "' AND line_name='" + line + "'";
                            Statement deleteStationStat = newConnect.createStatement();
                            ResultSet deleteStationSet = deleteStationStat.executeQuery(deleteQuery);
                            System.out.println("You have deleted station " + displayArr[Integer.parseInt(sallal)][0] + " from line " + line);
                            break;
                        } else if (deleteChoice.equalsIgnoreCase("NO")) {
                            break;
                        } else {
                            System.out.println("Please enter YES or NO");
                        }
                    }
                }
            } else {
                String[] choiceArr = lineChoice.split(" ");
                if (choiceArr[0].trim().equalsIgnoreCase("sort") && (choiceArr[1].equals("station_name") || choiceArr[1].equals("order_number")) && (choiceArr[2].trim().equalsIgnoreCase("asc") || choiceArr[2].trim().equalsIgnoreCase("desc"))) {
                    System.out.println("I'M IN WHERE I NEED TO BE.");
                    String choiceMake = choiceArr[1] + " " + choiceArr[2]; // i'm not even going to check whether they typed the right thing
                    System.out.println(choiceMake);
                    return lineDisplay(line, newConnect, choiceMake); // make this recursive to sort.
                } else {
                    System.out.println("Type correctly next time.");
                }

            }

        }
    }

    public static int editAdmin(String ID) throws Exception {
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
                        if (firstName.length() == 0 || firstName.equals("NULL")) {
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
                                    if (mi.length() > 1 && !mi.equals("NULL")) {
                                        System.out.println("Middle initial should be one character.");
                                    } else {
                                        while (true) {
                                            System.out.println("ENTER LAST NAME: ");
                                            lastName = editor.nextLine();
                                            if (lastName.equalsIgnoreCase("exit")) {
                                                System.out.println("You exited the editor at LastName. Good-bye.");
                                                return 0;
                                            } else {
                                                if (lastName.length() == 0 || lastName.equals("NULL")) {
                                                    System.out.println("Type a last name please");
                                                } else {
                                                    while (true) {
                                                        System.out.println("ENTER USERID: ");
                                                        userID = editor.nextLine();
                                                        if (userID.equalsIgnoreCase("exit")) {
                                                            System.out.println("You exited the editor at UserID. Good-bye.");
                                                            return 0;
                                                        } else {
                                                            Statement usrcheck = newConnect.createStatement();
                                                            String usrchkQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                            ResultSet testingSet = usrcheck.executeQuery(usrchkQuery);
                                                            boolean unique = true;
                                                            if (userID.length() == 0 || userID.equals("NULL")) {
                                                                System.out.println("Type a UserID please");
                                                            } else if (testingSet.isBeforeFirst()){
                                                                System.out.println("Choose a unique UserID pls");
                                                            } else {
                                                                while (true) {
                                                                    System.out.println("ENTER PASSWORD: ");
                                                                    pass1 = editor.nextLine();
                                                                    if (pass1.equalsIgnoreCase("exit")) {
                                                                        System.out.println("You exited the editor at Password. Good-bye.");
                                                                        return 0;
                                                                    } else {
                                                                        if (pass1.length() == 0 || pass1.equals("NULL")) {
                                                                            System.out.println("Type a password please");
                                                                        } else {
                                                                            while (true) {
                                                                                System.out.println("RE-ENTER PASSWORD: ");
                                                                                pass2 = editor.nextLine();
                                                                                if (pass2.equalsIgnoreCase("exit")) {
                                                                                    System.out.println("You exited the editor at Password. Good-bye.");
                                                                                    return 0;
                                                                                } else {
                                                                                    if (!pass1.equals(pass2)) {
                                                                                        System.out.print("Make sure the passwords match");
                                                                                    } else {

                                                                                        mi = mi.equals("NULL") ? mi : "'" + mi + "'";

                                                                                        System.out.println("");
                                                                                        Statement rgstrcheck = newConnect.createStatement();
                                                                                        String passQuery = "UPDATE User SET ID = '" + userID + "', first_name = '" + firstName + "', minit = " + mi + ", last_name = '" + lastName + "', password = '" + pass1 + "' WHERE ID='" + ID + "'"; // YOU HAVE TO CHANGE WHERE IT'S ID.
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

    public static int addStation() throws Exception {
        Connection newConnect = getConnection();
        Scanner registration = new Scanner(System.in);

        Statement getAllLines = newConnect.createStatement();

        String getLineQueryNum = "SELECT COUNT(name) FROM Line"; // get the count of lines
        ResultSet getNumLines = getAllLines.executeQuery(getLineQueryNum); // get the count of stations.
        int lineCount = -5; // set random num
        while(getNumLines.next()) {
            lineCount = Integer.parseInt(getNumLines.getString("COUNT(name)")); // getNum = count of stations.
        }

        String name;
        String stateProvince;
        String address;
        int zipcode;
        String city;
        String status;
        String[] line = new String[lineCount];
        int count = 0;
        String[] orderNum = new String[lineCount];

        while(true) {
            System.out.print("ENTER STATION NAME: ");
            name = registration.nextLine();
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("You exited the registration at FirstName. Good-bye.");
                return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
            } else {

                Statement namecheck = newConnect.createStatement();
                String testNameQuery = "SELECT * FROM Station WHERE name='" + name + "'";
                ResultSet testNameSet = namecheck.executeQuery(testNameQuery);
                boolean uniqueName = true;

                if (name.length() == 0 || name.equals("NULL")) {
                    System.out.println("Type a station name please");
                } else if (testNameSet.isBeforeFirst()){
                    System.out.println("Choose a unique station name pls");
                } else {
                    while (true) {
                        System.out.println("ENTER STATE/PROVINCE: ");
                        stateProvince = registration.nextLine();
                        if (stateProvince.equalsIgnoreCase("exit")) {
                            System.out.println("You exited the registration at Middle Initial. Good-bye.");
                            return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
                        } else {
                            if (stateProvince.length() == 0 || stateProvince.equals("NULL")) {
                                System.out.println("Type a state/province please");
                            } else {
                                while (true) {
                                    System.out.println("ENTER ADDRESS: ");
                                    address = registration.nextLine();
                                    if (address.equalsIgnoreCase("exit")) {
                                        System.out.println("You exited the registration at address. Good-bye.");
                                        return 0;
                                    } else {
                                        if (address.length() == 0 || address.equals("NULL")) {
                                            System.out.println("Type a last name please");
                                        } else {
                                            while (true) {
                                                System.out.println("ENTER ZIPCODE OR -1 TO EXIT: ");

                                                Scanner zip = new Scanner(System.in);

                                                zipcode = zip.nextInt();
                                                if (zipcode == -1) {
                                                    System.out.println("You exited the registration at zipcode. Good-bye.");
                                                    return 0;
                                                } else {
                                                    if (zipcode > 0) {
                                                        while (true) {
                                                            System.out.println("ENTER CITY: ");
                                                            city = registration.nextLine();
                                                            if (city.equalsIgnoreCase("exit")) {
                                                                System.out.println("You exited the registration at city. Good-bye.");
                                                                return 0;
                                                            } else {
                                                                Statement locationcheck = newConnect.createStatement();
                                                                String testLocationQuery = "SELECT * FROM Station WHERE state_province='" + stateProvince + "' AND address='" + address + "' AND zipcode='" + zipcode + "' AND city='" + city + "'";
                                                                ResultSet testLocationSet = locationcheck.executeQuery(testLocationQuery);
                                                                boolean uniqueLocation = true;
                                                                if (city.length() == 0 || city.equals("NULL")) {
                                                                    System.out.println("Type a city please");
                                                                } else if (testLocationSet.isBeforeFirst()) {
                                                                    System.out.println("Choose a unique combination of state/province, address, zipcode, and city");
                                                                    break;
                                                                } else {

                                                                    while (true) {

                                                                        Scanner statusChoice = new Scanner(System.in);

                                                                        System.out.println("CHOOSE STATUS : ");
                                                                        System.out.println("(1): Open");
                                                                        System.out.println("(2): Closed");
                                                                        System.out.println("(3): Half-Open");
                                                                        System.out.println("(0): Quit");

                                                                        int theStatus = statusChoice.nextInt();
                                                                        //status = registration.nextLine();
                                                                        if (theStatus == 0) {
                                                                            System.out.println("You exited the registration at status. Good-bye.");
                                                                            return 0;
                                                                        } else {
                                                                            if (theStatus < 0 || theStatus > 3) {
                                                                                System.out.println("Type a valid status please");
                                                                            } else {
                                                                                status = theStatus == 1 ? "open" : theStatus == 2 ? "closed" : "half-open";
                                                                                while (true) {

                                                                                    Statement getAllLineNames = newConnect.createStatement();

                                                                                    String getLineNamesQuery = "SELECT name FROM Line ORDER BY name"; // order by name, assume we only have to get from stations.
                                                                                    ResultSet getLineNames = getAllLineNames.executeQuery(getLineNamesQuery); // get the stations.

                                                                                    String[] arrLineNames = new String[lineCount];
                                                                                    int fillIndex = 0;
                                                                                    while (getLineNames.next()) {
                                                                                                arrLineNames[fillIndex] = getLineNames.getString("name"); // get the value out of the column "name"
                                                                                                fillIndex++;
                                                                                            }
                                                                                    System.out.println("ARRAY FOR LINES: " + Arrays.toString(arrLineNames));

                                                                                    System.out.println("ENTER LINE NAME: ");
                                                                                    line[count] = registration.nextLine();
                                                                                    boolean isInLines = false;
                                                                                    for (int i = 0; i < fillIndex; i++) {
                                                                                        if (line[count].equals(arrLineNames[i])) {
                                                                                            isInLines = true;
                                                                                        }
                                                                                    }

                                                                                    if (line[count].equalsIgnoreCase("exit")) {
                                                                                        System.out.println("You exited the registration at line. Good-bye.");
                                                                                        return 0;
                                                                                    } else {
                                                                                        if (line[count].length() == 0 || line[count].equals("NULL") || !isInLines) {
                                                                                            System.out.println("Type a line please");
                                                                                        } else {
                                                                                            while (true) {
                                                                                                System.out.println("ENTER ORDER NUMBER OR -1 TO GO BACK TO LINE: ");
                                                                                                orderNum[count] = registration.nextLine();
                                                                                                if (orderNum[count].equalsIgnoreCase("exit")) {
                                                                                                    System.out.println("You exited the registration at order number. Good-bye.");
                                                                                                    return 0;
                                                                                                } else if (orderNum[count].equals("-1")) {
                                                                                                    break;
                                                                                                } else {

                                                                                                    Statement lineOrderCheck = newConnect.createStatement();
                                                                                                    String lineOrderQuery = "SELECT * FROM Station_On_Line WHERE line_name='" + line + "' AND order_number='" + orderNum[count] + "'";
                                                                                                    ResultSet lineOrderSet = lineOrderCheck.executeQuery(lineOrderQuery);
                                                                                                    boolean uniqueLineOrder = true;

                                                                                                    if (orderNum[count].length() == 0 || orderNum[count].equals("NULL")) {
                                                                                                        System.out.println("Type an order number please");
                                                                                                    } else if (lineOrderSet.isBeforeFirst()) {
                                                                                                        System.out.println("Choose a unique order number for the line please");

                                                                                                    } else {
                                                                                                        count++;
                                                                                                        while (true) {
                                                                                                            System.out.println("ADD STATION TO ANOTHER LINE?");
                                                                                                            System.out.println("(1): YES");
                                                                                                            System.out.println("(2): NO");

                                                                                                            Scanner stationAdder = new Scanner(System.in);
                                                                                                            int userSelection = stationAdder.nextInt();

                                                                                                            if (userSelection == 1) {
                                                                                                                break;
                                                                                                            } else if (userSelection == 2) {

                                                                                                                System.out.println("");

                                                                                                                Statement rgstrcheck = newConnect.createStatement();




                                                                                                                String passQuery = "INSERT INTO Station VALUES ('" + name + "', '" + status + "', '" + stateProvince + "', '" + address + "', '" + zipcode + "', '" + city + "')";
                                                                                                                ResultSet rgstrSet = rgstrcheck.executeQuery(passQuery);
                                                                                                                String adminAddQuery = "INSERT INTO Admin_Add_Station VALUES ('" + name + "', '" + userID + "', sysdate())";
                                                                                                                ResultSet adminAddSet = rgstrcheck.executeQuery(adminAddQuery);

                                                                                                                for (int j = 0; j < count; j++) {
                                                                                                                    String lineAddQuery = "INSERT INTO Station_On_Line VALUES ('" + name + "', '" + line[j] + "', '" + orderNum[j] + "')";
                                                                                                                    ResultSet lineAddSet = rgstrcheck.executeQuery(lineAddQuery);
                                                                                                                }

                                                                                                                // String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                                                                // ResultSet testSet = rgstrcheck.executeQuery(testQuery);

                                                                                                                // if (!(testSet.isBeforeFirst())) {
                                                                                                                //     System.out.println("username is not unique. try again.");
                                                                                                                //     System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");
                                                                                                                // } else {
                                                                                                                    System.out.println("Congrats, you've added a station.");
                                                                                                                    return 0; // break completely;
                                                                                                                // }

                                                                                                            } else {
                                                                                                                System.out.println("Enter a valid number");
                                                                                                            }
                                                                                                        }

                                                                                                        System.out.println("");

                                                                                                        Statement rgstrcheck = newConnect.createStatement();
                                                                                                        String passQuery = "INSERT INTO Station VALUES ('" + name + "', '" + status + "', " + stateProvince + ", '" + address + "', '" + zipcode + "', '" + city + "')";
                                                                                                        ResultSet rgstrSet = rgstrcheck.executeQuery(passQuery);
                                                                                                        String adminAddQuery = "INSERT INTO Admin_Add_Station VALUES ('" + name + "', " + userID + "', sysdate())";
                                                                                                        ResultSet amdinAddSet = rgstrcheck.executeQuery(adminAddQuery);

                                                                                                        // String testQuery = "SELECT * FROM User WHERE ID='" + userID + "'";
                                                                                                        // ResultSet testSet = rgstrcheck.executeQuery(testQuery);

                                                                                                        // if (!(testSet.isBeforeFirst())) {
                                                                                                        //     System.out.println("username is not unique. try again.");
                                                                                                        //     System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");
                                                                                                        // } else {
                                                                                                            System.out.println("Congrats, you've added a station.");
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

                                                    } else {
                                                       System.out.println("Type a zipcode please");
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static int addLine() throws Exception {
        Connection newConnect = getConnection();
        Scanner registration = new Scanner(System.in);

        Statement getAllStations = newConnect.createStatement();

        String getStationQueryNum = "SELECT COUNT(name) FROM Station"; // get the count of Stations
        ResultSet getNumStations = getAllStations.executeQuery(getStationQueryNum); // get the count of stations.
        int stationCount = -5; // set random num
        while(getNumStations.next()) {
            stationCount = Integer.parseInt(getNumStations.getString("COUNT(name)")); // getNum = count of stations.
        }

        String name;
        String station[] = new String[stationCount];
        int count = 0;

        while (true) {
            System.out.println("CHOOSE NAME FOR NEW LINE: ");
            name = registration.nextLine();
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("You exited the editor at Line Name. Good-bye.");
                return 0; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
            } else {
                if (name.length() == 0 || name.equals("NULL")) {
                    System.out.println("Type a line name please");
                } else {
                    while (true) {
                        String getStationNamesQuery = "SELECT name FROM Station ORDER BY name"; // order by name, assume we only have to get from stations.
                        ResultSet getStationNames = getAllStations.executeQuery(getStationNamesQuery); // get the stations.

                        String[] arrStationNames = new String[stationCount];
                        int[] arrStationNumbers = new int[stationCount];
                        int fillIndex = 0;
                        while (getStationNames.next()) {
                                    arrStationNames[fillIndex] = getStationNames.getString("name"); // get the value out of the column "name"
                                    //arrStationNumbers[fillIndex] = "Not On Line";
                                    fillIndex++;
                                }
                        //System.out.println("ARRAY FOR LINES: " + Arrays.toString(arrStationNames));
                        System.out.println("ALL STATIONS AND ORDER ON LINE: ");
                        String stationOrderNum = arrStationNumbers[count] == 0 ? "Not On Line" : String.valueOf(arrStationNumbers[count]);
                        for (int i = 0; i < fillIndex; i++) {
                            System.out.println(arrStationNames[i] + " [ " + stationOrderNum + " ]");
                        }

                        System.out.println();
                        System.out.println("ENTER *NAME OF* STATION YOU WOULD LIKE TO ADD TO LINE: " + name);
                        station[count] = registration.nextLine();
                        if (station[count].equalsIgnoreCase("exit")) {
                            System.out.println("You exited the editor at Add Station. Good-bye");
                            return 0;
                        } else {
                            boolean isInStations = false;
                            for (int i = 0; i < count; i++) {
                                if (station[count].equals(station[i]))
                                    isInStations = true;
                            }

                            if (name.length() == 0 || name.equals("NULL") || isInStations) {
                                System.out.println("Type a valid Station Name please");
                            } else {



                                while (true) {
                                    System.out.println("ENTER ORDER NUMBER FOR " + station[count] + " OR 0 TO EXIT");
                                    Scanner chooseOrderNum = new Scanner(System.in);
                                    arrStationNumbers[count] = chooseOrderNum.nextInt();
                                    if (arrStationNumbers[count] == 0) {
                                        System.out.println("You exited the editor at Order Number. Good-bye");
                                        return 0;
                                    } else {
                                        boolean isInNums = false;
                                        for(int i = 0; i < count; i++) {
                                            if (arrStationNumbers[count] == arrStationNumbers[i]) {
                                                isInNums = true;
                                            }
                                        }

                                        if (arrStationNumbers[count] < 0) {
                                            System.out.println("Enter a valid order number please");
                                        } else {
                                            count++;
                                            while (true) {
                                                System.out.println("ADD ANOTHER STATION TO LINE?");
                                                System.out.println("(1): YES");
                                                System.out.println("(2): NO");

                                                int userSelection = chooseOrderNum.nextInt();

                                                if (userSelection == 1) {
                                                    break;
                                                } else if (userSelection == 2) {

                                                    System.out.println("");

                                                    Statement rgstrcheck = newConnect.createStatement();




                                                    String passQuery = "INSERT INTO Line VALUES ('" + name + "')";
                                                    ResultSet rgstrSet = rgstrcheck.executeQuery(passQuery);
                                                    String adminAddQuery = "INSERT INTO Admin_Add_Line VALUES ('" + name + "', '" + userID + "', sysdate())";
                                                    ResultSet adminAddSet = rgstrcheck.executeQuery(adminAddQuery);

                                                    for (int j = 0; j < count; j++) {
                                                        String lineAddQuery = "INSERT INTO Station_On_Line VALUES ('" + station[j] + "', '" + name + "', '" + arrStationNumbers[j] + "')";
                                                        ResultSet lineAddSet = rgstrcheck.executeQuery(lineAddQuery);
                                                    }

                                                    System.out.println("Congrats you added a line");
                                                    return 0;

                                                } else {
                                                    System.out.println("Enter a valid number");
                                                }
                                            }
                                        }
                                        break;
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
