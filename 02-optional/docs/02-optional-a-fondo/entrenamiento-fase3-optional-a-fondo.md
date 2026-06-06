# 🏆 Entrenamiento Fase 3 — Optional a fondo: el arsenal completo

> **Por qué este archivo:** ya tenés el modelo mental (la caja), las intenciones básicas (`orElse`, `orElseThrow`, `ifPresent`, `map`) y el puente desde null-land (`ofNullable`). Esta fase completa el arsenal con los métodos que faltan, te da el **catálogo de dónde NO usar Optional**, el **árbol de decisión completo**, y cierra con un mini-proyecto integrador que tiene la misma forma que los servicios del código real que vas a leer en la materia.
>
> **Cómo se usa:** como siempre. Los ejemplos de repaso van sobre libros/empleados; los ejercicios, sobre tus `Producto`. Tipeás vos, criterio "✅ Terminado", chat si te trabás.

---

# PARTE 1 — El arsenal que falta

## 1.1 — `orElse` vs `orElseGet`: la diferencia fina (y la trampa para gente de JS)

Te debía esta. Los dos dan un default si la caja está vacía. La diferencia es **cuándo se fabrica ese default**:

- `orElse(valor)` → el argumento se evalúa **SIEMPRE**, esté la caja llena o vacía.
- `orElseGet(() -> valor)` → la lambda se ejecuta **solo si la caja está vacía**.

¿Por qué existe la trampa? Acá viene el insight para tu cabeza de JS. En JavaScript, `??` es perezoso de fábrica:

```javascript
const p = buscar("Dune") ?? fabricarDefault();   // fabricarDefault() solo corre si buscar dio null
```

En Java, `orElse(...)` es **una llamada a método**, y Java evalúa los argumentos de un método **antes** de llamarlo, siempre. Así que:

```java
Libro l = buscar("Dune").orElse(fabricarDefault());   // ⚠️ fabricarDefault() corre SIEMPRE,
                                                       //    aunque "Dune" exista y el default se tire
```

La lambda de `orElseGet` es la forma que tiene Java de decir "no lo evalúes todavía" — es el equivalente manual de la pereza que JS te daba gratis:

```java
Libro l = buscar("Dune").orElseGet(Libro::fabricarDefault);   // ✅ solo corre si está vacío
```

**La regla práctica:**

| El default es... | Usá | Ejemplo |
|---|---|---|
| Una constante o algo barato | `orElse` | `orElse(0)`, `orElse("(sin resultado)")`, `orElse(-1.0)` |
| Una llamada a método, un `new`, algo costoso o con efectos | `orElseGet` | `orElseGet(Libro::fabricarDefault)` |

Con constantes da exactamente igual (evaluar `0` no cuesta nada) — por eso en Fase 2 usamos `orElse` tranquilos. La diferencia importa cuando fabricar el default cuesta o tiene efectos secundarios. En el mini-proyecto lo vas a **ver** con tus propios ojos.

## 1.2 — `ifPresentOrElse`: el efecto con dos ramas

`ifPresent` hacía algo solo si la caja estaba llena. Su versión completa maneja **las dos ramas como acciones**:

```java
buscador.buscarPorTitulo("Dune").ifPresentOrElse(
        l -> System.out.println("Encontrado: " + l.getTitulo()),   // si está
        () -> System.out.println("No hay resultados")               // si no está
);
```

Es el `if/else` de los **efectos** (imprimir, loguear, notificar). No confundir con `map+orElse`, que es el if/else de los **valores**: si lo que necesitás es *un valor* para seguir trabajando → `map+orElse`; si lo que necesitás es *hacer algo distinto* en cada rama → `ifPresentOrElse`.

## 1.3 — `filter`: quedate con el valor solo si además cumple algo

`filter` sobre Optional funciona igual que en streams, pero sobre la caja de uno:

- Caja llena y el valor **cumple** el predicado → sigue llena.
- Caja llena y el valor **no cumple** → se **vacía**.
- Caja vacía → sigue vacía.

```java
// "Dame el libro... pero solo si está en stock":
Optional<Libro> disponible = buscador.buscarPorTitulo("Dune")
        .filter(l -> l.getStock() > 0);
```

Encadenado es donde brilla — "encontralo, validalo, transformalo, aterrizá":

