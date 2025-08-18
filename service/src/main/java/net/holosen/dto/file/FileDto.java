package net.holosen.dto.file;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FileDto {
    private Long id;
    private String name;
    private String path;
    private String uuid;
    private String extension;
    private String contentType;
    private Long size;

}
