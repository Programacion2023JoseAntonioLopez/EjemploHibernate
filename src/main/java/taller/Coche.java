package taller;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

import java.io.Serializable;

@Entity
@NamedQueries({
        //permite saber si hay coches para poder insertar unos ejemplos
        @NamedQuery(
                name = "Coche.contarTodos",
                query = "SELECT COUNT(c) FROM Coche c"
        ),
        //permite objener el listado de coches
        @NamedQuery(
                name = "Coche.listarTodos",
                query = "SELECT c FROM Coche c"
        ),
        // permite obtener el listado de coches de una marca
        @NamedQuery(
                name = "Coche.buscarPorMarca",
                query = "SELECT c FROM Coche c WHERE LOWER(c.marca) = LOWER(:marca)"
        )
})
//la clase tiene que ser serializable
public class Coche implements Serializable {
    //la clave primaria será la matrícula
    @Id
    private String matricula;

    private String marca;
    private String modelo;
    private int num_plazas;
    //constructor necesario
    public Coche() {
    }

    public Coche(String matricula, String marca, String modelo, int num_plazas) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.num_plazas = num_plazas;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getNum_plazas() {
        return num_plazas;
    }

    public void setNum_plazas(int num_plazas) {
        this.num_plazas = num_plazas;
    }

    @Override
    public String toString() {
        return String.format("%-12s %-12s %-15s %-10d",
                matricula, marca, modelo, num_plazas);
    }
}