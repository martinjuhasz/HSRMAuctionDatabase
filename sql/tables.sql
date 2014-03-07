-------------------------------------------------------------------------------------
--	DOMAINS
-------------------------------------------------------------------------------------

CREATE DOMAIN "EMAIL" AS VARCHAR CHECK (VALUE ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');
CREATE DOMAIN "BLZ" AS VARCHAR CHECK (VALUE ~* '^[1-8][0-9]{7}$');
CREATE DOMAIN "KTNR" AS VARCHAR CHECK (VALUE ~* '^[1-9][0-9]{4,10}$');
CREATE DOMAIN "NOT_EMPTY_VARCHAR" AS VARCHAR(255) CHECK (char_length(VALUE)>0);
CREATE DOMAIN "NOT_EMPTY_TEXT" AS TEXT CHECK (char_length(VALUE)>0);



-------------------------------------------------------------------------------------
--	TABLES
-------------------------------------------------------------------------------------

-- postal_code is a string to allow postal codes of other countries that possibly have another postal pattern
CREATE TABLE "city" (
	postal_code	"NOT_EMPTY_VARCHAR"	PRIMARY KEY,
	city 		"NOT_EMPTY_VARCHAR"	NOT NULL
);

-- primary key is a SERIAL to let the user change his username
-- if a user gets deleted, only the deleted flag is set to maintain all references to auctions, etc
-- because an auction and its related bids affects other users
CREATE TABLE "user" (
	id				SERIAL 					PRIMARY KEY,
	username 		"NOT_EMPTY_VARCHAR" 	UNIQUE NOT NULL,
	password		VARCHAR(255)			NOT NULL,
	first_name		"NOT_EMPTY_VARCHAR"		NOT NULL,
	last_name		"NOT_EMPTY_VARCHAR"		NOT NULL,
	email			"EMAIL"					NOT NULL,
	street			"NOT_EMPTY_VARCHAR"		NOT NULL,
	street_number	"NOT_EMPTY_VARCHAR"		NOT NULL,
	postal_code		"NOT_EMPTY_VARCHAR"		REFERENCES "city"(postal_code) NOT NULL,
	deleted			BOOLEAN					DEFAULT FALSE NOT NULL
);

-- account_holder is an additional field because the account holder could differ from the user itself
CREATE TABLE "bank_account" (
	bank_number		"BLZ"				NOT NULL,
	account_number	"KTNR"				NOT NULL,
	account_holder	"NOT_EMPTY_VARCHAR"	NOT NULL,
	uid				INT4				REFERENCES "user"(id) NOT NULL,
	PRIMARY KEY (bank_number, account_number)
);

CREATE TABLE "admin" (
	uid 	INT4 PRIMARY KEY REFERENCES "user"(id)
);

-- save a search to access it quickly
CREATE TABLE "search_term" (
	uid			INT4 				REFERENCES "user"(id),
	term 		"NOT_EMPTY_VARCHAR"	NOT NULL,
	PRIMARY KEY (uid, term)
);

-- name is not the primary key because with an extra SERIAL we are able to change the name without having to alter all references
CREATE TABLE "category" (
	id		SERIAL 				PRIMARY KEY,
	name 	"NOT_EMPTY_VARCHAR"	UNIQUE NOT NULL
);

-- the is_directbuy flag seperates between a real auction and a directly buyable entry
-- if it is a directly buyable entry, the price is the final price, else it's the starting price
CREATE TABLE "auction" (
	id					SERIAL 				PRIMARY KEY,
	start_time			TIMESTAMP 			DEFAULT CURRENT_TIMESTAMP NOT NULL,
	end_time			TIMESTAMP 			NOT NULL,
	title				"NOT_EMPTY_VARCHAR"	NOT NULL,
	description			TEXT				NOT NULL,
	image 				BYTEA,
	category			INT4				REFERENCES "category"(id) ON DELETE CASCADE NOT NULL,
	offerer				INT4 				REFERENCES "user"(id) NOT NULL,
	price 				INT 				NOT NULL CHECK(price > 0),
	is_directbuy		BOOLEAN				DEFAULT FALSE NOT NULL
);

CREATE TABLE "rating" (
	rater			INT4			REFERENCES "user"(id),
	auction			INT4			PRIMARY KEY REFERENCES "auction"(id) ON DELETE CASCADE,
	score			INT 			CHECK (score BETWEEN 1 AND 5)
);

CREATE TABLE "bid" (
	uid			INT4			REFERENCES "user"(id),
	auction 	INT4			REFERENCES "auction"(id) ON DELETE CASCADE,
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	price 		INT,
	PRIMARY KEY(uid, auction, time)
);

CREATE TABLE "comment" (
	uid			INT4				REFERENCES "user"(id),
	auction 	INT4				REFERENCES "auction"(id) ON DELETE CASCADE,
	time 		TIMESTAMP 			DEFAULT CURRENT_TIMESTAMP NOT NULL,
	content		"NOT_EMPTY_TEXT" 	NOT NULL,
	PRIMARY KEY (uid, auction, time) 		
);


-------------------------------------------------------------------------------------
--	VIEWS AND RULES
-------------------------------------------------------------------------------------

-- the view for the admins to maintain the user
-- the view combines the user and the city table, to simply get the needed information of the user
CREATE VIEW "user_view" AS
	SELECT u.username, u.first_name, u.last_name, u.email, u.street, u.street_number, u.postal_code, c.city, u.password, u.id 
	FROM "user" u LEFT JOIN city c ON u.postal_code=c.postal_code
	WHERE u.deleted=FALSE 
	ORDER BY u.username;

-- maps the user_view to the city and user table on insert
-- adds a city if needed
CREATE RULE "user_insert" AS ON INSERT TO "user_view" DO INSTEAD (
       INSERT INTO  "city"(postal_code, city) SELECT NEW.postal_code, NEW.city WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE postal_code = NEW.postal_code);
       INSERT INTO  "user"(username, password, first_name, last_name,email,street,street_number,postal_code) 
       		VALUES(NEW.username, NEW.password, NEW.first_name, NEW.last_name, NEW.email, NEW.street, NEW.street_number, NEW.postal_code);
);

