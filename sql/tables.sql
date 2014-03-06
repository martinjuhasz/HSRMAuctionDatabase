-------------------------------------------------------------------------------------
--	DOMAINS
-------------------------------------------------------------------------------------

CREATE DOMAIN "EMAIL" AS VARCHAR CHECK (VALUE ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');
CREATE DOMAIN "BLZ" AS VARCHAR CHECK (VALUE ~* '^[1-8][0-9]{7}$');
CREATE DOMAIN "KTNR" AS VARCHAR CHECK (VALUE ~* '^[1-9][0-9]{4,10}$');



-------------------------------------------------------------------------------------
--	TABLES
-------------------------------------------------------------------------------------

CREATE TABLE "city" (
	postal_code	VARCHAR(40)		PRIMARY KEY,
	city 		VARCHAR(255)	NOT NULL
);

CREATE TABLE "user" (
	username 		VARCHAR(100) 	PRIMARY KEY,
	password		VARCHAR(255)	NOT NULL,
	first_name		VARCHAR(255)	NOT NULL,
	last_name		VARCHAR(255)	NOT NULL,
	email			"EMAIL"			NOT NULL,
	street			VARCHAR(255)	NOT NULL,
	street_number	VARCHAR(255)	NOT NULL,
	postal_code		VARCHAR(40)		REFERENCES "city"(postal_code) NOT NULL,
	deleted			BOOLEAN			DEFAULT FALSE
);

CREATE TABLE "bank_account" (
	bank_number		"BLZ"			NOT NULL,
	account_number	"KTNR"			NOT NULL,
	account_holder	VARCHAR(255)	NOT NULL,
	username		VARCHAR(100)	REFERENCES "user"(username) NOT NULL,
	PRIMARY KEY (bank_number, account_number)
);

CREATE TABLE "admin" (
	username 	VARCHAR(100) PRIMARY KEY REFERENCES "user"(username)
);

CREATE TABLE "search_term" (
	username	VARCHAR(100) PRIMARY KEY REFERENCES "user"(username),
	term 		VARCHAR(255) NOT NULL
);

CREATE TABLE "category" (
	name 	VARCHAR(100)	PRIMARY KEY
);


-- wenn directbuy, endtime auf bidtime setzen
-- 2 views: auction und direktauction

CREATE TABLE "auction" (
	id					SERIAL 			PRIMARY KEY,
	start_time			TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	end_time			TIMESTAMP 		NOT NULL, -- automatisch setzen? vllcht immer 7 tage?
	title				VARCHAR(255)	NOT NULL,
	description			TEXT			NOT NULL,
	image 				BYTEA,
	category			VARCHAR(100)	REFERENCES "category"(name) NOT NULL,
	offerer				VARCHAR(100) 	REFERENCES "user"(username) NOT NULL,
	price 				INT 			NOT NULL,
	is_directbuy		BOOLEAN			DEFAULT FALSE NOT NULL
);

-- rating nur setzen wenn auction vorbei und rater == buyer
CREATE TABLE "rating" (
	rater			VARCHAR(100) 	REFERENCES "user"(username),
	auction			INT4			PRIMARY KEY REFERENCES "auction"(id),
	score			INT 			CHECK (score BETWEEN 0 AND 5),
	comment			TEXT
);

-- bid prüfen ob endtime schon abgelaufen
-- bid nur wenn nicht schon höchstbietender
CREATE TABLE "bid" (
	username	VARCHAR(100)	REFERENCES "user"(username),
	auction 	INT4			REFERENCES "auction"(id),
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	price 		INT, -- nur bid erstellen wenn price > maximal price der auction ist
	PRIMARY KEY(username, auction, time)
);

CREATE TABLE "comment" (
	username	VARCHAR(100)	REFERENCES "user"(username),
	auction 	INT4			REFERENCES "auction"(id),
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	content		TEXT 			NOT NULL,
	PRIMARY KEY (username, auction, time) 		
);


-------------------------------------------------------------------------------------
--	VIEWS AND RULES
-------------------------------------------------------------------------------------

CREATE VIEW "user_view" AS
	SELECT u.username, u.first_name, u.last_name, u.email, u.street, u.street_number, u.postal_code, c.city FROM "user" u LEFT JOIN city c ON u.postal_code=c.postal_code WHERE u.deleted=FALSE;

CREATE RULE "user_insert" AS ON INSERT TO "user_view" DO INSTEAD (
       --INSERT INTO  "city" VALUES(NEW.postal_code,NEW.city) WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE id = NEW.postal_code);
       INSERT INTO  "city" SELECT NEW.postal_code, NEW.city WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE postal_code = NEW.postal_code);
       INSERT INTO  "user" VALUES(NEW.username, NEW.first_name, NEW.last_name, NEW.email, NEW.street, NEW.street_number, NEW.postal_code);
);
CREATE RULE "user_update" AS ON UPDATE TO "user_view" DO INSTEAD (
       --INSERT INTO  "city" VALUES(NEW.postal_code,NEW.city) WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE id = NEW.postal_code);
       INSERT INTO  "city" SELECT NEW.postal_code, NEW.city WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE postal_code = NEW.postal_code);
       UPDATE "user" SET username=NEW.username, first_name=NEW.first_name, last_name=NEW.last_name, email=NEW.email, street=NEW.street,  street_number=NEW.street_number, postal_code=NEW.postal_code;
);
CREATE RULE "user_delete" AS ON DELETE TO "user_view" DO INSTEAD (
       --INSERT INTO  "city" VALUES(NEW.postal_code,NEW.city) WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE id = NEW.postal_code);
  	DELETE FROM "bank_account" WHERE username=OLD.username;
  	DELETE FROM "search_term" WHERE username=OLD.username;
  	DELETE FROM "admin" WHERE username=OLD.username;
  	UPDATE "user" SET deleted=TRUE;
);


-- max_bid sollte nicht höchster sein, sondern 2t höchster + 1
CREATE VIEW "auction_view" (title, category, end_time, max_bid) AS
	--SELECT a.title, a.category, a.end_time, bids.bid FROM "auction" a, (SELECT MAX(b.price) AS bid FROM "bid" b WHERE a.id = b.auction) AS bids
	SELECT a.title, 
			a.category, 
			a.end_time, 
			(coalesce((SELECT MAX(price) FROM "bid" b WHERE a.id = b.auction AND price < (SELECT MAX(price) FROM "bid" b WHERE a.id = b.auction)), a.price) + coalesce((SELECT 1 FROM auction c WHERE c.id=a.id AND NOT a.is_directbuy),0)) AS max_bid 
		FROM "auction" a



