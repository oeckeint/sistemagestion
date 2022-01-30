--SDGC-20
--Crear vista ClientePeajeEExcedentaria

CREATE 
	ALGORITHM = MERGE
VIEW clientePeajeEExcentaria AS
SELECT 
	 c.id_cliente as iDCliente, c.cups as CUPS, c.nombre_cliente as Cliente, c.tarifa as tarifa,
	 cx.idcontenido_xml as Peaje, cx.id_energia_excedentaria as idExcedentaria,
	 cxee.energia_excedentaria_01 as Ex1, cxee.energia_excedentaria_02  as Ex2, cxee.energia_excedentaria_03 as Ex3, cxee.energia_excedentaria_04 as Ex4, cxee.energia_excedentaria_05 as Ex5, cxee.energia_excedentaria_06 as Ex6, cxee.valor_total_energia_excedentaria as ExTot 
FROM 
	sge.cliente c inner join sge.contenido_xml cx on c.id_cliente = cx.id_cliente 
	inner join sge.contenido_xml_energia_excedentaria cxee on cx.id_energia_excedentaria  = cxee.id_energia_excedentaria 
	where cx.id_energia_excedentaria is not null;

commit;