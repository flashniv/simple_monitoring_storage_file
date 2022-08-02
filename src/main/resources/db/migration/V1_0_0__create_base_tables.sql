CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE alert (
    id bigint PRIMARY KEY NOT NULL,
    alert_timestamp timestamp without time zone,
    operation_data character varying(255),
    trigger_id character varying(255) NOT NULL
);

CREATE TABLE alert_channel (
    id bigint PRIMARY KEY NOT NULL,
    alerter_class character varying(255),
    alerter_parameters character varying(255)
);

CREATE TABLE alert_filter (
    id bigint PRIMARY KEY NOT NULL,
    regexp character varying(255),
    alert_channel_id bigint NOT NULL
);

CREATE TABLE metric (
    path character varying(255) PRIMARY KEY NOT NULL
);

CREATE TABLE parameter_group (
    id bigint PRIMARY KEY NOT NULL,
    json text,
    metric_id character varying(255) NOT NULL
);

CREATE TABLE role (
    id bigint PRIMARY KEY NOT NULL,
    name character varying(255)
);

CREATE TABLE user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);

CREATE TABLE users (
    id bigint PRIMARY KEY NOT NULL,
    account_non_expired boolean NOT NULL,
    account_non_locked boolean NOT NULL,
    credentials_non_expired boolean NOT NULL,
    enabled boolean NOT NULL,
    password character varying(255),
    username character varying(255)
);

CREATE TABLE trigger (
    id character varying(255) PRIMARY KEY NOT NULL,
    conf text NOT NULL,
    description text,
    enabled boolean,
    last_status character varying(255),
    last_status_update timestamp without time zone,
    name character varying(255) NOT NULL,
    priority character varying(255),
    trigger_id character varying(255) NOT NULL
);

ALTER TABLE alert_filter ADD CONSTRAINT fk7u8xdax9prnbybpw7d93dwq7h FOREIGN KEY (alert_channel_id) REFERENCES alert_channel(id);

ALTER TABLE parameter_group ADD CONSTRAINT fk8y5oxt4im8gb2wh00jb24x68i FOREIGN KEY (metric_id) REFERENCES metric(path);

ALTER TABLE user_role ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES role(id);

ALTER TABLE user_role ADD CONSTRAINT fkj345gk1bovqvfame88rcx7yyx FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE alert ADD CONSTRAINT fktkd1v536csk4aheewsoivyiks FOREIGN KEY (trigger_id) REFERENCES trigger(id);

