# 📚 Apunte — Listas en Java: construir, recorrer y procesar (con los puentes a JS)

> **Para quién es esto:** alguien que viene de JavaScript, donde un array (`[]`) es siempre mutable, hacés `push` y `[...a, ...b]` sin pensarlo, y recorrés con `for...of`. En Java hay **varias formas de crear una lista** (con una diferencia clave que en JS casi no existe: **mutable vs inmutable**), y además algunas piezas de sintaxis que **se parecen mucho a JS pero no son lo mismo** (`...`, `::`). Acá queda todo aclarado y junto.
>
> **Está dividido en dos partes:** la **Parte A** es construir listas. La **Parte B** son tres cositas relacionadas (recorrer y procesar colecciones) que se confunden fácil con JS y conviene tener al lado.

---

## 0. 🎯 TL;DR

**Construir:**
- `List.of(a, b, c)` → **inmutable**. Si le hacés `.add()`, explota en ejecución (`UnsupportedOperationException`). Para listas fijas que solo leés.
- `new ArrayList<>()` + `.add(...)` → **mutable**. Para construir de a poco.
- `new ArrayList<>(List.of(a, b, c))` → **mutable Y con elementos iniciales**. El mejor de los dos mundos.
- Agregar de a varios: `.addAll(otraLista)`.
- "Spread" (`[...a, ...b]`): Java no lo tiene; se hace con `addAll` o `Stream.concat`.

**Recorrer y procesar (Parte B):**
- `String... nombres` en la firma de un método = **varargs** (cantidad indefinida de argumentos). El primo del *rest* de JS.
- `for (String n : nombres)` = **for-each**. El primo del `for...of` de JS.
- `String::toUpperCase` = **method reference**. Atajo de lambda, solo cuando la lambda únicamente llama a un método.

---

# PARTE A — Construir listas

## 1. ⚠️ El concepto que en JS casi no existe: mutable vs inmutable

En JavaScript, un array es **mutable** por defecto: lo creás y le hacés `push`, `splice`, lo que sea. Tendrías que esforzarte (`Object.freeze`) para que sea inmutable.

En Java es según cómo la crees:

- **Lista mutable** = la podés modificar después de crearla (agregar, sacar, cambiar elementos).
- **Lista inmutable** = es de **solo lectura**. Podés recorrerla, filtrarla, leerla... pero si intentás modificarla, **falla en ejecución** (no al compilar — esto es lo traicionero).

```java
List<String> inmutable = List.of("a", "b");
inmutable.add("c");   // ⚠️ COMPILA, pero al correr lanza UnsupportedOperationException
```

> 💡 **Por qué importa:** si tenés datos fijos (configuración, opciones, una lista de prueba que solo leés), una inmutable es **más segura** — nadie la rompe sin querer. Pero si necesitás **ir armando** la lista (agregar en un bucle, por ejemplo), necesitás sí o sí una **mutable**. Elegir mal = crash en ejecución.

---

## 2. 🛠️ Las formas de construir una lista (tabla de referencia)

| Forma | ¿Mutable? | Cuándo usarla | Import necesario |
|---|:---:|---|---|
| `new ArrayList<>()` + `.add(...)` | ✅ Mutable | Construir de a poco, en un bucle | `java.util.ArrayList` |
| `new ArrayList<>(List.of(a, b, c))` | ✅ Mutable | Arrancar con elementos pero poder seguir modificando | `java.util.ArrayList` |
| `List.of(a, b, c)` | ❌ Inmutable | Lista fija, solo lectura (datos de prueba, constantes) | `java.util.List` |
| `Arrays.asList(a, b, c)` | ⚠️ Tamaño fijo | Casi no la uses; ver nota abajo | `java.util.Arrays` |
| `stream...toList()` | ❌ Inmutable | El resultado de filtrar/transformar con streams | (ninguno extra) |
| `List.copyOf(otraLista)` | ❌ Inmutable | Sacar una copia de solo lectura de otra lista | `java.util.List` |

### Detalle de cada una

