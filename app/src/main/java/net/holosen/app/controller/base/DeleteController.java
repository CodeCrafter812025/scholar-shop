package net.holosen.app.controller.base;

import net.holosen.app.model.APIResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DeleteController<Dto> {
    @DeleteMapping("{id}")
    APIResponse<Boolean> delete(@PathVariable Long id);
}
