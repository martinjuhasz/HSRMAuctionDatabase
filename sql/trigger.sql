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
