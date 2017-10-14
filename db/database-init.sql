DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'PROPERTY' AND COLUMN_NAME = 'GEOMETRY';

DROP TABLE property;
DROP TABLE ground_plan;
DROP TABLE owner;
DROP TABLE person;

DROP SEQUENCE "ground_plan_seq";
DROP SEQUENCE "property_seq";
DROP SEQUENCE "person_seq";

DROP INDEX property_map_index;

CREATE TABLE property(
    id NUMBER NOT NULL,
    -- ENUM('dom', panelak, 'apartment', terrace-house')
    property_type VARCHAR(16) CHECK( property_type in ('house', 'prefab', 'apartment', 'terrace-house')),
    geometry SDO_GEOMETRY,
    property_name VARCHAR(32) NOT NULL,
    price NUMBER NOT NULL,
    property_description VARCHAR(64) NOT NULL,
    CONSTRAINT pk_property PRIMARY KEY(id));

CREATE SEQUENCE "property_seq" START WITH 1 INCREMENT BY 1;

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


CREATE SEQUENCE "ground_plan_seq" START WITH 1 INCREMENT BY 1;

CREATE TABLE person(
    id NUMBER NOT NULL,
    fistname VARCHAR(32) NOT NULL,
    lastname VARCHAR(32) NOT NULL,
    street VARCHAR(32) NOT NULL,
    city VARCHAR(32) NOT NULL,
    psc VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY(id)
);

CREATE SEQUENCE "person_seq" START WITH 1 INCREMENT BY 1;

CREATE TABLE owner(
    id_owner NUMBER NOT NULL,
    id_property NUMBER NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    CONSTRAINT pk_owner PRIMARY KEY(id_owner, id_property),
    CONSTRAINT fk_owner FOREIGN KEY(id_owner) REFERENCES person(id) ON DELETE CASCADE,
    CONSTRAINT fk_property1 FOREIGN KEY(id_property) REFERENCES property(id) ON DELETE CASCADE
);
    
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
	'PROPERTY', 'GEOMETRY',
	-- souradnice LONGITUDE a LATITUDE Ceskej republiky s dostatocnou presnostou
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('LONGITUDE',11,19,0.000001),SDO_DIM_ELEMENT('LATITUDE',47, 52,0.000001)),
	2065);

-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;

CREATE INDEX property_map_index ON property(geometry) indextype is MDSYS.SPATIAL_INDEX;