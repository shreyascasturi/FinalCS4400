import java.util.Scanner;
import java.sql.*; // import the big package.
public class GUI {
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
                if (resultofLogin == 1) { // if 1, we'll choose to break.
                    break;
                }
            } else if (ChoiceIntroString == 2) { // assume register.
                System.out.println("GO BACK INTO IT, REGISTER");
                // goto register.
            } else if (ChoiceIntroString == 3) { // assume exit.
                System.out.println("Thank you for using our app.");
                ChoiceIntroPage.close(); // close the scanner.
                break;
            } else { // assume incorrect input.
                System.out.println("You entered an incorrect number. Try again. Or quit.");
                System.out.println("");
            }            
        }

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
        System.out.println("----------- LOGIN SCREEN -------------");
        while (true) { // this keeps repeating if your username is wrong/doesn't exist in the database.
            System.out.print("ENTER YOUR USERNAME: ");
            String username = userpwd.nextLine(); // get the username. // test "chal68";
            if (username.equalsIgnoreCase("exit")) {
                System.out.println("You exited the checkLogin at User. Good-bye.");
                break; // this goes to the two lines all the way at the end of the method, the 'unreachables.'
            } else {
                //System.out.println(""); // need to go down.
                Statement usercheck = newConnect.createStatement(); // we create a statement to be used.
                String userQuery = "SELECT ID FROM User WHERE ID = '" + username + "'"; // create the query and then execute.
                ResultSet userSet = usercheck.executeQuery(userQuery); // see? Get the results as a ResultSet.
                if (!(userSet.isBeforeFirst())) { // i'm not gonna explain this one... but... if the set has no rows, then the set is empty.
                    System.out.println("you failed. Username is incorrect. Try again."); // if the set is empty... then the username is wrong.
                    System.out.println("------- RETURNING TO LOGIN SCREEN, USERNAME DOESN'T EXIST --------");
                } else {
                    while (true) { // an inner while-true loop, that only executes repeatedly if you fail your password login.
                        System.out.print("ENTER YOUR PASSWORD: "); // enter the password. test "eightchar".
                        String password = userpwd.nextLine();
                        if (password.equalsIgnoreCase("exit")) {
                            System.out.println("You exited the checkLogin at Password. Good-bye.");
                            break;
                        } else {
                            System.out.println("");
                            Statement pwdcheck = newConnect.createStatement();
                            String passQuery = "SELECT password FROM User WHERE ID = '" + username + "' AND password = '" + password + "'";
                            ResultSet pwdSet = pwdcheck.executeQuery(passQuery);
                            if (!(pwdSet.isBeforeFirst())) {
                                System.out.println("you entered password wrong. try again.");
                                System.out.println("-------- RETURNING TO LOGIN SCREEN, PASSWORD DOESN'T MATCH WITH GIVEN USERNAME ---------");                        
                            } else {
                                System.out.println("Congrats, you're now logged in.");
                                System.exit(1); // break completely;
                            }
                        }

                    }
                    break; // breaks out of the big while-loop, will hit if exit at password.
                }
                            
            }           
        }
        System.out.println("HITTING LINE OUTSIDE BIG BLOCK, DONE WHEN EXIT TYPED IN USERNAME CHECK");
        return 0; // this should never hit.
        
    }
   
}
