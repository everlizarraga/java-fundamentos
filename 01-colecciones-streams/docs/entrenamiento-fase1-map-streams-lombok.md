# 🔥 Entrenamiento Fase 1 — Lombok + Streams/Lambdas + Map (sin Optional)

> **Qué es esto:** un calentamiento de práctica pura para que la sintaxis básica de Java te salga sin pensar. Cuatro bloques, de menor a mayor. Cuando termines, lo "nuevo" (Optional en Fase 2, Tests después) lo vas a encarar con la cabeza libre.
>
> **Cómo se usa:** leés el repaso de cada bloque, hacés los ejercicios **tipeando vos el código**, y cuando cumplís el criterio de "✅ Terminado" pasás al siguiente. Si te trabás, lo charlamos por chat — no hace falta que pulas nada al infinito (Governor activado).
>
> **Regla de oro:** yo te doy la consigna y el criterio de terminado; el código lo tipeás vos. Los ejemplos de repaso van sobre un dominio **distinto** al del ejercicio a propósito, así no hay copy-paste posible.

---

## ⚙️ Setup (5 minutos, una sola vez)

No hace falta proyecto nuevo. **Reusá tu `proyecto-0`**, que ya tiene Maven y Lombok configurados desde la Etapa 3. Solo creá un package nuevo para no ensuciar lo anterior:

```
src/main/java/ar/edu/tu_apellido/proyecto0/
└── practica/
    └── fase1/
        ├── Main.java          ← acá corrés tus experimentos
        └── (las clases que vayas creando)
```

`Main.java` arranca así (recordá: en Java todo vive en una clase, y `main` es la puerta de entrada):

```java
package ar.edu.tu_apellido.proyecto0.practica.fase1;

public class Main {
    public static void main(String[] args) {
        System.out.println("Fase 1 arrancada");
        // Acá vas a ir probando cada ejercicio
    }
}
```

Para correr: botón ▶️ al lado de `main` en IntelliJ. Cada ejercicio lo probás llamándolo desde acá e imprimiendo con `System.out.println(...)`.

> **Nota:** en esta fase **no usamos JUnit todavía** (eso es más adelante). Verificás "a ojo" imprimiendo en consola. Está perfecto para calentar.

---

# 🅰️ Bloque A — Lombok + repaso de objeto (calentar los dedos)

**Objetivo:** que crear una clase de datos te lleve 20 segundos, no 5 minutos de tipear getters.

## Repaso: qué te genera Lombok

Lombok es una librería que **genera código repetitivo en tiempo de compilación** a partir de annotations. Vos escribís 5 líneas, el compilador ve 50.

| Annotation | Qué te genera |
|---|---|
| `@Getter` / `@Setter` | Los getters / setters de todos los campos |
| `@ToString` | Un `toString()` legible (para imprimir el objeto) |
| `@EqualsAndHashCode` | `equals()` y `hashCode()` |
| `@Data` | **Todo lo de arriba junto** (getter + setter + toString + equals + hashCode) |
| `@NoArgsConstructor` | Constructor vacío: `new Libro()` |
| `@AllArgsConstructor` | Constructor con todos los campos: `new Libro(titulo, autor, ...)` |
| `@Builder` | Permite crear con `Libro.builder().titulo("...").build()` |

**Ejemplo (dominio: libros — NO es tu ejercicio):**

```java
package ar.edu.tu_apellido.proyecto0.practica.fase1;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Libro {
    private String titulo;
    private String autor;
    private int paginas;
    private boolean leido;
}
```

Con esas 4 annotations ya podés:

```java
Libro l = new Libro("El Aleph", "Borges", 120, false);  // por @AllArgsConstructor
l.getTitulo();          // "El Aleph"        ← por @Data (getter)
l.setLeido(true);       //                   ← por @Data (setter)
System.out.println(l);  // Libro(titulo=El Aleph, autor=Borges, ...)  ← por @Data (toString)
```

### Puente con tu JS

```typescript
// En TS escribías esto y ya tenías todo:
class Libro {
  constructor(public titulo: string, public autor: string, public paginas: number) {}
}
```

En Java no existe ese atajo del constructor, **pero Lombok lo compensa**: declarás los campos `private` y las annotations te arman constructor + getters/setters. Es la forma "java-eña" de lograr lo mismo.

### Bonus: `@Builder` (cuando hay muchos campos)

Cuando una clase tiene 6+ campos, `new Cosa(a, b, c, d, e, f)` se vuelve ilegible (¿cuál era el tercero?). El builder lo arregla:

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Libro { ... }

