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

-- Supress rating, when the auction is not completed or a another user than the buyer tries to rate
CREATE FUNCTION supressRatingBeforeEnd() RETURNS TRIGGER AS
$BODY$
DECLARE openAuction boolean;
DECLARE buyer integer;
BEGIN
SELECT coalesce((SELECT TRUE FROM "auction" a WHERE a.id=NEW.auction AND a.end_time > now()), FALSE) INTO openAuction;
IF openAuction THEN
	RETURN NULL;
END IF;

SELECT max_bidder(NEW.auction) INTO buyer;
IF buyer != NEW.rater THEN
	RETURN NULL;
END IF;

RETURN NEW;
END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER supressRatingBeforeEndTrigger BEFORE INSERT ON "rating" FOR EACH ROW EXECUTE PROCEDURE supressRatingBeforeEnd();

-- supress bidding if auction is finished
-- supress bidding if bid is lower than the max bid
-- supress bidding on own auction
-- update the bid if a user bids for an auctions where he's currently the highest bidder
-- end auction if a bid was made to a direct buy auction
CREATE FUNCTION supressBiddingAfterEnd() RETURNS TRIGGER AS
$BODY$
DECLARE openAuction boolean;
DECLARE maxBid integer;
DECLARE maxBidder integer;
DECLARE maxBidTime TIMESTAMP;
DECLARE auctionOfferer integer;
BEGIN
SELECT coalesce((SELECT TRUE FROM "auction" a WHERE a.id=NEW.auction AND a.end_time > now()), FALSE) INTO openAuction;
IF NOT openAuction THEN
	RETURN NULL;
END IF;

SELECT max_bid(NEW.auction) INTO maxBid;
IF NEW.price < maxBid THEN
	RAISE EXCEPTION 'Das Gebot muss größer sein, als das aktuelle Höchstgebot.';
	RETURN NULL;
END IF;

SELECT max_bidder(NEW.auction) INTO maxBidder;
IF maxBidder = NEW.uid THEN
	SELECT time FROM "bid" WHERE "bid".uid = NEW.uid AND "bid".auction =  NEW.auction AND "bid".price >= maxBid INTO maxBidTime;
	UPDATE "bid" SET price=NEW.price WHERE "bid".uid = NEW.uid AND "bid".auction = NEW.auction AND "bid".time = maxBidTime;
	RETURN NULL;
END IF;

SELECT offerer FROM "auction" a WHERE id=NEW.auction INTO auctionOfferer;
IF NEW.uid = auctionOfferer THEN
	RAISE EXCEPTION 'Sie können nicht auf ihre eigene Auktion bieten';
	RETURN NULL;
END IF;

UPDATE auction SET end_time=now() WHERE id=NEW.auction AND is_directbuy=TRUE;
RETURN NEW;
END;
$BODY$ LANGUAGE 'plpgsql';

CREATE TRIGGER supressBiddingAfterEndTrigger BEFORE INSERT ON "bid" FOR EACH ROW EXECUTE PROCEDURE supressBiddingAfterEnd();