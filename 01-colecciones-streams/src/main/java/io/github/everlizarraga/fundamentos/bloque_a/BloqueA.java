package io.github.everlizarraga.fundamentos.bloque_a;

import io.github.everlizarraga.fundamentos.Cancion;

public class BloqueA {
  public static void resolverBloqueA() {
    Cancion cancion1 = new Cancion();
    Cancion cancion2 = Cancion.builder().artista("Ever").titulo("Conquistanod el mundo").build();
    System.out.println("> " + cancion1);
    System.out.println("> " + cancion2);
  }
}