```java
String oferta = buscador.buscarPorTitulo("Dune")
        .filter(l -> l.getStock() > 0)        // existe Y hay stock
        .map(Libro::getTitulo)
        .orElse("(no disponible)");
```

En JS esto era el chequeo compuesto medio torpe: `const t = (libro && libro.stock > 0) ? libro.titulo : "(no disponible)"`. Acá es un eslabón más de la cadena.

## 1.4 — `or()`: si no está acá, buscá en OTRA fuente

`orElse` te saca de la caja con un **valor**. `or()` se queda **en el mundo-caja**: si está vacía, prueba con *otro Optional* (otra fuente de búsqueda):

```java
// Buscar en el catálogo principal; si no está, en el de usados:
Optional<Libro> resultado = catalogoNuevos.buscarPorTitulo("Dune")
        .or(() -> catalogoUsados.buscarPorTitulo("Dune"));
```

Fijate que recibe una **lambda que devuelve un Optional** (perezosa, como `orElseGet`: la segunda búsqueda solo corre si la primera falló). Y como el resultado sigue siendo Optional, podés seguir encadenando: dos fuentes, después `filter`, después `map`, y recién al final aterrizar.

En JS era `buscarA(x) ?? buscarB(x)` — dos fuentes nullables encadenadas. Mismo espíritu.

**El mapa de los tres "orElse-parientes":**

| Método | Si la caja está vacía... | Te deja en... |
|---|---|---|
| `orElse(v)` / `orElseGet(s)` | te da un **valor** default | fuera de la caja (un `T`) |
| `orElseThrow(...)` | **explota** | fuera de la caja (un `T`, o excepción) |
| `or(s)` | prueba **otro Optional** | dentro de la caja (otro `Optional<T>`) |

## 1.5 — `flatMap`: cuando la transformación TAMBIÉN puede no encontrar

El caso: tenés una caja, y la función que querés aplicarle **devuelve a su vez un Optional**. Si usás `map`, mirá lo que pasa con los tipos:

```java
// obtenerEditorial(Libro) devuelve Optional<Editorial> (un libro puede no tener editorial cargada)
Optional<Optional<Editorial>> ups = buscador.buscarPorTitulo("Dune")
        .map(l -> obtenerEditorial(l));        // 😵 caja DENTRO de caja
```

`Optional<Optional<Editorial>>` — una caja con otra caja adentro. Inútil de manejar. `flatMap` hace lo mismo que `map` pero **aplana**: las dos posibles ausencias (no encontré el libro / el libro no tiene editorial) colapsan en una sola caja:

```java
Optional<Editorial> editorial = buscador.buscarPorTitulo("Dune")
        .flatMap(l -> obtenerEditorial(l));    // ✅ una sola caja
```

**La regla para elegir entre `map` y `flatMap`** (es mecánica, ni lo pienses):
- La función devuelve un valor **plano** (`getTitulo` → String) → `map`.
- La función devuelve un **Optional** (otra búsqueda, otro "quizás") → `flatMap`.

El puente JS: esto es el `?.` encadenado donde **cada salto** puede fallar: `pedido?.cliente?.direccion`. Cada `?.` es un flatMap conceptual: navegás hacia adentro y en cada paso puede no haber nada, pero al final tenés UNA respuesta (el valor o undefined), no una matrioshka.

> Bonus de cultura general: los **streams** también tienen `flatMap`, con la misma idea de "aplanar" (un `Stream<List<T>>` → `Stream<T>`). Cuando lo cruces en código ajeno, ya sabés qué sospechar: "algo anidado se está aplanando".

---

# PARTE 2 — El catálogo de malas prácticas (dónde NO usar Optional)

Optional nació para UNA cosa: **retornos de métodos que buscan y pueden no encontrar.** Fuera de ahí, casi siempre estorba. El catálogo:

### ❌ 1. Optional como campo de clase

```java
// ❌ NO:
public class Producto {
    private Optional<LocalDate> vencimiento;   // mal
}
// ✅ SÍ: campo nullable común y corriente...
public class Producto {
    private LocalDate vencimiento;             // puede ser null, y está BIEN
}
// ...y si querés elegancia, un MÉTODO que lo envuelva al consultarlo:
public Optional<LocalDate> getVencimientoOpt() {
    return Optional.ofNullable(vencimiento);
}
```

