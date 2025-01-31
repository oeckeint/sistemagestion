-- liquibase formatted sql

-- changeset jesus:SGE-19 logicalFilePath:db/changelog/automatic/MedidasQH.sql
ALTER TABLE sge.medidaqh ADD origen varchar(100) NOT NULL COMMENT 'establece el nombre del archivo de origen' AFTER metod_obt;