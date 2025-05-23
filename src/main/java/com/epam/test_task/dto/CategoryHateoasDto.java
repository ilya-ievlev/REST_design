package com.epam.test_task.dto;

import java.util.*;

public class CategoryHateoasDto {

    private Long id;
    private String name;
    private List<BookHateoasDto> books = new ArrayList<>();
    private Map<String, Link> _links = new HashMap<>();

    public CategoryHateoasDto() {
    }

    public CategoryHateoasDto(Long id, String name, List<BookHateoasDto> books) {
        this.id = id;
        this.name = name;
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookHateoasDto> getBooks() {
        return books;
    }

    public void setBooks(List<BookHateoasDto> books) {
        this.books = books;
    }

    public Map<String, Link> get_links() {
        return _links;
    }

    public void set_links(Map<String, Link> _links) {
        this._links = _links;
    }

    public void addLink(String action, String url) {

    }
}