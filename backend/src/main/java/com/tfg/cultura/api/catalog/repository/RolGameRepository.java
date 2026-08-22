package com.tfg.cultura.api.catalog.repository;

import java.util.List;

import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;

public interface RolGameRepository extends ItemRepository<RolGame> {
    List<RolGame> findAllBySaga(RolSaga saga);
    void deleteAllBySaga(RolSaga saga);
}
