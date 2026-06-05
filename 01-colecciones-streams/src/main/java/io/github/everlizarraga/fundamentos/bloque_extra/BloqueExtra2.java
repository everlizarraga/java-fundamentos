package io.github.everlizarraga.fundamentos.bloque_extra;

import io.github.everlizarraga.fundamentos.Producto;
import io.github.everlizarraga.fundamentos.TipoProducto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BloqueExtra2 {
  public static void resolverParte02() {
    List<Producto> productos = BloqueExtra1.PRODUCTOS;
    resolverParte2_1(productos);
    resolverParte2_2(productos);
    resolverParte2_3(productos);
  }

  private static void resolverParte2_3(List<Producto> productos) {
    /*P2.3 — multinivel con reversed: ordená por categoría (alfabético) y,
    dentro de cada categoría, por precio descendente (del más caro al más barato dentro del grupo).
    ✅ Terminado: funciona. Pista: el .reversed() solo debe afectar al precio, no a la categoría —
    vas a tener que pensar dónde ponerlo, o usar la forma*/
    var productosOrdenadosPorCategoriaAlfabeticoYPrecioDescendente = productos.stream()
        .sorted(Comparator
            .comparing(Producto::getTipo, Comparator.comparing(TipoProducto::name))
            //.thenComparing(Comparator.comparingDouble(Producto::getPrecio).reversed())
            .thenComparing(Producto::getPrecio, Comparator.reverseOrder())
            //.reversed()
        )
        .map(p -> "<" + p.getTipo() + "|" + p.getPrecio() + "|" + p.getNombre() + ">")
        .toList();
    System.out.println("================");
    System.out.println("Productos ordenados por categoria alfabetica y precios descendente:");
    System.out.println("> " + productosOrdenadosPorCategoriaAlfabeticoYPrecioDescendente);
  }

  private static void resolverParte2_2(List<Producto> productos) {
    /*thenComparing (el importante): ordená los productos por categoría (alfabético) y,
    dentro de cada categoría, por precio ascendente.
    Imprimí nombre, categoría y precio para verificar el orden.*/
    var productosOrdenadosPorCategoriaYPrecio = productos.stream()
        //.sorted(Comparator.comparing((Producto p) -> p.getTipo().name()).thenComparing(Producto::getPrecio))
        .sorted(Comparator
            .comparing(Producto::getTipo, Comparator.comparing(TipoProducto::name))
            .thenComparing(Producto::getPrecio))
        .map(p -> "<" + p.getTipo() + "|" + p.getPrecio() + "|" + p.getNombre() + ">")
        .collect(Collectors.toCollection(ArrayList::new));
    System.out.println("================");
    System.out.println("Productos ordenados por categoria y precio:");
    System.out.println("> " + productosOrdenadosPorCategoriaYPrecio);
  }

  public static void resolverParte2_1(List<Producto> productos) {
    /*comparingDouble: ordená los productos por precio ascendente e imprimí nombre + precio.
    Usá comparingDouble (no comparing pelado), ya que el precio es double*/
    var productosPorPrecioAscendente = productos.stream()
        .sorted(Comparator.comparingDouble(Producto::getPrecio))
        .map(p -> p.getNombre() + ": $" + p.getPrecio())
        .collect(Collectors.toCollection(ArrayList::new));
    System.out.println("================");
    System.out.println("Productos por precio ascendete: " + productosPorPrecioAscendente);
  }
}
