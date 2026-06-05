# 🎯 Entrenamiento Fase 1 — EXTRA: Collectors y Comparator a fondo

> **Por qué este archivo:** ya sabés *usar* `groupingBy`, `Comparator.comparing` y `collect`, pero la **terminología** todavía te obliga a "ir a mirar a algún lado". Este bloque cierra eso: te explica **qué SON** un Collector y un Comparator (no solo cómo se usan), y te muestra los miembros de cada familia que son re útiles y que todavía no conocías. Cuando termines, estas palabras van a ser tuyas, no prestadas.
>
> **Cómo se usa:** igual que la Fase 1. Leés el repaso, hacés los ejercicios tipeando vos, cumplís el criterio "✅ Terminado" y avanzás. Ejemplos de repaso en un dominio distinto al del ejercicio, para que no haya copy-paste.

---

# PARTE 1 — Collectors: ¿qué es esa cosa?

## 1.1 — Desmitificando la palabra "Collector"

Veníamos usando `.toList()` para cerrar un stream. Pero `.toList()` es solo **el caso más simple** de algo más general: `.collect(...)`.

`.collect(...)` es una operación **terminal** que dice "juntá los resultados del stream en algo". Pero "algo" puede tener muchas formas: una lista, un set, un map, un String concatenado, una suma, un map agrupado... Para decirle **en qué forma** querés juntar, le pasás una **receta**. Esa receta es lo que se llama un **Collector**.

> **El modelo mental que te va a destrabar todo:**
> Un **Collector es una receta de "en qué forma junto los resultados"**. La clase `Collectors` (con "s" al final) es el **recetario**: un montón de métodos estáticos, cada uno te da una receta distinta.

```java
.collect(Collectors.toList())       // receta: "juntá en una lista"
.collect(Collectors.toSet())        // receta: "juntá en un set (sin repetidos)"
.collect(Collectors.joining(", "))  // receta: "juntá en un String separado por comas"
.collect(Collectors.groupingBy(...)) // receta: "juntá agrupando por un criterio"
```

Todas son `Collectors.algo()`. Cuando ves `Collectors.`, leelo como "abrí el recetario y elegí cómo juntar".

> Dato: `.toList()` (el método directo del stream, Java 16+) es básicamente un atajo de `.collect(Collectors.toList())`. Por eso lo usabas sin pensar en collectors — ya estabas usando uno, escondido.

## 1.2 — El recetario (la tabla para tener a mano)

Esta tabla es **el lugar para dar la ojeada** cuando dudes. Tenela acá y dejá de buscar en mil lados:

| Collector | En qué forma junta | Devuelve |
|---|---|---|
| `toList()` | una lista | `List<T>` |
| `toSet()` | un conjunto sin repetidos | `Set<T>` |
| `toMap(clave, valor)` | un map clave→valor | `Map<K,V>` |
| `joining(sep)` | un String concatenado | `String` |
| `counting()` | cuenta cuántos hay | `Long` |
| `summingInt/Long/Double(campo)` | suma un campo numérico | número |
| `averagingInt/Long/Double(campo)` | promedia un campo | `Double` |
| `groupingBy(criterio)` | agrupa en map por un criterio | `Map<K, List<T>>` |
| `groupingBy(criterio, sub-receta)` | agrupa y a cada grupo le aplica otra receta | `Map<K, ...>` |
| `partitioningBy(condición)` | parte en dos: los que cumplen y los que no | `Map<Boolean, List<T>>` |
| `mapping(transformación, sub-receta)` | transforma y después junta | depende |

Los más jugosos que **todavía no usaste** y que vamos a practicar: `toMap`, `partitioningBy`, y `groupingBy` con **sub-receta** (downstream). Esos tres te abren un mundo.

## 1.3 — Los que ya conocés (repaso express)

**`groupingBy` básico** (ya lo hiciste): agrupa una lista en un map.
```java
Map<String, List<Empleado>> porDepto = empleados.stream()
        .collect(Collectors.groupingBy(Empleado::getDepartamento));
```

