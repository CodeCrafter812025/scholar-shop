package net.holosen.app.controller.open;

import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.site.NavDto;
import net.holosen.service.site.NavService;
import net.holosen.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nav")

public class NavController {

    private final NavService service;

    @Autowired
    public NavController(NavService service) {
        this.service = service;
    }

    @GetMapping("")
    public APIResponse<List<NavDto>> getAll(){
        return APIResponse.<List<NavDto>>builder()
                .status(APIStatus.Success)
                .data(service.readAll())
                .build();
    }
}
