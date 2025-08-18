package net.holosen.service.base;

import net.holosen.common.exceptions.ValidationException;

public interface HasValidation <Dto> {
    void checkValidation(Dto dto) throws ValidationException;
}
