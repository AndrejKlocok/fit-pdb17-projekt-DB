DROP INDEX property_map_index FORCE;

DROP TABLE owner;
DROP TABLE property_price;
DROP TABLE ground_plan;
DROP TABLE person;
DROP TABLE property;


DROP SEQUENCE property_price_seq;
DROP SEQUENCE GROUND_PLAN_SEQ;
DROP SEQUENCE PROPERTY_SEQ;
DROP SEQUENCE PERSON_SEQ;

DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'PROPERTY' AND COLUMN_NAME = 'GEOMETRY';

CREATE TABLE property(
    id_property NUMBER NOT NULL,
    -- ENUM('dom', panelak, 'apartment', terrace-house')
    property_type VARCHAR(16) CHECK( property_type in ('house', 'prefab', 'apartment', 'terrace-house', 'land')),
    geometry SDO_GEOMETRY,
    property_name VARCHAR(32) NOT NULL,
    property_description VARCHAR(64) NOT NULL,
    CONSTRAINT pk_property PRIMARY KEY(id_property));

CREATE SEQUENCE property_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE property_price(
    id_price NUMBER NOT NULL,
    id_property NUMBER NOT NULL,
    price NUMBER NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    CONSTRAINT property_price_pk PRIMARY KEY(id_price),
    CONSTRAINT fk_property_price FOREIGN KEY(id_property) REFERENCES property(id_property) ON DELETE CASCADE,
    CONSTRAINT date_viable_price CHECK(valid_from <= valid_to)
);


CREATE SEQUENCE property_price_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE ground_plan(
    id_ground_plan NUMBER NOT NULL,
    id_property NUMBER NOT NULL,
    img ORDSYS.ORDImage,
    img_si ORDSYS.SI_StillImage,
	img_ac ORDSYS.SI_AverageColor,
	img_ch ORDSYS.SI_ColorHistogram,
	img_pc ORDSYS.SI_PositionalColor,
	img_tx ORDSYS.SI_Texture,
    CONSTRAINT pk_ground_plan PRIMARY KEY(id_ground_plan),
    CONSTRAINT fk_property FOREIGN KEY(id_property) REFERENCES property(id_property) ON DELETE CASCADE
);


CREATE SEQUENCE ground_plan_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE person(
    id_person NUMBER NOT NULL,
    firstname VARCHAR(32) NOT NULL,
    lastname VARCHAR(32) NOT NULL,
    street VARCHAR(32) NOT NULL,
    city VARCHAR(32) NOT NULL,
    psc VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY(id_person)
);

CREATE SEQUENCE person_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE owner(
    id_owner NUMBER NOT NULL,
    id_property NUMBER NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    CONSTRAINT pk_owner PRIMARY KEY(id_owner, id_property, valid_from, valid_to),
    CONSTRAINT fk_owner FOREIGN KEY(id_owner) REFERENCES person(id_person) ON DELETE CASCADE,
    CONSTRAINT fk_property1 FOREIGN KEY(id_property) REFERENCES property(id_property) ON DELETE CASCADE,
    CONSTRAINT date_viable_owner CHECK(valid_from <= valid_to)
);


INSERT INTO USER_SDO_GEOM_METADATA VALUES (
    'PROPERTY', 'GEOMETRY',
    --suradnice Brno    
    SDO_DIM_ARRAY(SDO_DIM_ELEMENT('LONGITUDE',-180, 180, 0.5), SDO_DIM_ELEMENT('LATITUDE', -90, 90, 0.5)),
    8307);                                       

-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;

CREATE INDEX property_map_index ON property(geometry) indextype is MDSYS.SPATIAL_INDEX NOPARALLEL ;
-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;


--inserts
INSERT INTO PROPERTY (ID_PROPERTY,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PROPERTY_DESCRIPTION) VALUES (
    10,'house',SDO_GEOMETRY(         2001, 
                                    8307, 
                                    SDO_POINT_TYPE(16.603033, 49.203700 ,NULL),
                                    NULL,
                                    NULL
                                ),
    'The Czechoslovak Hussite Church', 'Botanická 590/1, 602 00 Brno-stred-Veverí');
    
INSERT INTO PROPERTY (ID_property,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PROPERTY_DESCRIPTION) VALUES (
    11, 'prefab', SDO_GEOMETRY(
                                        2001, 
                                        8307, 
                                        SDO_POINT_TYPE(16.584492,49.229635, NULL),
                                        NULL,
                                        NULL
                                    ),
    'Panelák Her?íkova', 'Her?íkova 2498/18');

INSERT INTO PROPERTY (ID_property,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PROPERTY_DESCRIPTION) VALUES (
    12, 'house', SDO_GEOMETRY(
                                        2001, 
                                        8307, 
                                        SDO_POINT_TYPE(16.582020,49.228377, NULL),
                                        NULL,
                                        NULL
                                    ),
    'Technologicke Muzeum', 'Purkynova ');


Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,1,to_date('19-JUL-01','DD-MON-RR'),to_date('19-JUL-01','DD-MON-RR'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,2,to_date('19-JUL-01','DD-MON-RR'),to_date('19-JUL-01','DD-MON-RR'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,3,to_date('19-JUL-01','DD-MON-RR'),to_date('19-JUL-01','DD-MON-RR'));

Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (2,'Vladimir','Pes','street','city','psc','email');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (3,'Milos','Milos','street','city','psc','email');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (1,'Jozef','Mak','street','city','psc','email');

Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (4,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1, 11, 2003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.605299, 49.192204, 16.605467, 49.191726, 16.60592, 49.191791, 16.605744, 49.192274, 16.605299, 49.192204, 16.6055, 49.192087, 16.605573, 49.191874, 16.605722, 49.191899, 16.605647, 49.192108, 16.6055, 49.192087)),'dom4','diera');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (2,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.603125, 49.203747, 16.603033, 49.2037, 16.603319, 49.203454, 16.603418, 49.2035, 16.603125, 49.203747)),'dom2','desc2');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (3,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.606089, 49.191362, 16.606155, 49.191373, 16.606149, 49.191385, 16.606294, 49.191429, 16.606284, 49.191449, 16.606266, 49.191457, 16.606235, 49.191536, 16.606241, 49.191543, 16.606256, 49.191543, 16.606284, 49.191571, 16.606243, 49.191595, 16.606342, 49.191633, 16.606344, 49.191604, 16.6064, 49.191604, 16.606405, 49.191644, 16.6065, 49.191605, 16.606456, 49.191563, 16.606534, 49.191533, 16.60658, 49.191566, 16.606637, 49.191545, 16.606727, 49.191647, 16.606717, 49.191673, 16.6064, 49.191805, 16.606376, 49.191805, 16.605984, 49.191663, 16.605984, 49.191622, 16.606089, 49.191362)),'dom3','desc3');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (1,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.607206, 49.191432, 16.607436, 49.191344, 16.607542, 49.191457, 16.60731, 49.19155, 16.607206, 49.191432)),'dom1','desc1');


SELECT SDO_GEOM.SDO_DISTANCE(a.geometry, g.geometry, 1, 'unit=M') vzdalenost_budov_a_g
FROM PROPERTY a, PROPERTY g
WHERE a.id_property = 1 AND g.id_property = 2;
