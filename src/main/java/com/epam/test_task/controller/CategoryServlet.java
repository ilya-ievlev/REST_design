package com.epam.test_task.controller;

import com.epam.test_task.converter.ObjectMapper;
import com.epam.test_task.dto.BookHateoasDto;
import com.epam.test_task.dto.CategoryHateoasDto;
import com.epam.test_task.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "CategoryServlet", urlPatterns = "/categories/*")
public class CategoryServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "max-age=60");

        if (path == null || path.equals("/")) {
            int page = parseIntOrDefault(req.getParameter("page"), 1);
            int size = parseIntOrDefault(req.getParameter("size"), 10);
            List<CategoryHateoasDto> categories = categoryService.findPaginated(page, size);
            for (CategoryHateoasDto dto : categories) {
                addHateoasLinks(dto);
            }
            objectMapper.writeValue(resp.getOutputStream(), categories);
            resp.setStatus(HttpServletResponse.SC_OK); // 200 OK: list of categories returned
        } else if (path.endsWith("/books")) {
            try {
                long categoryId = Long.parseLong(path.substring(1, path.length() - "/books".length()));
                int page = parseIntOrDefault(req.getParameter("page"), 1);
                int size = parseIntOrDefault(req.getParameter("size"), 10);
                List<BookHateoasDto> books = categoryService.findBooksByCategoryPaginated(categoryId, page, size);
                if (books == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found: category not found
                    return;
                }
                for (BookHateoasDto book : books) {
                    book.addLink("self", "/books/" + book.getId(), "get");
                    book.addLink("author", "/authors/" + book.getAuthorId(), "get");
                }
                objectMapper.writeValue(resp.getOutputStream(), books);
                resp.setStatus(HttpServletResponse.SC_OK); // 200 OK: list of books in category returned
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            try {
                long id = Long.parseLong(path.substring(1));
                CategoryHateoasDto category = categoryService.findById(id);
                if (category == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found: category not found
                    return;
                }
                addHateoasLinks(category);
                objectMapper.writeValue(resp.getOutputStream(), category);
                resp.setStatus(HttpServletResponse.SC_OK); // 200 OK: category details returned
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CategoryHateoasDto dto = (CategoryHateoasDto) objectMapper.readValue(req.getInputStream(), CategoryHateoasDto.class);
        if (dto.getName() == null || dto.getName().isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request: name is required
            return;
        }
        CategoryHateoasDto created = categoryService.save(dto);
        addHateoasLinks(created);
        String location = req.getRequestURL().append("/").append(created.getId()).toString();
        resp.setHeader("Location", location);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), created);
        resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created: category created
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request: no ID
            return;
        }
        try {
            long id = Long.parseLong(path.substring(1));
            CategoryHateoasDto dto = (CategoryHateoasDto) objectMapper.readValue(req.getInputStream(), CategoryHateoasDto.class);
            if (dto.getName() == null || dto.getName().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request: name required
                return;
            }
            CategoryHateoasDto updated = categoryService.update(id, dto);
            if (updated == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found: category not found
                return;
            }
            addHateoasLinks(updated);
            objectMapper.writeValue(resp.getOutputStream(), updated);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK); // 200 OK: category updated
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request: no ID
            return;
        }
        try {
            long id = Long.parseLong(path.substring(1));
            boolean deleted = categoryService.delete(id);
            if (!deleted) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found: category not found
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content: category deleted
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void addHateoasLinks(CategoryHateoasDto dto) {
        dto.addLink("self", "/categories/" + dto.getId());
        dto.addLink("books", "/categories/" + dto.getId() + "/books");
        dto.addLink("delete", "/categories/" + dto.getId());
    }

    private int parseIntOrDefault(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
