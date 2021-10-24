package datos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cliente_contrato")
public class ClienteContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    int idClienteContrato;
    
    @Column(name = "")
    String inicioContrato;
    
    @Column(name = "")
    String finContrato;
    
    @Column(name = "")
    int status;
    
    @Column(name = "")
    String producto;
    
    @Column(name = "")
    String costeGestion;
    
    @Column(name = "")
    String alquieres;
    
}
