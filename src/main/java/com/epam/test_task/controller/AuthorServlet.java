package com.epam.test_task.controller;

import com.epam.test_task.converter.ObjectMapper;
import com.epam.test_task.dto.AuthorHateoasDto;
import com.epam.test_task.dto.BookHateoasDto;
import com.epam.test_task.service.AuthorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "AuthorServlet", urlPatterns = "/authors/*")
public class AuthorServlet extends HttpServlet {

    private AuthorService authorService = new AuthorService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "max-age=60");

        if (path == null || path.equals("/")) {
            int page = parseIntOrDefault(req.getParameter("page"), 1);
            int size = parseIntOrDefault(req.getParameter("size"), 10);
            List<AuthorHateoasDto> authors = authorService.findPaginated(page, size);
            for (AuthorHateoasDto dto : authors) {
                addHateoasLinks(dto);
            }
            objectMapper.writeValue(resp.getOutputStream(), authors);
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (path.endsWith("/books")) {
            try {
                long authorId = Long.parseLong(path.substring(1, path.length() - "/books".length()));
                int page = parseIntOrDefault(req.getParameter("page"), 1);
                int size = parseIntOrDefault(req.getParameter("size"), 10);
                List<BookHateoasDto> books = authorService.findBooksByAuthorPaginated(authorId, page, size);
                if (books == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                for (BookHateoasDto book : books) {
                    book.addLink("self", "/books/" + book.getId(), "get");
                    book.addLink("author", "/authors/" + book.getAuthorId(), "get");
                }
                objectMapper.writeValue(resp.getOutputStream(), books);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            try {
                Long id = Long.parseLong(path.substring(1));
                AuthorHateoasDto author = authorService.findById(id);
                if (author == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                addHateoasLinks(author);
                objectMapper.writeValue(resp.getOutputStream(), author);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthorHateoasDto dto = (AuthorHateoasDto) objectMapper.readValue(req.getInputStream(), AuthorHateoasDto.class);
        if (dto.getName() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        AuthorHateoasDto created = authorService.save(dto);
        addHateoasLinks(created);
        String location = req.getRequestURL().append("/").append(created.getId()).toString();
        resp.setHeader("Location", location);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), created);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            long id = Long.parseLong(path.substring(1));
            AuthorHateoasDto dto = (AuthorHateoasDto) objectMapper.readValue(req.getInputStream(), AuthorHateoasDto.class);
            AuthorHateoasDto updated = authorService.update(id, dto);
            if (updated == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            addHateoasLinks(updated);
            objectMapper.writeValue(resp.getOutputStream(), updated);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            long id = Long.parseLong(path.substring(1));
            boolean deleted = authorService.delete(id);
            if (!deleted) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void addHateoasLinks(AuthorHateoasDto dto) {
        dto.addLink("self", "/authors/" + dto.getId());
        dto.addLink("books", "/authors/" + dto.getId() + "/books");
        dto.addLink("delete", "/authors/" + dto.getId());
    }

    private int parseIntOrDefault(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
