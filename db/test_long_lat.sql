DROP INDEX property_map_index;
DROP TABLE PROPERTY;
DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'PROPERTY' AND COLUMN_NAME = 'GEOMETRY';


CREATE TABLE property(
    id NUMBER NOT NULL,
    -- ENUM('dom', panelak, 'apartment', terrace-house')
    property_type VARCHAR(16) CHECK( property_type in ('house', 'prefab', 'apartment', 'terrace-house')),
    geometry SDO_GEOMETRY,
    property_name VARCHAR(32) NOT NULL,
    price NUMBER NOT NULL,
    property_description VARCHAR(64) NOT NULL,
    CONSTRAINT pk_property PRIMARY KEY(id));

INSERT INTO USER_SDO_GEOM_METADATA VALUES (
    'PROPERTY', 'GEOMETRY',
    --suradnice Brno    
    SDO_DIM_ARRAY(SDO_DIM_ELEMENT('LONGITUDE',-180, 180, 0.5), SDO_DIM_ELEMENT('LATITUDE', -90, 90, 0.5)),
    8307);                                       

-- Kontrola validity
SELECT property_name, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(geometry, 0.000001) valid FROM PROPERTY;

SELECT p.property_name, p.geometry.ST_IsValid() FROM property p;

CREATE INDEX property_map_index ON property(geometry) indextype is MDSYS.SPATIAL_INDEX;

--inserts
INSERT INTO PROPERTY (ID,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PRICE, PROPERTY_DESCRIPTION) VALUES (
    1,'house',SDO_GEOMETRY(         2001, 
                                    8307, 
                                    SDO_POINT_TYPE(16.603033, 49.203700 ,NULL),
                                    NULL,
                                    NULL
                                ),
    'The Czechoslovak Hussite Church', 5000000, 'Botanická 590/1, 602 00 Brno-st?ed-Veve?í');
    
INSERT INTO PROPERTY (ID,PROPERTY_TYPE, GEOMETRY, PROPERTY_NAME, PRICE, PROPERTY_DESCRIPTION) VALUES (
    2, 'prefab', SDO_GEOMETRY(
                                        2001, 
                                        8307, 
                                        SDO_POINT_TYPE(16.584492,49.229635, NULL),
                                        NULL,
                                        NULL
                                    ),
    'Panelák Her?íkova', 0, 'Her?íkova 2498/18');

SELECT SDO_GEOM.SDO_DISTANCE(a.geometry, g.geometry, 1, 'unit=M') vzdalenost_budov_a_g
FROM PROPERTY a, PROPERTY g
WHERE a.id = 1 AND g.id = 2;


