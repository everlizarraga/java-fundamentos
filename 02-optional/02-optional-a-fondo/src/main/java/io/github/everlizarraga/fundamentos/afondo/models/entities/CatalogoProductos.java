package io.github.everlizarraga.fundamentos.afondo.models.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CatalogoProductos {

  private final List<Producto> productos;
  private final List<Producto> promociones;

  public CatalogoProductos(List<Producto> productos, List<Producto> promociones) {
    this.productos = productos;
    this.promociones = promociones;
  }

  public Optional<Producto> buscarPorNombre(String nombre) {
    Optional<Producto> producto = this.productos.stream()
        .filter(p -> nombre.equalsIgnoreCase(p.getNombre()))
        .findFirst();
    return producto;
  }

  public Optional<Producto> buscarDisponible(String nombre) {
    return this.buscarPorNombre(nombre)
        .filter(p -> p.getStock() > 0);
  }

  public Producto obtener(String nombre) {
    return buscarPorNombre(nombre)
        .orElseThrow(() -> new NoSuchElementException("No existe el producto: " + nombre));
  }

  public Optional<Producto> buscarConPromos(String nombre) {
    return this.buscarPorNombre(nombre)
        .or(() -> this.promociones.stream()
            .filter(p -> nombre.equalsIgnoreCase(p.getNombre()))
            .findFirst());
  }

  public Optional<LocalDate> vencimientoDe(String nombre) {
    return buscarConPromos(nombre)
        .flatMap(p -> Optional.ofNullable(p.getVencimiento()));
  }

  public Producto fabricarDefault() {
    System.out.println("      ⚙️ fabricando producto default...");
    return Producto.builder()
        .nombre("(default)")
        .precio(0)
        .tipo(TipoProducto.SNACK)
        .stock(0)
        .build();
  }

  public void reportar(String nombre) {
    this.buscarConPromos(nombre).ifPresentOrElse(
        p -> System.out.println("📦 " + p.getNombre() + " - $" + p.getPrecio()),
        () -> System.out.println("❌ No encontrado: " + nombre)
    );
  }

  public String etiquetaOferta(String nombre) {
    return this.buscarConPromos(nombre)
        .filter(p -> p.getStock() > 0)
        .map(p -> "OFERTA: " + nombre + " a $" + p.getPrecio())
        .orElse("Sin ofertas para: " + nombre);
  }

}
