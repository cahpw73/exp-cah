
--Create patch to Module to add column module_system
--Create patch to UserRole to add column module_system
--Create patch to add Dt Module_Granted_Access
--Create patch to add data in Dt Module (project,report,admin,etc.)
--Create patch to User for remove column role

--Add data to UserRole
INSERT INTO USER_ROLE VALUES (1,0,10,10);
INSERT INTO USER_ROLE VALUES (2,0,11,13);
INSERT INTO USER_ROLE VALUES (3,0,12,10);
INSERT INTO USER_ROLE VALUES (4,0,13,11);
INSERT INTO USER_ROLE VALUES (5,0,14,12);

--Add column module_system to role
INSERT INTO ROLE VALUES (10,'SENIOR',0);
INSERT INTO ROLE VALUES (11,'JUNIOR',0);
INSERT INTO ROLE VALUES (12,'VISITOR',0);
INSERT INTO ROLE VALUES (13,'ADMINISTRATOR',0);
