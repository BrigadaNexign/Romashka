CREATE TABLE "tariffs" (
    "tariff_id" BIGINT NOT NULL UNIQUE,
    "tariff_name" VARCHAR(100) NOT NULL UNIQUE,
    "tariff_desc" TEXT,
    PRIMARY KEY("tariff_id")
);

CREATE TABLE "call_pricing" (
    "tariff_id" BIGINT NOT NULL,
    "call_type" BIGINT,
    "cost_per_min" NUMERIC(10, 2),
    FOREIGN KEY("tariff_id") REFERENCES "tariffs"("tariff_id") ON DELETE CASCADE
);

CREATE TABLE "params" (
    "param_id" BIGINT NOT NULL UNIQUE,
    "param_name" VARCHAR(100) NOT NULL UNIQUE,
    "param_desc" TEXT,
    "units" VARCHAR(50),
    PRIMARY KEY("param_id")
);

CREATE TABLE "tariff_intervals" (
    "tariff_id" BIGINT NOT NULL,
    "interval" BIGINT NOT NULL,
    "price" NUMERIC(10,2) NOT NULL,
    FOREIGN KEY("tariff_id") REFERENCES "tariffs"("tariff_id") ON DELETE CASCADE
);

CREATE TABLE "tariff_params" (
    "tariff_id" BIGINT NOT NULL,
    "param_id" BIGINT NOT NULL,
    "param_value" NUMERIC NOT NULL,
    FOREIGN KEY("param_id") REFERENCES "params"("param_id") ON DELETE CASCADE,
    FOREIGN KEY("tariff_id") REFERENCES "tariffs"("tariff_id") ON DELETE CASCADE
);