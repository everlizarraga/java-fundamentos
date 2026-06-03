package io.github.everlizarraga.fundamentos;

import io.github.everlizarraga.fundamentos.bloque_d.BloqueD;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
  private String nombre;
  private double precio;
  private TipoProducto tipo;
  private Date vencimiento;
}
