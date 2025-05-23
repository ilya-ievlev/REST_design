package com.epam.test_task.service;

import com.epam.test_task.dto.BookHateoasDto;
import com.epam.test_task.dto.CategoryHateoasDto;

import java.util.List;

public class CategoryService {

    public CategoryHateoasDto save (CategoryHateoasDto category){
        return null;
    }

    public CategoryHateoasDto update (long id, CategoryHateoasDto category){
        return null;
    }

    public boolean delete (long id){
        return true;
    }

    public CategoryHateoasDto findById (Long id){
        return null;
    }

    public List<CategoryHateoasDto> findPaginated (int page, int size){
        return null;
    }

    public List<BookHateoasDto> findBooksByCategoryPaginated (long id, int page, int size){
        return null;
    }




}
