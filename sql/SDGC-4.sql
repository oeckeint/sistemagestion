--SDGC-4
--Crear vista clientePeajes y clienteFacturas

create
	algorithm = merge
view ClientePeaje as 
	SELECT 
	 c.id_cliente as iDCliente, c.cups as CUPS, c.nombre_cliente as Cliente, c.tarifa as tarifa, c.id_cliente_punto_suministro as puntoSuministro, c.id_cliente_datos as clienteDatos, c.id_cliente_contrato,
	 cx.idcontenido_xml, cx.c_emp_emi, cx.c_emp_des, cx.c_cod_pro, cx.c_cod_pas, cx.c_cod_sol, cx.c_fec_sol, cx.codigo_fiscal_factura, cx.imp_tot_fac, cx.tipo_factura, cx.motivo_facturacion,
	 cx.codigo_factura_rectificada_anulada, cx.fecha_factura, cx.tarifa_atr_fact, cx.modo_control_potencia, cx.marca_medida_con_perdidas, cx.vas_trafo, cx.porcentaje_perdidas, cx.numero_dias, cx.exceso_potencia1,
	 cx.exceso_potencia2, cx.exceso_potencia3, cx.exceso_potencia4, cx.exceso_potencia5, cx.exceso_potencia6, cx.exceso_importe_total, cx.potencia_contratada1, cx.potencia_contratada2, cx.potencia_contratada3,
	 cx.potencia_contratada4, cx.potencia_contratada5, cx.potencia_contratada6, cx.potencia_max1, cx.potencia_max2, cx.potencia_max3, cx.potencia_max4, cx.potencia_max5, cx.potencia_max6, cx.potencia_fac1,
	 cx.potencia_fac2, cx.potencia_fac3, cx.potencia_pre1, cx.potencia_pre2, cx.potencia_pre3, cx.potencia_pre4, cx.potencia_pre5, cx.potencia_pre6, cx.potencia_imp_tot, cx.ea_fecha_desde1, cx.ea_fecha_hasta1,
	 cx.ea_fecha_desde2, cx.ea_fecha_hasta2, cx.ea_val1, cx.ea_val2, cx.ea_val3, cx.ea_val4, cx.ea_val5, cx.ea_val6, cx.ea_val_sum, cx.ea_pre1, cx.ea_pre2, cx.ea_pre3, cx.ea_pre4, cx.ea_pre5, cx.ea_pre6, cx.ea_imp_tot,
	 cx.car1_01, cx.car2_01, cx.car3_01, cx.car4_01, cx.car5_01, cx.car6_01, cx.car_imp_tot_01, cx.car1_02, cx.car2_02, cx.car3_02, cx.car4_02, cx.car5_02, cx.car6_02, cx.car_imp_tot_02, cx.ie_importe, cx.a_imp_fact,
	 cx.i_bas_imp, cx.ae_cons1, cx.ae_cons2, cx.ae_cons3, cx.ae_cons4, cx.ae_cons5, cx.ae_cons6, cx.ae_cons_sum, cx.ae_lec_des1, cx.ae_lec_des2, cx.ae_lec_des3, cx.ae_lec_des4, cx.ae_lec_des5, cx.ae_lec_des6,
	 cx.ae_lec_has1, cx.ae_lec_has3, cx.ae_lec_has2, cx.ae_lec_has4, cx.ae_lec_has5, cx.ae_lec_has6, cx.ae_pro_des, cx.ae_pro_has, cx.r_con1, cx.r_con2, cx.r_con3, cx.r_con4, cx.r_con5, cx.r_con6, cx.r_con_sum,
	 cx.r_lec_des1, cx.r_lec_des2, cx.r_lec_des3, cx.r_lec_des4, cx.r_lec_des5, cx.r_lec_des6, cx.r_lec_has1, cx.r_lec_has2, cx.r_lec_has3, cx.r_lec_has4, cx.r_lec_has5, cx.r_lec_has6, cx.r_imp_tot, cx.pm_con1,
	 cx.pm_con2, cx.pm_con3, cx.pm_con4, cx.pm_con5, cx.pm_con6, cx.pm_con_sum, cx.pm_lec_has1, cx.pm_lec_has2, cx.pm_lec_has3, cx.pm_lec_has4, cx.pm_lec_has5, cx.pm_lec_has6, cx.rf_imp_tot, cx.rf_sal_tot_fac, cx.rf_tot_rec,
	 cx.rf_fec_val, cx.rf_fec_lim_pag, cx.rf_id_rem, cx.comentarios, cx.id_error, cx.remesa_pago, cx.estado_pago, cx.id_energia_excedentaria
	FROM sge.cliente c inner join sge.contenido_xml cx on c.id_cliente = cx.id_cliente;
	
