package com.epam.test_task.service;

import com.epam.test_task.dto.AuthorHateoasDto;
import com.epam.test_task.dto.BookHateoasDto;

import java.util.List;

public class AuthorService {

    public List<AuthorHateoasDto> findPaginated(int page, int size) {
        return null;
    }

    public List<BookHateoasDto> findBooksByAuthorPaginated(long id, int page, int size) {
        return null;
    }

    public AuthorHateoasDto findById(Long id) {
        return null;
    }

    public AuthorHateoasDto save(AuthorHateoasDto author){
        return null;
    }

    public AuthorHateoasDto update(long id, AuthorHateoasDto author){
        return null;
    }

    public boolean delete(long id){
        return true;
    }
}
