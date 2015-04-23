DROP TABLE IF EXISTS ATTACHMENT_COMMENT CASCADE;
DROP TABLE IF EXISTS ATTACHMENT_SCOPE_SUPPLY CASCADE;
DROP TABLE IF EXISTS ROLE CASCADE;
DROP TABLE IF EXISTS ROLE_OPTION CASCADE;
DROP TABLE IF EXISTS USER_ROLE CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS STATUS CASCADE;
DROP TABLE IF EXISTS SUPPLIER CASCADE;
DROP TABLE IF EXISTS ATTACHMENT CASCADE;
DROP TABLE IF EXISTS COMMENTS CASCADE;
DROP TABLE IF EXISTS TRANSIT_DELIVERY_POINT CASCADE;
DROP TABLE IF EXISTS SCOPE_SUPPLY CASCADE;
DROP TABLE IF EXISTS PURCHASE_ORDER CASCADE;
DROP TABLE IF EXISTS VERIFICATION_TOKEN CASCADE;
DROP TABLE IF EXISTS MODULE_ENTITY CASCADE;
DROP TABLE IF EXISTS OPTIONS CASCADE;

DROP SEQUENCE IF EXISTS ATTACHMENT_COMMENT_SEQ CASCADE;
DROP SEQUENCE IF EXISTS ATTACHMENT_SCOPE_SUPPLY_SEQ CASCADE;
DROP SEQUENCE IF EXISTS COMMENT_ID_SEQ CASCADE;
DROP SEQUENCE IF EXISTS PURCHARSE_ORDER_ID_SEQ CASCADE;
DROP SEQUENCE IF EXISTS SCOPE_SUPPLY_ID_SEQ CASCADE;
DROP SEQUENCE IF EXISTS SUPPLIER_ID_SEQ CASCADE;
DROP SEQUENCE IF EXISTS USERS_ID_SEQ;
DROP SEQUENCE IF EXISTS VERIFICATION_TOKEN_ID_SEQ;
DROP SEQUENCE IF EXISTS TRANSIT_DELIVERY_POINT_SEQ;

CREATE SEQUENCE ATTACHMENT_COMMENT_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE ATTACHMENT_SCOPE_SUPPLY_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE COMMENT_ID_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE PURCHARSE_ORDER_ID_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE SCOPE_SUPPLY_ID_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE TRANSIT_DELIVERY_POINT_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE SUPPLIER_ID_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE USERS_ID_SEQ INCREMENT 1 START 100;
CREATE SEQUENCE VERIFICATION_TOKEN_ID_SEQ INCREMENT 1 START 100;


CREATE TABLE ATTACHMENT_COMMENT(
  	ID BIGINT DEFAULT NEXTVAL(('ATTACHMENT_COMMENT_SEQ'::TEXT)::REGCLASS) NOT NULL,
  	FILE_NAME VARCHAR(255)NOT NULL,
  	MIME_TYPE VARCHAR(255)NOT NULL,
  	PATH      VARCHAR(255),
  	COMMENT_ID BIGINT NOT NULL,
  	FILE_ATTACHED BYTEA,
  	STATUS_ID INTEGER NOT NULL,
  	LAST_UPDATE TIMESTAMP NOT NULL
);

CREATE TABLE ATTACHMENT_SCOPE_SUPPLY(
  	ID BIGINT DEFAULT NEXTVAL(('ATTACHMENT_SCOPE_SUPPLY_SEQ'::TEXT)::REGCLASS) NOT NULL,
  	FILE_NAME VARCHAR(255)NOT NULL,
  	MIME_TYPE VARCHAR(255)NOT NULL,
  	PATH      VARCHAR(255),
  	SCOPE_SUPPLY_ID BIGINT NOT NULL,
  	FILE_ATTACHED BYTEA,
  	STATUS_ID INTEGER NOT NULL,
  	LAST_UPDATE TIMESTAMP NOT NULL
);