// Crear con builder — cada campo nombrado, en cualquier orden:
Libro l = Libro.builder()
        .titulo("Ficciones")
        .autor("Borges")
        .paginas(200)
        .build();
```

> **Gotcha de Lombok:** si usás `@Builder`, agregá también `@AllArgsConstructor` y `@NoArgsConstructor`, porque el builder necesita el constructor con todos los campos. Es la combinación segura.

---

## 🏋️ Ejercicio A1 — Clase `Cancion`

Creá una clase `Cancion` con Lombok que tenga: `titulo` (String), `artista` (String), `duracionSegundos` (int), `genero` (String), `reproducciones` (long).

Usá `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`.

**✅ Terminado cuando:** desde `Main` podés crear una canción con `new Cancion(...)`, imprimirla con `System.out.println` y ver todos los campos, y podés hacer `cancion.getTitulo()` y `cancion.setReproducciones(1000L)` sin escribir vos ningún getter/setter.

> Pista: `reproducciones` es `long`, así que el literal lleva `L` al final: `5000000L`.

## 🏋️ Ejercicio A2 — `Cancion` con `@Builder`

Agregale `@Builder` a `Cancion` (más `@AllArgsConstructor` y `@NoArgsConstructor`). Creá una canción usando el builder, nombrando cada campo.

**✅ Terminado cuando:** creaste una `Cancion` con `Cancion.builder().titulo(...).artista(...)....build()` y la imprimís correctamente.

---

# 🅱️ Bloque B — Streams + Lambdas (tu zona de confort de JS, con otra cara)

**Objetivo:** que `.stream().filter(...).map(...).toList()` te salga tan natural como `.filter().map()` en JS.

## Repaso: el modelo mental

En JS, los arrays tienen los métodos pegados directo:

```javascript
const resultado = numeros.filter(n => n > 10).map(n => n * 2);
```

En Java, una `List` **no** tiene `.filter()` directo. Primero la convertís en un **stream** (una "cinta transportadora" de elementos), encadenás operaciones, y al final la "cerrás" volviendo a una `List` con `.toList()`:

```java
List<Integer> resultado = numeros.stream()      // 1. abrís el stream
        .filter(n -> n > 10)                     // 2. operación intermedia
        .map(n -> n * 2)                         // 3. otra intermedia
        .toList();                               // 4. operación terminal → vuelve a List
```

**La regla que tenés que grabar:**
- `.stream()` al principio (abrís la cinta).
- Operaciones intermedias en el medio (devuelven otro stream → se encadenan).
- **Una** operación terminal al final (cierra la cinta y produce el resultado).

Si te olvidás el `.toList()` (o la terminal que sea), el stream **no se ejecuta**. Es la diferencia más común que vas a sentir viniendo de JS.

## Repaso: las operaciones que vas a usar

**Ejemplo (dominio: números — NO es tu ejercicio):**

```java
import java.util.List;

List<Integer> nums = List.of(3, 8, 15, 22, 4, 30);
```

| Querés... | En JS | En Java | Resultado del ejemplo |
|---|---|---|---|
| Filtrar | `nums.filter(n => n > 10)` | `nums.stream().filter(n -> n > 10).toList()` | `[15, 22, 30]` |
| Transformar | `nums.map(n => n * 2)` | `nums.stream().map(n -> n * 2).toList()` | `[6, 16, 30, ...]` |
| ¿Alguno cumple? | `nums.some(n => n > 25)` | `nums.stream().anyMatch(n -> n > 25)` | `true` |
| ¿Todos cumplen? | `nums.every(n => n > 0)` | `nums.stream().allMatch(n -> n > 0)` | `true` |
| ¿Ninguno cumple? | `!nums.some(...)` | `nums.stream().noneMatch(n -> n > 100)` | `true` |
| Contar (tras filtrar) | `nums.filter(...).length` | `nums.stream().filter(...).count()` | `3` (devuelve `long`) |
| Ordenar | `[...nums].sort((a,b)=>a-b)` | `nums.stream().sorted().toList()` | `[3, 4, 8, ...]` |
| Primeros N | `nums.slice(0, 2)` | `nums.stream().limit(2).toList()` | `[3, 8]` |
| Sin repetidos | `[...new Set(nums)]` | `nums.stream().distinct().toList()` | (saca duplicados) |
| Sumar | `nums.reduce((a,b)=>a+b, 0)` | `nums.stream().mapToInt(n -> n).sum()` | `82` |

### Lambdas: misma idea que arrow functions

```java
n -> n > 10              // como  n => n > 10
(a, b) -> a + b          // como  (a, b) => a + b
p -> p.getTitulo()       // como  p => p.getTitulo()
```

### Method references (un atajo nuevo, no existe en JS)

Cuando tu lambda **solo llama a un método**, podés acortarla:

```java
.map(p -> p.getTitulo())     // lambda normal
.map(Cancion::getTitulo)     // method reference — equivalente, más corto
```

Leelo como "para cada elemento, llamá a su `getTitulo`". No lo fuerces; usalo cuando la lambda es literalmente "llamar un método y nada más".

### Ordenar por un campo (lo vas a necesitar en el ejercicio)

```java
import java.util.Comparator;

