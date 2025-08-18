package net.holosen.service.base;

public interface CRUDService <Dto> extends
        CreateService<Dto>,
        UpdateService<Dto>,
        ReadService<Dto>,
        DeleteService<Dto> {
}
