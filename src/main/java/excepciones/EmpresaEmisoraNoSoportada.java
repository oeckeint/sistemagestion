package excepciones;

public class EmpresaEmisoraNoSoportada extends Exception{

    public EmpresaEmisoraNoSoportada(int empEmi) {
        super("La empresa emisora <Strong>" + empEmi + "</Strong>, no esta soportada");
    }
    
}
