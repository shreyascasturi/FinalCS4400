DROP DATABASE IF EXISTS TMB;
CREATE DATABASE TMB;
USE TMB;

CREATE TABLE User
(
    ID varchar(255),
    first_name varchar(255),
    minit char(1),
    last_name varchar(255),
    password varchar(255) NOT NULL,
    CONSTRAINT PASSWORDLENCHECK
              CHECK (char_length(password) >= 8), /* a constraint on password length.
              We check if the password length is actually at least 8 chars. */
    passenger_email varchar(255),
    PRIMARY KEY (ID) 
);

CREATE TABLE Admin
(
    ID varchar(255) PRIMARY KEY,
    FOREIGN KEY(ID) REFERENCES User(ID) ON UPDATE CASCADE ON DELETE CASCADE
			/*ON UPDATE CASCADE,
			CONSTRAINT ADMINIDCASCADES FOREIGN KEY (ID) REFERENCES User(ID) ON DELETE CASCADE*/
);

CREATE TABLE Station
(
    name varchar(255),
    status varchar(255),
    CONSTRAINT ADDINGSTATUSTYPE
               CHECK (status = 'open' OR status = 'closed' OR status = 'half-capacity'),
               /* When creating station tuples, we want to check if the status is one of these.
               It cannot be anything but.*/
    state_province varchar(255),
    address varchar(255),
    zipcode int,
    city varchar(255),
    PRIMARY KEY(name),
    UNIQUE(state_province, address, zipcode, city)
);

/* TRYING TO ENFORCE CONSTRAINT/TRIGGER on admin updating station statuses. // we could also just check in java for this.
CREATE TRIGGER stationUpdates
       BEFORE UPDATE OF status ON STATION
              FOR EACH ROW
                            WHEN (NEW.status = 'open' OR NEW.status = 'closed' OR status = 'half-capacity')
                                 UPDATE STATION
                                 SET status = NEW.status;
                                 WHERE (station is the selected one) */
CREATE TABLE Line
(
    name varchar(255) PRIMARY KEY
);

CREATE TABLE Card
(
    user_ID varchar(255),
    type_of_card varchar(255), /*T-mes: 30 DAYS, INF USES | T-10: NO EXP, 10 USES 
    | T-50/30: 30 DAYS, 50 USES | T-jove: 90 DAYS, INF USES*/
    CONSTRAINT TYPECHECKCARD
               CHECK (type_of_card = 'T-mes' OR type_of_card = 'T-10' OR type_of_card = 'T-50/30'
                              OR type_of_card = 'T-jove'),
                              /*When adding tuples/buying cards, we want to make sure the card is only of these forms.*/
    purchase_date_time Datetime,
    uses_left int, 
    expiration_date Date,
    PRIMARY KEY (user_ID, type_of_card, purchase_date_time),
    FOREIGN KEY(user_ID) REFERENCES User(ID) ON UPDATE CASCADE ON DELETE CASCADE/*,
			CONSTRAINT USERCARDCASCADES FOREIGN KEY (user_ID) REFERENCES User(ID) ON UPDATE CASCADE ON DELETE CASCADE*/
);

CREATE TABLE Trip
(
    user_ID varchar(255),
    card_type varchar(255),
    card_purchase_date_time Datetime,
    start_date_time Datetime,
    end_date_time Datetime,
    from_station_name varchar(255) NOT NULL,
    to_station_name varchar(255),
    PRIMARY KEY(user_ID, card_type, card_purchase_date_time, start_date_time),
    FOREIGN KEY (from_station_name) REFERENCES Station(name) ON UPDATE CASCADE ON DELETE CASCADE,
			/*ON DELETE CASCADE,
            CONSTRAINT TRIPFROMSTATION FOREIGN KEY(from_station_name) REFERENCES Station(name),*/
    FOREIGN KEY (to_station_name) REFERENCES Station(name) ON UPDATE CASCADE ON DELETE CASCADE,
			/*ON DELETE CASCADE,
            CONSTRAINT TRIPTOSTATION FOREIGN KEY(to_station_name) REFERENCES Station(name),*/
    FOREIGN KEY (user_ID, card_type, card_purchase_date_time) REFERENCES Card(user_ID, type_of_card, purchase_date_time) ON UPDATE CASCADE ON DELETE CASCADE
            /*ON DELETE CASCADE,
			CONSTRAINT CARDTRIPCASCADES FOREIGN KEY (user_ID, card_type, card_purchase_date_time) REFERENCES Card(user_ID, type_of_card, purchase_date_time) ON UPDATE CASCADE*/
);