¿Por qué no en el campo? Optional no fue diseñado para persistir: no es serializable, los frameworks (Lombok, JPA, Jackson — todos los que vas a usar en la materia) lo pelean, y encima el campo Optional **puede él mismo ser null** (una caja-que-no-existe: el absurdo de la doble ausencia). El dato ausente en un campo se representa con null de toda la vida; la caja se usa **al consultar**.

### ❌ 2. Optional como parámetro de método

```java
// ❌ NO:
void procesar(Optional<String> codigoDescuento) { ... }
// ✅ SÍ: dos métodos (sobrecarga), o un parámetro nullable documentado:
void procesar() { ... }
void procesar(String codigoDescuento) { ... }
```

¿Por qué? Obligás a TODOS los llamadores a envolver (`procesar(Optional.of("X"))` — ruido puro), y el parámetro ahora tiene **tres** estados posibles: null, vacío, lleno. Empeoraste lo que venías a arreglar.

### ❌ 3. `Optional<List<T>>` (o de cualquier colección)

```java
// ❌ NO:
Optional<List<Producto>> buscarPorTipo(TipoProducto t)
// ✅ SÍ:
List<Producto> buscarPorTipo(TipoProducto t)   // si no hay, devuelve List.of() — lista VACÍA
```

¿Por qué? Una lista vacía **ya representa** "no encontré nada" perfectamente. La caja no agrega información, solo un desempaque inútil. Regla: **colecciones nunca null, nunca Optional — vacías.** (Optional es para "UN elemento que quizás no está"; las colecciones ya saben estar vacías solas.)

### ❌ 4. Devolver `null` donde la firma dice `Optional`

La traición máxima (ya la conocés de Fase 2): el que llama confió en que la caja existe y le va a llamar `.map` encima → NPE. Si no hay resultado: `Optional.empty()`. Siempre.

### ❌ 5. `get()` pelado / `isPresent() + get()`

El antipatrón #1 de Fase 2, listado acá por completitud: el primero es usar-sin-chequear con otro nombre; el segundo es un null-check con pasos extra. Cadena o métodos por intención, siempre.

### ❌ 6. `Optional.of(algoQuePuedeSerNull)`

`of` exige no-null (si no, NPE ahí mismo). Si no estás SEGURO de que hay valor → `ofNullable`. Regla rápida: ¿lo acabás de construir vos con `new`? → `of`. ¿Viene de afuera (un get, un parámetro, una API)? → `ofNullable`.

### ⚠️ 7. La nuance final: a veces un `if` simple es MÁS claro

Honestidad de cierre: Optional es una herramienta, no una religión. Si tenés una variable local que puede ser null y la vas a usar dos líneas después, un `if (x != null)` es perfectamente legible y nadie te va a retar. Envolver en `Optional.ofNullable(x).ifPresent(...)` para un chequeo local de dos líneas es ceremonia sin ganancia. **Optional gana en las fronteras** (retornos de métodos, contratos entre capas) y en las **cadenas** (buscar→validar→transformar→aterrizar). En el micro-código local, usá el criterio: el objetivo es claridad, no coleccionar cajas.

---

# PARTE 3 — El árbol de decisión completo

El que pediste desde el día uno. Dos situaciones: cuando **devolvés** y cuando **recibís**.

## 🌳 A — Estoy ESCRIBIENDO un método... ¿devuelvo Optional?

```
¿Devuelve UNA cosa que puede no encontrarse?  (búsqueda, lookup, "el primero que...")
├── SÍ → Optional<T>          (y JAMÁS devuelvas null: vacío = Optional.empty())
└── NO:
    ¿Devuelve una colección? → la colección, VACÍA si no hay. Nunca Optional<List>.
    ¿La ausencia tiene un default natural del dominio? ("sin descuento = 0")
        → absorbé el orElse ADENTRO y devolvé el tipo plano (la lección de E5).
    ¿Siempre hay resultado? → el tipo plano, sin caja.
```

## 🌳 B — TENGO un Optional en la mano... ¿qué método uso?

**Paso 1 — ¿Necesito procesarlo antes de salir?** (todos estos te dejan EN la caja, encadenables)

```
¿Transformar el valor?
├── la función devuelve un valor plano   → .map(f)
└── la función devuelve OTRO Optional    → .flatMap(f)
¿Descartarlo si no cumple una condición? → .filter(pred)
¿Probar OTRA fuente si está vacío?       → .or(() -> otraBusqueda)
```

