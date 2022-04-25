--SDGC-56
--Nuevos campos en BaseDeDatos - registros xml

--Peajes
ALTER TABLE sge.contenido_xml ADD COLUMN created_on DATE DEFAULT NULL;
ALTER TABLE sge.contenido_xml ADD COLUMN created_by varchar(100) NULL;
ALTER TABLE sge.contenido_xml ADD COLUMN updated_on DATE DEFAULT NULL;
ALTER TABLE sge.contenido_xml ADD COLUMN updated_by varchar(100) NULL;

--Facturas
ALTER TABLE sge.contenido_xml_factura ADD created_on DATE DEFAULT NULL;
ALTER TABLE sge.contenido_xml_factura ADD created_by varchar(100) NULL;
ALTER TABLE sge.contenido_xml_factura ADD updated_on DATE DEFAULT NULL;
ALTER TABLE sge.contenido_xml_factura ADD updated_by varchar(100) NULL;

--OtrasFacturas
ALTER TABLE sge.contenido_xml_otras_facturas ADD created_on DATE DEFAULT NULL;
ALTER TABLE sge.contenido_xml_otras_facturas ADD created_by varchar(100) NULL;
ALTER TABLE sge.contenido_xml_otras_facturas ADD updated_on DATE DEFAULT NULL;
ALTER TABLE sge.contenido_xml_otras_facturas ADD updated_by varchar(100) NULL;

commit;
