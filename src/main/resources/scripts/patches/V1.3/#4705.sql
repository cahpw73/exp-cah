ALTER TABLE SCOPE_SUPPLY ADD COLUMN FROM_SS VARCHAR(100);
ALTER TABLE SCOPE_SUPPLY ADD COLUMN TO_SS VARCHAR(100);
ALTER TABLE SCOPE_SUPPLY ADD COLUMN DATE_SS TIMESTAMP;
ALTER TABLE SCOPE_SUPPLY ADD COLUMN DESCRIPTION_ATTACHMENT VARCHAR(1000);
ALTER TABLE COMMENTS ADD COLUMN TO_CMT VARCHAR(100);
