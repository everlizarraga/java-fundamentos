# 💛 Entrenamiento Fase 2 — Optional, reconstruido de cero (con amor)

> **Por qué este archivo:** Optional es de las cosas más usadas en el código que viene — el `buscarPorNombre` de cualquier servicio devuelve `Optional`, y media tabla de operaciones terminales de stream también. Pero viniendo de JavaScript es de las más fáciles de entender MAL, porque **se parece a cosas que no es**. Acá lo reconstruimos desde el problema que resuelve, despacio y sin asumir nada.
>
> **Cómo se usa:** como siempre. Leés cada sección, los ejercicios los tipeás vos, criterio "✅ Terminado" y avanzás. Si te trabás, chat.

---

## 0. 🧶 Primero, desarmar el nudo: Optional NO tiene nada que ver con async

Si venís de JS, es muy probable que tu cerebro haya metido a Optional en el cajón de las Promises: las dos son "cajas que envolvés y después abrís con métodos" (`then`/`await` por un lado, `map`/`orElse` por el otro). La forma se parece. **El significado no tiene nada que ver:**

| | Significa | La pregunta que responde |
|---|---|---|
| `Promise<Pais>` (JS) | un País que **todavía no llegó** | "¿**cuándo** lo tengo?" (tiempo) |
| `Optional<Pais>` (Java) | un País que **quizás no existe** | "¿lo tengo **o no**?" (presencia) |

El equivalente JS de `Optional<Pais>` **no es** `Promise<Pais>`. Es **`Pais | null`** — el null/undefined de toda la vida, pero hecho objeto explícito.

Y el dato que te libera: **acá no hay async en ningún lado.** En Java, el código normal es *bloqueante*: cuando llamás un método, la línea espera a que devuelva, y recién entonces sigue la próxima. Sin `await`, sin promesas, sin nada. Incluso las llamadas HTTP con `RestTemplate` (las del código que vas a leer más adelante) son bloqueantes: el programa frena hasta que llega la respuesta. Java *tiene* asincronismo (algo llamado `CompletableFuture`, su pariente de las Promises) — pero es **otro tema, de otro día, que no aparece en nada de lo que estamos haciendo**. Sacá "tiempo" y "espera" de la ecuación de Optional por completo. Cuando un método te devuelve un `Optional`, la búsqueda **ya terminó** — la respuesta es final: o encontró, o no.

---

## 1. 🕳️ El problema que Optional resuelve

En JS conocés este dolor de memoria:

```javascript
const user = findUser("ana");        // devuelve el user... o undefined
console.log(user.email);             // 💥 "Cannot read property 'email' of undefined"
```

La función *podía* no encontrar, vos te olvidaste de chequear, y el error explota en runtime — a veces lejos de donde estaba el verdadero bug.

Java tiene **exactamente el mismo problema** con `null`: un método devuelve `null` para decir "no encontré", el que llama se olvida del chequeo, y aparece el famoso `NullPointerException` (NPE).

El problema de fondo no es el null en sí — es que **la firma del método no te avisa**:

```java
Pais buscarPorNombre(String nombre)   // ¿puede devolver null? Mirando la firma... ni idea
```

La información "esto puede no encontrar nada" vive en la documentación, o en la memoria del que lo escribió. Y los humanos se olvidan.

**La jugada de Optional: poner el aviso EN EL TIPO.**

```java
Optional<Pais> buscarPorNombre(String nombre)   // la firma GRITA: "quizás no encuentro"
```

Ahora es imposible no enterarte: lo que te llega no es un `Pais` que podés usar directo (y olvidarte del chequeo) — es una **caja** que sí o sí tenés que abrir de alguna forma. Podés abrirla bien o mal, pero no podés *no verla*. El "quizás no hay" pasó de ser un detalle olvidable a ser parte del contrato del método, vigilado por el compilador.

---

## 2. 📦 El modelo mental: una caja con uno o nada

`Optional<T>` es una caja que **o contiene exactamente UN valor de tipo T, o está vacía**. Punto. Eso es todo el invento.

```
Optional[Producto(Detergente, ...)]   ← "busqué y te encontré esto"
Optional.empty                        ← "busqué y no había"
```

- **No** es una colección (nunca tiene "varios").
- **No** es una promesa (nada está "en camino" — la respuesta ya es final).
- **No** es magia: es literalmente un objetito con un campo adentro que puede estar vacío.

