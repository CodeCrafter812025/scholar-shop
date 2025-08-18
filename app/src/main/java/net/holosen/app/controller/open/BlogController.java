package net.holosen.app.controller.open;

import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.dto.site.LimitedBlogDto;
import net.holosen.dto.site.SingleBlogDto;
import net.holosen.service.site.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    private final BlogService service;

    @Autowired
    public BlogController(BlogService service) {
        this.service = service;
    }

    @GetMapping("")
    @Cacheable(cacheNames = "apiCache30m" , key = "'blog-all_' + #page + '-' + #size")
    public APIResponse<List<LimitedBlogDto>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer number){
        return APIResponse.<List<LimitedBlogDto>>builder()
                .status(APIStatus.Success)
                .data(service.readAllPublished(page,number))
                .build();
    }

    @GetMapping("{id}")
    @Cacheable(cacheNames = "apiCache30m" , key = "'blog_' + #id")
    public APIResponse<SingleBlogDto> getByKey(@PathVariable Long id){
        try {
            return APIResponse.<SingleBlogDto>builder()
                    .status(APIStatus.Success)
                    .data(service.read(id))
                    .build();
        } catch (NotFoundException e) {
            return APIResponse.<SingleBlogDto>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();
        }
    }

}
