package net.holosen.app.controller.open;

import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.site.SliderDto;
import net.holosen.service.site.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slider")
public class SliderController {

    private final SliderService service;

    @Autowired
    public SliderController(SliderService service) {
        this.service = service;
    }

    @GetMapping("")
    public APIResponse<List<SliderDto>> getAll() {
        return APIResponse.<List<SliderDto>>builder()
                .status(APIStatus.Success)
                .data(service.readAll())
                .build();
    }

}
