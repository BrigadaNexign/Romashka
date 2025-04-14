CREATE TABLE "users_saved" (
	"user_id"	INTEGER NOT NULL UNIQUE,
	"user_name"	TEXT NOT NULL,
	"tariff_id"	INTEGER,
	"msisdn"	CHAR(11) NOT NULL UNIQUE,
	"balance"	NUMERIC(10, 2) NOT NULL,
	"registration_date"	DATE NOT NULL,
	PRIMARY KEY("user_id" AUTOINCREMENT),
	FOREIGN KEY("tariff_id") REFERENCES "tariffs"("tariff_id") ON DELETE SET NULL
);

CREATE TABLE "user_params" (
	"user_id"	INTEGER NOT NULL,
	"payment_day"	DATE,
	"minutes"	INTEGER,
	FOREIGN KEY("user_id") REFERENCES "users_saved"("user_id") ON DELETE CASCADE
);
