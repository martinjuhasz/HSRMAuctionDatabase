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

CREATE FUNCTION setEndDateToNowOnBid() RETURNS TRIGGER AS
$BODY$
BEGIN
UPDATE auction SET end_time=now() WHERE id=NEW.auction AND is_directbuy=TRUE;
RETURN NEW;
END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER setEndDateToNowOnBidTrigger BEFORE INSERT ON "bid" FOR EACH ROW EXECUTE PROCEDURE setEndDateToNowOnBid();