CREATE TABLE COMMENTS(
	ID BIGINT NOT NULL,
	NAME VARCHAR(50)NOT NULL,
	REASON VARCHAR (50)NOT NULL,
	DESCRIPTION VARCHAR(1000)NOT NULL,
	F_NAME VARCHAR(255),
	MIME_TYPE VARCHAR(255),
	FILE_ATTACHED BYTEA,
	PURCHASE_ORDER_ID BIGINT NOT NULL,
	LAST_UPDATE TIMESTAMP NOT NULL,
	STATUS_ID INTEGER NOT NULL
);

CREATE TABLE MODULE_ENTITY (
	ID integer NOT NULL,
	NAME varchar(50) NOT NULL
);

CREATE TABLE OPTIONS (
	ID integer NOT NULL,
	NAME varchar(50) NOT NULL,
	MODULE_ID integer NOT NULL
);

CREATE TABLE  PURCHASE_ORDER(
	ID BIGINT NOT NULL,
	PROJECT VARCHAR(50)NOT NULL,
	PO VARCHAR(50)NOT NULL,
	VARIATION VARCHAR(50),
	PROJECT_NAME_COMMENT VARCHAR(1000),
	PO_TITLE VARCHAR(50),
	PO_INCO_TERM VARCHAR(50),
	FULL_INCO_TERM VARCHAR(1000),
	PO_DELIVERY_DATE TIMESTAMP,
	DELIVERY_DATE_COMMENT VARCHAR(1000),
	RESPONSIBLE_EXPEDITING VARCHAR (100),
	INTRO_EMAIL_SENT TIMESTAMP,
	INTRO_EMAIL_SENT_COMMENT VARCHAR(1000),
	REQUIRED_DATE TIMESTAMP,
	REQUIRED_SITE_DATE_COMMENT VARCHAR(1000),
	ACTUAL_DATE TIMESTAMP,
	ACTUAL_SITE_DATE_COMMENT VARCHAR(1000),
	LAST_UPDATE TIMESTAMP NOT NULL,
	STATUS_ID INTEGER NOT NULL,
	RFE_COMMENT VARCHAR (1000)
);



CREATE TABLE ROLE (
	ID INTEGER DEFAULT NEXTVAL(('ROLE_ID_SEQ'::TEXT)::REGCLASS) NOT NULL,
	NAME VARCHAR(50)
);

CREATE TABLE ROLE_OPTION (
	ID BIGINT NOT NULL,
	OPTION_ID INTEGER NOT NULL,
	ROLE_ID INTEGER NOT NULL
);

CREATE TABLE SCOPE_SUPPLY(
	ID BIGINT NOT NULL,
	COST DECIMAL (18,5),
	CURRENCY VARCHAR(5),
	CODE VARCHAR(50),
	UNIT VARCHAR(50),
	QUANTITY INTEGER,
	DESCRIPTION VARCHAR (1000),
	EX_WORK_DATE TIMESTAMP,
	EX_WORK_DATE_DESCRIPTION VARCHAR (1000),
	DELIVERY_LEAD_TIME_QT INTEGER,
	DELIVERY_LEAD_TIME_MS INTEGER,
	DELIVERY_LEAD_TIME_DESCRIPTION VARCHAR (1000),
	SITE_DATE TIMESTAMP,
	SITE_DATE_DESCRIPTION VARCHAR(1000),
	PURCHASE_ORDER_ID BIGINT NOT NULL,
	LAST_UPDATE TIMESTAMP NOT NULL,
	STATUS_ID INTEGER NOT NULL,
  ACTUAL_SITE_DATE TIMESTAMP,
  ACTUAL_EX_WORK_DATE TIMESTAMP,
  DELIVERY_DATE TIMESTAMP,
  REQUIRED_SITE_DATE TIMESTAMP,
  DELIVERY_DATE_OBS VARCHAR(1000),
  ACTUAL_EX_WORK_DATE_OBS VARCHAR(1000),
  REQUIRED_SITE_DATE_OBS VARCHAR (1000),
  ACTUAL_SITE_DATE_OBS VARCHAR (1000),
  IS_FORECAST_SITE_DATE_MANUAL BOOLEAN NOT NULL,
  SP_INCO_TERM VARCHAR(50),
  SP_INCO_TERM_DESCRIPTION VARCHAR(1000),
  RESPONSIBLE_EXPEDITING VARCHAR(100),
  RESPONSIBLE_EXPEDITING_OBSERVATION VARCHAR(1000),
  ORDERED INTEGER,
  TAG_NO VARCHAR(1000)
);

