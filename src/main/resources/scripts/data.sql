
DELETE FROM ROLE_OPTION;
DELETE FROM USER_ROLE ;
DELETE FROM USERS;
DELETE FROM ROLE;
DELETE FROM STATUS;
DELETE FROM OPTIONS;
DELETE FROM MODULE_ENTITY;

INSERT INTO ROLE(id,name,module_system) VALUES (10,'SENIOR',0);
INSERT INTO ROLE(id,name,module_system) VALUES (11,'JUNIOR',0);
INSERT INTO ROLE(id,name,module_system) VALUES (12,'VISITOR',0);
INSERT INTO ROLE(id,name,module_system) VALUES (13,'ADMINISTRATOR',0);

INSERT INTO ROLE(id,name,module_system) VALUES (14,'FULL_WITH_COMMIT',1);
INSERT INTO ROLE(id,name,module_system) VALUES (15,'FULL',1);
INSERT INTO ROLE(id,name,module_system) VALUES (16,'RESTRICTED',1);
INSERT INTO ROLE(id,name,module_system) VALUES (17,'READ_ONLY',1);

INSERT INTO STATUS(id,name) VALUES(1,'ACTIVE');
INSERT INTO STATUS(id,name) VALUES(2,'DISABLED');
INSERT INTO STATUS(id,name) VALUES(3,'REMOVED');


INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (10,'simon.mcneal@mail.com','Simon','Mcneal','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',2,'simon',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (11,'info@swissbytes.ch','Administrator','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'administrator',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (12,'senior.fqmes@yopmail.com','senior','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'senior',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (13,'junior.fqmes@yopmail.com','junior','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'junior',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (14,'visitor.fqmes@yopmail.com','visitor','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'visitor',CURRENT_DATE);

INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (1,0,10,10);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (2,0,11,13);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (3,0,12,10);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (4,0,13,11);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (5,0,12,12);

INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(1,'PURCHASE_ORDER',0);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(2,'REPORT',0);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(3,'USER',0);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(4,'PROJECTS',1);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(5,'REPORTS',1);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(6,'ADMIN',1);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(7,'PROFILE',1);

-- USER
INSERT INTO OPTIONS(id,name,module_id) VALUES(1,'USER_LIST',3);
INSERT INTO OPTIONS(id,name,module_id) VALUES(2,'USER_CREATE',3);
INSERT INTO OPTIONS(id,name,module_id) VALUES(3,'USER_UPDATE',3);
INSERT INTO OPTIONS(id,name,module_id) VALUES(4,'USER_VIEW',3);
INSERT INTO OPTIONS(id,name,module_id) VALUES(5,'USER_DELETE',3);

--PURCHASE ORDER

INSERT INTO OPTIONS(id,name,module_id) VALUES(100,'PURCHASE_ORDER_LIST',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(101,'PURCHASE_ORDER_CREATE',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(102,'PURCHASE_ORDER_UPDATE',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(103,'PURCHASE_ORDER_VIEW',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(104,'PURCHASE_ORDER_DELETE',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(105,'PURCHASE_ORDER_SPLIT_POIR',1);


--REPORT

INSERT INTO OPTIONS(id,name,module_id) VALUES(200,'REPORT_LIST',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(201,'REPORT_RECEIVABLE_MANIFEST',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(202,'REPORT_JOB_SUMMARY',1);


--FIELDS POR
INSERT INTO OPTIONS(id,name,module_id) VALUES(300,'EDIT_PROJECT',1);
INSERT INTO OPTIONS(id,name,module_id) VALUES(301,'EDIT_PO_REFERENCE',1);

--ADMINISTRATOR	
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(1,1,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(2,2,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(3,3,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(4,4,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(5,5,13);

-- SENIOR
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(100,100,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(101,101,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(102,102,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(103,103,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(104,104,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(105,105,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(106,200,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(107,201,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(108,202,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(109,300,10);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(110,301,10);
--JUNIOR

INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(200,100,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(201,102,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(202,103,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(203,105,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(204,200,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(205,201,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(206,202,11);

-- VISITOR

INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(300,100,12);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(301,103,12);