-------------------------------------------------------------------------------------------------------------------------------------------------------------	

create  
	algorithm = merge
view ClienteFactura as 
	SELECT 
	 c.id_cliente as iDCliente, c.cups as CUPS, c.nombre_cliente as Cliente, c.tarifa as tarifa, c.id_cliente_punto_suministro as puntoSuministro, c.id_cliente_datos as clienteDatos, c.id_cliente_contrato,
	 cxf.idcontenido_xml_factura, cxf.c_emp_emi, cxf.c_emp_des, cxf.c_cod_pro, cxf.c_cod_pas, cxf.c_cod_sol, cxf.c_fec_sol, cxf.codigo_fiscal_factura, cxf.imp_tot_fac, cxf.tipo_factura, cxf.motivo_facturacion,
	 cxf.codigo_factura_rectificada_anulada, cxf.fecha_factura, cxf.tarifa_atr_fact, cxf.modo_control_potencia, cxf.marca_medida_con_perdidas, cxf.vas_trafo, cxf.porcentaje_perdidas, cxf.numero_dias, cxf.exceso_potencia1,
	 cxf.exceso_potencia2, cxf.exceso_potencia3, cxf.exceso_potencia4, cxf.exceso_potencia5, cxf.exceso_potencia6, cxf.exceso_importe_total, cxf.potencia_contratada1, cxf.potencia_contratada2, cxf.potencia_contratada3,
	 cxf.potencia_contratada4, cxf.potencia_contratada5, cxf.potencia_contratada6, cxf.potencia_max1, cxf.potencia_max2, cxf.potencia_max3, cxf.potencia_max4, cxf.potencia_max5, cxf.potencia_max6, cxf.potencia_fac1,
	 cxf.potencia_fac2, cxf.potencia_fac3, cxf.potencia_pre1, cxf.potencia_pre2, cxf.potencia_pre3, cxf.potencia_pre4, cxf.potencia_pre5, cxf.potencia_pre6, cxf.potencia_imp_tot, cxf.ea_fecha_desde1, cxf.ea_fecha_hasta1,
	 cxf.ea_fecha_desde2, cxf.ea_fecha_hasta2, cxf.ea_val1, cxf.ea_val2, cxf.ea_val3, cxf.ea_val4, cxf.ea_val5, cxf.ea_val6, cxf.ea_val_sum, cxf.ea_pre1, cxf.ea_pre2, cxf.ea_pre3, cxf.ea_pre4, cxf.ea_pre5, cxf.ea_pre6, cxf.ea_imp_tot,	 cxf.ie_importe, cxf.a_imp_fact,
	 cxf.i_bas_imp, cxf.ae_cons1, cxf.ae_cons2, cxf.ae_cons3, cxf.ae_cons4, cxf.ae_cons5, cxf.ae_cons6, cxf.ae_cons_sum, cxf.ae_lec_des1, cxf.ae_lec_des2, cxf.ae_lec_des3, cxf.ae_lec_des4, cxf.ae_lec_des5, cxf.ae_lec_des6,
	 cxf.ae_lec_has1, cxf.ae_lec_has3, cxf.ae_lec_has2, cxf.ae_lec_has4, cxf.ae_lec_has5, cxf.ae_lec_has6, cxf.ae_pro_des, cxf.ae_pro_has, cxf.r_con1, cxf.r_con2, cxf.r_con3, cxf.r_con4, cxf.r_con5, cxf.r_con6, cxf.r_con_sum,
	 cxf.r_lec_des1, cxf.r_lec_des2, cxf.r_lec_des3, cxf.r_lec_des4, cxf.r_lec_des5, cxf.r_lec_des6, cxf.r_lec_has1, cxf.r_lec_has2, cxf.r_lec_has3, cxf.r_lec_has4, cxf.r_lec_has5, cxf.r_lec_has6, cxf.r_imp_tot, cxf.pm_con1,
	 cxf.pm_con2, cxf.pm_con3, cxf.pm_con4, cxf.pm_con5, cxf.pm_con6, cxf.pm_con_sum, cxf.pm_lec_has1, cxf.pm_lec_has2, cxf.pm_lec_has3, cxf.pm_lec_has4, cxf.pm_lec_has5, cxf.pm_lec_has6, cxf.rf_imp_tot, cxf.rf_sal_tot_fac, cxf.rf_tot_rec,
	 cxf.rf_fec_val, cxf.rf_fec_lim_pag, cxf.rf_id_rem, cxf.comentarios, cxf.id_error, cxf.remesa_pago, cxf.estado_pago
	FROM sge.cliente c inner join sge.contenido_xml_factura cxf on c.id_cliente = cxf.id_cliente;
	
-------------------------------------------------------------------------------------------------------------------------------------------------------------

COMMIT;