**`new ArrayList<>()` — la mutable clásica:**
```java
List<String> frutas = new ArrayList<>();
frutas.add("manzana");
frutas.add("banana");
frutas.remove("manzana");   // todo permitido
```

**`new ArrayList<>(List.of(...))` — mutable con valores iniciales (muy útil):**
```java
// Arranca con 3 elementos, pero PODÉS seguir agregando/sacando:
List<String> colores = new ArrayList<>(List.of("rojo", "verde", "azul"));
colores.add("amarillo");   // ✅ funciona, porque el ArrayList exterior es mutable
```
Esto es lo que querés cuando pensás "quiero arrancar con datos pero después modificar". El `List.of` de adentro solo sirve para *llenar* el `ArrayList`, que es el que manda.

**`List.of(...)` — inmutable, la más limpia para datos fijos:**
```java
List<String> diasHabiles = List.of("lun", "mar", "mie", "jue", "vie");
// Perfecta para algo que solo vas a leer. No se puede tocar.
```
> Detalle extra: `List.of(...)` **no admite `null`** entre sus elementos (tira `NullPointerException` si le pasás uno). Es a propósito, para listas "limpias".

**`Arrays.asList(...)` — la vieja, con una trampa:**
```java
List<String> lista = Arrays.asList("a", "b", "c");
lista.set(0, "z");   // ✅ permitido (podés CAMBIAR elementos)
lista.add("d");      // ❌ explota (NO podés agregar/sacar — tamaño fijo)
```
Es de antes de Java 9 (cuando no existía `List.of`). Tiene un comportamiento raro de "tamaño fijo pero elementos cambiables" que confunde. **Recomendación: usá `List.of` para inmutables y `new ArrayList<>(...)` para mutables, y olvidate de `Arrays.asList`** salvo que la veas en código ajeno.

**`stream...toList()` — el resultado de un stream:**
```java
List<String> mayus = palabras.stream()
        .map(String::toUpperCase)
        .toList();   // inmutable (Java 16+)
```
Cuando filtrás/transformás, lo que sale es inmutable. Si necesitás modificar ese resultado, envolvelo: `new ArrayList<>(stream...toList())`.

---

## 3. ➕ Agregar de a VARIOS elementos (lo que viste hacer al profe)

Hacer `.add()` uno por uno es tedioso. Para agregar varios de una:

**`.addAll(coleccion)` — agrega todos los de otra lista/colección (el más común):**
```java
List<String> base = new ArrayList<>(List.of("a", "b"));
List<String> extra = List.of("c", "d", "e");

base.addAll(extra);   // ahora base = [a, b, c, d, e]
```
Sirve para "pegar" una lista al final de otra. (Ojo: la lista a la que le hacés `addAll` tiene que ser **mutable**.)

**`Collections.addAll(lista, a, b, c)` — agrega elementos sueltos (varargs):**
```java
import java.util.Collections;

List<String> nombres = new ArrayList<>();
Collections.addAll(nombres, "Ana", "Beto", "Caro");   // los tres de una
```

---

## 4. 🌟 El "spread operator" de JS en Java

En JS hacés esto todo el tiempo:
```javascript
const combinada = [...lista1, ...lista2];   // unir dos
const conExtra  = [...lista, nuevo];         // copiar + agregar uno
```

Java **no tiene** un operador spread en la *llamada*. Pero el mismo resultado se logra así:

**Unir dos listas → `Stream.concat`:**
```java
import java.util.stream.Stream;

List<String> combinada = Stream.concat(lista1.stream(), lista2.stream())
        .toList();
```

**O con un ArrayList y `addAll` (más imperativo, igual de válido):**
```java
List<String> combinada = new ArrayList<>(lista1);   // copia de lista1
combinada.addAll(lista2);                            // le pega lista2
```

**Copiar + agregar uno (`[...lista, nuevo]`):**
```java
List<String> conExtra = new ArrayList<>(lista);
conExtra.add(nuevo);
```

> ⚠️ **Ojo, no confundir:** acá hablamos del spread de la *llamada* (`[...a, ...b]`), que Java NO tiene. Pero el `...` SÍ existe en Java en otro lugar — en la **declaración** de un método. Eso es el siguiente punto, y es otra cosa.

