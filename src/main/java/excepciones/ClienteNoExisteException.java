package excepciones;

public class ClienteNoExisteException extends Exception{
    
    public ClienteNoExisteException(String cups){
        super("el cliente con el cups (<Strong>" + cups + "</Strong>) no existe");
    }
    
    public ClienteNoExisteException(){
        super("no existe el cliente");
    }
}