CREATE TABLE STATUS (
	ID  INTEGER NOT NULL,
	NAME VARCHAR(25)NOT NULL
);

CREATE TABLE SUPPLIER(
	ID BIGINT NOT NULL,
	SUPPLIER VARCHAR(50)NOT NULL,
	ADDRESS VARCHAR (500),
	CONTACT_DETAIL VARCHAR(1000),
	CONTACT_NAME VARCHAR(100),
	EMAIL_ADDRESS VARCHAR(1000),
	PURCHASE_ORDER_ID BIGINT NOT NULL,
	LAST_UPDATE TIMESTAMP NOT NULL,
	STATUS_ID INTEGER NOT NULL
);


CREATE TABLE TRANSIT_DELIVERY_POINT(
  ID BIGINT NOT NULL,
  LOCATION VARCHAR(50) NOT NULL,
  LEAD_TIME INTEGER NOT NULL,
  MEASUREMENT_TIME INTEGER NOT NULL,
  FORECAST_DELIVERY_DATE TIMESTAMP,
  ACTUAL_DELIVERY_DATE TIMESTAMP,
  COMMENTS VARCHAR(1000),
  SCOPE_SUPPLY_ID BIGINT NOT NULL,
  STATUS_ID INTEGER NOT NULL,
  LAST_UPDATE TIMESTAMP NOT NULL,
  IS_FORECAST_SITE_DATE_MANUAL BOOL NOT NULL
);


CREATE TABLE USER_ROLE (
	USER_ID BIGINT NOT NULL,
	ROLE_ID INTEGER NOT NULL
);


CREATE TABLE USERS (
	ID BIGINT DEFAULT NEXTVAL(('USERS_ID_SEQ'::TEXT)::REGCLASS) NOT NULL,
	EMAIL VARCHAR(50) NOT NULL,
	FIRST_NAME VARCHAR(50) NOT NULL,
	NAME VARCHAR(50) NOT NULL,
	PWD_HASH VARCHAR(100) NOT NULL,
	STATUS_ID INTEGER,
	USER_NAME VARCHAR(50) NOT NULL,
	ROLE_ID INTEGER	 NOT NULL,
	LAST_UPDATE TIMESTAMP NOT NULL
);

CREATE TABLE VERIFICATION_TOKEN(
  ID BIGINT NOT NULL,
  TOKEN character varying(50),
  EXPIRATION_DATE TIMESTAMP,
  VERIFIED BOOLEAN,
  USER_ID BIGINT
);


ALTER TABLE ATTACHMENT_COMMENT ADD CONSTRAINT PK_ATTACHMENT_CMT
	PRIMARY KEY (ID);

ALTER TABLE ATTACHMENT_SCOPE_SUPPLY ADD CONSTRAINT PK_ATTACHMENT_SS
	PRIMARY KEY (ID);
	
ALTER TABLE COMMENTS ADD CONSTRAINT PK_COMMENTS
	PRIMARY KEY (ID);
	
ALTER TABLE MODULE_ENTITY ADD CONSTRAINT PK_MODULE
	PRIMARY KEY (ID);
	
ALTER TABLE OPTIONS ADD CONSTRAINT PK_OPTIONS
	PRIMARY KEY (ID);
	
