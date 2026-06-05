package io.github.everlizarraga.fundamentos.bloque_extra;

import io.github.everlizarraga.fundamentos.Producto;
import io.github.everlizarraga.fundamentos.TipoProducto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BloqueExtra3 {

  public static void resolverParte03() {
    System.out.println("FASE 01 - EXTRA - Parte 3");
    List<Producto> productos = BloqueExtra1.PRODUCTOS;

    resolverParte3_1(productos);
    resolverParte3_2(productos);
    resolverParte3_3(productos);
    resolverParte3_4(productos);
  }

  private static void resolverParte3_4(List<Producto> productos) {
    /*Un partitioningBy con alguna condición que tenga sentido en tu dominio.*/
    var req = productos.stream()
            .collect(Collectors.partitioningBy(
                p -> p.getTipo() == TipoProducto.BEBIDA,
                Collectors.mapping(Producto::getNombre, Collectors.toList())
            ));
    System.out.println("===================");
    System.out.println("> " + req);
  }

  private static void resolverParte3_3(List<Producto> productos) {
    /*Un ordenamiento de dos niveles con thenComparing.*/
    var req = productos.stream()
        .sorted(Comparator
            .comparing(Producto::getTipo, Comparator.comparing(TipoProducto::name))
            .thenComparing(Producto::getNombre)
        )
        .map(p -> "<" + p.getTipo() + "|" + p.getPrecio() + "|" + p.getNombre() + ">")
        .toList();
    System.out.println("===================");
    System.out.println("> " + req);
  }

  private static void resolverParte3_2(List<Producto> productos) {
    /*Un toMap de algún campo identificador → otro campo.*/
    var req = productos.stream()
        .collect(Collectors.toMap(
            Producto::getTipo
            , Producto::getNombre
            , (p1, p2) -> p2
        ));
    System.out.println("===================");
    System.out.println("> " + req);
  }

  private static void resolverParte3_1(List<Producto> productos) {
    /*Un groupingBy con sub-receta que NO sea counting (usá summing, averaging o mapping).*/
    var req = productos.stream()
        .collect(Collectors.groupingBy(
            Producto::getTipo,
            Collectors.mapping(Producto::getNombre, Collectors.toList())
            //Collectors.counting()
            //Collectors.summingInt(Producto::getStock)
            //Collectors.averagingDouble(Producto::getStock)
        ));
    System.out.println("===================");
    System.out.println("> " + req);
  }
}
