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
	id				SERIAL 			PRIMARY KEY,
	username 		VARCHAR(100) 	UNIQUE NOT NULL,
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
	uid				INT4			REFERENCES "user"(id) NOT NULL,
	PRIMARY KEY (bank_number, account_number)
);

CREATE TABLE "admin" (
	uid 	INT4 PRIMARY KEY REFERENCES "user"(id)
);

CREATE TABLE "search_term" (
	uid			INT4 			PRIMARY KEY REFERENCES "user"(id),
	term 		VARCHAR(255)	NOT NULL
);

CREATE TABLE "category" (
	id		SERIAL 			PRIMARY KEY,
	name 	VARCHAR(100)	UNIQUE NOT NULL
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
	category			INT4			REFERENCES "category"(id) NOT NULL,
	offerer				INT4 			REFERENCES "user"(id) NOT NULL,
	price 				INT 			NOT NULL,
	is_directbuy		BOOLEAN			DEFAULT FALSE NOT NULL
);

-- rating nur setzen wenn auction vorbei und rater == buyer
CREATE TABLE "rating" (
	rater			INT4			REFERENCES "user"(id),
	auction			INT4			PRIMARY KEY REFERENCES "auction"(id),
	score			INT 			CHECK (score BETWEEN 0 AND 5),
	comment			TEXT
);

-- bid prüfen ob endtime schon abgelaufen
-- bid nur wenn nicht schon höchstbietender
CREATE TABLE "bid" (
	uid			INT4			REFERENCES "user"(id),
	auction 	INT4			REFERENCES "auction"(id),
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	price 		INT, -- nur bid erstellen wenn price > maximal price der auction ist
	PRIMARY KEY(uid, auction, time)
);

CREATE TABLE "comment" (
	uid			INT4			REFERENCES "user"(id),
	auction 	INT4			REFERENCES "auction"(id),
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	content		TEXT 			NOT NULL,
	PRIMARY KEY (uid, auction, time) 		
);


-------------------------------------------------------------------------------------
--	VIEWS AND RULES
-------------------------------------------------------------------------------------

CREATE VIEW "user_view" AS
	SELECT u.username, u.first_name, u.last_name, u.email, u.street, u.street_number, u.postal_code, c.city, u.password, u.id FROM "user" u LEFT JOIN city c ON u.postal_code=c.postal_code WHERE u.deleted=FALSE;

CREATE RULE "user_insert" AS ON INSERT TO "user_view" DO INSTEAD (
       INSERT INTO  "city"(postal_code, city) SELECT NEW.postal_code, NEW.city WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE postal_code = NEW.postal_code);
       INSERT INTO  "user"(username, password, first_name, last_name,email,street,street_number,postal_code) VALUES(NEW.username, NEW.password, NEW.first_name, NEW.last_name, NEW.email, NEW.street, NEW.street_number, NEW.postal_code);
);
CREATE RULE "user_update" AS ON UPDATE TO "user_view" DO INSTEAD (
       INSERT INTO  "city"(postal_code, city) SELECT NEW.postal_code, NEW.city WHERE NOT EXISTS ( SELECT postal_code FROM "city" WHERE postal_code = NEW.postal_code);
       UPDATE "user" SET username=NEW.username, first_name=NEW.first_name, last_name=NEW.last_name, email=NEW.email, street=NEW.street,  street_number=NEW.street_number, postal_code=NEW.postal_code, password=NEW.password WHERE id=NEW.id;
);
CREATE RULE "user_delete" AS ON DELETE TO "user_view" DO INSTEAD (
  	DELETE FROM "bank_account" WHERE uid=OLD.id;
  	DELETE FROM "search_term" WHERE uid=OLD.id;
  	DELETE FROM "admin" WHERE uid=OLD.id;
  	UPDATE "user" SET deleted=TRUE WHERE id=OLD.id;
);

-- max_bid sollte nicht höchster sein, sondern 2t höchster + 1
CREATE VIEW "auction_view" (title, end_time, max_bid, category) AS
	SELECT	a.title,
			CASE WHEN (a.end_time >= now()::date AND a.end_time < (now()::date + interval '24h')) THEN 'Heute' ELSE to_char(a.end_time, 'DD.MM.YYYY') END AS end_time,
			(coalesce((SELECT MAX(price) FROM "bid" b WHERE a.id = b.auction AND price < (SELECT MAX(price) FROM "bid" b WHERE a.id = b.auction)), a.price) + coalesce((SELECT 1 FROM auction c WHERE c.id=a.id AND NOT a.is_directbuy),0)) AS max_bid,
			a.category
	FROM "auction" a;


CREATE VIEW "closed_auctions_view" AS
	SELECT	cat.name, 
		(SELECT COUNT(*) FROM auction a WHERE a.category=cat.id AND a.end_time < now()) AS count,
		coalesce((SELECT SUM(prices.price) as maximum FROM (SELECT MAX(d.price) AS price FROM auction c, bid d WHERE c.category=cat.id AND d.auction=c.id GROUP BY c.id) AS prices), 0) AS sum
	FROM category cat;



