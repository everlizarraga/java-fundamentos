package io.github.everlizarraga.fundamentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

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
}
