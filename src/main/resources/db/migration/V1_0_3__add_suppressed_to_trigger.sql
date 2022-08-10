ALTER TABLE trigger ADD COLUMN suppressed boolean DEFAULT false;
ALTER TABLE trigger ADD COLUMN suppressedUpdate timestamp without time zone;