---

# PARTE B — Recorrer y procesar (cositas que se confunden con JS)

## 5. ⚙️ Los tres puntitos `...` en una firma = varargs (NO es el spread)

Si viste al profe escribir `...` en la **declaración de un método**, eso es **varargs** (argumentos variables): un método que acepta **una cantidad indefinida de argumentos** del mismo tipo. Se escribe igual que el spread de JS, por eso confunde — pero es otra cosa.

```java
public void imprimirNombres(String... nombres) {
    // adentro, "nombres" se comporta como un array String[]
    for (String n : nombres) {
        System.out.println(n);
    }
}
```

Y lo llamás con **los que quieras**, sueltos:
```java
imprimirNombres("Ana");                    // 1 argumento
imprimirNombres("Ana", "Beto", "Caro");    // 3
imprimirNombres();                          // 0, también vale
```

**La conexión que cierra el círculo:** ¿te acordás que `List.of("a", "b", "c")` acepta cualquier cantidad de elementos? Es **exactamente por esto** — por dentro está declarado con varargs (`List.of(E... elements)`). Lo mismo `System.out.printf`, `String.format`, etc.

### Rest (JS) vs Spread (JS) vs Varargs (Java)

En JS, los `...` hacen **dos** trabajos según dónde estén. Esta es la distinción clave:

| `...` en... | En JS | En Java |
|---|---|---|
| la **definición** del método (juntar argumentos) | *rest*: `function f(...args)` | **varargs**: `void f(String... args)` ✅ existe |
| la **llamada** (desparramar un array) | *spread*: `f(...miArray)` | ❌ no existe como operador |

O sea: el `...` que viste en Java es el primo del **rest** de JS (en la firma, juntando), no del **spread** (en la llamada, desparramando).

> Detalle práctico: como por dentro `nombres` es un array, si ya tenés un array podés pasárselo directo: `imprimirNombres(miArrayDeStrings)`. Esa es la única forma en que Java se acerca a "desparramar un array en argumentos".
>
> Regla: el parámetro varargs tiene que ser **el último** de la firma.

---

## 6. 🔁 Recorrer una lista: el for-each `for (x : lista)`

El **for-each** ("para cada") es el primo directo del `for...of` de JS:

```java
for (String n : nombres) {
    System.out.println(n);
}
```

Se lee: **"para cada `String n` dentro de `nombres`, hacé esto"**. Las tres partes:
- `String n` → el tipo y nombre de la variable que toma **cada elemento** en cada vuelta.
- `:` → leelo como "en" / "de" (el `of` de JS).
- `nombres` → la colección o array que recorrés.

**Puente con JS:**
```javascript
for (const n of nombres) { console.log(n); }          // JS
```
```java
for (String n : nombres) { System.out.println(n); }   // Java, lo mismo
```

**For-each vs for clásico:** el for-each **no te da el índice**, te da directo el elemento. Por eso es más corto cuando no necesitás saber "en qué posición estoy":
```java
// Clásico (cuando necesitás el índice i):
for (int i = 0; i < nombres.size(); i++) {
    String n = nombres.get(i);
}
// For-each (cuando solo querés cada elemento):
for (String n : nombres) { ... }
```

**Cuándo NO usar for-each:**
- Cuando necesitás el índice → for clásico.
- Cuando vas a **modificar la colección mientras la recorrés** (sacar elementos) → tira `ConcurrentModificationException`. Usá for clásico o un iterador.

Funciona sobre arrays, `List`, `Set`, y cualquier cosa "iterable". (Un `Map` no se recorre directo así; ahí usás `.entrySet()`, `.keySet()` o `.values()`.)

---

## 7. ⚡ Method references `::` — el atajo de lambda

El `::` es una forma de escribir una lambda **solo en situaciones especiales**: cuando la lambda **no hace nada más que llamar a un método**.

```java
.map(s -> s.toUpperCase())   // lambda normal
.map(String::toUpperCase)    // method reference — EQUIVALENTE, más corto
```

Leelo como: **"a cada elemento, aplicale su `toUpperCase`"**.

