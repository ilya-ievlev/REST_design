package com.epam.test_task.dto;

import java.util.*;

public class BookHateoasDto {

    private Long id;
    private String title;
    private Long authorId; // it is a very abstract example
    private String authorName;
    private List<CategoryHateoasDto> categories = new ArrayList<>();
    private Map<String, Link> _links = new HashMap<>();

    public BookHateoasDto() {
    }

    public BookHateoasDto(Long id, String title, Long authorId, String authorName, List<CategoryHateoasDto> categories) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<CategoryHateoasDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryHateoasDto> categories) {
        this.categories = categories;
    }

    public Map<String, Link> get_links() {
        return _links;
    }

    public void set_links(Map<String, Link> _links) {
        this._links = _links;
    }

    public void addLink(String rel, String href, String method) {
        _links.put(rel, new Link(href, method));
    }
}