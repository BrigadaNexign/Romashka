-- Creating table for users_saved
CREATE TABLE users_saved (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    tariff_id BIGINT,
    msisdn VARCHAR(11) NOT NULL UNIQUE,
    balance DECIMAL(10, 2) NOT NULL,
    registration_date TIMESTAMP NOT NULL
);

-- Creating table for user_params
CREATE TABLE user_params (
    user_id BIGINT NOT NULL REFERENCES users_saved(user_id) ON DELETE CASCADE,
    payment_day DATE,
    minutes INTEGER
);

-- Optional: Create indexes for performance
CREATE INDEX idx_users_saved_msisdn ON users_saved(msisdn);
CREATE INDEX idx_user_params_user_id ON user_params(user_id);

CREATE TABLE call_records (
    call_id BIGSERIAL PRIMARY KEY,
    call_type VARCHAR(2) NOT NULL,
    caller_msisdn VARCHAR(11) NOT NULL,
    receiver_msisdn VARCHAR(11) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_msisdn ON users_saved (msisdn);

LOCK TABLE users_saved IN EXCLUSIVE MODE;