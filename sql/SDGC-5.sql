--SDGC-5
--Filtrar las tarifas desactivadas en Editar/Agregar Clientes

UPDATE tarifa SET status = 0 WHERE id_tarifa in (1, 2, 3, 4, 5, 7, 8, 10, 11);

commit;