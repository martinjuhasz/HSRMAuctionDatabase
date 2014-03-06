DROP RULE "user_insert" ON "user_view";
DROP RULE "user_update" ON "user_view";
DROP RULE "user_delete" ON "user_view";
DROP VIEW "user_view";
DROP VIEW "auction_view";
DROP VIEW "closed_auctions_view";

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