**`joining`** (lo vimos al pasar): concatena Strings.
```java
String nombres = empleados.stream()
        .map(Empleado::getNombre)
        .collect(Collectors.joining(", "));
```

## 1.4 — Los nuevos (repaso con ejemplos, dominio: empleados)

### `toMap` — convertir una lista en un map clave→valor

Cuando querés un map donde **vos elegís qué es la clave y qué es el valor**. Le pasás dos funciones: una saca la clave, otra saca el valor.

```java
// Map de "nombre del empleado" → "su salario"
Map<String, Double> salarioPorNombre = empleados.stream()
        .collect(Collectors.toMap(
                Empleado::getNombre,    // de cada empleado, la CLAVE
                Empleado::getSalario));  // de cada empleado, el VALOR
// { "Ana" -> 5000.0, "Beto" -> 4200.0, ... }
```

> ⚠️ **Trampa de `toMap`:** si dos elementos producen la **misma clave**, explota con `IllegalStateException` ("Duplicate key"). Tiene sentido: un map no puede tener dos veces la misma clave. Si puede pasar, le agregás una tercera función que decide cuál gana:
> ```java
> Collectors.toMap(Empleado::getNombre, Empleado::getSalario,
>         (sal1, sal2) -> sal1)   // si hay clave repetida, quedate con el primero
> ```

### `partitioningBy` — partir en dos según una condición

Como `groupingBy`, pero el criterio es **sí/no** (un booleano). Te devuelve un map con exactamente dos claves: `true` y `false`.

```java
// Partir empleados entre los que ganan más de 4500 y los que no
Map<Boolean, List<Empleado>> particion = empleados.stream()
        .collect(Collectors.partitioningBy(e -> e.getSalario() > 4500));

particion.get(true);   // lista de los que ganan más de 4500
particion.get(false);  // lista del resto
```

Usalo cuando la división es binaria ("aprobados/desaprobados", "stock/sin stock", "mayores/menores"). Es más claro que un `groupingBy` con un booleano.

### `groupingBy` con sub-receta (downstream) — el combo poderoso

Acá está lo que más te va a servir. `groupingBy` normal te da `Map<K, List<T>>` — o sea, cada grupo es una **lista**. Pero a veces no querés la lista entera del grupo: querés **cuántos hay**, o **la suma**, o **solo un campo**. Para eso, `groupingBy` acepta una **segunda receta** que se aplica a cada grupo:

```java
// En vez de la lista de empleados por depto, quiero CUÁNTOS hay por depto:
Map<String, Long> cantidadPorDepto = empleados.stream()
        .collect(Collectors.groupingBy(
                Empleado::getDepartamento,   // 1. agrupá por depto
                Collectors.counting()));      // 2. de cada grupo, contá
// { "Ventas" -> 12, "IT" -> 5, ... }

// La SUMA de salarios por depto:
Map<String, Double> masaSalarialPorDepto = empleados.stream()
        .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.summingDouble(Empleado::getSalario)));

// Solo los NOMBRES por depto (no el empleado entero):
Map<String, List<String>> nombresPorDepto = empleados.stream()
        .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.mapping(Empleado::getNombre, Collectors.toList())));
```

Leelo siempre igual: "agrupá por X, **y de cada grupo** hacé Y". La segunda receta reemplaza al `List` por defecto.

---

## 🏋️ Ejercicios Parte 1 (dominio: productos de una tienda)

Armá una lista de 7-8 `Producto` con campos `nombre` (String), `categoria` (String, ej: "bebida", "limpieza", "snack"), `precio` (double) y `stock` (int). Variá las categorías (que haya 2-3 por categoría) y los precios.

**P1.1 — `toMap`:** generá un `Map<String, Double>` de nombre del producto → su precio.
**✅ Terminado:** imprimís el map y ves cada producto con su precio. Usaste `Collectors.toMap`.

**P1.2 — `partitioningBy`:** partí los productos entre "caros" (precio > 1000) y el resto. Imprimí cuántos hay de cada lado.
**✅ Terminado:** un `Map<Boolean, List<Producto>>`, y mostrás el `size()` de `get(true)` y `get(false)`.

