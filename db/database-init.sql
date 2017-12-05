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


-- create tables

CREATE TABLE property(
    id_property NUMBER NOT NULL,
    -- ENUM('dom', panelak, 'apartment', terrace-house')
    property_type VARCHAR(16) CHECK( property_type in ('house', 'prefab', 'apartment', 'terrace_house', 'land')),
    geometry SDO_GEOMETRY,
    property_name VARCHAR(32) NOT NULL,
    property_description VARCHAR(512) NOT NULL,
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
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.607206, 49.191432, 16.607436, 49.191344, 16.607542, 49.191457, 16.60731, 49.19155, 16.607206, 49.191432)),'Diecezni muzeum','Petrov 275/1');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'apartment',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.6047915816307, 49.1924667690904, 16.604811042876, 49.192472037187, 16.6048191039948, 49.1924847554995, 16.604811042886, 49.1924974738153, 16.6047915816307, 49.1925027419152, 16.6047721203754, 49.1924974738153, 16.6047640592666, 49.1924847554995, 16.6047721203854, 49.192472037187, 16.6047915816307, 49.1924667690904)),'Byt v Atrium Apartments','4-izbovy byt v centre mesta');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.606089, 49.191362, 16.606155, 49.191373, 16.606149, 49.191385, 16.606294, 49.191429, 16.606284, 49.191449, 16.606266, 49.191457, 16.606235, 49.191536, 16.606241, 49.191543, 16.606256, 49.191543, 16.606284, 49.191571, 16.606243, 49.191595, 16.606342, 49.191633, 16.606344, 49.191604, 16.6064, 49.191604, 16.606405, 49.191644, 16.6065, 49.191605, 16.606456, 49.191563, 16.606534, 49.191533, 16.60658, 49.191566, 16.606637, 49.191545, 16.606727, 49.191647, 16.606717, 49.191673, 16.6064, 49.191805, 16.606376, 49.191805, 16.605984, 49.191663, 16.605984, 49.191622, 16.606089, 49.191362)),'Biskupska 569/4','Pivnice U Kocoura, Pametni deska Pavel a Hugo Haas, Bishop Apartments');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.605299, 49.192204, 16.605467, 49.191726, 16.60592, 49.191791, 16.605744, 49.192274, 16.605299, 49.192204)),'Silingrovo nam. 265/2','Palazzo Restaurant, Barcelo Brno Palace');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'house',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.607886, 49.192957, 16.608316, 49.193081, 16.608278, 49.193144, 16.608650, 49.193253 ,16.608433, 49.193623, 16.607752, 49.193358, 16.607886, 49.192957)),'Budova Panska','Restaurace, obchody, KAM v Brne - kulturni servis, Centrala Cestovniho Ruchu - Jizni Morava');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'land',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.6067750751972, 49.1899758867648, 16.6068998856306, 49.1899460863366, 16.6069870574236, 49.1900903487265, 16.6067697107792, 49.1901446905119, 16.6066838800907, 49.1900008665217, 16.6067750751972, 49.1899758867648)),'Kopecna 197/6','Pozemok na predaj');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'land',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.6065786033869, 49.1899364450178, 16.6066363585949, 49.1899215447941, 16.6066856441855, 49.1900007284645, 16.6065725684166, 49.1900272420524, 16.6065276414156, 49.1899504687537, 16.6065786033869, 49.1899364450178)),'Investicni aukce','Volny pozemok');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'land',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.6067697107792, 49.1898805691557, 16.6068109935999, 49.1898922921359, 16.6068569702863, 49.189902262145, 16.606890330261, 49.1899072125971, 16.6069236902356, 49.1899134777749, 16.6068985445261, 49.189945708781, 16.6068449003458, 49.1899595336221, 16.6066858917475, 49.1900005091951, 16.6066505201161, 49.1899420730579, 16.6066349716857, 49.1899223146704, 16.606605341658, 49.1898861221932, 16.6066280147061, 49.1898813735842, 16.6066506877542, 49.1898783779441, 16.6067246161401, 49.1898619438543, 16.6067697107792, 49.1898805691557)),'Kopecna 217/8','Brno-stred pozemok na predaj.');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'land',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.6082207858562, 49.1908457884137, 16.6083140803337, 49.1908799706346, 16.60809279809, 49.1911552634121, 16.6079552471638, 49.1911035521256, 16.6081295907497, 49.1908528001532, 16.6082207858562, 49.1908457884137)),'Pozemok pod Petrovom','Restaurace L Eau Vive');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'prefab',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 3), MDSYS.SDO_ORDINATE_ARRAY(16.603949368, 49.1919098085251, 16.6041807962417, 49.1922043798399)),'Panelak Pekarska','Vyborna poloha v centre mesta');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'terrace_house',MDSYS.SDO_GEOMETRY(2002, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 2, 1), MDSYS.SDO_ORDINATE_ARRAY(16.603399515152, 49.1927389220063, 16.6044597459793, 49.1927863360643)),'Rodinne domy Pelicova','Radove domy na ulici Pelicova');
Insert into PROPERTY (ID_PROPERTY,PROPERTY_TYPE,GEOMETRY,PROPERTY_NAME,PROPERTY_DESCRIPTION) values (property_seq.nextval,'apartment',MDSYS.SDO_GEOMETRY(2003, 8307, NULL, MDSYS.SDO_ELEM_INFO_ARRAY(1, 1003, 1), MDSYS.SDO_ORDINATE_ARRAY(16.6048935055733, 49.1924089241176, 16.6049129667958, 49.1924141922141, 16.6049210279052, 49.1924269105267, 16.6049129668058, 49.1924396288424, 16.6048935055733, 49.1924448969423, 16.6048740443407, 49.1924396288424, 16.6048659832414, 49.1924269105267, 16.6048740443508, 49.1924141922141, 16.6048935055733, 49.1924089241176)),'Byt v Atrium Apartments','Zastavka mhd a krcma rovno pri budove bytu');

Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Jozef','Mak','Veveri 52','Brno','60200','jozef@mak.cz');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Vladimir','Pes','Zemedelska 68','Brno','61300','pes.vladimir@gmail.com');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Milos','Milos','Tlumacovska 13','Praha','15500','milos@seznam.cz');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Karol','Zeman','Panska 1','Praha','11000','kzeman@gmail.com');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Aneta','Nová','Orli 20','Brno','60200','anetan@gmail.com');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Jana','Navrátilová','Novobranska 3','Brno','60200','jana@navratilova.cz');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Monika','Nováková','Tisnovska 149','Brno','61400','novakova.m@seznam.cz');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Jozef','Starý','Koterova 2','Brno','61300','jstary@starysro.cz');
Insert into PERSON (ID_PERSON,FIRSTNAME,LASTNAME,STREET,CITY,PSC,EMAIL) values (person_seq.nextval,'Lucia','Malá','Travniky 15','Brno','61300','mala@gmail.com');

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,1,TO_DATE('2010-1-1','yyyy-mm-dd'),TO_DATE('2015-1-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,1,TO_DATE('2015-1-2','yyyy-mm-dd'),TO_DATE('2016-12-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,1,TO_DATE('2017-1-1','yyyy-mm-dd'),TO_DATE('2017-6-24','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,1,TO_DATE('2017-6-25','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (8,2,TO_DATE('2010-1-1','yyyy-mm-dd'),TO_DATE('2011-11-17','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,2,TO_DATE('2012-1-1','yyyy-mm-dd'),TO_DATE('2014-8-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,2,TO_DATE('2014-1-1','yyyy-mm-dd'),TO_DATE('2015-1-19','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,2,TO_DATE('2015-1-20','yyyy-mm-dd'),TO_DATE('2015-11-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,2,TO_DATE('2015-12-22','yyyy-mm-dd'),TO_DATE('2016-8-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,3,TO_DATE('2016-9-1','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (9,3,TO_DATE('2010-1-1','yyyy-mm-dd'),TO_DATE('2012-12-14','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,3,TO_DATE('2013-1-1','yyyy-mm-dd'),TO_DATE('2015-12-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,3,TO_DATE('2015-1-1','yyyy-mm-dd'),TO_DATE('2017-6-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,3,TO_DATE('2016-8-1','yyyy-mm-dd'),TO_DATE('2016-12-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,3,TO_DATE('2017-1-1','yyyy-mm-dd'),TO_DATE('2017-6-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,3,TO_DATE('2017-7-30','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,4,TO_DATE('2016-1-11','yyyy-mm-dd'),TO_DATE('2017-10-18','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,4,TO_DATE('2017-10-18','yyyy-mm-dd'),TO_DATE('2017-10-20','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (7,4,TO_DATE('2017-10-20','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,5,TO_DATE('2013-8-1','yyyy-mm-dd'),TO_DATE('2014-12-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,5,TO_DATE('2014-12-28','yyyy-mm-dd'),TO_DATE('2016-6-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,5,TO_DATE('2016-6-30','yyyy-mm-dd'),TO_DATE('2017-1-1','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,5,TO_DATE('2017-1-1','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,6,TO_DATE('2013-1-1','yyyy-mm-dd'),TO_DATE('2014-11-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,6,TO_DATE('2014-11-28','yyyy-mm-dd'),TO_DATE('2016-4-30','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,6,TO_DATE('2016-4-30','yyyy-mm-dd'),TO_DATE('2017-4-4','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,6,TO_DATE('2017-4-4','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,7,TO_DATE('2012-1-12','yyyy-mm-dd'),TO_DATE('2015-11-11','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,7,TO_DATE('2015-11-11','yyyy-mm-dd'),TO_DATE('2016-4-12','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,7,TO_DATE('2016-4-12','yyyy-mm-dd'),TO_DATE('2017-5-8','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,7,TO_DATE('2017-5-8','yyyy-mm-dd'),TO_DATE('2017-9-14','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,7,TO_DATE('2017-9-14','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (8,8,TO_DATE('2012-1-1','yyyy-mm-dd'),TO_DATE('2016-11-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,8,TO_DATE('2016-11-28','yyyy-mm-dd'),TO_DATE('2017-1-13','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,8,TO_DATE('2017-1-13','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,9,TO_DATE('2014-1-1','yyyy-mm-dd'),TO_DATE('2015-11-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,9,TO_DATE('2015-11-28','yyyy-mm-dd'),TO_DATE('2016-2-13','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (3,9,TO_DATE('2016-2-13','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (4,10,TO_DATE('2015-2-2','yyyy-mm-dd'),TO_DATE('2016-12-28','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,10,TO_DATE('2016-12-28','yyyy-mm-dd'),TO_DATE('2017-2-13','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,10,TO_DATE('2017-2-13','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));

Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (2,11,TO_DATE('2014-3-2','yyyy-mm-dd'),TO_DATE('2015-10-21','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (1,11,TO_DATE('2015-10-21','yyyy-mm-dd'),TO_DATE('2016-4-13','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (5,11,TO_DATE('2016-4-13','yyyy-mm-dd'),TO_DATE('2017-11-22','yyyy-mm-dd'));
Insert into OWNER (ID_OWNER,ID_PROPERTY,VALID_FROM,VALID_TO) values (6,11,TO_DATE('2017-11-22','yyyy-mm-dd'),TO_DATE('9999-12-30','yyyy-mm-dd'));


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
                1380000,  TO_DATE('2017-6-25','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

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
                1180000,  TO_DATE('2017-4-22','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

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
                1240000,  TO_DATE('2017-1-19','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 4,
                1450000,  TO_DATE('2016-1-11','yyyy-mm-dd'), TO_DATE('2016-8-7','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 4,
                1990000,  TO_DATE('2016-12-8','yyyy-mm-dd'), TO_DATE('2017-1-4','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 4,
                1080000,  TO_DATE('2017-5-5','yyyy-mm-dd'), TO_DATE('2017-9-18','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 4,
                1840000,  TO_DATE('2017-10-19','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 5,
                2350000,  TO_DATE('2013-8-1','yyyy-mm-dd'), TO_DATE('2014-8-7','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 5,
                4559000,  TO_DATE('2014-8-7','yyyy-mm-dd'), TO_DATE('2016-8-4','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 5,
                2080000,  TO_DATE('2016-8-4','yyyy-mm-dd'), TO_DATE('2016-12-18','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 5,
                2840000,  TO_DATE('2017-1-19','yyyy-mm-dd'), TO_DATE('2017-5-14','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 5,
                1240000,  TO_DATE('2017-5-14','yyyy-mm-dd'), TO_DATE('2017-9-18','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 5,
                2330000,  TO_DATE('2017-9-18','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 6,
                1490000,  TO_DATE('2013-1-1','yyyy-mm-dd'), TO_DATE('2014-8-8','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 6,
                1790000,  TO_DATE('2014-8-8','yyyy-mm-dd'), TO_DATE('2015-5-4','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 6,
                2450000,  TO_DATE('2015-5-4','yyyy-mm-dd'), TO_DATE('2016-8-18','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 6,
                3840000,  TO_DATE('2016-8-18','yyyy-mm-dd'), TO_DATE('2016-12-14','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 6,
                1000000,  TO_DATE('2016-12-14','yyyy-mm-dd'), TO_DATE('2017-4-18','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 6,
                2220000,  TO_DATE('2017-4-18','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 7,
                2550000,  TO_DATE('2012-1-12','yyyy-mm-dd'), TO_DATE('2013-8-15','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 7,
                2990000,  TO_DATE('2013-8-15','yyyy-mm-dd'), TO_DATE('2014-7-14','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 7,
                4080000,  TO_DATE('2014-7-14','yyyy-mm-dd'), TO_DATE('2015-8-12','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 7,
                1640000,  TO_DATE('2015-8-12','yyyy-mm-dd'), TO_DATE('2016-11-12','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 7,
                1480000,  TO_DATE('2016-11-12','yyyy-mm-dd'), TO_DATE('2016-12-27','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 7,
                2240000,  TO_DATE('2016-12-27','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 8,
                2350000,  TO_DATE('2012-1-1','yyyy-mm-dd'), TO_DATE('2013-7-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 8,
                1990000,  TO_DATE('2013-7-1','yyyy-mm-dd'), TO_DATE('2014-3-24','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 8,
                1280000,  TO_DATE('2014-3-24','yyyy-mm-dd'), TO_DATE('2015-8-11','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 8,
                1540000,  TO_DATE('2015-8-11','yyyy-mm-dd'), TO_DATE('2015-12-11','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 8,
                1780000,  TO_DATE('2016-11-5','yyyy-mm-dd'), TO_DATE('2017-5-22','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 8,
                1530000,  TO_DATE('2017-5-22','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 9,
                2050000,  TO_DATE('2014-1-1','yyyy-mm-dd'), TO_DATE('2014-12-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 9,
                1890000,  TO_DATE('2014-12-1','yyyy-mm-dd'), TO_DATE('2015-3-21','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 9,
                1580000,  TO_DATE('2015-3-21','yyyy-mm-dd'), TO_DATE('2015-8-11','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 9,
                1550000,  TO_DATE('2015-8-11','yyyy-mm-dd'), TO_DATE('2016-8-11','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 9,
                1770000,  TO_DATE('2016-8-11','yyyy-mm-dd'), TO_DATE('2016-12-22','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 9,
                1430000,  TO_DATE('2016-12-22','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 10,
                2450000,  TO_DATE('2015-2-2','yyyy-mm-dd'), TO_DATE('2015-8-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 10,
                1490000,  TO_DATE('2015-8-1','yyyy-mm-dd'), TO_DATE('2015-11-21','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 10,
                1980000,  TO_DATE('2015-11-21','yyyy-mm-dd'), TO_DATE('2016-1-11','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 10,
                1560000,  TO_DATE('2016-1-11','yyyy-mm-dd'), TO_DATE('2016-9-10','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 10,
                1970000,  TO_DATE('2016-9-10','yyyy-mm-dd'), TO_DATE('2016-11-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 10,
                2430000,  TO_DATE('2016-11-1','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 11,
                3450000,  TO_DATE('2014-3-2','yyyy-mm-dd'), TO_DATE('2014-8-14','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 11,
                2490000,  TO_DATE('2014-8-14','yyyy-mm-dd'), TO_DATE('2015-1-21','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 11,
                3980000,  TO_DATE('2015-1-21','yyyy-mm-dd'), TO_DATE('2015-12-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 11,
                2560000,  TO_DATE('2015-12-1','yyyy-mm-dd'), TO_DATE('2016-5-27','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 11,
                2070000,  TO_DATE('2016-5-27','yyyy-mm-dd'), TO_DATE('2017-3-1','yyyy-mm-dd'));
INSERT INTO property_price(id_price, id_property, price, valid_from, valid_to) VALUES(property_price_seq.NEXTVAL, 11,
                2230000,  TO_DATE('2017-3-1','yyyy-mm-dd'), TO_DATE('9999-12-30','yyyy-mm-dd'));