Traducción a tu idioma: es el `Pais | null` de JS, pero **convertido en objeto explícito y con herramientas atornilladas** para manejar el "¿y si no está?" sin if-manuales que te podés olvidar.

---

## 3. 🏭 De dónde salen los Optionals

Esto es clave: **vas a RECIBIR Optionals mucho más de lo que vas a crearlos.**

### a) De los streams (ya los venís usando sin saberlo)

¿Te acordás de la tabla de operaciones terminales? Varias devuelven `Optional`: `findFirst()`, `findAny()`, `min(...)`, `max(...)`. Ahora ya sabés por qué: ¿cuál es el primer elemento de un stream que quedó vacío después de filtrar? **No hay respuesta** — y Java, en vez de devolverte un null traicionero, te devuelve la caja vacía.

```java
Optional<Libro> masBarato = libros.stream()
        .min(Comparator.comparingDouble(Libro::getPrecio));
// Si la lista tenía libros → Optional[el más barato]. Si estaba vacía → Optional.empty
```

> Nota al margen: las versiones numéricas (`mapToInt(...).max()`, `.average()`) devuelven variantes primitivas (`OptionalInt`, `OptionalDouble`). Misma idea, nombres de métodos apenas distintos. Lo cruzamos cuando aparezca; no te distraigas con eso ahora.

### b) De métodos que escribís VOS (el patrón clásico)

Este es el patrón que vas a escribir mil veces — un buscador:

```java
public Optional<Libro> buscarPorTitulo(String titulo) {
    return libros.stream()
            .filter(l -> l.getTitulo().equalsIgnoreCase(titulo))
            .findFirst();
}
```

Fijate la belleza: `filter` + `findFirst` **ya fabrican el Optional por vos**. Si el filtro dejó algo, la caja viene llena; si no dejó nada, viene vacía. Muchas veces creás Optionals sin llamar nunca a un constructor.

### c) A mano (las 3 fábricas)

| Fábrica | Qué hace | Cuándo |
|---|---|---|
| `Optional.of(valor)` | envuelve el valor; si le pasás `null` → **NPE** | cuando estás seguro de que tenés algo |
| `Optional.ofNullable(valor)` | envuelve el valor; si es `null` → caja **vacía** | **EL PUENTE** desde el mundo-null |
| `Optional.empty()` | la caja vacía, explícita | cuando querés devolver "no hay" |

El importante es **`ofNullable`**: es el adaptador entre los dos mundos. Java está lleno de APIs viejas que devuelven `null` (el `Map.get` que esquivamos en Fase 1 con `getOrDefault`, por ejemplo). `ofNullable` agarra ese null-o-valor y lo convierte en caja:

```java
// Map.get devuelve null si la clave no está... ofNullable lo civiliza:
Optional<Integer> stock = Optional.ofNullable(stockPorNombre.get("yerba"));
```

Null-land y Optional-land, y `ofNullable` es el puente de un lado al otro.

---

## 4. 🎯 Qué hacer con un Optional en la mano — organizado por INTENCIÓN

Acá está el corazón del asunto, y el antídoto contra el mareo de "me tiraron ocho métodos y no sé cuál va". El secreto: **no memorices métodos — preguntate qué querés hacer.** Cada intención tiene SU método:

### Intención A — "dame el valor, y si no hay, usá este por defecto" → `orElse`

```java
String titulo = buscador.buscarPorTitulo("Dune")
        .map(Libro::getTitulo)
        .orElse("(sin resultado)");
```

**Es el `??` de JS.** `const titulo = libro?.titulo ?? "(sin resultado)"`. La caja se abre sí o sí: con lo de adentro, o con tu default. Después de esta línea ya no hay caja — hay un valor concreto, garantizado.

> Existe un primo, `orElseGet(() -> ...)`, para cuando el default es costoso de fabricar. La diferencia fina la vemos en Fase 3; por ahora, `orElse` y listo.

### Intención B — "si no hay, es un ERROR: explotá claro" → `orElseThrow`

```java
Libro libro = buscador.buscarPorTitulo("Dune").orElseThrow();
```

Si la caja está vacía, lanza `NoSuchElementException` **ahí mismo**, con un error claro en el lugar correcto — no un NPE misterioso 30 líneas después cuando alguien usa el null. Usalo cuando "no encontrar" significa que algo anda mal y no tiene sentido seguir.

