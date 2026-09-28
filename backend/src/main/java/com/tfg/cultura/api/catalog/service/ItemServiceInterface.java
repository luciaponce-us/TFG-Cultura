package com.tfg.cultura.api.catalog.service;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.dto.ItemRequest;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.file.FileUploadException;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ItemServiceInterface<T extends Item, C extends ItemRequest, S> {

	S create(C request, MultipartFile image) throws FileUploadException, IllegalArgumentException;

	T findById(String id) throws NotFoundException;

	S getById(String id) throws NotFoundException;

	Page<S> getAll(Pageable pageable, String nameContains, Set<String> categoryIds);

	S update(String id, C request, MultipartFile image)
			throws NotFoundException, FileUploadException, IllegalArgumentException;

	void delete(String id) throws NotFoundException;
}
