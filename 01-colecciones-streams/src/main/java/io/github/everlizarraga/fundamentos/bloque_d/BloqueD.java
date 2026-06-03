package io.github.everlizarraga.fundamentos.bloque_d;

import io.github.everlizarraga.fundamentos.Cancion;
import io.github.everlizarraga.fundamentos.Producto;
import io.github.everlizarraga.fundamentos.TipoProducto;
import io.github.everlizarraga.fundamentos.bloque_a.BloqueA;
import io.github.everlizarraga.fundamentos.bloque_b.BloqueB;
import io.github.everlizarraga.fundamentos.bloque_c.BloqueC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BloqueD {

  public static List<Producto> PRODUCTOS = List.of(
      new Producto("Coca-cola", 5500, TipoProducto.GASEOSA, null),
      new Producto("Pepsi", 4800, TipoProducto.GASEOSA, null),
      new Producto("Leche", 7000, TipoProducto.LACTEO, null)
  );

  public static void resolverBloqueD1() {
    System.out.println("BLOQUE D1");
    List<Cancion> canciones = BloqueB.CANCIONES;

    System.out.println("===============");
    System.out.println("1. Un Map<String, List<Cancion>> agrupado por género, " +
        "usando Collectors.groupingBy(Cancion::getGenero). " +
        "Imprimí el map (vas a ver cada género con su lista)");
    var mapCancionesPorGenero = canciones.stream()
        .collect(Collectors.groupingBy(Cancion::getGenero));
    BloqueC.imprimirMapDeCancionesSegunGenero1(mapCancionesPorGenero);
    System.out.println("===============");
    System.out.println("2. Un Map<String, Long> con la cantidad de canciones por género, " +
        "usando groupingBy + Collectors.counting()");
    var mapContadorDeCancionesPorGenero = canciones.stream()
        .collect(Collectors.groupingBy(Cancion::getGenero, Collectors.counting()));
    System.out.println("> " + mapContadorDeCancionesPorGenero);
  }

  public static void resolverBloqueD2() {
    List<Producto> productos = new ArrayList<>(PRODUCTOS);
    System.out.println("===================");
    System.out.println("1. Un filtro + transformación encadenados " +
        "(ej: \"nombres de los productos que cuestan más de X, ordenados por precio\").");
    double precioBase = 5000;
    List<String> filtroNombreProductosValenMasDe5000OrdenadosPorPrecio = productos.stream()
        .filter(p -> p.getPrecio() > precioBase)
        .sorted(Comparator.comparing(Producto::getPrecio)) // <*****
        .map(Producto::getNombre)
        .toList();
    System.out.println("> " + filtroNombreProductosValenMasDe5000OrdenadosPorPrecio);
    System.out.println("===================");
    System.out.println("2. Un agrupamiento con groupingBy (ej: \"productos por categoría\").");
    Map<TipoProducto, List<Producto>> mapProductosPorCategoria = productos.stream()
        .collect(Collectors.groupingBy(Producto::getTipo)); // <*****
    System.out.println("> " + mapProductosPorCategoria);
    System.out.println("===================");
    System.out.println("3. Una cuenta o suma con stream (ej: \"valor total del inventario\" " +
        "con mapToInt/mapToLong + sum()).");
    /*
    double preciototalDeProductos = productos.stream()
        .mapToDouble(Producto::getPrecio)
        .sum();
    */
    double preciototalDeProductos = productos.stream()
        .map(Producto::getPrecio)
        .reduce(0.0, Double::sum);      // <*****
    System.out.println("> " + preciototalDeProductos);
  }
}