// Ordenar canciones por duración (ascendente):
lista.stream()
     .sorted(Comparator.comparing(Cancion::getDuracionSegundos))
     .toList();

// Descendente:
lista.stream()
     .sorted(Comparator.comparing(Cancion::getDuracionSegundos).reversed())
     .toList();
```

---

## 🏋️ Ejercicio B1 — Calentamiento con tus canciones

En `Main`, armá una lista de 6-8 `Cancion` variadas (distintos géneros, duraciones y reproducciones). Usá:

```java
List<Cancion> canciones = List.of(
    new Cancion("...", "...", 210, "rock", 1500000L),
    // ... 5-7 más, variadas
);
```

Después, **usando streams**, resolvé e imprimí cada una:

1. Las canciones que duran más de 200 segundos.
2. Solo los títulos de todas las canciones (lista de `String`).
3. ¿Hay alguna canción de género "jazz"? (booleano)
4. ¿Cuántas canciones son de género "rock"?
5. Los títulos de las canciones, **ordenadas por duración** ascendente.

**✅ Terminado cuando:** cada punto imprime un resultado correcto en consola, y cada uno usa `.stream()...terminal`. Si alguno te quedó con un `for` clásico, reescribilo con stream — el punto del ejercicio es la cinta transportadora.

## 🏋️ Ejercicio B2 — Encadenar varias operaciones

En una sola cadena de stream:

1. De las canciones de género "rock", quedate con sus títulos, ordenados alfabéticamente. (`filter` → `map` → `sorted` → `toList`)
2. La suma total de reproducciones de **todas** las canciones. (Pista: `mapToLong(Cancion::getReproducciones).sum()` porque es `long`).
3. Los títulos de las 3 canciones más reproducidas. (Pista: `sorted` con `reversed()` → `limit(3)` → `map` → `toList`).

**✅ Terminado cuando:** los tres salen bien y al menos uno encadena 3+ operaciones intermedias antes de la terminal.

---

# 🅲️ Bloque C — Map + estructuras anidadas

**Objetivo:** manejar `Map` con soltura, incluyendo cuando el valor es una lista u otro objeto (que es exactamente lo que aparece en el código del profe con `monedas` e `idiomas`).

## Repaso: Map básico

Un `Map<K, V>` es un diccionario clave→valor. Igual que el `Map` o un objeto plano de JS.

**Ejemplo (dominio: stock de un kiosco — NO es tu ejercicio):**

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> stock = new HashMap<>();   // clave String, valor Integer
stock.put("alfajor", 12);                        // agregar / actualizar
stock.put("gaseosa", 5);
stock.put("chicle", 30);

stock.get("alfajor");                 // 12
stock.containsKey("gaseosa");         // true
stock.getOrDefault("agua", 0);        // 0  (no existe "agua" → devuelve el default)
stock.size();                         // 3
```

| Querés... | En JS | En Java |
|---|---|---|
| Crear | `const m = new Map()` | `Map<String, Integer> m = new HashMap<>()` |
| Agregar/actualizar | `m.set("k", v)` | `m.put("k", v)` |
| Leer | `m.get("k")` | `m.get("k")` |
| ¿Existe la clave? | `m.has("k")` | `m.containsKey("k")` |
| Leer con default | `m.get("k") ?? 0` | `m.getOrDefault("k", 0)` |
| Cantidad | `m.size` | `m.size()` |

> **⚠️ Sobre `.get()` y los `null`:** si la clave no existe, `map.get("loquesea")` devuelve `null`. En esta fase **lo esquivamos a propósito** usando `getOrDefault(...)` o chequeando antes con `containsKey(...)`. El manejo elegante de "puede no estar" es justo lo que vamos a ver en la **Fase 2 con Optional** — por ahora, evitá el null con esas dos herramientas.

## Repaso: recorrer un Map

En JS hacés `for (const [k, v] of map)`. En Java tenés tres formas según qué necesites:

