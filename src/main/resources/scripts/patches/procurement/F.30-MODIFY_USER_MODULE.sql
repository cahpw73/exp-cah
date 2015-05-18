
--Create patch to Module to add column module_system
--Create patch to UserRole to add column module_system
--Create patch to add Dt Module_Granted_Access
--Create patch to add data in Dt Module (project,report,admin,etc.)
--Create patch to User for remove column role

--Add data to UserRole
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (1,0,10,10);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (2,0,11,13);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (3,0,12,10);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (4,0,13,11);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (5,0,14,12);

--Add column module_system to role
INSERT INTO ROLE(id,name,module_system) VALUES (14,'FULL_WITH_COMMIT',1);
INSERT INTO ROLE(id,name,module_system) VALUES (15,'FULL',1);
INSERT INTO ROLE(id,name,module_system) VALUES (16,'RESTRICTED',1);
INSERT INTO ROLE(id,name,module_system) VALUES (17,'READ_ONLY',1);
