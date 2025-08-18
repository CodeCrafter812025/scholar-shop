package net.holosen.app.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.holosen.app.model.enums.APIStatus;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class APIPanelResponse<T> extends APIResponse<T> {
    private Long totalCount = 0L;
    private Integer totalPages = 0;
}
