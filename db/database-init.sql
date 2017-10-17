DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'PROPERTY' AND COLUMN_NAME = 'GEOMETRY';

DROP TABLE property;
DROP TABLE ground_plan;
DROP TABLE owner;
DROP TABLE person;

DROP SEQUENCE GROUND_PLAN_SEQ;
DROP SEQUENCE PROPERTY_SEQ;
DROP SEQUENCE PERSON_SEQ;

DROP INDEX property_map_index;
ALTER TABLE property DROP CONSTRAINT pk_property;
DROP INDEX pk_property;

CREATE TABLE property(
    id NUMBER NOT NULL,
    -- ENUM('dom', panelak, 'apartment', terrace-house')
    property_type VARCHAR(16) CHECK( property_type in ('house', 'prefab', 'apartment', 'terrace-house')),
    geometry SDO_GEOMETRY,
    property_name VARCHAR(32) NOT NULL,
    price NUMBER NOT NULL,
    property_description VARCHAR(64) NOT NULL,
    CONSTRAINT pk_property PRIMARY KEY(id));

CREATE SEQUENCE property_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE ground_plan(
    id NUMBER NOT NULL,
    id_property NUMBER NOT NULL,
    img ORDSYS.ORDImage,
    img_si ORDSYS.SI_StillImage,
	img_ac ORDSYS.SI_AverageColor,
	img_ch ORDSYS.SI_ColorHistogram,
	img_pc ORDSYS.SI_PositionalColor,
	img_tx ORDSYS.SI_Texture,
    CONSTRAINT pk_ground_plan PRIMARY KEY(id),
    CONSTRAINT fk_property FOREIGN KEY(id_property) REFERENCES property(id) ON DELETE CASCADE
);


CREATE SEQUENCE ground_plan_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE person(
    id NUMBER NOT NULL,
    firstname VARCHAR(32) NOT NULL,
    lastname VARCHAR(32) NOT NULL,
    street VARCHAR(32) NOT NULL,
    city VARCHAR(32) NOT NULL,
    psc VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY(id)
);

CREATE SEQUENCE person_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE owner(
    id_owner NUMBER NOT NULL,
    id_property NUMBER NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    CONSTRAINT pk_owner PRIMARY KEY(id_owner, id_property),
    CONSTRAINT fk_owner FOREIGN KEY(id_owner) REFERENCES person(id) ON DELETE CASCADE,
    CONSTRAINT fk_property1 FOREIGN KEY(id_property) REFERENCES property(id) ON DELETE CASCADE
);


/*    
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
	'PROPERTY', 'GEOMETRY',
	-- souradnice LONGITUDE a LATITUDE Ceskej republiky s dostatocnou presnostou
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('LONGITUDE',11,19,0.000001),SDO_DIM_ELEMENT('LATITUDE',47, 52,0.000001)),
	2065);
*/

INSERT INTO USER_SDO_GEOM_METADATA VALUES (
    'PROPERTY', 'GEOMETRY',
    --suradnice Brno    
    SDO_DIM_ARRAY(SDO_DIM_ELEMENT('LONGITUDE',49.216852, 49.242859, 0.000001), SDO_DIM_ELEMENT('LATITUDE', 16.626813, 16.629612, 0.000001)),
    8307);                                       

-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;

CREATE INDEX property_map_index ON property(geometry) indextype is MDSYS.SPATIAL_INDEX;

--inserts
INSERT INTO PROPERTY (ID,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PRICE, PROPERTY_DESCRIPTION) VALUES (
    property_seq.nextval,'house',SDO_GEOMETRY(2003, 
                                    8307, 
                                    NULL, --2D polygon
                                    SDO_ELEM_INFO_ARRAY(1, 1003, 3),
                                    SDO_ORDINATE_ARRAY(16.603033, 49.203700, 16.603418, 49.203500)
                                ),
    'The Czechoslovak Hussite Church', 5000000, 'Botanická 590/1, 602 00 Brno-st?ed-Veve?í');
    
INSERT INTO PROPERTY (ID,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PRICE, PROPERTY_DESCRIPTION) VALUES (
    PROPERTY_SEQ.NEXTVAL, 'prefab', SDO_GEOMETRY(
                                        2003, 
                                        8307, 
                                        NULL, --2D polygon
                                        SDO_ELEM_INFO_ARRAY(1, 1003, 3),
                                        SDO_ORDINATE_ARRAY(16.584492, 49.229635, 16.584727, 49.229434)
                                    ),
    'Panelák Her?íkova', 0, 'Her?íkova 2498/18');

INSERT INTO PROPERTY (ID,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PRICE, PROPERTY_DESCRIPTION) VALUES (
    PROPERTY_SEQ.NEXTVAL, 'apartment', SDO_GEOMETRY(
                                        2001, 
                                        8307, 
                                        SDO_POINT_TYPE(16.584575, 49.229547, NULL), 
                                        NULL, 
                                        NULL),
    'Byt Her?íkova', 1000000, 'Her?íkova 2498/18');

INSERT INTO PERSON (ID, FIRSTNAME, LASTNAME, STREET, CITY, PSC, EMAIL) VALUES (
    PERSON_SEQ.NEXTVAL, 'Jirka', 'Babiš', 'Vlhká 13', 'Brno', '60200', 'jirka@gmail.com'
);

INSERT INTO PERSON (ID, FIRSTNAME, LASTNAME, STREET, CITY, PSC, EMAIL) VALUES (
    PERSON_SEQ.NEXTVAL, 'Michal', 'Zeman', 'Kolejní 2', 'Brno', '60204', 'michal@seznam.cz'
);

INSERT INTO PERSON (ID, FIRSTNAME, LASTNAME, STREET, CITY, PSC, EMAIL) VALUES (
    PERSON_SEQ.NEXTVAL, 'Václav', 'Novák', 'Ceská 56', 'Brno' ,'60205', 'novak@gmaik.com'
);

INSERT INTO OWNER (ID_OWNER, ID_PROPERTY, DATE_FROM, DATE_TO) VALUES (
    1, 1, DATE '2000-12-24', DATE '2017-08-28'
);

INSERT INTO OWNER (ID_OWNER, ID_PROPERTY, DATE_FROM, DATE_TO) VALUES (
    2, 3, DATE '2008-05-12', DATE '2009-03-20'
);

INSERT INTO OWNER (ID_OWNER, ID_PROPERTY, DATE_FROM, DATE_TO) VALUES (
    3, 2, DATE '2007-01-01', DATE '2008-06-17'
);

