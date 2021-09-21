/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excepciones;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class MasDeUnClienteEncontrado extends Exception{

    public MasDeUnClienteEncontrado(String cups) {
        super("Se encontró más de un cliente con el mismo cups (<Strong>" + cups + "</Strong>).");
    }
    
}