**Paso 2 — ¿Cómo salgo de la caja?** (elegí UNO según la intención)

```
¿La ausencia tiene un default?
├── default constante/barato             → .orElse(valor)
└── default costoso de fabricar          → .orElseGet(() -> fabricar())
¿La ausencia es un ERROR?                → .orElseThrow()  (o con mensaje custom, ver mini-proyecto)
¿Quiero hacer una ACCIÓN, no obtener un valor?
├── solo si está                         → .ifPresent(v -> ...)
└── una acción para cada rama            → .ifPresentOrElse(v -> ..., () -> ...)
¿Solo necesito el sí/no?                 → .isPresent() / .isEmpty()
```

**La forma general de casi todo uso real:**

```
buscar(...)                  ← nace la caja
    .filter(...)             ← (opcional) validar
    .map(...) / .flatMap(...) ← (opcional) transformar
    .or(...)                 ← (opcional) otra fuente
    .orElse / .orElseThrow / .ifPresent...   ← aterrizar (UNA sola vez, al final)
```

Procesás en el mundo-caja, aterrizás una vez al final. Si te encontrás aterrizando en el medio (`...orElse(x)` y después volver a envolver), algo está torcido — misma alarma que el `.toList().stream()` del medio de un pipeline.

---

# PARTE 4 — Mini-proyecto integrador: el catálogo con búsquedas

El cierre. Vas a armar una clase con la **misma forma** que los servicios del código real de la materia: una clase de instancia (no estáticos esta vez) que recibe sus datos por constructor y expone métodos de búsqueda. Es, deliberadamente, el ensayo general para leer un `BuscadorDeX` real sin pestañear.

### La estructura

```java
public class CatalogoProductos {

    private final List<Producto> productos;
    private final List<Producto> promociones;   // un segundo catálogo, para el or()

    public CatalogoProductos(List<Producto> productos, List<Producto> promociones) {
        this.productos = productos;
        this.promociones = promociones;
    }

    // ... acá van los métodos que vas a escribir
}
```

> ¿Por qué de instancia y con constructor? Porque así están escritos los servicios reales (los `@Component` de Spring que vas a leer: clase + dependencias por constructor + métodos de búsqueda). Los estáticos eran cómodos para ejercicios sueltos; esto es la forma adulta. Notá el `final` en los campos: se asignan una vez en el constructor y nunca más — el mismo `final` de las constantes, aplicado a dependencias.

En tu `Main`: creá el catálogo con `new CatalogoProductos(Producto.productosEjemplo(), promos)` donde `promos` es una listita de 2 productos nuevos que NO están en la principal (ej: "Fernet" y "Galletitas"). Para el flatMap vas a usar el campo `vencimiento`, que en tus datos es casi siempre null — **dato real nullable, ya lo tenías**. Asegurate de que al menos UN producto tenga vencimiento no-null (el del builder, agregale `.vencimiento(LocalDate.now().plusDays(30))`).

### Los métodos a escribir (cada uno ejercita una pieza)

**M1 — La base (de instancia ahora):** `Optional<Producto> buscarPorNombre(String nombre)` — tu clásico `filter`+`findFirst`, pero buscando en `this.productos`.
**✅:** encontrado e inexistente funcionan como siempre.

**M2 — `filter`:** `Optional<Producto> buscarDisponible(String nombre)` — como M1, pero solo "está" si además tiene `stock > 0`. Reusá M1 y encadenale el filter.
**✅:** un producto con stock aparece; si le ponés stock 0 a uno en los datos y lo buscás, da vacío **aunque exista**.

**M3 — `orElseThrow` con mensaje custom:** `Producto obtener(String nombre)` — como el `obtenerObligatorio` de Fase 2, pero ahora con la sobrecarga que recibe una lambda fabricadora de excepción:

```java
.orElseThrow(() -> new NoSuchElementException("No existe el producto: " + nombre))
```

Esto es nuevo: `orElseThrow()` pelado lanzaba un error genérico; esta versión lanza **tu mensaje**, con el dato que faltó adentro. (Fabricar tipos de excepción propios llega en la etapa de Excepciones del Proyecto 0 — por ahora, mismo tipo, mensaje tuyo. Fijate que es otro Supplier perezoso: la excepción solo se construye si hace falta.)
**✅:** con un inexistente, el stacktrace muestra TU mensaje con el nombre buscado.

