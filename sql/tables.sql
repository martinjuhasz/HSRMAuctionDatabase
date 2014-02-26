
CREATE TABLE "city" (
	postal_code	VARCHAR(40)		PRIMARY KEY,
	city 		VARCHAR(255)	NOT NULL
);

CREATE TABLE "user" (
	username 		VARCHAR(100) 	PRIMARY KEY,
	first_name		VARCHAR(255)	NOT NULL,
	last_name		VARCHAR(255)	NOT NULL,
	email			VARCHAR(255)	NOT NULL, -- email validation
	street			VARCHAR(255)	NOT NULL,
	street_number	VARCHAR(255)	NOT NULL,
	postal_code		VARCHAR(40)		REFERENCES "city"(postal_code) NOT NULL
);

CREATE TABLE "bank_ccount" (
	bank_number		VARCHAR(20),	-- validation?
	account_number	VARCHAR(20),	-- validation?
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
	id 		SERIAL 			PRIMARY KEY,
	name 	VARCHAR(100)	NOT NULL -- name vielleicht auch unique oder direkt als primary key?
);

CREATE TABLE "auction" (
	id					SERIAL 			PRIMARY KEY,
	start_time			TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL, -- modifizieren verhindern?
	end_time			TIMESTAMP 		NOT NULL, -- automatisch setzen? vllcht immer 7 tage?
	title				VARCHAR(255)	NOT NULL,
	description			TEXT			NOT NULL,
	image 				BYTEA,
	min_price			INT, -- hier muss geprpueft werden ob min price oder direct buy price
	direct_buy_price	INT, -- hier ebenso nur eins von beiden	
	category			INT4			REFERENCES "category"(id) NOT NULL,
	rater				VARCHAR(100) 	REFERENCES "user"(username), -- darf erst nach beenden der auktion gesetzt werden und nur vom buyer
	offerer				VARCHAR(100) 	REFERENCES "user"(username) NOT NULL,
	buyer				VARCHAR(100) 	REFERENCES "user"(username), -- darf erst am ende der auktion gesetzt werden und wenn direct buy

	-- vielleicht sollte man buyer gar nicht benutzen, sondern regeln einführen:
	-- wenn direct buy price dann ist buyer der mit der einzigen bid
	-- dann darf natürlich auch nur eine bid gesetzt werden
	-- und wenn nicht, dann ist buyer der der nach beeenden die höschste bid hat
	buy_time			TIMESTAMP 		NOT NULL, -- ist das für direct buy?
	rating				INT 			CHECK (rating BETWEEN 0 AND 5), -- auch erst nach beeden der auktion nur vom buyer
	rating_text			TEXT -- auch wieder erst nach beeden und nur vom buyer
);

CREATE TABLE "bid" (
	username	VARCHAR(100)	REFERENCES "user"(username),
	auction 	INT4			REFERENCES "auction"(id),
	-- nr?
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	price 		INT 			NOT NULL, -- darf nur höher sein, als der momentan höchste bid
	PRIMARY KEY(username, auction, time)
);

CREATE TABLE "comment" (
	username	VARCHAR(100)	REFERENCES "user"(username),
	auction 	INT4			REFERENCES "auction"(id),
	-- nr? comment geht immer oder? vllcht ne funktion dass nur comment wenn mind 1 bid?
	time 		TIMESTAMP 		DEFAULT CURRENT_TIMESTAMP NOT NULL,
	content		TEXT 			NOT NULL,
	PRIMARY KEY (username, auction, time) 		
);




