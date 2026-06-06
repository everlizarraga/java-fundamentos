package io.github.everlizarraga.fundamentos.basico.ejercicios;

import io.github.everlizarraga.fundamentos.basico.models.entities.Producto;

import java.util.List;

public class E2 {

  public static void resolverE2() {
    List<Producto> productos = Producto.productosEjemplo();
    double precioDetergente = obtenerPrecioDeUnProducto(productos, "detergente");
    double precioLavandina = obtenerPrecioDeUnProducto(productos, "lavandina");
    System.out.println("2. =======================");
    System.out.println("  > Precio detergente: " + precioDetergente);
    System.out.println("  > Precio lavandina: " + precioLavandina);
  }

  public static double obtenerPrecioDeUnProducto(List<Producto> productos, String nombre) {
    return E1.buscarPorNombre(productos, nombre)
        .map(Producto::getPrecio)
        .orElse(-1.0);
  }
}
