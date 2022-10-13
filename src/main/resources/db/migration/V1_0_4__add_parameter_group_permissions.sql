CREATE TABLE parameter_group_permission (
    id bigint PRIMARY KEY NOT NULL,
    user_id bigint NOT NULL,
    expression text
);

ALTER TABLE parameter_group_permission ADD CONSTRAINT parameter_group_permission_user FOREIGN KEY (user_id) REFERENCES users(id);
