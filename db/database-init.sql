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
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (PROPERTY_SEQ.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.607206, 49.191432, 16.607436, 49.191344, 16.607542, 49.191457, 16.60731, 49.19155, 16.607206, 49.191432)),'dom1','desc1');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (PROPERTY_SEQ.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.603125, 49.203747, 16.603033, 49.2037, 16.603319, 49.203454, 16.603418, 49.2035, 16.603125, 49.203747)),'dom2','desc2');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (PROPERTY_SEQ.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.606089, 49.191362, 16.606155, 49.191373, 16.606149, 49.191385, 16.606294, 49.191429, 16.606284, 49.191449, 16.606266, 49.191457, 16.606235, 49.191536, 16.606241, 49.191543, 16.606256, 49.191543, 16.606284, 49.191571, 16.606243, 49.191595, 16.606342, 49.191633, 16.606344, 49.191604, 16.6064, 49.191604, 16.606405, 49.191644, 16.6065, 49.191605, 16.606456, 49.191563, 16.606534, 49.191533, 16.60658, 49.191566, 16.606637, 49.191545, 16.606727, 49.191647, 16.606717, 49.191673, 16.6064, 49.191805, 16.606376, 49.191805, 16.605984, 49.191663, 16.605984, 49.191622, 16.606089, 49.191362)),'dom3','desc3');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (PROPERTY_SEQ.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1, 11, 2003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.605299, 49.192204, 16.605467, 49.191726, 16.60592, 49.191791, 16.605744, 49.192274, 16.605299, 49.192204, 16.6055, 49.192087, 16.605573, 49.191874, 16.605722, 49.191899, 16.605647, 49.192108, 16.6055, 49.192087)),'dom4','diera');

Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Jozef','Mak','street','city','psc','email');        --1
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Vladimir','Pes','street','city','psc','email');     --2
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Milos','Milos','street','city','psc','email');      --3
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Karol','Zeman','street','city','psc','email');      --4
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Aneta','Nováková','street','city','psc','email');   --5
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Jana','Navrátilová','street','city','psc','email'); --6
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Monika','Nová','street','city','psc','email');      --7
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Jozef','Starý','street','city','psc','email');      --8
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (PERSON_SEQ.nextval,'Lucia','Malá','street','city','psc','email');       --9

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,1,TO_DATE('2010-1-1','yyyy-mm-dd'),TO_DATE('2015-1-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,1,TO_DATE('2015-1-2','yyyy-mm-dd'),TO_DATE('2016-12-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,1,TO_DATE('2017-1-1','yyyy-mm-dd'),TO_DATE('2017-6-24','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,1,TO_DATE('2017-6-25','yyyy-mm-dd'),TO_DATE('2017-11-7','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (8,2,TO_DATE('2010-1-1','yyyy-mm-dd'),TO_DATE('2011-11-17','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,2,TO_DATE('2012-1-1','yyyy-mm-dd'),TO_DATE('2014-8-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,2,TO_DATE('2014-1-1','yyyy-mm-dd'),TO_DATE('2015-1-19','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,2,TO_DATE('2015-1-20','yyyy-mm-dd'),TO_DATE('2015-11-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,2,TO_DATE('2015-12-22','yyyy-mm-dd'),TO_DATE('2016-8-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,3,TO_DATE('2016-9-1','yyyy-mm-dd'),TO_DATE('2017-7-11','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (9,3,TO_DATE('2010-1-1','yyyy-mm-dd'),TO_DATE('2012-12-14','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,3,TO_DATE('2013-1-1','yyyy-mm-dd'),TO_DATE('2015-12-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,3,TO_DATE('2015-1-1','yyyy-mm-dd'),TO_DATE('2017-6-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,3,TO_DATE('2016-8-1','yyyy-mm-dd'),TO_DATE('2016-12-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,3,TO_DATE('2017-1-1','yyyy-mm-dd'),TO_DATE('2017-6-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,3,TO_DATE('2017-7-30','yyyy-mm-dd'),TO_DATE('2017-11-7','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                800000,  TO_DATE('2010-1-1','yyyy-mm-dd'), TO_DATE('2011-6-30','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                900000,  TO_DATE('2011-7-1','yyyy-mm-dd'), TO_DATE('2011-7-31','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1200000,  TO_DATE('2011-8-1','yyyy-mm-dd'), TO_DATE('2011-11-30','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1000000,  TO_DATE('2011-12-1','yyyy-mm-dd'), TO_DATE('2014-4-30','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1400000,  TO_DATE('2014-5-1','yyyy-mm-dd'), TO_DATE('2014-8-31','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1350000,  TO_DATE('2014-9-1','yyyy-mm-dd'), TO_DATE('2014-12-20','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1250000,  TO_DATE('2014-12-21','yyyy-mm-dd'), TO_DATE('2015-1-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1150000,  TO_DATE('2015-1-2','yyyy-mm-dd'), TO_DATE('2016-12-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1280000,  TO_DATE('2016-12-2','yyyy-mm-dd'), TO_DATE('2017-6-24','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 1,
                1380000,  TO_DATE('2017-6-25','yyyy-mm-dd'), TO_DATE('2017-11-7','yyyy-mm-dd'));
                
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                750000,  TO_DATE('2010-1-1','yyyy-mm-dd'), TO_DATE('2012-7-31','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                820000,  TO_DATE('2012-8-1','yyyy-mm-dd'), TO_DATE('2012-11-20','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                740000,  TO_DATE('2012-11-21','yyyy-mm-dd'), TO_DATE('2012-11-30','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                900000,  TO_DATE('2012-12-1','yyyy-mm-dd'), TO_DATE('2013-5-15','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                1100000,  TO_DATE('2013-5-16','yyyy-mm-dd'), TO_DATE('2013-10-28','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                950000,  TO_DATE('2013-10-29','yyyy-mm-dd'), TO_DATE('2014-12-20','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                1050000,  TO_DATE('2014-12-21','yyyy-mm-dd'), TO_DATE('2015-1-8','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                1250000,  TO_DATE('2015-1-9','yyyy-mm-dd'), TO_DATE('2016-12-20','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                1380000,  TO_DATE('2016-12-21','yyyy-mm-dd'), TO_DATE('2017-4-21','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 2,
                1180000,  TO_DATE('2017-4-22','yyyy-mm-dd'), TO_DATE('2017-11-7','yyyy-mm-dd'));
                
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                850000,  TO_DATE('2010-1-1','yyyy-mm-dd'), TO_DATE('2011-8-28','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                970000,  TO_DATE('2011-8-29','yyyy-mm-dd'), TO_DATE('2012-4-20','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1120000,  TO_DATE('2012-4-21','yyyy-mm-dd'), TO_DATE('2012-11-22','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                900000,  TO_DATE('2012-11-23','yyyy-mm-dd'), TO_DATE('2013-6-17','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1410000,  TO_DATE('2013-6-18','yyyy-mm-dd'), TO_DATE('2013-11-25','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1150000,  TO_DATE('2013-11-26','yyyy-mm-dd'), TO_DATE('2014-8-7','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1250000,  TO_DATE('2014-8-8','yyyy-mm-dd'), TO_DATE('2015-3-7','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1350000,  TO_DATE('2015-3-8','yyyy-mm-dd'), TO_DATE('2016-12-4','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1280000,  TO_DATE('2016-12-5','yyyy-mm-dd'), TO_DATE('2017-1-18','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 3,
                1240000,  TO_DATE('2017-1-19','yyyy-mm-dd'), TO_DATE('2017-11-7','yyyy-mm-dd'));

-- Spatial selects --------------------------------------------------------------------------------------------------------------
-- Return land area of property
SELECT SDO_GEOM.SDO_AREA(PR.geometry,1,'unit=SQ_M') FROM property PR WHERE id_property=1;

-- Returns length of property
SELECT SDO_GEOM.SDO_LENGTH(PR.geometry,1,'unit=M') FROM property PR WHERE id_property=1;

-- Retrun SUM of all land areas of properties which are owned by one person
SELECT O.id_owner, P.lastname, P.firstname, SUM(SDO_GEOM.SDO_AREA(PR.geometry,1,'unit=SQ_M') )
FROM property PR JOIN owner O ON (PR.id_property=O.id_property) JOIN person P ON (P.id_person=O.id_owner) 
GROUP BY O.id_owner, P.lastname, P.firstname
ORDER BY P.lastname, P.firstname;

-- Returns owner of N properties with SUM of all land area in that time interval
SELECT O.id_owner, P.lastname, P.firstname, COUNT(*) as properties, SUM(SDO_GEOM.SDO_AREA(PR.geometry,1,'unit=SQ_M') ) as area
FROM property PR JOIN owner O ON (PR.id_property=O.id_property) JOIN person P ON (P.id_person=O.id_owner) 
WHERE CURRENT_DATE  BETWEEN O.valid_from AND O.valid_to AND CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to
GROUP BY O.id_owner, P.lastname, P.firstname
ORDER BY P.lastname, P.firstname;


-- Finding closest property to PR1(actual position on map), which is available right now
SELECT P.id_property, P.distance
FROM (SELECT /*+ ORDERED */ PR2.id_property AS id_property, MDSYS.SDO_NN_DISTANCE(1) as distance
        FROM property PR1, property PR2
        WHERE PR1.id_property=2 AND PR1.id_property <> PR2.id_property AND PR2.property_type <> 'land' AND
        MDSYS.SDO_NN(PR2.geometry, PR1.geometry, 'UNIT=meter', 1) = 'TRUE'
        ORDER BY distance) P, 
     (SELECT DISTINCT PR.id_property
        FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND
        CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR O.id_owner IS NULL ) p_available
WHERE P.id_property=p_available.id_property AND ROWNUM = 1
ORDER BY P.distance ;



-- Finding closest properties to PR1(actual position on map)
SELECT /*+ ORDERED */ PR1.id_property, PR2.id_property, MDSYS.SDO_NN_DISTANCE(1) as distance
FROM property PR1, property PR2
WHERE PR1.id_property=2 AND PR1.id_property <> PR2.id_property AND PR2.property_type <> 'land' AND 
MDSYS.SDO_NN(PR2.geometry, PR1.geometry, 'SDO_NUM_RES=2 UNIT=meter', 1) = 'TRUE'
ORDER BY distance;


-- Select neighbours of property
SELECT /* ORDERED*/  PR2.id_property FROM property PR1, property PR2
WHERE PR1.id_property = 1 AND PR1.id_property <> PR2.id_property AND PR2.property_type<>'land' AND
MDSYS.SDO_RELATE(PR1.geometry, PR2.geometry, 'mask=touch') = 'TRUE';
---------------------------------------------------------------------------------------------------------------------------------
-- Temporal selects -------------------------------------------------------------------------------------------------------------
-- Selects history of one property
SELECT PP.id_property, PP.price, PP.valid_from, PP.valid_to FROM property_price PP WHERE PP.id_property=1;


-- Selects properties, which are available in current date
SELECT P.id_property FROM property P where p.id_property IN(
SELECT DISTINCT PR.id_property
    FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND
        CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR (O.id_owner IS NULL));

-- Selects properties similar ground_plan, which doesn't have owner in desired time period
SELECT src.id_ground_plan as source, dst.id_ground_plan as similar_properties , SI_ScoreByFtrList (
new SI_FeatureList(
src.img_ac, 0.3, src.img_ch, 0.3 ,
src.img_pc , 0.1 , src.img_tx , 0.3 ) , dst.img_si ) as similarity
FROM ground_plan src , ground_plan dst
WHERE src.id_ground_plan <> dst.id_ground_plan AND src.id_ground_plan = 1 AND dst.id_property IN
(SELECT DISTINCT PR.id_property
    FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND
        CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR (O.id_owner IS NULL))
ORDER BY similarity ASC;


-- Temporal database abstraction -> proceures, triggers, packages ---------------------------------------------------------------
    
-- Package holds data after row level trigger and statement level trigger uses them
CREATE OR REPLACE PACKAGE package_property_price AS 
    id_price property_price.id_price%TYPE;
    id_property property_price.id_property%TYPE;
    price property_price.price%TYPE;
    valid_from property_price.valid_from%TYPE;
    valid_to    property_price.valid_to%TYPE;
END;
/

-- Package holds data after row level trigger and statement level trigger uses them
CREATE OR REPLACE PACKAGE package_owner AS
    id_owner owner.id_owner%TYPE;
    id_property owner.id_property%TYPE;
    valid_from owner.valid_from%TYPE;
    valid_to owner.valid_to%TYPE;
END;
/

-- Row level select adds data to it's package
CREATE OR REPLACE TRIGGER trigger_insert_package_price AFTER INSERT OR UPDATE ON property_price
FOR EACH ROW
BEGIN
    package_property_price.id_price:=:NEW.id_price;
    package_property_price.id_property:=:NEW.id_property;
    package_property_price.price:=:NEW.price;
    package_property_price.valid_from:=:NEW.valid_from;
    package_property_price.valid_to:=:NEW.valid_to;    
END;
/
-- Row level select adds data to it's package
CREATE OR REPLACE TRIGGER trigger_insert_package_owner AFTER INSERT OR UPDATE ON owner
FOR EACH ROW
BEGIN
    package_owner.id_owner:=:NEW.id_owner;
    package_owner.id_property:=:NEW.id_property;
    package_owner.valid_from:=:NEW.valid_from;
    package_owner.valid_to:=:NEW.valid_to;    
END;
/

--Sticks together temporal data with same date boundaries on property_price
CREATE OR REPLACE TRIGGER temporal_trigger_property_price AFTER INSERT OR UPDATE ON property_price 
DECLARE
    old_from DATE;
    old_to DATE;
    old_id NUMBER;
BEGIN
    BEGIN
        SELECT id_price, valid_from INTO old_id, old_from FROM property_price WHERE price=package_property_price.price AND id_property=package_property_price.id_property AND
        (valid_to + 1)=package_property_price.valid_from;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                old_from:= NULL;
                old_id:= NULL;
        END;
     
    IF old_from IS NOT NULL THEN
        --Dbms_Output.Put_Line('FROM');
        --Dbms_Output.Put_Line(old_id);

        UPDATE property_price SET valid_from=old_from where id_price=package_property_price.id_price;
        DELETE FROM property_price WHERE id_price=old_id;
    END IF;

    BEGIN
        SELECT id_price, valid_to INTO old_id, old_to FROM property_price WHERE price=package_property_price.price AND id_property=package_property_price.id_property AND
        (valid_from - 1)=package_property_price.valid_to;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                old_to:= NULL;
                old_id:= NULL;
        END;
     
     IF old_to IS NOT NULL THEN
        --Dbms_Output.Put_Line('TO');
        --Dbms_Output.Put_Line(old_id);

        UPDATE property_price SET valid_to=old_to where id_price=package_property_price.id_price;
        DELETE FROM property_price WHERE id_price=old_id;
    END IF;
    
END;
/

--Sticks together temporal data with same date boundaries on property_price
CREATE OR REPLACE TRIGGER temporal_trigger_owner AFTER INSERT OR UPDATE ON owner 
DECLARE
    old_from DATE;
    old_to DATE;
    
    old_id NUMBER;
    old_id_property NUMBER;
BEGIN
    BEGIN
        SELECT id_owner, id_property, valid_from, valid_to INTO old_id, old_id_property, old_from, old_to FROM owner WHERE id_owner=package_owner.id_owner AND id_property=package_owner.id_property AND
        (valid_to + 1)=package_owner.valid_from;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                old_from:= NULL;
                old_id:= NULL;
                old_id_property:= NULL;
                old_to:=NULL;
        END;
     
    IF old_from IS NOT NULL THEN
        --Dbms_Output.Put_Line('FROM');
        --Dbms_Output.Put_Line(old_id);

        UPDATE owner SET valid_from=old_from where id_owner=package_owner.id_owner AND 
        id_property=package_owner.id_property AND valid_from=package_owner.valid_from AND valid_to=package_owner.valid_to;
        
        DELETE FROM owner WHERE id_owner=old_id AND 
        id_property=old_id_property AND valid_from=old_from AND valid_to=old_to;
    END IF;

    BEGIN
        SELECT id_owner, id_property, valid_from, valid_to INTO old_id, old_id_property, old_from, old_to FROM owner WHERE id_owner=package_owner.id_owner AND id_property=package_owner.id_property AND
        (valid_from - 1)=package_owner.valid_to;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                old_to:= NULL;
                old_id:= NULL;
                old_id_property:= NULL;
                old_from:= NULL;
        END;
     
     IF old_to IS NOT NULL THEN
        --Dbms_Output.Put_Line('TO');
        --Dbms_Output.Put_Line(old_id);

        UPDATE owner SET valid_to=old_to where id_owner=package_owner.id_owner AND 
        id_property=package_owner.id_property AND valid_from=package_owner.valid_from AND valid_to=package_owner.valid_to;
        
        DELETE FROM owner WHERE id_owner=old_id AND 
        id_property=old_id_property AND valid_from=old_from AND valid_to=old_to;
    END IF;   
END;
/

--Insert temporal data to table
CREATE OR REPLACE PROCEDURE temporal_insert(
table_name_param VARCHAR2, id_property_param NUMBER, temporal_param NUMBER, valid_from_param DATE, valid_to_param DATE
)IS
BEGIN
    temporal_delete(table_name_param, id_property_param, valid_from_param, valid_to_param);
    IF table_name_param = 'property_price' THEN
        INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) 
        VALUES(property_price_seq.NEXTVAL, id_property_param, temporal_param, valid_from_param, valid_to_param);
            
    ELSE
        INSERT INTO OWNER (id_owner, id_property, valid_from, valid_to) 
        VALUES ( temporal_param, id_property_param, valid_from_param, valid_to_param);
    END IF;
END;
/
-- Procedure deletes temporal data in table according to index of property
CREATE OR REPLACE PROCEDURE temporal_delete(
table_name_param VARCHAR2, id_property_param NUMBER, valid_from_param DATE, valid_to_param DATE
)IS
    cursor_delete SYS_REFCURSOR;
    old_id NUMBER;
    old_id_property NUMBER;
    old_price NUMBER;
    old_valid_from DATE;
    old_valid_to DATE;
    
BEGIN
    IF table_name_param = 'property_price' THEN
        OPEN cursor_delete FOR SELECT id_price, id_property,
            price, valid_from, valid_to  FROM property_price WHERE id_property=id_property_param AND
            ( (valid_from_param BETWEEN valid_from  AND valid_to) OR
            (valid_to_param BETWEEN valid_from AND valid_to) OR
            valid_from > valid_from_param AND valid_to < valid_to_param);
    
    ELSE
        OPEN cursor_delete FOR SELECT id_owner, id_property,
            valid_from, valid_to  FROM owner WHERE id_property=id_property_param AND
            ( (valid_from_param BETWEEN valid_from  AND valid_to) OR
            (valid_to_param BETWEEN valid_from AND valid_to) OR
            valid_from > valid_from_param AND valid_to < valid_to_param);
    
    END IF;
    
    Dbms_Output.Put_Line('STARTS ');
    LOOP
        IF table_name_param = 'property_price' THEN
            FETCH cursor_delete INTO old_id, old_id_property, old_price, old_valid_from, old_valid_to;
        ELSE
            FETCH cursor_delete INTO old_id, old_id_property, old_valid_from, old_valid_to;
        END IF;
        EXIT WHEN cursor_delete%NOTFOUND;
        
        
        Dbms_Output.Put_Line(old_id);
        --  OLD    /--------/
        --  DELETE /--------/
        IF old_valid_from = valid_from_param AND
            old_valid_to = valid_to_param  THEN
            Dbms_Output.Put_Line('DOT ZERO');
            
            IF table_name_param = 'property_price' THEN
                DELETE FROM property_price WHERE id_price=old_id;
            ELSE
                DELETE FROM owner WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            END IF;
            
        
        --  OLD            /----------------/
        --  DELETE    /---------/
        ELSIF  old_valid_from >= valid_from_param AND
            old_valid_from <= valid_to_param AND
            old_valid_to > valid_to_param  THEN
                
            Dbms_Output.Put_Line('First');
            -- Cut old data
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET valid_from = valid_to_param + 1 WHERE id_price=old_id;
            ELSE
                UPDATE owner SET valid_from = valid_to_param + 1 WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            END IF;
            
       
        --  OLD    /----------------/
        --  DELETE             /---------/ 
        ELSIF old_valid_to >= valid_from_param AND 
              old_valid_to <= valid_to_param  AND
              old_valid_from < valid_from_param THEN
                
            Dbms_Output.Put_Line('Second');
            -- Cut old data
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET valid_to = valid_from_param - 1 WHERE id_price=old_id;
            ELSE
                UPDATE owner SET valid_to = valid_from_param - 1 WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            END IF;
            
        --  OLD    /----------------/
        --  DELETE     /--------/           
        ELSIF old_valid_from < valid_from_param AND 
                old_valid_to > valid_to_param THEN
            --   Cut first part
            --  Insert last part  
            Dbms_Output.Put_Line('THIRD');
      
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET valid_to = valid_from_param - 1 WHERE id_price=old_id;        
           
                INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to)
                    VALUES(property_price_seq.NEXTVAL, old_id_property,
                        old_price, valid_to_param + 1, old_valid_to);
            ELSE
                UPDATE owner SET valid_to = valid_from_param - 1 WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;        

                INSERT INTO owner(id_owner, id_property, valid_from, valid_to)
                    VALUES(old_id, old_id_property, valid_to_param + 1, old_valid_to);
            END IF;
        --  OLD        /----/
        --  DELETE /--------/ 
        --  OLD    /----/
        --  DELETE /--------/ 
        ELSE
            Dbms_Output.Put_Line('FOURTH');
            
            IF table_name_param = 'property_price' THEN
                DELETE FROM property_price WHERE id_price=old_id;
            ELSE
                DELETE FROM owner WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            END IF;      
        END IF;
    END LOOP;
    CLOSE cursor_delete;
END;
/

-- Procedura aktualizuje temporalne udaje v tabulke
CREATE OR REPLACE PROCEDURE temporal_update(
table_name_param VARCHAR2, id_property_param NUMBER, temporal_param NUMBER, valid_from_param DATE, valid_to_param DATE
)IS
    cursor_update SYS_REFCURSOR;
    old_id NUMBER;
    old_id_property NUMBER;
    old_temporal NUMBER;
    old_valid_from DATE;
    old_valid_to DATE;
    
BEGIN
    IF table_name_param = 'property_price' THEN
        OPEN cursor_update FOR SELECT id_price, id_property,
            price, valid_from, valid_to  FROM property_price WHERE id_property=id_property_param AND
            ( (valid_from_param BETWEEN valid_from  AND valid_to) OR
            (valid_to_param BETWEEN valid_from AND valid_to) OR
            valid_from > valid_from_param AND valid_to < valid_to_param);
    
    ELSE
        OPEN cursor_update FOR SELECT id_owner, id_property,
            valid_from, valid_to  FROM owner WHERE id_property=id_property_param AND
            ( (valid_from_param BETWEEN valid_from  AND valid_to) OR
            (valid_to_param BETWEEN valid_from AND valid_to) OR
            valid_from > valid_from_param AND valid_to < valid_to_param);
    
    END IF;
    
    Dbms_Output.Put_Line('STARTS ');
    LOOP
        IF table_name_param = 'property_price' THEN
            FETCH cursor_update INTO old_id, old_id_property, old_temporal, old_valid_from, old_valid_to;
        ELSE
            FETCH cursor_update INTO old_id, old_id_property, old_valid_from, old_valid_to;
        END IF;
        EXIT WHEN cursor_update%NOTFOUND;
        
        
        Dbms_Output.Put_Line(old_id);
        --  OLD    /--------/
        --  UPDATE /--------/
        IF old_valid_from = valid_from_param AND
            old_valid_to = valid_to_param  THEN
            Dbms_Output.Put_Line('DOT ZERO');
            
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET price = temporal_param WHERE id_price=old_id;
            ELSE
                UPDATE owner SET id_owner = temporal_param WHERE id_owner = old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            END IF;
        --  OLD            /----------------/
        --  UPDATE    /---------/
        ELSIF  old_valid_from >= valid_from_param AND
            old_valid_from <= valid_to_param AND
            old_valid_to > valid_to_param  THEN
                
            Dbms_Output.Put_Line('First');
            -- Cut old data
            -- Insert updated data
            IF table_name_param = 'property_price' THEN
                 UPDATE property_price SET valid_from = valid_to_param + 1 WHERE id_price=old_id;
            
                INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to)
                    VALUES(property_price_seq.NEXTVAL, old_id_property,
                        temporal_param, old_valid_from, valid_to_param);
            ELSE
                UPDATE owner SET valid_from = valid_to_param + 1 WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            
                INSERT INTO owner(id_owner, id_property, valid_from, valid_to)
                    VALUES(temporal_param, old_id_property, old_valid_from, valid_to_param);
            END IF;
           
        --  OLD    /----------------/
        --  UPDATE             /---------/ 
        ELSIF old_valid_to >= valid_from_param AND 
              old_valid_to <= valid_to_param  AND
              old_valid_from < valid_from_param THEN
                
            Dbms_Output.Put_Line('Second');
            -- Cut old data
            -- Insert updated data
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET valid_to = valid_from_param - 1 WHERE id_price=old_id;
            
                INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to)
                    VALUES(property_price_seq.NEXTVAL, old_id_property,
                    temporal_param, valid_from_param, old_valid_to);
            ELSE
                UPDATE owner SET valid_to = valid_from_param - 1 WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            
                INSERT INTO owner(id_owner, id_property, valid_from, valid_to)
                    VALUES(temporal_param, old_id_property, valid_from_param, old_valid_to);
            END IF;
            
            
        --  OLD    /----------------/
        --  UPDATE     /--------/           
        ELSIF old_valid_from < valid_from_param AND 
                old_valid_to > valid_to_param THEN
            
                
            Dbms_Output.Put_Line('THIRD');
            --   Cut first part
            --  Insert last part
             -- Insert updated data
             
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET valid_to = valid_from_param - 1 WHERE id_price=old_id;        
            
                INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to)
                    VALUES(property_price_seq.NEXTVAL, old_id_property,
                    old_temporal, valid_to_param + 1, old_valid_to);
            
                INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to)
                    VALUES(property_price_seq.NEXTVAL, old_id_property,
                    temporal_param, valid_from_param, valid_to_param);
            ELSE
                UPDATE owner SET valid_to = valid_from_param - 1 WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;       
            
                INSERT INTO owner(id_owner, id_property, valid_from, valid_to)
                    VALUES(temporal_param, old_id_property, valid_to_param + 1, old_valid_to);
            
                INSERT INTO owner(id_owner, id_property, valid_from, valid_to)
                    VALUES(temporal_param, old_id_property, valid_from_param, valid_to_param);            
            END IF;
            
        
        --  OLD        /----/
        --  UPDATE /--------/ 
        --  OLD    /----/
        --  UPDATE /--------/ 
        ELSE
            Dbms_Output.Put_Line('FOURTH');
            
            IF table_name_param = 'property_price' THEN
                UPDATE property_price SET price = temporal_param WHERE id_price=old_id;
            ELSE
                UPDATE owner SET id_owner = temporal_param WHERE id_owner=old_id AND 
                    id_property=old_id_property AND valid_from = old_valid_from AND valid_to=old_valid_to;
            END IF;
            
        END IF;
    END LOOP;
    CLOSE cursor_update;
END;
/


-- Procedure rotates image 90/270 degrees and saves changes
CREATE OR REPLACE PROCEDURE rotate_image( 
id_param NUMBER,
direction_param BOOLEAN
)IS
    image ORDSYS.ORDImage;
BEGIN
    SELECT img INTO image FROM ground_plan where id_ground_plan=id_param;

     IF direction_param THEN
        image.process('rotate=90');
     ELSE
        image.process('rotate=270');
     END IF;
    
    UPDATE ground_plan SET img=image where id_ground_plan=id_param;
    
    COMMIT;
END;
/