CREATE TABLE subscriber (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    msisdn VARCHAR(12) NOT NULL UNIQUE
);

CREATE TABLE fragment (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    call_type VARCHAR(2) NOT NULL CHECK (call_type IN ('01', '02')),
    caller_msisdn VARCHAR(12) NOT NULL,
    receiver_msisdn VARCHAR(12) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_valid_times CHECK (end_time > start_time)
);

CREATE INDEX idx_fragment_caller ON fragment(caller_msisdn);
CREATE INDEX idx_fragment_time_range ON fragment(start_time, end_time);