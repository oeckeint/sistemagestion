#SDGC-64
#Nuevos campos de fechas

#Agregar columna de Filtro a Tabla "contenido_xml"

ALTER TABLE sge.contenido_xml ADD tp_fecha_desde varchar(100) NULL;
ALTER TABLE sge.contenido_xml CHANGE tp_fecha_desde tp_fecha_desde varchar(100) NULL AFTER fecha_factura;

ALTER TABLE sge.contenido_xml ADD tp_fecha_hasta varchar(100) NULL;
ALTER TABLE sge.contenido_xml CHANGE tp_fecha_hasta tp_fecha_hasta varchar(100) NULL AFTER tp_fecha_desde;

COMMIT;