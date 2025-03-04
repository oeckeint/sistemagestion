-- liquibase formatted sql

-- changeset jesus:SGE-12
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN fecha_desde DATETIME AFTER valor_total_energia_excedentaria;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN fecha_hasta DATETIME AFTER fecha_desde;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN neta_generada_01 DECIMAL(15,6) AFTER fecha_hasta;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN neta_generada_02 DECIMAL(15,6) AFTER neta_generada_01;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN neta_generada_03 DECIMAL(15,6) AFTER neta_generada_02;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN neta_generada_04 DECIMAL(15,6) AFTER neta_generada_03;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN neta_generada_05 DECIMAL(15,6) AFTER neta_generada_04;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN neta_generada_06 DECIMAL(15,6) AFTER neta_generada_05;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN autoconsumida_01 DECIMAL(15,6) AFTER neta_generada_06;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN pago_tda_01 DECIMAL(15,6) AFTER autoconsumida_01;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN autoconsumida_02 DECIMAL(15,6) AFTER pago_tda_01;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN pago_tda_02 DECIMAL(15,6) AFTER autoconsumida_02;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN autoconsumida_03 DECIMAL(15,6) AFTER pago_tda_02;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN pago_tda_03 DECIMAL(15,6) AFTER autoconsumida_03;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN autoconsumida_04 DECIMAL(15,6) AFTER pago_tda_03;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN pago_tda_04 DECIMAL(15,6) AFTER autoconsumida_04;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN autoconsumida_05 DECIMAL(15,6) AFTER pago_tda_04;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN pago_tda_05 DECIMAL(15,6) AFTER autoconsumida_05;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN autoconsumida_06 DECIMAL(15,6) AFTER pago_tda_05;
ALTER TABLE contenido_xml_energia_excedentaria ADD COLUMN pago_tda_06 DECIMAL(15,6) AFTER autoconsumida_06;