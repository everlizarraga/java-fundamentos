package io.github.everlizarraga.fundamentos;

import io.github.everlizarraga.fundamentos.bloque_a.BloqueA;
import io.github.everlizarraga.fundamentos.bloque_b.BloqueB;
import io.github.everlizarraga.fundamentos.bloque_c.BloqueC;
import io.github.everlizarraga.fundamentos.bloque_d.BloqueD;
import io.github.everlizarraga.fundamentos.bloque_extra.BloqueExtra1;
import io.github.everlizarraga.fundamentos.bloque_extra.BloqueExtra2;
import io.github.everlizarraga.fundamentos.bloque_extra.BloqueExtra3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;

public class Main {
  public static void main(String[] args) {
    //BloqueA.resolverBloqueA();
    //BloqueB.resolverBloqueB1();
    //BloqueB.resolverBloqueB2();
    //BloqueC.resolverBloqueC1();
    //BloqueC.resolverBloqueC2();
    //BloqueD.resolverBloqueD1();
    //BloqueD.resolverBloqueD2();

    //BloqueExtra1.resolverParte01();
    //BloqueExtra2.resolverParte02();
    BloqueExtra3.resolverParte03();

    //experimentos01();
    //experimentos02();
  }

  private static void experimentos01() {
    System.out.println("EXPERIMENTOS 01 !!!");
    List<Integer> notas = new ArrayList<>(List.of(8, 4, 9, 10, 4, 6, 7, 7, 8));
    int sumaNotas = notas.stream()
        //.collect(summingInt(n -> n));
        //.collect(Collectors.summingInt(n -> n));
        .mapToInt(n -> n).sum(); //Function.identity()
    //.mapToInt(Integer::intValue).sum();
    //.reduce(0, (acum, n) -> acum + n);
    //.reduce(0, Integer::sum);
    System.out.println("NOTAS: " + notas);
    System.out.println("Suma: " + sumaNotas);
    List<Integer> listaEnteros = new ArrayList<>();
    var listaMutable = notas.stream()
        .map(n -> n * 2)
        //.collect(Collectors.toList());
        //.collect(Collectors.toList()); // <-- No garantiza la mutabilidad
        //.collect(Collectors.toCollection(() -> new ArrayList<>())); // <-Hace lo mismo que el de abajo
        .collect(Collectors.toCollection(ArrayList::new)); // <-- Receta para crear una lista nueva
    //.collect(Collectors.toCollection(() -> listaEnteros)); // <- Le doy una lista ya creada
    listaMutable.add(100);
    System.out.println("Lista Mutable: " + listaMutable);
    listaMutable.add(201);
    listaEnteros.add(401);
    System.out.println("=========");
    System.out.println("Lista Mutable: " + listaMutable);
    System.out.println("Lista Enteros: " + listaEnteros);
  }

  private static void experimentos02() {
    List<Integer> numeros = new ArrayList<>(List.of(8, 4, 9, 10, 4, 6, 7, 7, 8));
    //var listaOrdenada = numeros.sort(Comparator.comparing(Integer::intValue));
    //var listaOrdenada = numeros.stream().sorted().toList();
    //var listaOrdenada = numeros.stream().sorted(Comparator.comparing(Integer::intValue).reversed()).toList();
    //var listaOrdenada = numeros.stream().sorted(Comparator.comparing(Integer::intValue)).toList();
    //var listaOrdenada = numeros.stream().sorted(Comparator.comparingInt(Integer::intValue)).toList();
    var listaOrdenada = numeros.stream().sorted(Comparator.naturalOrder()).toList();
    System.out.println("Lista Ordenada: " + listaOrdenada);
  }
}
