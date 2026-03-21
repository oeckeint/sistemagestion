package excepciones;

public class MasDeUnClienteEncontrado extends RuntimeException{

    public MasDeUnClienteEncontrado(String cups) {
        super("Se encontró más de un cliente con el mismo cups (<Strong>" + cups + "</Strong>).");
    }
    
}
