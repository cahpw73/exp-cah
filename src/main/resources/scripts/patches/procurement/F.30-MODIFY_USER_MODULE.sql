
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
