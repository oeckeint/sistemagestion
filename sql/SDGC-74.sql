--SDGC-74
--Creacion filtros

--Agregar columna de Filtro a Tabla "contenido_xml"

ALTER TABLE sge.contenido_xml ADD filtro INT DEFAULT 0 NULL;
ALTER TABLE sge.contenido_xml CHANGE filtro filtro INT DEFAULT 0 NULL AFTER id_energia_excedentaria;

--Agregar columna de Filtro a Tabla "contenido_xml_factura"

ALTER TABLE sge.contenido_xml_factura ADD filtro INT DEFAULT 0 NULL;
ALTER TABLE sge.contenido_xml_factura CHANGE filtro filtro INT DEFAULT 0 NULL AFTER estado_pago;


--Agregar columna de Filtro a Tabla "contenido_xml_otras_facturas"

ALTER TABLE sge.contenido_xml_otras_facturas ADD filtro INT DEFAULT 0 NULL;
ALTER TABLE sge.contenido_xml_otras_facturas CHANGE filtro filtro INT DEFAULT 0 NULL AFTER estado_pago;

COMMIT;