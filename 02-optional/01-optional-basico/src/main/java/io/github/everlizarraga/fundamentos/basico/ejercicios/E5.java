package io.github.everlizarraga.fundamentos.basico.ejercicios;

import io.github.everlizarraga.fundamentos.basico.models.entities.Producto;
import io.github.everlizarraga.fundamentos.basico.models.entities.TipoProducto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class E5 {

  private static final Map<TipoProducto, Integer> DESCUENTOS =
      Map.of(TipoProducto.BEBIDA, 10, TipoProducto.SNACK, 25);

  public static void resolverE5() {
    var productos = Producto.productosEjemplo();
    System.out.println("5. =======================");
    E1.buscarPorNombre(productos, "agua mineral")
        .map(E5::obtenerDescuentoDelProducto)
        .ifPresent(d -> System.out.println("  > Descuento de Agua Mineral: " + d));
    E1.buscarPorNombre(productos, "detergente")
        .map(E5::obtenerDescuentoDelProducto)
        .ifPresent(d -> System.out.println("  > Descuento de Detergente: " + d));
  }

  public static int obtenerDescuentoDelProducto(Producto producto) {
    return Optional
        .ofNullable(DESCUENTOS.get(producto.getTipo()))
        .orElse(0);
  }
}
