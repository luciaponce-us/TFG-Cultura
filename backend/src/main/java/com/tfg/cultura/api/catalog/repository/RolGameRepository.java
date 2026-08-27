package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;
import java.util.List;

public interface RolGameRepository extends AbstractItemRepository<RolGame> {
	List<RolGame> findAllBySaga(RolSaga saga);
	void deleteAllBySaga(RolSaga saga);
}