```java
// 1. Las claves:
for (String clave : stock.keySet()) {
    System.out.println(clave);
}

// 2. Los valores:
for (Integer cantidad : stock.values()) {
    System.out.println(cantidad);
}

// 3. Clave + valor juntos (la más usada):
for (Map.Entry<String, Integer> entrada : stock.entrySet()) {
    System.out.println(entrada.getKey() + " → " + entrada.getValue());
}

// 3-bis. Con lambda (más corto):
stock.forEach((producto, cantidad) -> System.out.println(producto + " → " + cantidad));
```

## Repaso: estructuras anidadas (acá está lo importante)

El valor de un Map puede ser **otra cosa compleja**: una `List`, o un objeto tuyo. Esto es exactamente lo que vas a encontrar en el `Pais` del profe (`Map<String, DetalleMoneda> monedas`).

**Valor = lista:** `Map<String, List<String>>`

```java
// Un map de "país" → "lista de ciudades"
Map<String, List<String>> ciudadesPorPais = new HashMap<>();
ciudadesPorPais.put("Argentina", List.of("Buenos Aires", "Córdoba", "Rosario"));
ciudadesPorPais.put("Chile", List.of("Santiago", "Valparaíso"));

ciudadesPorPais.get("Argentina");           // [Buenos Aires, Córdoba, Rosario]
ciudadesPorPais.get("Argentina").size();    // 3  (llegás a la lista y le pedís size)
ciudadesPorPais.get("Argentina").get(0);    // "Buenos Aires"
```

**Valor = objeto tuyo:** `Map<String, DetalleMoneda>` (estilo profe)

```java
// DetalleMoneda sería una clase con @Data { String nombre; String simbolo; }
Map<String, DetalleMoneda> monedas = new HashMap<>();
monedas.put("ARS", new DetalleMoneda("Peso argentino", "$"));
monedas.put("USD", new DetalleMoneda("US Dollar", "$"));

monedas.get("ARS").getNombre();   // "Peso argentino" → llegás al objeto y le pedís su getter
```

La clave para no marearte: **`map.get(clave)` te devuelve el valor; sobre ese valor seguís operando** como con cualquier objeto/lista. Es "encadenar accesos", igual que en JS harías `obj["ARS"].nombre`.

---

## 🏋️ Ejercicio C1 — Map básico de duraciones

Construí un `Map<String, Integer>` que mapee **título de canción → duración en segundos**, cargándolo a partir de tu lista de canciones del Bloque B (podés hacerlo con un `for` recorriendo la lista y un `put`, o más adelante con streams).

Después:
1. Imprimí la duración de una canción puntual por su título.
2. Pedí la duración de un título que **no existe** usando `getOrDefault(titulo, 0)` y verificá que devuelve 0 (sin explotar).
3. Recorré todo el map con `forEach` imprimiendo "título → duración".

**✅ Terminado cuando:** los tres puntos andan y usaste `getOrDefault` para el caso del título inexistente (sin tocar `null` a mano).

## 🏋️ Ejercicio C2 — Estructura anidada `Map<String, List<Cancion>>`

Armá un `Map<String, List<Cancion>>` que mapee **género → lista de canciones de ese género**. Cargalo a mano con `put` (ej: `mapa.put("rock", List.of(cancion1, cancion3))`).

Después:
1. Imprimí cuántas canciones hay en el género "rock" (`get("rock").size()`).
2. Imprimí los títulos de las canciones de "rock" recorriendo la lista interna.
3. Para un género que no existe, usá `getOrDefault("reggae", List.of())` y mostrá que devuelve lista vacía (size 0), sin romper.

**✅ Terminado cuando:** llegás a la lista interna desde el map y operás sobre ella, y el caso del género inexistente devuelve lista vacía limpia.

---

# 🆎 Bloque D — Combinado (streams + Map juntos)

**Objetivo:** que streams y maps dejen de ser dos cosas separadas. Acá agrupamos una lista en un map automáticamente — el patrón estrella de Java.

## Repaso: `groupingBy` (esto te va a volar la cabeza un poco, en el buen sentido)

En C2 cargaste el `Map<String, List<Cancion>>` a mano. Pero Java puede **agruparte una lista en un map automáticamente** según un criterio. El equivalente JS sería un `reduce` armando un objeto — pero acá es una línea:

**Ejemplo (dominio: empleados — NO es tu ejercicio):**

```java
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Tengo una lista plana de empleados y los quiero agrupar por departamento:
Map<String, List<Empleado>> porDepto = empleados.stream()
        .collect(Collectors.groupingBy(Empleado::getDepartamento));

// porDepto queda así, armado solo:
// { "Ventas" -> [emp1, emp4], "IT" -> [emp2, emp3], ... }
```

`Collectors.groupingBy(criterio)` toma cada elemento, calcula su clave con el criterio, y lo mete en la lista correspondiente. Si la clave no existía, crea la lista; si existía, agrega. **Vos no manejás el map a mano — el collector lo arma.**

