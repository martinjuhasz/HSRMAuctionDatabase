-------------------------------------------------------------------------------------
--	TRIGGER
-------------------------------------------------------------------------------------

-- automatically sets the start and end date when a auction is created
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

-- end auction if a bid was made to a direct buy auction
CREATE FUNCTION setEndDateToNowOnBid() RETURNS TRIGGER AS
$BODY$
BEGIN
UPDATE auction SET end_time=now() WHERE id=NEW.auction AND is_directbuy=TRUE;
RETURN NEW;
END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER setEndDateToNowOnBidTrigger BEFORE INSERT ON "bid" FOR EACH ROW EXECUTE PROCEDURE setEndDateToNowOnBid();

-- if password is blank on update, keep old password
CREATE FUNCTION keepOldPassword() RETURNS TRIGGER AS
$BODY$
BEGIN
IF char_length(NEW.password) = 0
THEN
	NEW.password = OLD.password;
END IF;
RETURN NEW;
END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER keepOldPasswordTrigger BEFORE UPDATE ON "user" FOR EACH ROW EXECUTE PROCEDURE keepOldPassword();