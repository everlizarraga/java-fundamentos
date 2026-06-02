package io.github.everlizarraga.fundamentos.bloque_c;

import io.github.everlizarraga.fundamentos.Cancion;
import io.github.everlizarraga.fundamentos.bloque_b.BloqueB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BloqueC {

  public static void resolverBloqueC1() {
    System.out.println("BLOQUE C1: MAP");
    Map<String, Integer> cancionesMap = mapeoDeCanciones(BloqueB.CANCIONES);

    System.out.println("1. Imprimí la duración de una canción puntual por su título");
    System.out.println("> Billie Jean: " + cancionesMap.get("Billie Jean"));

    System.out.println("------------------");
    System.out.println("2. Pedí la duración de un título que no existe");
    var duracionDeCancionQueNoExiste = cancionesMap.getOrDefault("Experience", 0);
    System.out.println("> Experience: " + duracionDeCancionQueNoExiste);

    System.out.println("------------------");
    System.out.println("3. Recorré todo el map con forEach imprimiendo \"título → duración\"");
    cancionesMap.forEach((titulo, duracion) -> System.out.println("> " + titulo + " -> " + duracion));
  }

  public static void resolverBloqueC2() {
    System.out.println("BLOQUE C2");
    Map<String, List<Cancion>> mapSegunGenero = mapeoSegunGenero(BloqueB.CANCIONES);
    //imprimirMapDeCancionesSegunGenero1(mapSegunGenero);
    imprimirMapDeCancionesSegunGenero2(mapSegunGenero);
    System.out.println("================");
    System.out.println("1. Imprimí cuántas canciones hay en el género \"rock\"");
    int cantidadDeCancionesDelGeneroRock = mapSegunGenero.getOrDefault("rock", new ArrayList<>()).size();
    System.out.println("> " + cantidadDeCancionesDelGeneroRock);
    System.out.println("================");
    System.out.println("2. Imprimí los títulos de las canciones de \"rock\" recorriendo la lista interna");
    mapSegunGenero.getOrDefault("rock", List.of()).forEach(cancion ->
        System.out.println("> " + cancion.getTitulo()));
    System.out.println("================");
    System.out.println("3. Para un género que no existe, usá getOrDefault(\"reggae\", List.of()) y " +
        "mostrá que devuelve lista vacía (size 0), sin romper");
    String unGenero = "Jedi";
    List<Cancion> canciones = mapSegunGenero.getOrDefault(unGenero, List.of());
    System.out.println("> Genero: Jedi - size: " + canciones.size());
  }

  // ============================================
  private static Map<String, Integer> mapeoDeCanciones(List<Cancion> canciones) {
    Map<String, Integer> mapDeCanciones = new HashMap<>();
    for (Cancion cancion : canciones) {
      mapDeCanciones.put(cancion.getTitulo(), cancion.getDuracionSegundos());
    }
    return mapDeCanciones;
  }

  private static Map<String, List<Cancion>> mapeoSegunGenero(List<Cancion> canciones) {
    Map<String, List<Cancion>> mapSegunGenero = new HashMap<>();
    canciones.forEach(cancion -> {
      String unGenero = cancion.getGenero();
      if (!mapSegunGenero.containsKey(unGenero)) mapSegunGenero.put(unGenero, new ArrayList<>());
      List<Cancion> listaDeCancionesCorrespondientesAlGenero = mapSegunGenero.get(unGenero);
      listaDeCancionesCorrespondientesAlGenero.add(cancion);
    });
    return mapSegunGenero;
  }

  private static void imprimirMapDeCancionesSegunGenero1(Map<String, List<Cancion>> map) {
    for (Map.Entry<String, List<Cancion>> entrada : map.entrySet()) {
      String genero = entrada.getKey();
      String str = entrada.getValue().stream()
          .map(Cancion::getTitulo)
          .collect(Collectors.joining(" | "));
      System.out.println("> [" + genero + "]: " + str);
    }
  }

  private static void imprimirMapDeCancionesSegunGenero2(Map<String, List<Cancion>> map) {
    for (Map.Entry<String, List<Cancion>> entrada : map.entrySet()) {
      String genero = entrada.getKey();
      List<String> canciones = entrada.getValue().stream()
          .map(Cancion::getTitulo)
          //.collect(Collectors.toList()));
          .toList();
      String str = String.join(" | ", canciones);
      System.out.println("> [" + genero + "]: " + str);
    }
  }
}
