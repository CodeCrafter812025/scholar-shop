package net.holosen.service.base;

public interface CreateService <Dto>{
    Dto create(Dto dto) throws Exception;
}