### Intención C — "si hay algo, hacé esto con él (y si no, nada)" → `ifPresent`

```java
buscador.buscarPorTitulo("Dune")
        .ifPresent(l -> System.out.println("Encontrado: " + l.getTitulo()));
```

Es el `if (x) hacer(x)` de JS, empaquetado en un solo movimiento imposible de olvidar. No devuelve nada: es para *efectos* (imprimir, guardar, notificar).

### Intención D — "transformá lo de adentro SIN abrir la caja" → `map`

```java
Optional<Double> precio = buscador.buscarPorTitulo("Dune").map(Libro::getPrecio);
```

Si la caja tenía un libro → ahora tiene su precio. Si estaba vacía → **sigue vacía, sin explotar**. **Es el `?.` de JS** (`libro?.precio`): navegás hacia adentro con la red puesta.

Y acá viene **el patrón estrella**, que une los dos operadores que más amabas de JS:

```java
double precio = buscador.buscarPorTitulo("Dune")
        .map(Libro::getPrecio)     // el ?.
        .orElse(0.0);              // el ??
// En JS:  const precio = libro?.precio ?? 0.0
```

`map` + `orElse`. Encadenás transformaciones con la red puesta y al final aterrizás en un valor concreto. El 80% de tu uso de Optional va a ser esta dupla.

### Intención E — "solo quiero saber SI hay o no" → `isPresent()` / `isEmpty()`

Devuelven un boolean. Son legítimos **cuando el boolean ES la respuesta** ("¿existe un producto con ese nombre? → true/false"). Pero si después del if vas a sacar el valor... estás por caer en:

### 🚫 El antipatrón #1: `isPresent()` + `get()`

```java
// ❌ Esto es un null-check con pasos extra:
Optional<Libro> opt = buscador.buscarPorTitulo("Dune");
if (opt.isPresent()) {
    System.out.println(opt.get().getTitulo());
}

// ✅ Lo mismo, en el espíritu de Optional:
buscador.buscarPorTitulo("Dune")
        .map(Libro::getTitulo)
        .ifPresent(System.out::println);
```

¿Por qué es antipatrón? Porque reescribiste `if (x != null)` con más letras — Optional no te aportó **nada**. Los métodos por intención existen justamente para que chequear-y-usar sea UN movimiento que no podés olvidar a medias.

Y `.get()` pelado (sin chequear antes) es peor todavía: es *exactamente* usar la variable sin chequear null. Caja vacía → `NoSuchElementException`. **Regla simple: `.get()` no se usa.** Si tu situación es "sé que está", declaralo con `orElseThrow()` — hace lo mismo, pero el nombre dice tu intención.

### 📎 La tabla por intención (tu nueva brújula)

| Querés... | Método | En JS era... |
|---|---|---|
| el valor, o un default | `orElse(def)` | `x ?? def` |
| el valor, o reventar claro | `orElseThrow()` | `if (!x) throw ...` |
| hacer algo si está | `ifPresent(v -> ...)` | `if (x) hacer(x)` |
| transformar sin abrir | `map(f)` | `x?.campo` |
| transformar y aterrizar | `map(f).orElse(def)` | `x?.campo ?? def` |
| solo el sí/no | `isPresent()` / `isEmpty()` | `x != null` / `x == null` |
| ~~(no se usa)~~ | ~~`get()`~~ | usar sin chequear |

---

## 5. 🧭 Tres reglas de oro para arrancar bien

1. **Optional nació para los RETORNOS de métodos** ("busqué, quizás no encontré"). No lo uses en campos de clases ni en parámetros — eso tiene letra chica que vemos en Fase 3. Por ahora: Optional = lo que un buscador devuelve.
2. **Un método que devuelve Optional JAMÁS devuelve null.** Si no hay resultado, devuelve `Optional.empty()`. (Devolver null donde prometiste Optional es la traición máxima: el que te llama confió en que la caja existe.)
3. **Elegí el método por intención, no por memoria.** ¿Default? `orElse`. ¿Error? `orElseThrow`. ¿Efecto? `ifPresent`. ¿Transformar? `map`. Esa pregunta-respuesta es todo el sistema.

---

## 🏋️ Ejercicios (dominio: tus `Producto` y `PRODUCTOS` de la fase extra)

### E1 — Tu primer productor de Optionals

