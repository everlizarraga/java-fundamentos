package io.github.everlizarraga.fundamentos.basico.ejercicios;

import io.github.everlizarraga.fundamentos.basico.models.entities.Producto;

public class E4 {
  public static void resolverE4() {
    var productos = Producto.productosEjemplo();
    System.out.println("4. =======================");
    E1.buscarPorNombre(productos, "detergente")
        .ifPresent(p -> System.out.println("  > STOCK: " + p.getStock()));
    E1.buscarPorNombre(productos, "aceite")
        .ifPresent(System.out::println);
    //System.out.println("  > ");
  }
}
