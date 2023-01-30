#SDGC-76
#Nuevos campos de fechas

#Agregar columnas de FechaDesdeFactura y FechaHastaFactura a Tabla "contenido_xml_facturas"

ALTER TABLE sge.contenido_xml_factura ADD tp_fecha_desde varchar(100) NULL;
ALTER TABLE sge.contenido_xml_factura CHANGE tp_fecha_desde tp_fecha_desde varchar(100) NULL AFTER porcentaje_perdidas;

ALTER TABLE sge.contenido_xml_factura ADD tp_fecha_hasta varchar(100) NULL;
ALTER TABLE sge.contenido_xml_factura CHANGE tp_fecha_hasta tp_fecha_hasta varchar(100) NULL AFTER tp_fecha_desde;

COMMIT;