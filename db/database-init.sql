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
    CONSTRAINT date_viable_price CHECK(valid_from <= valid_to),
    CONSTRAINT unique_valid_from_price UNIQUE( id_price, valid_from),
    CONSTRAINT unique_valid_to_price UNIQUE( id_price, valid_to)
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
    CONSTRAINT date_viable_owner CHECK(valid_from <= valid_to),
    CONSTRAINT unique_valid_from_owner UNIQUE( id_property, valid_from),
    CONSTRAINT unique_valid_to_owner UNIQUE( id_property, valid_to)
);


INSERT INTO USER_SDO_GEOM_METADATA VALUES (
    'PROPERTY', 'GEOMETRY',
    --suradnice Brno    
    SDO_DIM_ARRAY(SDO_DIM_ELEMENT('LONGITUDE',-180, 180, 0.5), SDO_DIM_ELEMENT('LATITUDE', -90, 90, 0.5)),
    8307);                                       

-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;

CREATE INDEX property_map_index ON property(geometry) indextype is MDSYS.SPATIAL_INDEX;
-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;



--inserts
INSERT INTO PROPERTY (ID_PROPERTY,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PROPERTY_DESCRIPTION) VALUES (
    1,'house',SDO_GEOMETRY(         2001, 
                                    8307, 
                                    SDO_POINT_TYPE(16.603033, 49.203700 ,NULL),
                                    NULL,
                                    NULL
                                ),
    'The Czechoslovak Hussite Church', 'Botanická 590/1, 602 00 Brno-stred-Veverí');
    
INSERT INTO PROPERTY (ID_property,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PROPERTY_DESCRIPTION) VALUES (
    2, 'prefab', SDO_GEOMETRY(
                                        2001, 
                                        8307, 
                                        SDO_POINT_TYPE(16.584492,49.229635, NULL),
                                        NULL,
                                        NULL
                                    ),
    'Panelák Her?íkova', 'Her?íkova 2498/18');

INSERT INTO PERSON (id_person, FIRSTNAME, LASTNAME, STREET, CITY, PSC, EMAIL) VALUES (
    PERSON_SEQ.NEXTVAL, 'Jirka', 'Babiš', 'Vlhká 13', 'Brno', '60200', 'jirka@gmail.com'
);

INSERT INTO PERSON (id_person, FIRSTNAME, LASTNAME, STREET, CITY, PSC, EMAIL) VALUES (
    PERSON_SEQ.NEXTVAL, 'Michal', 'Zeman', 'Kolejní 2', 'Brno', '60204', 'michal@seznam.cz'
);

INSERT INTO PERSON (id_person, FIRSTNAME, LASTNAME, STREET, CITY, PSC, EMAIL) VALUES (
    PERSON_SEQ.NEXTVAL, 'Václav', 'Novák', 'Ceská 56', 'Brno' ,'60205', 'novak@gmaik.com'
);

INSERT INTO OWNER (ID_OWNER, ID_PROPERTY, VALID_FROM, VALID_TO) VALUES (
    1, 1, DATE '2000-12-24', DATE '2017-08-28'
);

INSERT INTO OWNER (ID_OWNER, ID_PROPERTY, VALID_FROM, VALID_TO) VALUES (
    2, 2, DATE '2008-05-12', DATE '2009-03-20'
);

INSERT INTO OWNER (ID_OWNER, ID_PROPERTY, VALID_FROM, VALID_TO) VALUES (
    3, 2, DATE '2007-01-01', DATE '2008-06-17'
);

SELECT SDO_GEOM.SDO_DISTANCE(a.geometry, g.geometry, 1, 'unit=M') vzdalenost_budov_a_g
FROM PROPERTY a, PROPERTY g
WHERE a.id_property = 1 AND g.id_property = 2;