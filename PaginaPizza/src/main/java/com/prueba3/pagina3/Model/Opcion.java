package com.prueba3.pagina3.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(name = "Opcion", description = "Representa una pizza/ opción disponible")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1")
    private Long id;
    @Schema(description = "Nombre de la pizza", example = "Margarita")
    private String nombre;
    @Schema(description = "Descripción breve", example = "Pizza con tomate y queso")
    private String descripcion;
    @Schema(description = "Precio tamaño pequeña", example = "5.99")
    private double precioPequena;
    @Schema(description = "Precio tamaño mediana", example = "8.50")
    private double precioMediana;
    @Schema(description = "Precio tamaño grande", example = "11.00")
    private double precioGrande;
    @Schema(description = "Nombre o ruta de la imagen", example = "margarita.jpg")
    private String imagen;

    public Opcion() {}

    public Opcion(Long id, String nombre, String descripcion, double precioPequena, double precioMediana, double precioGrande, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioPequena = precioPequena;
        this.precioMediana = precioMediana;
        this.precioGrande = precioGrande;
        this.imagen = imagen;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioPequena() { return precioPequena; }
    public void setPrecioPequena(double precioPequena) { this.precioPequena = precioPequena; }

    public double getPrecioMediana() { return precioMediana; }
    public void setPrecioMediana(double precioMediana) { this.precioMediana = precioMediana; }

    public double getPrecioGrande() { return precioGrande; }
    public void setPrecioGrande(double precioGrande) { this.precioGrande = precioGrande; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    // Método auxiliar para obtener precio por tamaño
    public double getPrecioPorTamano(String tamano) {
        switch (tamano.toLowerCase()) {
            case "pequena":
            case "pequeña":
                return precioPequena;
            case "mediana":
                return precioMediana;
            case "grande":
                return precioGrande;
            default:
                return precioPequena;
        }
    }
}