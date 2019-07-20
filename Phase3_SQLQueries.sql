# SAMPLE INSERT STATEMENTS
INSERT INTO User VALUES ("chall68", "Charles", "J", "Hall", "eightchar", "scasturi3@gatech.edu");
INSERT INTO User VALUES("AMERICA", "AM", "E", "RICA", "notefxxx", "america.gmail.com");
INSERT INTO User VALUES("shcar", "Shreyas", "R", "Casturi", "chareno", "american@gatech.edu");
INSERT INTO User VALUES('wwhite12', 'Walter', 'H', 'White', 'pass1234', NULL);
INSERT INTO Admin VALUES("wwhite12");
INSERT INTO Admin VALUES('shcar');
INSERT INTO Station VALUES ('Catalunya', 'open', 'Catalonia', '08002 Barcelona', 08002, 'Barcelona');
INSERT INTO Station VALUES ('Arc de Triomf', 'closed', 'Catalonia', '12345 Barcelona', 12345, 'Barcelona');
INSERT INTO Line VALUES('L1');
INSERT INTO Card VALUES('chall68', 'T-mes', '12/04/2011 12:00:00 AM', NULL, '01/05/2012 12:00:00 AM');
INSERT INTO Trip VALUES('chall68', 'T-mes', '12/04/2011 12:00:00 AM', '12/05/2011 8:00:00 AM', NULL, 'Catalunya', NULL);
INSERT INTO Review VALUES('chall68', 1, 3, 4, 'Cool station. Kinda hot though.', 'wwhite12', 'approved', NULL, 'Catalunya');
INSERT INTO Admin_Add_Line VALUES('L1', 'wwhite12', '05/10/2019 8:32:00 AM');
INSERT INTO Admin_Add_Station VALUES('Catalunya', 'wwhite12', '5/10/2019 8:34:00 AM');
INSERT INTO Station_On_Line VALUES('Catalunya', 'L1', 16);





'''LOGIN QUERIES: '''
# check username validity
SELECT * FROM User WHERE ID = "";
# check username and password combination validity
SELECT * FROM User WHERE ID = "" AND password  = "";
# check if the user credentials match a user in the admin table
SELECT * FROM Admin JOIN User USING (ID) WHERE User.ID = Admin.ID;



''' REGISTRATION QUERIES: '''
# register a new user
INSERT INTO User VALUES ("chall68", "Charles", "J", "Hall", "eightchar", "scasturi3@gatech.edu");



''' PASSENGER LANDING QUERIES: '''
# display the user's first and last name
SELECT CONCAT(User.first_name, '', User.last_name) FROM User WHERE User.ID = '';



''' LEAVE REVIEW: '''
# display all stations in alphabetical order
SELECT name FROM Station ORDER BY Station.name;
# insert a review into the Review table
INSERT into Review VALUES("AMERICA", 101, 3, 4, "", "shcar", "pending", NULL, 'Arc de Triomf');



''' VIEW REVIEWS: '''
# display all of the reviews that a specific user has left
SELECT * FROM Review WHERE Review.passenger_id = 'shcar';



''' EDIT REVIEWS: '''
# display all of the information for a particular review left by a specific user
SELECT * FROM Review WHERE passenger_id = '' AND rid = '';
# delete a review from the database
DELETE FROM Review WHERE passenger_id = '' AND rid = '';
# update the review and give it the corresponding date/time
UPDATE Review SET edit_timestamp = '' WHERE passenger_id = '' AND rid = '';
# update the review, changing its status back to pending
UPDATE Review SET approval_status = 'pending' WHERE passenger_id = '' AND rid = '';



''' STATION INFO: '''
# pull all the info for a certain station
SELECT * FROM Station WHERE name = '';
# calculate average values for shopping and connection speed ratings
SELECT AVG(shopping) FROM Review WHERE station_name = '' AND Station.status = 'approved';
SELECT AVG(connection_speed) FROM Review WHERE station_name = '' AND Station.status = 'approved';
# display all of a station's approved reviews
SELECT * FROM Review WHERE approval_status = 'approved';



''' LINE SUMMARY: '''
# display the line name and all its corresponding stations
SELECT Line.name, Station_On_Line.station_name FROM Line, Station_On_Line WHERE Line.name = Station_On_Line.line_name;
# display the total number of stops on the line
SELECT COUNT(order_number) FROM Station_On_Line, Line WHERE line_name = '';



''' EDIT PROFILE: '''
# display all the user's information
SELECT * FROM User WHERE User.ID = '';
# IMPORTANT: REMEMBER TO INCLUDE ON DELETE CASCADE FOR ALL REVIEWS, CARDS, AND TRIPS SO THAT WHEN A USER IS DELETED, THEIR REPSECTIVE CARDS/TRIPS/REVIEWS DELETE TOO



''' BUY CARD: '''
# insert a card holder into the database based on the type they choose
INSERT INTO Card VALUES('chall68', 'T-mes', '12/04/2011 12:00:00 AM', NULL, '01/05/2012 12:00:00 AM');
# do we have to handle the expiration date requirements for the lightweight option, or is that handled by the GUI?



''' GO ON A TRIP: '''
# display all the stations that have had cards used by them in alphabetical order, along with the datetime the card was used
SELECT from_station_name