**P1.3 — `groupingBy` + `counting`:** cuántos productos hay por categoría.
**✅ Terminado:** un `Map<String, Long>` que imprime cada categoría con su cantidad.

**P1.4 — `groupingBy` + `summingInt`:** el stock total por categoría.
**✅ Terminado:** un `Map<String, Integer>` con la suma de stock de cada categoría.

**P1.5 — `groupingBy` + `mapping`:** los **nombres** de los productos por categoría (no el producto entero).
**✅ Terminado:** un `Map<String, List<String>>` donde cada categoría tiene la lista de nombres.

---

# PARTE 2 — Comparator: ¿qué es esa cosa?

## 2.1 — Desmitificando la palabra "Comparator"

Cuando hacés `.sorted()` pelado, Java usa el "orden natural": números de menor a mayor, Strings alfabéticamente. Pero un `Producto` o un `Empleado` **no tienen orden natural** — ¿por cuál campo los ordeno? Hay que decírselo.

> **El modelo mental:**
> Un **Comparator es un objeto que sabe comparar dos elementos para decidir cuál va primero.** No es un dato — es una "regla de ordenamiento" que le pasás a `.sorted(...)`.

`Comparator.comparing(campo)` **fabrica** ese objeto a partir de un campo:

```java
.sorted(Comparator.comparing(Producto::getPrecio))   // "ordená comparando por precio"
```

`Comparator.comparing(...)` significa: "armame una regla de orden que compare los elementos **por este campo**". El resultado es un Comparator, que `.sorted` usa para ordenar.

## 2.2 — La familia Comparator (tabla para la ojeada)

| Método | Qué hace |
|---|---|
| `comparing(campo)` | ordena por ese campo (ascendente, orden natural del campo) |
| `comparingInt/Long/Double(campo)` | igual, pero para campos numéricos primitivos (más eficiente) |
| `.reversed()` | invierte el orden (descendente) |
| `.thenComparing(campo2)` | **desempate**: si el primer campo empata, ordena por el segundo |
| `Comparator.naturalOrder()` | el orden natural (para streams de números/Strings directos) |
| `Comparator.reverseOrder()` | el orden natural al revés |
| `Comparator.nullsFirst(cmp)` / `nullsLast(cmp)` | manejar elementos `null` sin que explote |

Los que **todavía no usaste** y son oro: `comparingInt` (el correcto para números), `thenComparing` (orden multinivel), y `reverseOrder` (para streams de valores simples).

## 2.3 — El que ya conocés (repaso)

```java
// Ordenar por precio ascendente, y descendente con reversed:
.sorted(Comparator.comparing(Producto::getPrecio))
.sorted(Comparator.comparing(Producto::getPrecio).reversed())
```

## 2.4 — Los nuevos (repaso con ejemplos, dominio: libros)

### `comparingInt` / `comparingLong` / `comparingDouble` — la versión correcta para números

Igual que con `mapToInt` vs `map`, hay una versión especializada para campos numéricos primitivos que evita el "envolver/desenvolver" objetos. Para ordenar por un campo `int`/`long`/`double`, esta es la idiomática:

```java
.sorted(Comparator.comparingInt(Libro::getPaginas))      // campo int
.sorted(Comparator.comparingDouble(Libro::getPrecio))    // campo double
```

`comparing` a secas también funciona, pero `comparingInt` es más prolijo y eficiente para primitivos.

### `thenComparing` — orden multinivel (el desempate)

Esto es lo más útil que te faltaba. Cuando ordenás por un campo y hay **empates**, `thenComparing` define el criterio de desempate. Ejemplo: ordenar libros por género, y **dentro de cada género**, por título alfabético.

```java
List<Libro> ordenados = libros.stream()
        .sorted(Comparator.comparing(Libro::getGenero)        // 1ro: por género
                .thenComparing(Libro::getTitulo))              // empate → por título
        .toList();
```

