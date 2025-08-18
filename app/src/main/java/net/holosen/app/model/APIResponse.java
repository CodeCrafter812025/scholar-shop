package net.holosen.app.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.holosen.app.model.enums.APIStatus;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
    private String message = "";
    private APIStatus status;
    private T data;
}