ALTER TABLE PURCHASE_ORDER ADD CONSTRAINT PK_PURCHASE_ORDER
	PRIMARY KEY (ID);
	
ALTER TABLE ROLE ADD CONSTRAINT PK_ROLE
	PRIMARY KEY (ID);
	
ALTER TABLE SUPPLIER ADD CONSTRAINT PK_SUPPLIER
	PRIMARY KEY (ID);
	
ALTER TABLE ROLE_OPTION ADD CONSTRAINT PK_ROLE_OPTION
	PRIMARY KEY (ID);

ALTER TABLE SCOPE_SUPPLY ADD CONSTRAINT PK_SCOPE_SUPPLY
	PRIMARY KEY (ID);
	
ALTER TABLE STATUS ADD CONSTRAINT PK_STATUS
	PRIMARY KEY (ID);

ALTER TABLE TRANSIT_DELIVERY_POINT ADD CONSTRAINT PK_TRANSIT_DELIVERY_POINT
	PRIMARY KEY (ID);
	
ALTER TABLE USER_ROLE ADD CONSTRAINT PK_USER_ROLE
	PRIMARY KEY (USER_ID, ROLE_ID);
	
ALTER TABLE USERS ADD CONSTRAINT PK_USERS
	PRIMARY KEY (ID);

ALTER TABLE VERIFICATION_TOKEN ADD CONSTRAINT PK_TOKEN
	PRIMARY KEY (ID);

ALTER TABLE MODULE_ENTITY
	ADD CONSTRAINT UQ_MODULE_NAME UNIQUE (NAME);
	
ALTER TABLE OPTIONS
	ADD CONSTRAINT UQ_OPTION_NAME UNIQUE (NAME);
	
ALTER TABLE ROLE
	ADD CONSTRAINT UQ_ROLE_ID UNIQUE (ID);
	
ALTER TABLE ROLE_OPTION
	ADD CONSTRAINT UQ_ROLE_OPTION_ID UNIQUE (ID);
	
ALTER TABLE USERS
	ADD CONSTRAINT UQ_USER_ID UNIQUE (ID);

ALTER TABLE VERIFICATION_TOKEN
 ADD CONSTRAINT UQ_TOKEN UNIQUE (TOKEN);

ALTER TABLE ATTACHMENT_COMMENT ADD CONSTRAINT FK_AT_CMT_ID
	FOREIGN KEY (COMMENT_ID) REFERENCES COMMENTS (ID);

ALTER TABLE ATTACHMENT_COMMENT ADD CONSTRAINT FK_ATCMT_STATUS_ID
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);

ALTER TABLE ATTACHMENT_SCOPE_SUPPLY ADD CONSTRAINT FK_AT_SS_ID
	FOREIGN KEY (SCOPE_SUPPLY_ID) REFERENCES SCOPE_SUPPLY (ID);

ALTER TABLE ATTACHMENT_SCOPE_SUPPLY ADD CONSTRAINT FK_ATSS_STATUS_ID
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);
	
ALTER TABLE COMMENTS ADD CONSTRAINT FK_COMMENTS_STATUS_ID 
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);
	
ALTER TABLE OPTIONS ADD CONSTRAINT FK_OPTIONS_MODULE
	FOREIGN KEY (MODULE_ID) REFERENCES MODULE_ENTITY (ID);
	
ALTER TABLE ROLE_OPTION ADD CONSTRAINT FK_ROLE_OPTION_ROLE
	FOREIGN KEY (ROLE_ID) REFERENCES ROLE (ID);
	
ALTER TABLE PURCHASE_ORDER ADD CONSTRAINT FK_PO_STATUS_ID 
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);
	
ALTER TABLE SCOPE_SUPPLY ADD CONSTRAINT FK_SS_STATUS_ID 
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);

ALTER TABLE SUPPLIER ADD CONSTRAINT FK_SUPPLIER_STATUS_ID 
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);
	
