
--@author Alvaro Cardozo

CREATE OR REPLACE FUNCTION isnumeric(v_input text)
RETURNS boolean AS $$
DECLARE v_int_value INTEGER DEFAULT NULL;
BEGIN
    BEGIN
        v_int_value := v_input::INTEGER;
        RETURN TRUE;
    EXCEPTION WHEN OTHERS THEN
        RETURN FALSE;
    END;
END;
$$ LANGUAGE plpgsql;