**La regla de oro (el "solo en situaciones especiales"):** podés usar `::` **únicamente** si la lambda es literalmente "llamar un método y pasarle los argumentos tal cual". Si hace **cualquier otra cosa** (sumar, comparar, concatenar, computar), tenés que usar lambda:

```java
.map(String::toUpperCase)          // ✅ solo llama al método → se puede
.map(s -> s.toUpperCase() + "!")   // ❌ hace algo extra (+"!") → tiene que ser lambda
```

**Las formas que vas a ver** (no las memorices, solo reconocelas):

| Forma | Method reference | Equivale a la lambda |
|---|---|---|
| Método estático | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Método de un objeto fijo | `System.out::println` | `x -> System.out.println(x)` |
| Método del propio elemento | `String::toUpperCase` | `s -> s.toUpperCase()` |
| Constructor | `Cancion::new` | `() -> new Cancion()` |

La tercera (`String::toUpperCase`) es la que más confunde: parece que falta el argumento, pero **el elemento mismo es sobre quien se llama el método**. `Cancion::getTitulo` = "a cada canción, pedile su `getTitulo`".

**Puente con JS:** en JS a veces pasás una función por nombre en vez de envolverla:
```javascript
arr.forEach(console.log)        // en vez de  arr.forEach(x => console.log(x))
arr.map(Number)                 // en vez de  arr.map(x => Number(x))
```
El `::` es la versión ordenada y tipada de Java de esa misma idea: "pasá el método por nombre, sin envolverlo en una lambda".

---

## 8. 🧭 Guía rápida: ¿qué uso cuándo?

| Lo que necesito | Usá esto |
|---|---|
| Una lista fija que solo voy a leer | `List.of(a, b, c)` |
| Ir armando una lista de a poco (bucle, etc.) | `new ArrayList<>()` + `.add()` |
| Arrancar con datos PERO poder modificar después | `new ArrayList<>(List.of(a, b, c))` |
| Pegar una lista al final de otra | `lista.addAll(otra)` (la primera debe ser mutable) |
| Unir dos listas en una nueva | `Stream.concat(a.stream(), b.stream()).toList()` |
| Una copia de solo lectura de otra lista | `List.copyOf(otra)` |
| Recorrer leyendo cada elemento | `for (Tipo x : lista)` (for-each) |
| Recorrer necesitando el índice | `for (int i = 0; ...)` clásico |
| Un método que recibe "0 o más" de algo | varargs: `metodo(Tipo... cosas)` |
| Una lambda que solo llama a un método | method reference: `Clase::metodo` |

---

## 9. 🪤 La trampa #1 para que no te agarre

El error que vas a cometer al menos una vez (todos lo hacemos viniendo de JS):

```java
List<String> lista = List.of("a", "b");   // inmutable, no lo notás
// ...más abajo en el código...
lista.add("c");   // 💥 UnsupportedOperationException en ejecución
```

**Compila sin chistar**, así que el rojo no te avisa. Recién falla cuando corrés. Si ves `UnsupportedOperationException`, el 99% de las veces es esto: estás intentando modificar una lista inmutable. Solución: si necesitás modificarla, creala mutable desde el principio (`new ArrayList<>(...)`).

---

## 🌅 Para recordar

**Construir:**
- En Java la mutabilidad **depende de cómo creaste la lista**, no es siempre mutable como en JS.
- `List.of(...)` = inmutable (solo lectura). `new ArrayList<>(...)` = mutable.
- ¿Vas a modificarla? → mutable. ¿Solo leerla? → inmutable (más segura).
- "Agregar varios" = `addAll`. "Spread" (`[...a,...b]`) = `Stream.concat` o `addAll`.
- Si te salta `UnsupportedOperationException`, casi seguro tocaste una inmutable.

**Recorrer y procesar (las que se confunden con JS):**
- `Tipo... x` en una firma = **varargs** (0 o más argumentos). Es el *rest* de JS, no el *spread*.
- `for (Tipo x : lista)` = **for-each**, el `for...of` de JS. No da índice.
- `Clase::metodo` = **method reference**, atajo de lambda, solo cuando la lambda únicamente llama a un método.
