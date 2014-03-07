DROP RULE "user_insert" ON "user_view";
DROP RULE "user_update" ON "user_view";
DROP RULE "user_delete" ON "user_view";
DROP VIEW "user_view";
DROP VIEW "auction_view";
DROP VIEW "closed_auctions_view";
DROP VIEW "auction_detail_view";
DROP VIEW "auction_comment_view";
DROP TRIGGER setStartEndDateToAuctionTrigger ON "auction";

DROP TABLE "bid";
DROP TABLE "comment";
DROP TABLE "rating";
DROP TABLE "auction";
DROP TABLE "category";
DROP TABLE "search_term";
DROP TABLE "bank_account";
DROP TABLE "admin";
DROP TABLE "user";
DROP TABLE "city";

DROP DOMAIN "EMAIL";
DROP DOMAIN "BLZ";
DROP DOMAIN "KTNR";

DROP FUNCTION max_bid(integer);
DROP FUNCTION max_bidder(integer);
DROP FUNCTION setStartEndDateToAuction();
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

-- name kein empty string
CREATE TABLE "category" (
	id		SERIAL 			PRIMARY KEY,
	name 	VARCHAR(100)	UNIQUE NOT NULL
);


-- wenn directbuy, endtime auf bidtime setzen
-- 2 views: auction und direktauction
-- min price bei bid mind 1
-- title kein empty string
-- description kein empty string
CREATE TABLE "auction" (
	id					SERIAL 			PRIMARY KEY,
	start_time			TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	end_time			TIMESTAMP 		NOT NULL, -- automatisch setzen? vllcht immer 7 tage?
	title				VARCHAR(255)	NOT NULL,
	description			TEXT			NOT NULL,
	image 				BYTEA,
	category			INT4			REFERENCES "category"(id) ON DELETE CASCADE NOT NULL,
	offerer				INT4 			REFERENCES "user"(id) NOT NULL,
	price 				INT 			NOT NULL,
	is_directbuy		BOOLEAN			DEFAULT FALSE NOT NULL
);

-- rating nur setzen wenn auction vorbei und rater == buyer
CREATE TABLE "rating" (
	rater			INT4			REFERENCES "user"(id),
	auction			INT4			PRIMARY KEY REFERENCES "auction"(id) ON DELETE CASCADE,
	score			INT 			CHECK (score BETWEEN 0 AND 5),
	comment			TEXT
);

-- bid prüfen ob endtime schon abgelaufen
-- bid nur wenn nicht schon höchstbietender
-- bid nur höher als mindestgebot und höher als 2t höchstes Gebot plus 1
-- bid nur wenn Datum und Uhrzeit im Zeitraum liegt
CREATE TABLE "bid" (
	uid			INT4			REFERENCES "user"(id),
	auction 	INT4			REFERENCES "auction"(id) ON DELETE CASCADE,
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	price 		INT, -- nur bid erstellen wenn price > maximal price der auction ist
	PRIMARY KEY(uid, auction, time)
);

