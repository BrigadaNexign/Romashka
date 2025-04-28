CREATE TABLE calls_generated (
    type VARCHAR(2) NOT NULL,
    msisdn_main VARCHAR(11) NOT NULL,
    msisdn_sec VARCHAR(11) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
);

CREATE TABLE "users" (
    "msisdn" CHAR(11) NOT NULL UNIQUE
);