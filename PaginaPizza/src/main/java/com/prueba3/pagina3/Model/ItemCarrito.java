package com.prueba3.pagina3.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "item_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ItemCarrito", description = "Item agregado al carrito por un usuario")
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del item", example = "10")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "opcion_id", nullable = false)
    @Schema(description = "Opción/pizza asociada")
    private Opcion opcion;
    @Column(nullable = false)
    @Schema(description = "Nombre de la pizza", example = "Margarita")
    private String nombre;
    @Column(nullable = false)
    @Schema(description = "Tamaño escogido", example = "mediana")
    private String tamano;
    @Column(nullable = false)
    @Schema(description = "Cantidad solicitada", example = "2")
    private int cantidad;
    @Column(nullable = false)
    @Schema(description = "Precio unitario según tamaño", example = "8.50")
    private double precioUnitario;
    @Column(nullable = false)
    @Schema(description = "Subtotal (precioUnitario * cantidad)", example = "17.0")
    private double subtotal;

    @Column(name = "usuario_email")
    @Schema(description = "Email del usuario dueño del carrito", example = "juan@example.com")
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

    public double getSubtotal() { 
        // Recalcular el subtotal cada vez que se accede, asegurando que siempre sea correcto
        this.subtotal = this.precioUnitario * this.cantidad;
        return this.subtotal;
    }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }





    // Metodo auxiliar para actualizar el subtotal
    private void actualizarSubtotal() {
        this.subtotal = this.precioUnitario * this.cantidad;
    }

  
}