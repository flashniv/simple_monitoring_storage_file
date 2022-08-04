ALTER TABLE alert_filter ADD COLUMN allow boolean DEFAULT true;
ALTER TABLE alert_filter ADD COLUMN priority smallint DEFAULT 100;