Variante útil: **contar** por grupo en vez de listar:

```java
Map<String, Long> cantidadPorDepto = empleados.stream()
        .collect(Collectors.groupingBy(
                Empleado::getDepartamento,
                Collectors.counting()));
// { "Ventas" -> 2, "IT" -> 5, ... }
```

---

## 🏋️ Ejercicio D1 — Agrupar canciones por género (automático)

Tomá tu lista plana de canciones del Bloque B y, con **una sola cadena de stream**, generá:

1. Un `Map<String, List<Cancion>>` agrupado por género, usando `Collectors.groupingBy(Cancion::getGenero)`. Imprimí el map (vas a ver cada género con su lista).
2. Un `Map<String, Long>` con la **cantidad** de canciones por género, usando `groupingBy` + `Collectors.counting()`.

**✅ Terminado cuando:** ambos maps se generan en una línea de stream cada uno (sin `for` ni `put` manual) e imprimen lo esperado. Compará mentalmente con lo que hiciste a mano en C2 — esto es lo mismo, automático.

## 🏋️ Ejercicio D2 — Desafío de cierre (libre)

Cambiá de dominio para demostrarte que ya no dependés de "canciones". Elegí lo que quieras (productos de una tienda, jugadores de un equipo, juegos de tu biblioteca, lo que sea), creá la clase con Lombok, una lista de 6-8 elementos, y resolvé con streams/map:

1. Un filtro + transformación encadenados (ej: "nombres de los productos que cuestan más de X, ordenados por precio").
2. Un agrupamiento con `groupingBy` (ej: "productos por categoría").
3. Una cuenta o suma con stream (ej: "valor total del inventario" con `mapToInt`/`mapToLong` + `sum()`).

**✅ Terminado cuando:** armaste un dominio nuevo de cero y los tres puntos salen sin que tengas que volver a mirar los repasos de arriba. Ese "sin volver a mirar" es la señal de que la Fase 1 cumplió su objetivo.

---

# ✅ Checkpoint — ¿listo para la Fase 2 (Optional)?

Marcá mentalmente. Si todas te salen sin frenar, pasamos a Optional con tranquilidad:

1. Crear una clase de datos con Lombok (`@Data` + constructores) en menos de un minuto.
2. Escribir `lista.stream().filter(...).map(...).toList()` sin googlear el orden.
3. Saber que si te olvidás la operación terminal, el stream no corre.
4. Usar un method reference (`Clase::getCampo`) donde corresponde.
5. Crear un `Map`, leerlo con `getOrDefault`, recorrerlo con `forEach`.
6. Llegar a una lista u objeto que está **adentro** de un map y operar sobre él.
7. Agrupar una lista en un map con `groupingBy` sin pensarlo demasiado.

Si alguna te cuesta, decímelo y reforzamos ese punto puntual antes de avanzar. No hay apuro: la Fase 1 existe para que la Fase 2 sea liviana.

---

## 📎 Mini-cheatsheet JS ↔ Java (para tener a mano mientras practicás)

| Concepto | JS | Java |
|---|---|---|
| Filtrar lista | `arr.filter(x => ...)` | `lista.stream().filter(x -> ...).toList()` |
| Transformar | `arr.map(x => ...)` | `lista.stream().map(x -> ...).toList()` |
| ¿Alguno? | `arr.some(...)` | `lista.stream().anyMatch(...)` |
| ¿Todos? | `arr.every(...)` | `lista.stream().allMatch(...)` |
| Ordenar por campo | `arr.sort((a,b)=>a.x-b.x)` | `lista.stream().sorted(Comparator.comparing(C::getX)).toList()` |
| Sumar | `arr.reduce((a,b)=>a+b,0)` | `lista.stream().mapToInt(C::getX).sum()` |
| Crear map | `new Map()` | `new HashMap<>()` |
| Set / Get | `m.set(k,v)` / `m.get(k)` | `m.put(k,v)` / `m.get(k)` |
| Get con default | `m.get(k) ?? def` | `m.getOrDefault(k, def)` |
| Recorrer map | `for (const [k,v] of m)` | `m.forEach((k,v) -> ...)` |
| Agrupar lista→map | `arr.reduce(...)` armando objeto | `.collect(Collectors.groupingBy(C::getX))` |
| Lambda | `x => x + 1` | `x -> x + 1` |
| Method reference | — | `Clase::metodo` |

---

**FIN — Entrenamiento Fase 1**

**Siguiente:** Fase 2 → Optional, reconstruido de cero y con mucho amor.