ALTER TABLE USERS ADD CONSTRAINT FK_STATUS_ID
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);

ALTER TABLE USERS ADD CONSTRAINT FK_ROLE_ID
	FOREIGN KEY (ROLE_ID) REFERENCES ROLE (ID);
	
ALTER TABLE COMMENTS ADD CONSTRAINT FK_COMMENT_PO_ID
	FOREIGN KEY (PURCHASE_ORDER_ID) REFERENCES PURCHASE_ORDER (ID);
	
ALTER TABLE SCOPE_SUPPLY ADD CONSTRAINT FK_SS_PURCHASE_ORDER_ID
	FOREIGN KEY (PURCHASE_ORDER_ID) REFERENCES PURCHASE_ORDER (ID);
	
ALTER TABLE SUPPLIER ADD CONSTRAINT FK_PURCHASE_ORDER_ID
	FOREIGN KEY (PURCHASE_ORDER_ID) REFERENCES PURCHASE_ORDER (ID);

ALTER TABLE TRANSIT_DELIVERY_POINT ADD CONSTRAINT FK_TDP
	FOREIGN KEY (SCOPE_SUPPLY_ID) REFERENCES SCOPE_SUPPLY (ID);

ALTER TABLE TRANSIT_DELIVERY_POINT ADD CONSTRAINT FK_TDP_STATUS
	FOREIGN KEY (STATUS_ID) REFERENCES STATUS (ID);
	
ALTER TABLE USER_ROLE ADD CONSTRAINT FK_ROLE_ID
	FOREIGN KEY (ROLE_ID) REFERENCES ROLE (ID);
	
ALTER TABLE USER_ROLE ADD CONSTRAINT FK_USER_ID
	FOREIGN KEY (USER_ID) REFERENCES USERS (ID);

ALTER TABLE VERIFICATION_TOKEN ADD CONSTRAINT FK_VERIFICATION_TOKEN_USERS
	FOREIGN KEY (USER_ID) REFERENCES users (ID);




DROP VIEW V_PURCHASE_ORDER;
CREATE OR REPLACE VIEW V_PURCHASE_ORDER AS
SELECT ROW_NUMBER() OVER () AS ID, PO.ID AS PO_ID,
	PO.PROJECT, PO.PO, PO.VARIATION, PO.PO_TITLE, S.SUPPLIER,PO.RESPONSIBLE_EXPEDITING, PO.PO_INCO_TERM, PO.PO_DELIVERY_DATE
   FROM PURCHASE_ORDER  PO INNER JOIN SUPPLIER S ON PO.ID=S.PURCHASE_ORDER_ID
   WHERE PO.STATUS_ID<>3
   ORDER BY CASE WHEN ISNUMERIC(PO) THEN LPAD(PO.PO, 50, '0') ELSE PO.PO END;

CREATE OR REPLACE FUNCTION isnumeric(v_input text)
RETURNS boolean AS $$
DECLARE v_int_value INTEGER DEFAULT NULL;
BEGIN
    BEGIN
        v_int_value := v_input::INTEGER;
        RETURN TRUE;
    EXCEPTION WHEN OTHERS THEN
        RETURN FALSE;
    END;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION po_sorting(ponumber text)
  RETURNS text AS
$BODY$
    declare
     target text;
    begin
    if(not isnumeric(poNumber)) then
	target='a'||poNumber;
    else
	target = '0' || poNumber;
    end if;
  return target;
  end;


CREATE OR REPLACE FUNCTION isnumeric(v_input text)
  RETURNS boolean AS
$BODY$
DECLARE v_int_value INTEGER DEFAULT NULL;
BEGIN
    BEGIN
        v_int_value := v_input::INTEGER;
        RETURN TRUE;
    EXCEPTION WHEN OTHERS THEN
        RETURN FALSE;
    END;
END;
$BODY$
  LANGUAGE plpgsql;