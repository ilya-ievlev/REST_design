package com.epam.test_task.service;

import com.epam.test_task.dto.BookHateoasDto;
import com.epam.test_task.entity.Book;

import java.util.List;

public class BookService {

    public List<BookHateoasDto> findPaginated(int page, int size) {
        return null;
    }

    public BookHateoasDto findById(Long id) {
        return null;
    }

    public boolean delete(long id){
        return true;
    }

    public BookHateoasDto save(BookHateoasDto book){
        return null;
    }

    public BookHateoasDto update(long id, BookHateoasDto book){
        return null;
    }
}