-- maps the user_view to the city and user table on update
-- adds a city if needed
CREATE RULE "user_update" AS ON UPDATE TO "user_view" DO INSTEAD (
       INSERT INTO  "city"(postal_code, city) SELECT NEW.postal_code, NEW.city WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE postal_code = NEW.postal_code);
       UPDATE "user" SET username=NEW.username, first_name=NEW.first_name, last_name=NEW.last_name, email=NEW.email, street=NEW.street,  
       			street_number=NEW.street_number, postal_code=NEW.postal_code, password=NEW.password 
       		WHERE id=NEW.id;
);

-- maps the user_view to the city and user table on delete
-- city gets not deleted because its perhaps needed by another user
CREATE RULE "user_delete" AS ON DELETE TO "user_view" DO INSTEAD (
  	DELETE FROM "bank_account" WHERE uid=OLD.id;
  	DELETE FROM "search_term" WHERE uid=OLD.id;
  	DELETE FROM "admin" WHERE uid=OLD.id;
  	UPDATE "user" SET deleted=TRUE WHERE id=OLD.id;
);

-- if auction is a direct buy or no bid was made it returns the price
-- else it returns the second highest bid plus 1
CREATE FUNCTION max_bid(integer)
RETURNS integer
AS '
	SELECT coalesce((SELECT MAX(price) FROM "bid" b 
					 WHERE a.id = b.auction AND price < (SELECT MAX(price) 
					 FROM "bid" b WHERE a.id = b.auction)), 
					 a.price) + 
		   coalesce((SELECT 1 FROM auction c WHERE c.id=a.id AND NOT a.is_directbuy AND EXISTS (SELECT * FROM "bid" d WHERE a.id = d.auction)), 0)
	FROM auction a WHERE a.id=$1;
' LANGUAGE 'SQL';

-- returns the person id with the highest bidder
CREATE FUNCTION max_bidder(integer)
RETURNS integer
AS '
	SELECT u.id FROM "user" u, "bid" b1 
	WHERE u.id = b1.uid AND b1.price >= (SELECT MAX(bb.price) 
	FROM "bid" bb WHERE bb.auction = $1) AND b1.auction = $1;
' LANGUAGE 'SQL';

-- formats a timestamp to german time format
CREATE FUNCTION date_format(TIMESTAMP)
RETURNS VARCHAR
AS
$BODY$
	SELECT CASE WHEN ($1 >= now()::date AND $1< (now()::date + interval '24h')) THEN ('Heute ' || to_char($1, 'HH24:MM')) ELSE to_char($1, 'DD.MM.YYYY HH24:MM') END;
$BODY$ LANGUAGE 'SQL';

-- bundles the currently running auctions into a view with max bid and formatted date
CREATE VIEW "auction_view" (title, end_time, max_bid, category) AS
	SELECT	a.title,
			date_format(a.end_time),
			max_bid(a.id) AS max_bid,
			a.category,
			a.description,
			a.id
	FROM "auction" a WHERE a.end_time >= now() ORDER BY a.end_time;

-- gets als auctions that are finished
CREATE VIEW "auctions_won_view" (title, max_bid, max_bidder, id) AS
	SELECT	a.title,
			max_bid(a.id),
			max_bidder(a.id),
			a.id
	FROM "auction" a WHERE a.end_time < now() ORDER BY a.end_time;

-- creates a view for viewing all details of a auction including max bid, formatted time, ratings, etc
CREATE VIEW "auction_detail_view" AS
	SELECT a.id, date_format(a.start_time) AS start_time, date_format(a.end_time) AS end_time, a.title, a.description, a.image, c.name AS category, u.username AS offerer, a.price, a.is_directbuy,
	max_bid(a.id), (SELECT u2.username FROM "user" u2 WHERE u2.id=max_bidder(a.id)) AS max_bidder, 
	coalesce((SELECT TRUE WHERE a.end_time > now()), FALSE) AS open, max_bidder(a.id) AS max_bidder_id,
	coalesce(r.score, 0)
	FROM "auction" a JOIN "category" c ON a.category=c.id JOIN "user" u ON a.offerer=u.id LEFT JOIN "rating" r ON r.auction=a.id;

-- displays all comments including their username
CREATE VIEW "auction_comment_view" AS
	SELECT u.username, c.content, c.time, c.auction FROM "comment" c JOIN "user" u ON u.id=c.uid ORDER BY c.time DESC;

-- lists all closed auctions
CREATE VIEW "closed_auctions_view" AS
	SELECT	cat.name, 
		(SELECT COUNT(*) FROM auction a WHERE a.category=cat.id AND a.end_time < now()) AS count,
		coalesce((SELECT SUM(prices.price) as maximum 
			FROM (SELECT MAX(d.price) AS price FROM auction c, bid d 
			WHERE c.category=cat.id AND d.auction=c.id AND c.end_time < now() 
			GROUP BY c.id) AS prices), 0) AS sum
	FROM category cat ORDER BY sum DESC;


