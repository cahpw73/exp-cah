
DELETE FROM ROLE_OPTION;
DELETE FROM USER_ROLE ;
DELETE FROM MODULE_GRANTED_ACCESS;
DELETE FROM VERIFICATION_TOKEN;
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


INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (10,'simon.mcneal@mail.com','Simon','Mcneal','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'simon',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (11,'info@swissbytes.ch','Administrator','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'administrator',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (12,'senior.fqmes@yopmail.com','senior','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'senior',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (13,'junior.fqmes@yopmail.com','junior','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'junior',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (14,'visitor.fqmes@yopmail.com','visitor','Fqmes','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'visitor',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (15,'procurement.fqmes@yopmail.com','procurement','procurement','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'procurement',CURRENT_DATE);
INSERT INTO USERS(id,email,first_name,name,pwd_hash,status_id,user_name,last_update) VALUES (16,'expediting.fqmes@yopmail.com','expediting','expediting','jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=',1,'expediting',CURRENT_DATE);

INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (1,0,10,10);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (2,0,11,13);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (3,0,12,10);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (4,0,13,11);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (5,0,10,12);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (6,1,14,12);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (7,1,10,15);
INSERT INTO USER_ROLE(id,module_system,role_id,user_id) VALUES (8,0,10,16);

INSERT INTO MODULE_GRANTED_ACCESS (id,module_access,module_system,user_id) VALUES(1,true,0,12);
INSERT INTO MODULE_GRANTED_ACCESS (id,module_access,module_system,user_id) VALUES(2,true,1,12);
INSERT INTO MODULE_GRANTED_ACCESS (id,module_access,module_system,user_id) VALUES(3,true,1,15);
INSERT INTO MODULE_GRANTED_ACCESS (id,module_access,module_system,user_id) VALUES(4,true,0,16);


INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(1,'PURCHASE_ORDER',0);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(2,'REPORT',0);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(3,'USER',0);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(4,'PROJECTS',1);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(5,'REPORTS',1);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(6,'ADMIN',1);
INSERT INTO MODULE_ENTITY(id,name,module_system) VALUES(7,'PROFILE',1);


INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1,'USER_LIST',3,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(2,'USER_CREATE',3,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(3,'USER_UPDATE',3,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(4,'USER_VIEW',3,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(5,'USER_DELETE',3,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(100,'PURCHASE_ORDER_LIST',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(101,'PURCHASE_ORDER_CREATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(102,'PURCHASE_ORDER_UPDATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(103,'PURCHASE_ORDER_VIEW',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(104,'PURCHASE_ORDER_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(105,'PURCHASE_ORDER_SPLIT_POIR',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(200,'REPORT_LIST',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(201,'REPORT_RECEIVABLE_MANIFEST',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(202,'REPORT_JOB_SUMMARY',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(300,'EDIT_PROJECT',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(301,'EDIT_PO_REFERENCE',1,'');
--Starts permission for procurement
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(400,'ADMIN',1,'procurement/admin/admin.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(401,'SUPPLIER_LIST',1,'procurement/admin/supplier/list.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(402,'SUPPLIER_CREATE',1,'procurement/admin/supplier/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(403,'SUPPLIER_EDIT',1,'procurement/admin/supplier/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(405,'SUPPLIER_VIEW',1,'procurement/admin/supplier/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(404,'SUPPLIER_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(500,'USER_LIST',1,'procurement/admin/user/list.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(501,'USER_CREATE',1,'procurement/admin/user/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(502,'USER_EDIT',1,'procurement/admin/user/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(503,'USER_VIEW',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(504,'USER_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(505,'USER_RESET',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(506,'PROFILE',1,'procurement/profile/profile.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(600,'CURRENCY_LIST',1,'procurement/admin/currency/currency.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(601,'CURRENCY_CREATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(602,'CURRENCY_EDIT',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(603,'CURRENCY_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(700,'BRAND_LIST',1,'procurement/admin/brands/brands.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(701,'BRAND_CREATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(702,'BRAND_EDIT',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(703,'BRAND_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(800,'CATEGORY_LIST',1,'procurement/admin/category/category.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(801,'CATEGORY_CREATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(802,'CATEGORY_EDIT',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(803,'CATEGORY_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(900,'STANDARD_TEXT_LIST',1,'procurement/admin/textSnippet/textSnippet.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(901,'STANDARD_TEXT_CREATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(903,'STANDARD_TEXT_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(902,'STANDARD_TEXT_EDIT',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1000,'LOGO_LIST',1,'procurement/admin/logo/logo.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1001,'LOGO_CREATE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1002,'LOGO_EDIT',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1003,'LOGO_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1004,'LOGO_LIST',1,'procurement/admin/logo/preview.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1100,'CLIENT_LIST',1,'procurement/admin/client/list.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1101,'CLIENT_CREATE',1,'procurement/admin/client/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1102,'CLIENT_EDIT',1,'procurement/admin/client/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1103,'CLIENT_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1200,'REPORT_LIST',1,'procurement/report/report.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1201,'REPORT_DELIVERABLE',1,'procurement/report/deliverables/deliverables.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1202,'REPORT_BIDDER_LIST',1,'procurement/report/bidderList/categorySelection.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1203,'REPORT_PROJECT_PROCUREMENT',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1204,'REPORT_DETAILED_PROCUREMENT',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1205,'REPORT_SUMMARY_PO',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1207,'REPORT_PROJECT_SUPPLIER_CONTACTS',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1208,'REPORT_REQUIRED_RETENTIONS',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1209,'REPORT_COMMITED_CURRENCIES',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1210,'REPORT_MATERIALS_REQUISITION',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1211,'REPORT_UNCOMMITTED_DATA',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1212,'REPORT_DETAILED_SUPPLIER',1,'procurement/report/print-reports.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1213,'REPORT_EXPEDITING',1,'procurement/report/expediting/expediting.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1300,'PROJECT_LIST',1,'procurement/project/list.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1301,'PROJECT_CREATE',1,'procurement/project/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1302,'PROJECT_EDIT',1,'procurement/project/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1303,'PROJECT_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1304,'PROJECT_VIEW',1,'procurement/project/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1400,'PO_LIST',1,'procurement/project/purchase-order/list.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1401,'PO_CREATE',1,'procurement/project/purchase-order/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1402,'PO_EDIT',1,'procurement/project/purchase-order/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1403,'PO_DELETE',1,'');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1404,'PO_VIEW',1,'procurement/project/purchase-order/edit.jsf');
INSERT INTO OPTIONS(id,name,module_id,url) VALUES(1405,'PO_COMMIT',1,'');




INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(1,1,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(2,2,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(3,3,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(4,4,13);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(5,5,13);
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
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(200,100,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(201,102,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(202,103,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(203,105,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(204,200,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(205,201,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(206,202,11);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(300,100,12);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(301,103,12);


INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(400,400,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(401,401,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(402,402,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(403,403,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(404,404,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(405,405,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(406,500,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(407,501,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(408,502,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(409,503,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(410,504,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(411,505,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(412,506,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(413,600,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(414,601,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(415,602,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(416,603,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(417,700,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(418,701,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(419,702,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(420,703,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(421,800,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(422,801,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(423,802,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(424,803,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(425,900,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(426,901,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(427,902,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(428,903,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(429,1000,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(430,1001,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(431,1002,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(432,1003,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(433,1004,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(434,1100,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(435,1101,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(436,1102,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(437,1103,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(438,1200,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(439,1201,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(440,1202,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(441,1203,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(442,1204,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(443,1205,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(444,1207,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(445,1208,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(446,1209,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(447,1210,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(448,1211,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(449,1212,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(450,1213,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(451,1300,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(452,1301,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(453,1302,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(454,1303,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(455,1304,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(456,1400,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(457,1401,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(458,1402,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(459,1403,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(460,1404,14);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(461,1405,14);

INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(500,400,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(501,401,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(502,402,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(503,403,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(504,404,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(505,405,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(506,500,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(507,501,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(508,502,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(509,503,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(510,504,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(511,505,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(512,506,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(513,600,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(514,601,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(515,602,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(516,603,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(517,700,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(518,701,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(519,702,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(520,703,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(521,800,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(522,801,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(523,802,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(524,803,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(525,900,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(526,901,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(527,902,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(528,903,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(529,1000,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(530,1001,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(531,1002,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(532,1003,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(533,1004,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(534,1100,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(535,1101,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(536,1102,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(537,1103,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(538,1200,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(539,1201,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(540,1202,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(541,1203,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(542,1204,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(543,1205,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(544,1207,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(545,1208,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(546,1209,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(547,1210,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(548,1211,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(549,1212,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(550,1213,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(551,1300,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(552,1301,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(553,1302,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(554,1303,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(555,1304,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(556,1400,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(557,1401,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(558,1402,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(559,1403,15);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(560,1404,15);

INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(600,1200,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(601,1201,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(602,1202,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(603,1203,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(605,1204,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(606,1205,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(607,1207,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(608,1208,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(609,1209,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(610,1210,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(611,1211,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(612,1212,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(613,1213,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(614,1300,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(615,1301,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(616,1302,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(617,1303,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(618,1304,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(619,1400,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(620,1401,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(621,1402,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(622,1403,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(623,1404,16);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(624,506,16);

INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(700,1300,17);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(701,1304,17);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(702,1400,17);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(703,1404,17);
INSERT INTO ROLE_OPTION(id,option_id,role_id) VALUES(704,506,17);
