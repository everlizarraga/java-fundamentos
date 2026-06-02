package io.github.everlizarraga.fundamentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cancion {
  private String titulo;
  private String artista;
  private Integer duracionSegundos;
  private String genero;
  private Long reproducciones;
}
