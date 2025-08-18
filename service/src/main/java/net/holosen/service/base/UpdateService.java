package net.holosen.service.base;

public interface UpdateService<Dto> {
    Dto update(Dto dto) throws Exception;
}
