package io.github.everlizarraga.fundamentos.basico.ejercicios;

import io.github.everlizarraga.fundamentos.basico.models.entities.Producto;

import java.util.List;
import java.util.Optional;

public class E1 {

  public static void resolverE1() {
    List<Producto> productos = Producto.productosEjemplo();
    System.out.println("1. =======================");
    System.out.println("  > " + buscarPorNombre(productos, "Detergente"));
    System.out.println("  > " + buscarPorNombre(productos, "Lavandina"));
  }

  public static Optional<Producto> buscarPorNombre(List<Producto> productos, String nombre) {
    return productos.stream()
        .filter(p -> nombre.equalsIgnoreCase(p.getNombre()))
        .findFirst();
  }

}
