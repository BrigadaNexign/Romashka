CREATE TABLE "calls_generated" (
	"type"	CHAR(2) NOT NULL,
	"msisdn_main"	CHAR(11) NOT NULL,
	"msisdn_sec"	CHAR(11) NOT NULL,
	"start"	DATETIME NOT NULL,
	"end"	DATETIME NOT NULL
);

CREATE TABLE "users" (
	"msisdn"	CHAR(11) NOT NULL UNIQUE
);
