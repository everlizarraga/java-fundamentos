package io.github.everlizarraga.fundamentos.basico.ejercicios;

import io.github.everlizarraga.fundamentos.basico.models.entities.Producto;

import java.util.List;
import java.util.Optional;

public class E6 {

  public static void resolverE6() {
    List<Producto> productos = Producto.productosEjemplo();
    Optional<Producto> opt = E1.buscarPorNombre(productos, "Papas Fritas");
    String rpta = opt
        .map(p -> p.getNombre().toUpperCase())
        .orElse("NO ENCONTRADO");
    System.out.println("6. =======================");
    System.out.println("  > " + rpta);
  }
}
