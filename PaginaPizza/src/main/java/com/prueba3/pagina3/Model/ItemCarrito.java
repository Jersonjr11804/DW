package com.prueba3.pagina3.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "opcion_id", nullable = false)
    private Opcion opcion;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String tamano;
    @Column(nullable = false)
    private int cantidad;
    @Column(nullable = false)
    private double precioUnitario;
    @Column(nullable = false)
    private double subtotal;

    @Column(name = "usuario_email")
    private String usuarioEmail;

  


    public Opcion getOpcion() { return opcion; }
    public void setOpcion(Opcion opcion) { 
        this.opcion = opcion;
        if (opcion != null) {
            this.nombre = opcion.getNombre();
        }
        actualizarSubtotal();
    }

    public String getTamano() { return tamano; }
    public void setTamano(String tamano) { 
        this.tamano = tamano;
        if (opcion != null) {
            this.precioUnitario = opcion.getPrecioPorTamano(tamano);
        }
        actualizarSubtotal();
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { 
        this.precioUnitario = precioUnitario;
        actualizarSubtotal();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }





    // Metodo auxiliar para actualizar el subtotal
    private void actualizarSubtotal() {
        this.subtotal = this.precioUnitario * this.cantidad;
    }

  
}