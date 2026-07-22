package com.tfg.cultura.api.core.service;

public interface BusinessSpecification<T> {
    void validate(T candidate);
}