Podés encadenar varios `thenComparing`. Y combinarlo con `reversed()` (ojo: `reversed()` invierte **todo** lo construido hasta ahí, prestá atención a dónde lo ponés).

### `reverseOrder` / `naturalOrder` — para streams de valores simples

Cuando el stream ya es de números o Strings (no de objetos), no necesitás extraer ningún campo:

```java
List<String> alfabeticoInverso = palabras.stream()
        .sorted(Comparator.reverseOrder())   // Z → A
        .toList();
```

---

## 🏋️ Ejercicios Parte 2 (seguí con tus `Producto`)

**P2.1 — `comparingDouble`:** ordená los productos por precio ascendente e imprimí nombre + precio. Usá `comparingDouble` (no `comparing` pelado), ya que el precio es `double`.
**✅ Terminado:** salen del más barato al más caro, usando `Comparator.comparingDouble`.

**P2.2 — `thenComparing` (el importante):** ordená los productos **por categoría** (alfabético) y, dentro de cada categoría, **por precio** ascendente. Imprimí nombre, categoría y precio para verificar el orden.
**✅ Terminado:** ves los productos agrupados visualmente por categoría, y dentro de cada una ordenados por precio. Usaste `comparing(...).thenComparing(...)`.

**P2.3 — multinivel con reversed:** ordená por categoría (alfabético) y, dentro de cada categoría, por precio **descendente** (del más caro al más barato dentro del grupo).
**✅ Terminado:** funciona. Pista: el `.reversed()` solo debe afectar al precio, no a la categoría — vas a tener que pensar dónde ponerlo, o usar la forma `thenComparing(Comparator.comparingDouble(...).reversed())`. Si te trabás acá, es un buen momento para preguntar por chat: este caso tiene una sutileza real.

---

# PARTE 3 — Desafío de cierre (mezcla todo)

Dominio libre (estudiantes, jugadores, canciones otra vez, lo que quieras). Creá la clase con Lombok y una lista de 8+ elementos. Resolvé:

1. Un `groupingBy` con sub-receta que NO sea `counting` (usá `summing`, `averaging` o `mapping`).
2. Un `toMap` de algún campo identificador → otro campo.
3. Un ordenamiento de **dos niveles** con `thenComparing`.
4. Un `partitioningBy` con alguna condición que tenga sentido en tu dominio.

**✅ Terminado cuando:** los cuatro salen **sin volver a abrir las tablas de este archivo**. Ese "sin volver a mirar" es la señal exacta que buscabas: la terminología ya es tuya.

---

# ✅ Checkpoint — ¿ahora sí, listo para Fase 2?

Marcá honesto:

1. Explicar en una frase qué es un **Collector** (una receta de en qué forma juntar) y qué es la clase `Collectors` (el recetario).
2. Usar `toMap`, `partitioningBy` y `groupingBy` con sub-receta sin googlear.
3. Explicar en una frase qué es un **Comparator** (un objeto que sabe ordenar dos elementos).
4. Hacer un orden de dos niveles con `thenComparing`.
5. Saber cuándo va `comparingInt/Double` en vez de `comparing` pelado.

Si estos cinco te salen fluidos, la terminología ya no te frena, y **ahí sí** Optional te va a resultar liviano (encima, varias terminales de Optional aparecen al final de los streams, así que llegás con el terreno preparado).

---

## 📎 Cheatsheet final (el único lugar donde mirar si dudás)

**Collectors** (`.collect(Collectors.___)`):
`toList()` · `toSet()` · `toMap(clave, valor)` · `joining(sep)` · `counting()` · `summingInt/Long/Double(campo)` · `averagingInt/Long/Double(campo)` · `groupingBy(criterio)` · `groupingBy(criterio, subReceta)` · `partitioningBy(condición)` · `mapping(transf, subReceta)`

**Comparator** (`.sorted(Comparator.___)`):
`comparing(campo)` · `comparingInt/Long/Double(campo)` · `.reversed()` · `.thenComparing(campo2)` · `naturalOrder()` · `reverseOrder()` · `nullsFirst(cmp)` / `nullsLast(cmp)`
