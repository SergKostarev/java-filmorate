package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    @EqualsAndHashCode.Include
    private Long id;

    //@NotNull(message = "Пустое описание")
    //@NotBlank(message = "Пустое описание")
    private String name;

}