CREATE TABLE Review
(
    passenger_ID varchar(255),
    rid int,
    shopping int,
    connection_speed int,
    comment text,
    approver_ID varchar(255),
    approval_status varchar(255), /*SET DEFAULT 'Pending'*/ /* just set the default approval status to 
    pending.*/
    edit_timestamp Datetime,
    station_name varchar(255) NOT NULL,
    PRIMARY KEY (passenger_ID, rid),
    FOREIGN KEY(passenger_ID) REFERENCES User(ID) ON UPDATE CASCADE ON DELETE CASCADE,
            /*ON DELETE CASCADE,
			CONSTRAINT USERREVIEWCASCADES FOREIGN KEY (passenger_ID) REFERENCES User(ID) ON UPDATE CASCADE,*/
    FOREIGN KEY(approver_ID) REFERENCES Admin(ID) ON UPDATE CASCADE ON DELETE SET NULL,
			/*ON DELETE CASCADE,
            CONSTRAINT ADMINREVIEWCASCADES FOREIGN KEY (approver_ID) REFERENCES Admin(ID) ON UPDATE CASCADE, */
    FOREIGN KEY(station_name) REFERENCES Station(name) ON UPDATE CASCADE ON DELETE CASCADE
			/* ON DELETE CASCADE,
            CONSTRAINT STATIONNAMECASCADE FOREIGN KEY (station_name) REFERENCES Station(name) ON UPDATE CASCADE*/
);

CREATE TABLE Admin_Add_Line
(
    line_name varchar(255) PRIMARY KEY,
    admin_ID varchar(255),
    date_time Datetime,
    FOREIGN KEY(admin_ID) REFERENCES Admin(ID) ON UPDATE CASCADE ON DELETE CASCADE,
			/*ON DELETE CASCADE,
            CONSTRAINT ADMINADDLINE FOREIGN KEY (admin_ID) REFERENCES Admin(ID) ON UPDATE CASCADE,*/
    FOREIGN KEY(line_name) REFERENCES Line(name) ON UPDATE CASCADE ON DELETE CASCADE
			/*ON DELETE CASCADE,
            CONSTRAINT LINENAME FOREIGN KEY (line_name) REFERENCES Line(name) ON UPDATE CASCADE*/
);

CREATE TABLE Admin_Add_Station
(
    station_name varchar(255) PRIMARY KEY,
    admin_ID varchar(255),
    date_time Datetime,
    FOREIGN KEY(admin_ID) REFERENCES Admin(ID) ON UPDATE CASCADE ON DELETE CASCADE,
			/*ON DELETE CASCADE,
            CONSTRAINT ADMINADDSTATION FOREIGN KEY (admin_ID) REFERENCES Admin(ID) ON UPDATE CASCADE,*/
    FOREIGN KEY(station_name) REFERENCES Station(name) ON UPDATE CASCADE ON DELETE CASCADE
			/*ON DELETE CASCADE,
            CONSTRAINT ADMINSTATIONADD FOREIGN KEY (station_name) REFERENCES Station(name) ON UPDATE CASCADE*/
);

CREATE TABLE Station_On_Line
(
    station_name varchar(255),
    line_name varchar(255),
    order_number int,
    PRIMARY KEY(station_name, line_name),
    FOREIGN KEY(station_name) REFERENCES Station(name) ON UPDATE CASCADE ON DELETE CASCADE,
			/*ON DELETE CASCADE,
            CONSTRAINT STATIONONLINENAMECASCADE FOREIGN KEY (station_name) REFERENCES Station(name) ON UPDATE CASCADE,*/
    FOREIGN KEY(line_name) REFERENCES Line(name) ON UPDATE CASCADE ON DELETE CASCADE
			/*ON DELETE CASCADE,
            CONSTRAINT LINEFORSTATIONNAMECASCADE FOREIGN KEY (line_name) REFERENCES Line(name) ON UPDATE CASCADE*/
);

/* populate some stuff.*/
INSERT INTO User VALUES("chal68", "Charles", "J", "Hall", "eightchar", "scasturi3@gatech.edu"); /*check if 
       password constraint holds.*/
INSERT INTO User VALUES("AMERICA", "AM", "E", "RICA", "notefxxx", "america.gmail.com");
/*INSERT INTO User VALUES("shcar", "Shreyas", "R", "Casturi", "chareno", "american@gatech.edu"); /* should not be added.*/
INSERT INTO Station VALUES("Arc", "open", "GA", "2714 Something", "30062", "Marietta");
INSERT INTO Station VALUES("Not arc", "closed", "GA", "AMERICA", "3063", "WHAT");
INSERT INTO Station VALUES("random arc", "open", "CA", "AMERICA", "40333", "JM");
INSERT INTO Line VALUES("The Test Line"); /*testing line and station for reviews.*/
INSERT INTO Station_On_Line VALUES("Arc", "The Test Line", 3);
INSERT INTO Station_On_Line VALUES("Not arc", "The Test Line", 2);
INSERT INTO Admin VALUES("chal68");
INSERT INTO Card VALUES("AMERICA", "T-mes", sysdate(), NULL, "2019-12-13");
/*INSERT INTO Card VALUES("chal68", "T-mes", SYSDATE())
INSERT INTO Card VALUES("chal68", "T-mes", 20191212)*/
/*INSERT INTO Card VALUES("chal68", "T-mes", 20190506, 50, 20191213);*/

