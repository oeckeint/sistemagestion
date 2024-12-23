#SDGC-95

#Especificación de nuevos campos Medida

ALTER TABLE sge.medida ADD created_on DATETIME NULL;
ALTER TABLE sge.medida ADD updated_on DATETIME NULL;
ALTER TABLE sge.medida ADD created_by varchar(50) NULL;
ALTER TABLE sge.medida CHANGE created_by created_by varchar(50) NULL AFTER created_on;
ALTER TABLE sge.medida ADD updated_by varchar(50) NULL;

COMMIT;