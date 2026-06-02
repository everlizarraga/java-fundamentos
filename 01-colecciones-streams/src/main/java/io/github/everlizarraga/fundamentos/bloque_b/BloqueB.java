package io.github.everlizarraga.fundamentos.bloque_b;

import io.github.everlizarraga.fundamentos.Cancion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BloqueB {
  public static final List<Cancion> CANCIONES = new ArrayList<>(List.of(
      new Cancion("Bohemian Rhapsody", "Queen", 354, "rock", 1800000000L),
      new Cancion("Billie Jean", "Michael Jackson", 194, "pop", 1200000000L),
      new Cancion("Take Five", "Dave Brubeck", 324, "jazz", 350000000L),
      new Cancion("Blitzkrieg Bop", "Ramones", 134, "rock", 95000000L),
      new Cancion("One More Time", "Daft Punk", 320, "electronica", 620000000L),
      new Cancion("Lose Yourself", "Eminem", 326, "hip hop", 980000000L),
      new Cancion("Shape of You", "Ed Sheeran", 234, "pop", 5900000000L),
      new Cancion("Seven Nation Army", "The White Stripes", 132, "rock", 760000000L)
  ));

  public static void resolverBloqueB1() {
    System.out.println("Hola Bloque B !!!");
    System.out.println("--------------------");
    System.out.println("1. Canciones que duran mas de 200 segundos");
    List<String> req1 = CANCIONES.stream()
        .filter(c -> c.getDuracionSegundos() > 200)
        .map(Cancion::getTitulo)
        .toList();
    System.out.println("> " + String.join("/ ", req1));
    System.out.println("--------------------");
    System.out.println("2. Títulos de todas las canciones");
    List<String> req2 = CANCIONES.stream().map(Cancion::getTitulo).toList();
    System.out.println("> " + req2);
    System.out.println("--------------------");
    System.out.println("3. ¿Hay alguna canción del genero JAZZ ?");
    //Boolean esAlgunaDelGeneroJazz = req2.stream().anyMatch(t -> "jazz".equals(t.toLowerCase()))
    boolean esAlgunaDelGeneroJazz = CANCIONES.stream()
        //.anyMatch(c -> "JAZZ".toLowerCase().equals(c.getGenero().toLowerCase()));
        .anyMatch(c -> "JAZZ".equalsIgnoreCase(c.getGenero()));
    if(esAlgunaDelGeneroJazz) System.out.println("> Alguno es del genero Jazz");
    else System.out.println("> Ninguno es del genero Jazz");
    System.out.println("--------------------");
    System.out.println("4. Cantidad de canciones del genero ROCK");
    int req4 = (int) CANCIONES.stream()
        .filter(c -> "ROCK".equalsIgnoreCase(c.getGenero()))
        .count();
    System.out.println("> " + req4);
    System.out.println("--------------------");
    System.out.println("5. Títulos de las canciones ordenadas por duración ascendente");
    List<String> req5 = CANCIONES.stream()
        .sorted(Comparator.comparing(Cancion::getDuracionSegundos))
        .map(c -> "[" + c.getDuracionSegundos() + ":" + c.getTitulo().substring(0, 4) +"]")
        //.map(Cancion::getTitulo)
        .toList();
        //.reversed();
    System.out.println("> " + req5);
  }

  public static void resolverBloqueB2() {
    System.out.println("BLOQUE B2");
    System.out.println("--------------------");
    System.out.println("1. De las canciones de género \"rock\", quedate con sus títulos, " +
        "ordenados alfabéticamente.");
    var cancionesDeRock = CANCIONES.stream()
        .filter(c -> "rock".equalsIgnoreCase(c.getGenero()))
        .map(Cancion::getTitulo)
        .sorted()
        .map(t -> t.substring(0, 3))
        .toList();
    System.out.println("> " + cancionesDeRock);
    System.out.println("--------------------");
    System.out.println("2. La suma total de reproducciones de todas las canciones");
    var sumTotalReproducciones = CANCIONES.stream()
        .mapToLong(Cancion::getReproducciones)
        .sum();
    System.out.println("> " + sumTotalReproducciones);
    System.out.println("--------------------");
    System.out.println("3. Títulos de las 3 canciones más reproducidas");
    var tituloTop3CancionesMasReproducidas = CANCIONES.stream()
        .sorted(Comparator.comparing(Cancion::getReproducciones).reversed())
        .limit(3)
        .map(Cancion::getTitulo)
        .toList();
    System.out.println("> " + tituloTop3CancionesMasReproducidas);
  }
}
