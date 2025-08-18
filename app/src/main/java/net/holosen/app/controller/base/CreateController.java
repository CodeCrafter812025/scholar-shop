package net.holosen.app.controller.base;

import net.holosen.app.model.APIResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CreateController<Dto> {
    @PostMapping("add")
    APIResponse<Dto> add(@RequestBody Dto dto) throws Exception;

}
