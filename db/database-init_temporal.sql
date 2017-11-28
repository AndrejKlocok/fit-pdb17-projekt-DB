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
                    VALUES(old_id, old_id_property, valid_to_param + 1, old_valid_to);
            
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