package com.tfg.cultura.api.catalog.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.dto.ItemRequest;
import com.tfg.cultura.api.core.exception.FileUploadException;

public interface ItemServiceInterface<T extends Item, C extends ItemRequest, RES> {

    RES create(C request, MultipartFile image) throws FileUploadException, IllegalArgumentException;

    T findById(String id) throws ItemNotFoundException;

    RES getById(String id) throws ItemNotFoundException;

    Page<RES> getAll(Pageable pageable, String nameContains, Set<String> categoryIds);

    RES update(String id, C request, MultipartFile image)
            throws ItemNotFoundException, FileUploadException, IllegalArgumentException;

    void delete(String id) throws ItemNotFoundException;
}
