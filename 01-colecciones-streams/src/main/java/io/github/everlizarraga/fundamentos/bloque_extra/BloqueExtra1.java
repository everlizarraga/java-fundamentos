package io.github.everlizarraga.fundamentos.bloque_extra;

import io.github.everlizarraga.fundamentos.Producto;
import io.github.everlizarraga.fundamentos.TipoProducto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BloqueExtra1 {

  public static final List<Producto> PRODUCTOS = List.of(
      Producto.builder().nombre("Coca-cola 2.5L").precio(3200.0).tipo(TipoProducto.BEBIDA).stock(8).build(),
      new Producto("Coca-Cola 1.5L", 2500.0, TipoProducto.BEBIDA, null, 24),
      new Producto("Agua Mineral", 800.0, TipoProducto.BEBIDA, null, 50),
      new Producto("Jugo de Naranja", 1200.0, TipoProducto.BEBIDA, null, 18),
      new Producto("Papas Fritas", 950.0, TipoProducto.SNACK, null, 30),
      new Producto("Mani Salado", 600.0, TipoProducto.SNACK, null, 12),
      new Producto("Leche Entera", 1100.0, TipoProducto.LACTEO, null, 36),
      new Producto("Yogur Bebible", 750.0, TipoProducto.LACTEO, null, 20),
      new Producto("Detergente", 1800.0, TipoProducto.LIMPIEZA, null, 15)
  );

  public static void resolverParte01() {
    //System.out.println("Lista Productos: " + PRODUCTOS);
    resolverParte1_1(PRODUCTOS);
    resolverParte1_2(PRODUCTOS);
    resolverParte1_3(PRODUCTOS);
    resolverParte1_4(PRODUCTOS);
    resolverParte1_5(PRODUCTOS);
  }

  private static void resolverParte1_5(List<Producto> productos) {
    /*groupingBy + mapping: los nombres de los productos por categoría (no el producto entero).*/
    var mapNombreProductosPorCategoria = productos.stream()
        .collect(Collectors.groupingBy(
            Producto::getTipo,
            //Collectors.mapping(Producto::getNombre, Collectors.toList())
            Collectors.mapping(Producto::getNombre, Collectors.toCollection(ArrayList::new))
        ));
    System.out.println("===================");
    System.out.println("Nombre de productos por categoria: " + mapNombreProductosPorCategoria);
  }

  private static void resolverParte1_1(List<Producto> productos) {
    /*toMap: generá un Map<String, Double> de nombre del producto → su precio.*/
    Map<String, Double> mapDeNombreDeProductoYPrecio = productos.stream()
        .collect(Collectors.toMap(
            Producto::getNombre,
            Producto::getPrecio
        ));
    System.out.println("ToMap: " + mapDeNombreDeProductoYPrecio);
  }

  private static void resolverParte1_2(List<Producto> productos) {
    /*partitioningBy: partí los productos entre "caros" (precio > 1000) y el resto.
    Imprimí cuántos hay de cada lado.*/
//    var mapPreciosCaros = productos.stream()
//        .collect(Collectors.partitioningBy(
//            producto -> producto.getPrecio() > 1000,
//            Collectors.counting()));
    double precioCaroReferencia = 1000.0;
    var mapPreciosCaros = productos.stream()
        .collect(Collectors.partitioningBy(p -> p.getPrecio() > precioCaroReferencia));
    System.out.println("===================");
    System.out.println("Los productos caros: " + mapPreciosCaros);
    System.out.println("get(true): " + mapPreciosCaros.get(true).size());
    System.out.println("get(false): " + mapPreciosCaros.get(false).size());
  }

  private static void resolverParte1_3(List<Producto> productos) {
    /*groupingBy + counting: cuántos productos hay por categoría.*/
    var mapCantidadProductosPorCategoria = productos.stream()
        .collect(Collectors.groupingBy(
            Producto::getTipo,
            Collectors.counting()
        ));
    System.out.println("===================");
    System.out.println("Cantidad Productos por Categoria: " + mapCantidadProductosPorCategoria);
  }

  private static void resolverParte1_4(List<Producto> productos) {
    /*groupingBy + summingInt: el stock total por categoría.*/
    var mapStockTotalPorCategoria = productos.stream()
        .collect(Collectors.groupingBy(
            Producto::getTipo,
            Collectors.summingInt(Producto::getStock)
        ));
    System.out.println("===================");
    System.out.println("Stock total por categoria: " + mapStockTotalPorCategoria);
  }

}