CREATE TABLE "comment" (
	uid			INT4			REFERENCES "user"(id),
	auction 	INT4			REFERENCES "auction"(id) ON DELETE CASCADE,
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

CREATE FUNCTION max_bid(integer)
RETURNS integer
AS '
	SELECT coalesce((SELECT MAX(price) FROM "bid" b 
					 WHERE a.id = b.auction AND price < (SELECT MAX(price) 
					 FROM "bid" b WHERE a.id = b.auction)), 
					 a.price) + 
		   coalesce((SELECT 1 FROM auction c WHERE c.id=a.id AND NOT a.is_directbuy), 0)
	FROM auction a WHERE a.id=$1;
' LANGUAGE 'SQL';

CREATE FUNCTION max_bidder(integer)
RETURNS text
AS '
	SELECT u.username FROM "user" u, "bid" b1 
	WHERE u.id = b1.uid AND b1.price >= (SELECT MAX(bb.price) 
	FROM "bid" bb WHERE bb.auction = $1) AND b1.auction = $1;
' LANGUAGE 'SQL';

-- max_bid sollte nicht höchster sein, sondern 2t höchster + 1
-- max_bid sollte bei fehlendem Gebot gleich dem startpreis sein und nicht startpreis + 1
CREATE VIEW "auction_view" (title, end_time, max_bid, category) AS
	SELECT	a.title,
			CASE WHEN (a.end_time >= now()::date AND a.end_time < (now()::date + interval '24h')) THEN 'Heute' ELSE to_char(a.end_time, 'DD.MM.YYYY') END AS end_time,
			max_bid(a.id) AS max_bid,
			a.category, a.id
	FROM "auction" a WHERE a.end_time >= now();

CREATE VIEW "auction_detail_view" AS
	SELECT a.id, a.start_time, a.end_time, a.title, a.description, a.image, c.name AS category, u.username AS offerer, a.price, a.is_directbuy,
	max_bid(a.id), max_bidder(a.id)
	FROM "auction" a JOIN "category" c ON a.category=c.id JOIN "user" u ON a.offerer=u.id;

CREATE VIEW "auction_comment_view" AS
	SELECT u.username, c.content, c.time, c.auction FROM "comment" c JOIN "user" u ON u.id=c.uid ORDER BY c.time DESC;


CREATE VIEW "closed_auctions_view" AS
	SELECT	cat.name, 
		(SELECT COUNT(*) FROM auction a WHERE a.category=cat.id AND a.end_time < now()) AS count,
		coalesce((SELECT SUM(prices.price) as maximum FROM (SELECT MAX(d.price) AS price FROM auction c, bid d WHERE c.category=cat.id AND d.auction=c.id GROUP BY c.id) AS prices), 0) AS sum
	FROM category cat;




INSERT INTO "city" VALUES('65195', 'Wiesbaden');
INSERT INTO "city" VALUES('10115', 'Berlin');
INSERT INTO "city" VALUES('20095', 'Hamburg');
INSERT INTO "city" VALUES('80331', 'München');
INSERT INTO "city" VALUES('50667', 'Köln');
INSERT INTO "city" VALUES('60308', 'Frankfurt am Main');
INSERT INTO "city" VALUES('70173', 'Stuttgart');
INSERT INTO "city" VALUES('44135', 'Dortmund');
INSERT INTO "city" VALUES('45127', 'Essen');
INSERT INTO "city" VALUES('55116', 'Mainz');





INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (1, 'batonball', '5f4dcc3b5aa765d61d8327deb882cf99', 'Sade','Ware','Cras.vehicula@mauris.com', 'Schermerhorn Viaduct', '10', '44135');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (2, 'ovarycrimson', '5f4dcc3b5aa765d61d8327deb882cf99', 'Colette','Small','pede.ultrices.a@temporloremeget.edu', 'West Fort Corloran Arch', '5a', '10115');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (3, 'junglewoodbow', '5f4dcc3b5aa765d61d8327deb882cf99', 'Ulric','Brock','euismod.enim@turpisIncondimentum.com', 'East Towns Mews', '22', '20095');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (4, 'oinkquean', '5f4dcc3b5aa765d61d8327deb882cf99', 'Lana','Bradford','eu.ligula@taciti.co.uk', 'Southwest Spittlesea Grove', '78', '50667');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (5, 'kingdommess', '5f4dcc3b5aa765d61d8327deb882cf99', 'Ivy','Patrick','commodo.ipsum.Suspendisse@ullamcorper.edu', 'Northeast Lodge Forest View', '24b', '55116');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (6, 'italianboard', '5f4dcc3b5aa765d61d8327deb882cf99', 'Fritz','Howell','Curabitur.egestas.nunc@tristiqueaceleifend.org', 'West Delancy Trace', '38', '44135');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (7, 'remarkableengineer', '5f4dcc3b5aa765d61d8327deb882cf99', 'Aristotle','Pennington','eu@Cum.com', 'Northwest Bushnell Manor', '7', '10115');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (8, 'facehandball',  '5f4dcc3b5aa765d61d8327deb882cf99','Isabella','Fletcher','aliquet@at.ca', 'Northwest Tolas Canyon', '5c', '80331');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (9, 'feedpox', '5f4dcc3b5aa765d61d8327deb882cf99', 'Alexis','Randolph','consectetuer.cursus@Nuncmauris.co.uk', 'Wasdale Head', '22', '20095');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (10, 'siegecooking', '5f4dcc3b5aa765d61d8327deb882cf99', 'Lesley','Little','mauris.Suspendisse.aliquet@Duiscursusdiam.org', 'Coveney Boulevard North', '54', '55116');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (11, 'maxwelltense', '5f4dcc3b5aa765d61d8327deb882cf99', 'Yuri','Russell','ac.feugiat@neque.com', 'West Lower Vickers Park', '76', '10115');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (12, 'gingerclocks', '5f4dcc3b5aa765d61d8327deb882cf99', 'Macey','Davenport','ligula@vitae.ca', 'North Delando Gate', '101', '45127');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (13, 'sneerignorant', '5f4dcc3b5aa765d61d8327deb882cf99', 'Basia','Shelton','Nulla.tempor.augue@nequepellentesque.ca', 'West Temple Hill Nook', '2a', '60308');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (14, 'bumpypacked', '5f4dcc3b5aa765d61d8327deb882cf99', 'Quin','Brewer','ipsum.dolor@magna.net', 'South Bartle Spur', '41', '70173');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (15, 'cornsnow', '5f4dcc3b5aa765d61d8327deb882cf99', 'Joel','Gordon','facilisis.facilisis.magna@Maecenasmalesuada.org', 'Northwest Rondeau Route', '17', '65195');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (16, 'slappergum', '5f4dcc3b5aa765d61d8327deb882cf99', 'Lucy','Walters','libero.Proin@ut.net', 'Mathy Passage West', '20', '50667');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (17, 'plasticbulb', '5f4dcc3b5aa765d61d8327deb882cf99', 'Kaye','Schwartz','ut@interdumlibero.net', 'South Potteries Quadrant', '31', '70173');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (18, 'framenibbles', '5f4dcc3b5aa765d61d8327deb882cf99', 'Martin','Marquez','Morbi.sit@eratEtiam.com', 'East Portola Heights Trace', '99', '20095');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (19, 'lotssatisfied', '5f4dcc3b5aa765d61d8327deb882cf99', 'Kelsie','Vincent','Phasellus.vitae.mauris@ornare.edu', 'Shadyview Gate Northeast', '56', '65195');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (20, 'bananaplum', '5f4dcc3b5aa765d61d8327deb882cf99', 'Kelly','Ferrell','at@fermentumfermentumarcu.com', 'Pewterspear Trace', '11b', '10115');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (21, 'blistersharp', '5f4dcc3b5aa765d61d8327deb882cf99', 'Devin','Gay','dapibus.id@Nullamvitae.ca', 'East Greten Viaduct', '9', '70173');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (22, 'bottledparrott', '5f4dcc3b5aa765d61d8327deb882cf99', 'Ora','Myers','Nullam.feugiat.placerat@dictumeu.org', 'North Pegamoid Court', '87', '45127');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (23, 'arrayridge', '5f4dcc3b5aa765d61d8327deb882cf99', 'Amery','Grimes','placerat.augue.Sed@faucibusidlibero.co.uk', 'Hoga Pike', '62', '80331');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (24, 'dadadvisor', '5f4dcc3b5aa765d61d8327deb882cf99', 'Brittany','Dyer','iaculis.odio.Nam@nonummy.com', 'North Montevina View', '13', '60308');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (25, 'choise', '5f4dcc3b5aa765d61d8327deb882cf99', 'Martin','Juhasz','info@martinjuhasz.de', 'Aarstraße', '56', '65195');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (26, 'Cookie', '5f4dcc3b5aa765d61d8327deb882cf99', 'Julia','Kraft','julia.kraft88@gmail.com', 'Aarstraße', '56', '65195');
INSERT INTO "user"(id, username, password, first_name, last_name,email,street,street_number,postal_code) VALUES (27, 'Eldorado', '5f4dcc3b5aa765d61d8327deb882cf99', 'Simon','Seyer','simon.seyer@gmail.com', 'Platter Straße', '57', '65195');
SELECT SETVAL((SELECT pg_get_serial_sequence('user', 'id')), 28, false);



INSERT INTO "bank_account" VALUES('20690500', '2475224', 'Sade Ware',1);
INSERT INTO "bank_account" VALUES('38000000', '67856278', 'Colette Small', 2);
INSERT INTO "bank_account" VALUES('38010111', '67864', 'Ulric Brock', 3);
INSERT INTO "bank_account" VALUES('59393000', '7613594', 'Lana Bradford', 4);
INSERT INTO "bank_account" VALUES('71160000', '1275934', 'Ivy Patrick', 5);
INSERT INTO "bank_account" VALUES('50400000', '10249305', 'Fritz Howell', 6);
INSERT INTO "bank_account" VALUES('80500000', '18460', 'Aristotle Pennington', 7);
INSERT INTO "bank_account" VALUES('71191000', '115499', 'Isabella Fletcher', 8);
INSERT INTO "bank_account" VALUES('42870077', '100458', 'Alexis Randolph', 9);
INSERT INTO "bank_account" VALUES('87070024', '70004829', 'Lesley Little', 10);
INSERT INTO "bank_account" VALUES('65110200', '5124398', 'Yuri Russell', 11);
INSERT INTO "bank_account" VALUES('80550200', '114576', 'Macey Davenport', 12);
INSERT INTO "bank_account" VALUES('50400000', '7779350', 'Basia Shelton', 13);
INSERT INTO "bank_account" VALUES('59320087', '4141563', 'Quin Brewer', 14);
INSERT INTO "bank_account" VALUES('87070024', '647258', 'Joel Gordon', 15);
INSERT INTO "bank_account" VALUES('42861608', '10057', 'Lucy Walters', 16);
INSERT INTO "bank_account" VALUES('60200000', '5666150', 'Kaye Schwartz', 17);
INSERT INTO "bank_account" VALUES('84094814', '4777592', 'Martin Marquez', 18);
INSERT INTO "bank_account" VALUES('54691200', '215364', 'Kelsie Vincent', 19);
INSERT INTO "bank_account" VALUES('65190110', '854471', 'Kelly Ferrell', 20);
INSERT INTO "bank_account" VALUES('38051290', '366540', 'Devin Gay', 21);
INSERT INTO "bank_account" VALUES('60270073', '335258', 'Ora Myers', 22);
INSERT INTO "bank_account" VALUES('84080000', '5578124', 'Amery Grimes', 23);
INSERT INTO "bank_account" VALUES('54661800', '4457269', 'Brittany Dyer', 24);
INSERT INTO "bank_account" VALUES('71162355', '110245', 'Kelly Ferrell', 25);
INSERT INTO "bank_account" VALUES('60270073', '4472405', 'Isabella Fletcher', 25);


INSERT INTO "admin" VALUES(25);
INSERT INTO "admin" VALUES(26);
INSERT INTO "admin" VALUES(27);

INSERT INTO "category" VALUES(1, 'Bücher');
INSERT INTO "category" VALUES(2, 'Spielzeug');
INSERT INTO "category" VALUES(3, 'Haushalt');
INSERT INTO "category" VALUES(4, 'Damenbekleidung');
INSERT INTO "category" VALUES(5, 'Herrenbekleidung');
INSERT INTO "category" VALUES(6, 'Möbel&Wohnen');
INSERT INTO "category" VALUES(7, 'Rund ums Baby');
INSERT INTO "category" VALUES(8, 'Computer&Zubehör');
INSERT INTO "category" VALUES(9, 'Filme&DVDs');
INSERT INTO "category" VALUES(10, 'Haustierbedarf');
INSERT INTO "category" VALUES(11, 'PC- & Videospiele');
SELECT SETVAL((SELECT pg_get_serial_sequence('category', 'id')), 12, false);

INSERT INTO "auction" VALUES(1, '2013-12-01 12:13:00.00', '2013-12-08 12:13:00.00', 'Lego Eisenbahn', 'neuwertige Legoeisenbahn', null, 2, 1, 50, false);
INSERT INTO "auction" VALUES(3, '2013-12-01 12:13:00.00', '2013-12-08 12:13:00.00', 'Lego Lokomotive', 'tolle Legolokomotive mit Lokführer', null, 2, 1, 60, false);
INSERT INTO "auction" VALUES(2, '2013-12-01 12:13:00.00', '2013-12-08 12:13:00.00', 'Lego Auto', 'neuwertiges Legoauto', null, 2, 3, 30, true);

INSERT INTO "auction" VALUES(4, '2014-03-05 19:22:00.00', '2014-03-12 19:22:00.00', 'Devil Staubsauger', 'roter Devilstaubsauger mit verschiedenen Aufsätzen', null, 3, 19, 1, false);
INSERT INTO "auction" VALUES(5, '2014-03-05 14:50:00.00', '2014-03-12 14:50:00.00', 'Bosch Mixer Neu OVP', 'Mixer ist unbenutzt und in original Verpackung', null, 3, 15, 30, true);
INSERT INTO "auction" VALUES(6, '2014-03-04 20:13:00.00', '2014-03-11 20:13:00.00', 'Vorwerk Thermo Mix', 'gut erhaltener Thermomix', null, 3, 5, 500, false);

INSERT INTO "auction" VALUES(7, '2014-03-04 17:33:00.00', '2014-03-11 17:33:00.00', 'gebrauchte Gucci Pumps', 'gebrauchte Gucci Pumps in lila Größe 39', null, 4, 6, 220, true);
INSERT INTO "auction" VALUES(8, '2014-03-03 10:15:00.00', '2014-03-10 10:15:00.00', 'H&M Jeans Größe 36', 'H&M Jeans Größe 36 in blau, guter Zustand', null, 4, 10, 1, false);
INSERT INTO "auction" VALUES(9, '2013-11-01 12:13:00.00', '2013-11-08 12:13:00.00', 'Süßes Spaghettiträger-Top', 'Süßes Spaghettiträger-Top Größe 34', null, 4, 22, 1, false);

INSERT INTO "auction" VALUES(10, '2014-03-03 15:13:00.00', '2014-03-10 15:13:00.00', 'Big Bang Theory Shirt', 'Big Bang Theory Shirt XXL', null, 5, 17, 1, false);
INSERT INTO "auction" VALUES(11, '2014-03-02 22:13:00.00', '2014-03-09 22:13:00.00', 'Jogging Hose von Esprit', 'Jogging Hose von Esprit NEU', null, 5, 24, 50, true);
INSERT INTO "auction" VALUES(12, '2014-02-22 11:55:00.00', '2014-03-01 11:55:00.00', 'Gürtel von Armani', 'Gürtel von Armani mit Silberschnalle', null, 5, 12, 1, false);

INSERT INTO "auction" VALUES(13, '2014-02-23 16:55:00.00', '2014-03-02 16:55:00.00', 'Stehlampe', 'Stehlampe aus Metall', null, 6, 1, 1, false);
INSERT INTO "auction" VALUES(14, '2014-02-24 12:55:00.00', '2014-03-03 12:55:00.00', 'Schreibtisch aus Nussbaumholz', 'Schreibtisch aus Nussbaumholz', null, 6, 6, 200, true);
INSERT INTO "auction" VALUES(15, '2014-03-05 11:55:00.00', '2014-03-12 12:13:00.00', 'Schlafcouch', 'schwarze Schlafcouch aus Leder', null, 6, 8, 500, true);

INSERT INTO "auction" VALUES(16, '2014-03-04 15:13:00.00', '2014-03-11 15:13:00.00', 'Riesen Windel Packet', 'Riesen Windel Packet 500 Stück', null, 7, 18, 150, true);
INSERT INTO "auction" VALUES(17, '2014-03-05 15:13:00.00', '2014-03-12 15:13:00.00', 'Fahrradsitz', 'Fahrradsitz grün', null, 7, 15, 20, false);
INSERT INTO "auction" VALUES(18, '2014-03-06 15:13:00.00', '2014-03-13 15:13:00.00', 'Milchpumpe', 'Milchpumpe unbenutzt', null, 7, 17, 1, false);

INSERT INTO "auction" VALUES(19, '2014-03-07 10:13:00.00', '2014-03-14 10:13:00.00', 'Asus Laptop 17"', 'Asus Laptop 17" Neu', null, 8, 14, 1000, true);
INSERT INTO "auction" VALUES(20, '2014-03-07 10:13:00.00', '2014-03-14 10:13:00.00', 'Tastatur mit Maus', 'Tastatur mit Maus kabellos', null, 8, 17, 1, false);
INSERT INTO "auction" VALUES(21, '2014-02-05 15:13:00.00', '2014-02-12 15:13:00.00', '500Gb Festplatte', '500Gb Festplatte', null, 8, 9, 1, false);

INSERT INTO "auction" VALUES(22, '2014-03-07 10:13:00.00', '2014-03-14 10:13:00.00', 'Pulp Fiction DVD', 'Pulp Fiction DVD ohne Kratzer', null, 9, 8, 1, false);
INSERT INTO "auction" VALUES(23, '2014-03-04 10:13:00.00', '2014-03-11 10:13:00.00', 'König der Löwen Blue Ray', 'König der Löwen Blue Ray', null, 9, 4, 1, false);
INSERT INTO "auction" VALUES(24, '2014-02-07 10:13:00.00', '2014-02-14 10:13:00.00', 'Ice Age 3 in 3D', 'Ice Age 3 in 3D', null, 9, 19, 15, true);

INSERT INTO "auction" VALUES(25, '2014-03-06 10:13:00.00', '2014-03-13 10:13:00.00', 'Hamsterkäfig', 'Hamsterkäfig mit 3 Ebenen', null, 10, 3, 1, false);
INSERT INTO "auction" VALUES(26, '2014-03-01 12:13:00.00', '2014-03-08 12:13:00.00', 'Hundekörbchen in rot', 'Hundekörbchen in rot super kuschelig', null, 10, 7, 40, true);
INSERT INTO "auction" VALUES(27, '2014-02-04 10:13:00.00', '2014-02-11 10:13:00.00', 'Aquarium 60l mit Pumpe', 'Aquarium 60l mit Pumpe', null, 10, 13, 10, false);

INSERT INTO "auction" VALUES(28, '2014-03-05 14:13:00.00', '2014-03-12 14:13:00.00', 'Battlefield 4 Premium', 'Battlefield 4 Premium für PC', null, 11, 25, 35, true);
INSERT INTO "auction" VALUES(29, '2014-03-01 10:55:00.00', '2014-03-08 10:55:00.00', 'Die Sims 3', 'Die Sims 3 Playstation 3', null, 11, 16, 1, false);
INSERT INTO "auction" VALUES(30, '2014-02-06 10:13:00.00', '2014-02-13 10:13:00.00', 'Donkey Kong Country', 'Donkey Kong Country für die Super Nintendo', null, 11, 19, 1, false);
SELECT SETVAL((SELECT pg_get_serial_sequence('auction', 'id')), 31, false);

INSERT INTO "bid" VALUES(7, 1, '2013-12-03 12:13:00.00', 55);
INSERT INTO "bid" VALUES(7, 3, '2013-12-03 12:13:00.00', 65);
INSERT INTO "bid" VALUES(8, 3, '2013-12-03 12:15:00.00', 66);

INSERT INTO "bid" VALUES(4, 9, '2013-11-08 10:11:00.00', 2);
INSERT INTO "bid" VALUES(10, 9, '2013-12-03 12:12:00.00', 5);
INSERT INTO "bid" VALUES(12, 9, '2013-12-03 12:13:00.00', 7);

INSERT INTO "bid" VALUES(13, 12, '2013-03-01 09:15:00.00', 32);
INSERT INTO "bid" VALUES(21, 12, '2013-03-01 11:15:00.00', 88);

INSERT INTO "bid" VALUES(15, 14, '2014-03-01 14:18:00.00', 200);

INSERT INTO "bid" VALUES(9, 24, '2014-02-12 19:55:00.00', 15);

INSERT INTO "bid" VALUES(9, 27, '2014-02-05 19:55:00.00', 15);
INSERT INTO "bid" VALUES(14, 27, '2014-02-06 20:09:00.00', 21);
INSERT INTO "bid" VALUES(9, 27, '2014-02-06 21:55:00.00', 25);
INSERT INTO "bid" VALUES(26, 27, '2014-02-10 13:42:00.00', 27);


INSERT INTO "comment" VALUES(10, 1, '2013-12-02 17:55:00', 'Aus wievielen Teilen bestehlt die Legoeisenbahn?');

INSERT INTO "comment" VALUES(11, 6, '2014-03-04 11:32:00', 'Wie heiß wird das Gerät?');
INSERT INTO "comment" VALUES(5, 6, '2014-03-05 14:15:00', 'puh also ich verbrenne mir jedes mal die Finger dran, deswegen verkaufe ich es');
INSERT INTO "comment" VALUES(13, 6, '2014-03-06 12:58:00', 'Und was kann ich darin alles machen? Auch Lebereis?');

INSERT INTO "comment" VALUES(2, 10, '2014-03-04 10:04:00', 'Ich habe normal XXXL, meinen Sie ich sehe dick in dem TShirt aus?');

INSERT INTO "comment" VALUES(1, 16, '2014-03-04 14:04:00', 'Können diese Windeln auch von Erwachsenen getragen werden?');

INSERT INTO "comment" VALUES(5, 25, '2014-03-06 20:32:00', 'Wie groß ist der Käfig denn?');
INSERT INTO "comment" VALUES(3, 25, '2014-03-06 21:59:00', '60x100, also viel Platz für einen Hamster');
INSERT INTO "comment" VALUES(12, 25,'2014-03-07 12:55:00', 'ist außer den 3 Ebenen noch mehr Zubehör dabei?');

INSERT INTO "comment" VALUES(17, 28,'2014-03-06 13:44:00', 'In welchem Zustand ist der Artikel?');
INSERT INTO "comment" VALUES(25, 28,'2014-03-07 12:12:00', 'Der Artikel ist in einem neuwertigen Zustand');
-------------------------------------------------------------------------------------
--	TRIGGER
-------------------------------------------------------------------------------------

CREATE FUNCTION setStartEndDateToAuction() RETURNS TRIGGER AS
$BODY$
DECLARE auctionend TIMESTAMP;
BEGIN
SELECT (now() + interval '7d') INTO auctionend;
NEW.start_time := now();
NEW.end_time := auctionend;
RETURN NEW;
END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER setStartEndDateToAuctionTrigger BEFORE INSERT ON "auction" FOR EACH ROW EXECUTE PROCEDURE setStartEndDateToAuction();
