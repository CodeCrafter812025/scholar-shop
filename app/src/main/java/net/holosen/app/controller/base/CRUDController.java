package net.holosen.app.controller.base;

public interface CRUDController<Dto> extends
        CreateController<Dto>,
        ReadController<Dto>,
        UpdateController<Dto>,
        DeleteController<Dto> {
}
