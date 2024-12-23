-- liquibase formatted sql

-- changeset jesus:SGE-6
ALTER TABLE sge.medida_h MODIFY COLUMN actent decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qactent decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN actsal decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qactsal decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN r_q1 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qr_q1 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN r_q2 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qr_q2 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN r_q3 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qr_q3 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN r_q4 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qr_q4 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN medres1 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qmedres1 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN medres2 decimal(8,4);
ALTER TABLE sge.medida_h MODIFY COLUMN qmedres2 decimal(8,4);

-- changeset jesus:SGE-8
ALTER TABLE sge.medida_h ADD origen varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Define el origen de donde se extrajo el contenido puede ser de un archivo' AFTER temporal;