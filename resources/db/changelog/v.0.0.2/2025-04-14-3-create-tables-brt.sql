CREATE TABLE users_saved (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    tariff_id INTEGER,
    msisdn VARCHAR(11) NOT NULL UNIQUE,
    balance DECIMAL(10, 2) NOT NULL,
    registration_date TIMESTAMP NOT NULL
);

CREATE TABLE user_params (
    user_id INTEGER NOT NULL REFERENCES users_saved(user_id) ON DELETE CASCADE,
    payment_day DATE,
    minutes INTEGER
);

CREATE TABLE call_records (
    call_id SERIAL PRIMARY KEY,
    call_type VARCHAR(2) NOT NULL,
    caller_msisdn VARCHAR(11) NOT NULL,
    receiver_msisdn VARCHAR(11) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP
);