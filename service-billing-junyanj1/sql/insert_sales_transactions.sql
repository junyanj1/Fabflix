DELIMITER $$

CREATE PROCEDURE InsertSalesTransactions(
IN user varchar(50),
IN movies varchar(10),
IN quantities int(11),
IN tk varchar(50)
)
BEGIN
    DECLARE salesId INT UNSIGNED DEFAULT 0;
    INSERT INTO sales(email,movieId,quantity,saleDate)
    VALUES (user,movies,quantities,CURDATE());
    SET salesId = last_insert_id();
    INSERT INTO transactions(sId,token)
    VALUES (salesid,tk);
END$$

DELIMITER ;
