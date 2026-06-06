package io.github.everlizarraga.fundamentos.basico.ejercicios;

import io.github.everlizarraga.fundamentos.basico.models.entities.Producto;

import java.util.List;

public class E3 {

  public static void resolverE3() {
    var productos = Producto.productosEjemplo();
    var producto = obtenerObligatorio(productos, "detergente");
    System.out.println("3. =======================");
    System.out.println("  > Producto obligatorio: " + producto);
  }

  public static Producto obtenerObligatorio(List<Producto> productos, String nombre) {
    return E1.buscarPorNombre(productos, nombre).orElseThrow();
  }
}
