package io.github.everlizarraga.fundamentos.basico.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
  private String nombre;
  private double precio;
  private TipoProducto tipo;
  private LocalDate vencimiento;
  private int stock;

  public static List<Producto> productosEjemplo() {
    return List.of(
        Producto.builder().nombre("Coca-cola 2.5L").precio(3200.0).tipo(TipoProducto.BEBIDA).stock(8).build(),
        new Producto("Coca-Cola 1.5L", 2500.0, TipoProducto.BEBIDA, null, 24),
        new Producto("Agua Mineral", 800.0, TipoProducto.BEBIDA, null, 50),
        new Producto("Jugo de Naranja", 1200.0, TipoProducto.BEBIDA, null, 18),
        new Producto("Papas Fritas", 950.0, TipoProducto.SNACK, null, 30),
        new Producto("Mani Salado", 600.0, TipoProducto.SNACK, null, 12),
        new Producto("Leche Entera", 1100.0, TipoProducto.LACTEO, null, 36),
        new Producto("Yogur Bebible", 750.0, TipoProducto.LACTEO, null, 20),
        new Producto("Detergente", 1800.0, TipoProducto.LIMPIEZA, null, 15)
    );
  }
}
