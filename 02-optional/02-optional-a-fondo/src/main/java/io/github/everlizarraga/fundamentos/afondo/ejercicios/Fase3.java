package io.github.everlizarraga.fundamentos.afondo.ejercicios;

import io.github.everlizarraga.fundamentos.afondo.models.entities.CatalogoProductos;
import io.github.everlizarraga.fundamentos.afondo.models.entities.Producto;
import io.github.everlizarraga.fundamentos.afondo.models.entities.TipoProducto;

import java.time.LocalDate;
import java.util.List;

public class Fase3 {
  public static void resolverEjercicio() {
    System.out.println("Resolviendo fase 03 !!!");
    List<Producto> promos = List.of(
        new Producto("Galletitas", 800.0, TipoProducto.SNACK, null, 120),
        Producto.builder()
            .nombre("Fernet")
            .precio(7400.0)
            .tipo(TipoProducto.BEBIDA)
            .vencimiento(LocalDate.now().plusDays(30))
            .build()
    );
    var catalogo = new CatalogoProductos(Producto.productosEjemplo(), promos);

    System.out.println("==============");
    m4Or(catalogo);
    System.out.println("==============");
    m5flatMap(catalogo);
    System.out.println("==============");
    m6orElseVsOrElseGet(catalogo);
    System.out.println("==============");
    m7ifPresentOrElse(catalogo);
    System.out.println("==============");
    m8granFinal(catalogo);
  }

  private static void m4Or(CatalogoProductos catalogo) {
    System.out.println("> " + catalogo.buscarConPromos("detergente"));
    System.out.println("> " + catalogo.buscarConPromos("fernet"));
  }

  private static void m5flatMap(CatalogoProductos catalogo) {
    System.out.println("M5 - FlatMap");
    System.out.println("> Vencimiento detergente: " + catalogo.vencimientoDe("detergente"));
    System.out.println("> Vencimiento fernet: " + catalogo.vencimientoDe("fernet"));
  }

  private static void m6orElseVsOrElseGet(CatalogoProductos catalogo) {
    var producto1 = catalogo.buscarConPromos("detergente")
        .orElse(catalogo.fabricarDefault());
    var producto2 = catalogo.buscarConPromos("detergente")
        .orElseGet(catalogo::fabricarDefault);
    System.out.println("Prod 1: " + producto1);
    System.out.println("Prod 2: " + producto2);
  }

  private static void m7ifPresentOrElse(CatalogoProductos catalogo) {
    catalogo.reportar("detergente");
    catalogo.reportar("detergentexd");
  }

  private static void m8granFinal(CatalogoProductos catalogo) {
    System.out.println("> " + catalogo.etiquetaOferta("detergente"));
    System.out.println("> " + catalogo.etiquetaOferta("Fernet"));
    System.out.println("> " + catalogo.etiquetaOferta("fernetXd"));
    System.out.println("> " + catalogo.etiquetaOferta("Coca-Cola 1.5L"));
    System.out.println("> " + catalogo.etiquetaOferta("galletitas"));
  }
}
