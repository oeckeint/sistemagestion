-- liquibase formatted sql

-- changeset jesus:SGE-29
ALTER TABLE sge.contenido_xml_energia_excedentaria ADD COLUMN tipo_autoconsumo INT COMMENT 'Indica el tipo de autoconsumo asociado a la energía autoconsumida' AFTER neta_generada_06;