**M4 — `or()`:** `Optional<Producto> buscarConPromos(String nombre)` — buscá en `productos`; si no está, en `promociones`. Pista: vas a necesitar un buscador auxiliar privado que reciba la lista donde buscar, para no duplicar el stream dos veces.
**✅:** "Detergente" sale del catálogo principal; "Fernet" sale de promos; "Lavandina" da vacío.

**M5 — `flatMap`:** `Optional<LocalDate> vencimientoDe(String nombre)` — buscá el producto y, si está, su vencimiento... que también puede no estar (es null en casi todos). Dos ausencias, una caja: `buscarPorNombre(nombre).flatMap(p -> Optional.ofNullable(p.getVencimiento()))`. Antes de escribirlo, probá qué pasa si usás `map` en vez de `flatMap` y mirá el tipo que infiere IntelliJ (`Ctrl+Q` sobre la variable): vas a VER la caja-dentro-de-caja.
**✅:** el producto CON vencimiento da `Optional[fecha]`; uno sin vencimiento da vacío; uno inexistente da vacío. Tres orígenes de ausencia, una sola caja a la salida.

**M6 — `orElse` vs `orElseGet`, el laboratorio:** agregá este método a la clase:

```java
private Producto fabricarDefault() {
    System.out.println("      ⚙️ fabricando producto default...");
    return Producto.builder().nombre("(default)").precio(0).tipo(TipoProducto.SNACK).stock(0).build();
}
```

Y en el Main, buscá un producto **QUE EXISTE** dos veces: una rematando con `.orElse(catalogo.fabricarDefault())`... ojo, para llamarlo desde Main hacelo público o hacé el experimento dentro de un método del catálogo. Y otra con `.orElseGet(this::fabricarDefault)` (o `catalogo::fabricarDefault`).
**✅:** con `orElse`, el "⚙️ fabricando..." se imprime **aunque el producto exista** (¡el default se fabricó y se tiró!); con `orElseGet`, no se imprime. Acabás de ver la evaluación eager vs lazy con tus ojos. Después probá con uno inexistente: ahí los dos lo imprimen.

**M7 — `ifPresentOrElse`, el reporte:** un método `void reportar(String nombre)` que imprima `"📦 <nombre> — $<precio> (stock: <n>)"` si existe, o `"❌ No encontrado: <nombre>"` si no.
**✅:** las dos ramas se ven en consola con un existente y un inexistente.

**M8 — El gran final, la cadena completa:** `String etiquetaOferta(String nombre)` que combine TODO: buscá con promos incluidas (M4), filtrá que tenga stock, transformá a `"OFERTA: <nombre> a $<precio>"`, y si nada de eso prosperó, devolvé `"Sin ofertas para: <nombre>"`. Una sola cadena: `or` + `filter` + `map` + `orElse`.
**✅:** probalo con (a) uno del catálogo con stock, (b) uno de promos, (c) uno sin stock, (d) uno inexistente — cuatro caminos, cuatro salidas correctas, cero ifs.

---

# ✅ Checkpoint FINAL del entrenamiento

1. Explicar por qué `orElse(fabricar())` ejecuta `fabricar()` aunque la caja esté llena, y por qué en JS `??` no tenía este problema.
2. Elegir `map` vs `flatMap` mecánicamente (¿la función devuelve valor o caja?).
3. Recitar las tres prohibiciones grandes: ni campo, ni parámetro, ni envolviendo colecciones.
4. Recorrer el árbol de decisión de memoria: procesar en la caja (filter/map/flatMap/or) → aterrizar UNA vez (orElse/orElseGet/orElseThrow/ifPresent/ifPresentOrElse).
5. M8 te salió en una sola cadena, sin mirar el árbol.

Si esto fluye: **el entrenamiento completo está terminado.** Streams, Collectors, Comparator y Optional ya son tuyos — exactamente el toolbox que el código real de la materia da por sabido. Lo que sigue, cuando quieras: retomar el **Proyecto 0 en la etapa 7 (Tests con JUnit)** — donde vas a escribir tests que llaman a métodos que devuelven Optional y aserciones sobre streams... o sea, todo esto, pero verificado automáticamente. Llegás con el terreno más que preparado.