Escribí un método `Optional<Producto> buscarPorNombre(String nombre)` sobre tu lista `PRODUCTOS`, usando `filter` + `findFirst` y comparando con `equalsIgnoreCase`.

**✅ Terminado:** imprimís el resultado de buscar `"Detergente"` y `"Lavandina"` — el primero muestra `Optional[Producto(...)]`, el segundo `Optional.empty`. (Imprimir el Optional crudo está perfecto en ESTE ejercicio: el objetivo es *ver* los dos estados de la caja con tus propios ojos.)

### E2 — `orElse` (el `??`)

Usando tu buscador, obtené **el precio** de un producto por nombre, o `-1.0` si no existe. **Una sola cadena**: buscar → `map` → `orElse`.

**✅ Terminado:** `"Detergente"` da su precio; `"Lavandina"` da `-1.0`. Sin `if`, sin `isPresent`, sin variables intermedias.

### E3 — `orElseThrow` (el error claro)

Escribí `Producto obtenerObligatorio(String nombre)` que devuelve el producto o explota.

**✅ Terminado:** con un nombre válido devuelve el producto y seguís normal; con uno inválido el programa termina mostrando `NoSuchElementException`. **Está bien que explote** — el objetivo es que VEAS el error: mirá el stacktrace y notá que apunta exactamente a la línea del `orElseThrow`, clarito. Compará mentalmente con un NPE que aparece quién sabe dónde.

### E4 — `ifPresent` (el efecto)

Buscá un producto y, si está, imprimí `"STOCK: <n>"` usando `ifPresent`. Si no está, no debe pasar nada (ni imprimir, ni explotar).

**✅ Terminado:** con `"Papas Fritas"` imprime su stock; con `"Lavandina"` no imprime nada y el programa sigue vivo.

### E5 — `ofNullable` (el puente desde null-land)

Armá un `Map<TipoProducto, Integer> descuentoPorTipo` con descuento para **solo 2** de los 4 tipos (ej: `BEBIDA → 10`, `SNACK → 25`). Escribí un método que, dado un `Producto`, devuelva su % de descuento — o `0` si su tipo no tiene descuento — usando `Optional.ofNullable(map.get(...)).orElse(0)`.

**✅ Terminado:** un producto BEBIDA da 10; uno LACTEO da 0; nada explota.

> "¿Pero esto no era `getOrDefault`?" — sí, para ESTE caso puntual `getOrDefault` alcanza (por eso lo usamos en Fase 1, cuando Optional no existía para vos). La diferencia: `getOrDefault` es un atajo que solo existe en Map; **`ofNullable` es el puente universal** — funciona con *cualquier* API que devuelva null, y además te habilita encadenar `map`/`filter` en el camino. Acá practicás el puente general.

### E6 — Refactor del antipatrón

Te regalo código que funciona pero traiciona el espíritu. Reescribilo como UNA cadena, sin `if`, sin `isPresent`, sin `get`:

```java
Optional<Producto> opt = buscarPorNombre("Papas Fritas");
if (opt.isPresent()) {
    System.out.println(opt.get().getNombre().toUpperCase());
} else {
    System.out.println("NO ENCONTRADO");
}
```

**✅ Terminado:** misma salida exacta en ambos casos (existente e inexistente), en una cadena. Pista: las dos ramas del if te están diciendo qué dupla usar.

---

## ✅ Checkpoint — ¿listo para la Fase 3?

1. Explicar en una frase la diferencia entre un `Optional` y una `Promise`.
2. Explicar por qué `Optional<Pais>` en la firma es mejor que un `Pais` que "puede ser null".
3. Escribir el patrón `filter` + `findFirst` sin mirar.
4. Elegir método **por intención** sin dudar: ¿default? ¿error? ¿efecto? ¿transformación?
5. Reescribir un `isPresent()+get()` como cadena.
6. Decir qué hace `ofNullable` y por qué es "el puente".

Si esto fluye, en la **Fase 3** viene el arsenal completo: `orElseGet` vs `orElse` (la diferencia fina), `filter` y `flatMap` sobre Optional, `ifPresentOrElse`, el catálogo de malas prácticas, el **árbol de decisión completo** de "qué método cuándo", y un mini-proyecto integrador estilo catálogo-con-búsquedas — el ensayo general para leer el `buscarPorNombre` del código real sin pestañear.
