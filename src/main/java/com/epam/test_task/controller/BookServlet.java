package com.epam.test_task.controller;

import com.epam.test_task.converter.ObjectMapper;
import com.epam.test_task.dto.BookHateoasDto;
import com.epam.test_task.service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "BookServlet", urlPatterns = "/books/*")
public class BookServlet extends HttpServlet {

    private BookService bookService = new BookService();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "max-age=60"); // Caching GET responses for 60 seconds
        if (path == null || path.equals("/")) {
            int page = parseIntOrDefault(req.getParameter("page"), 1);
            int size = parseIntOrDefault(req.getParameter("size"), 10);

            List<BookHateoasDto> books = bookService.findPaginated(page, size);
            for (BookHateoasDto book : books) {
                addHateoasLinks(book);
            }

            objectMapper.writeValue(resp.getOutputStream(), books);
            resp.setStatus(HttpServletResponse.SC_OK); // 200 OK
        } else { // get by id
            try {
                long id = Long.parseLong(path.substring(1));
                BookHateoasDto book = bookService.findById(id); // add validation and responses that can show what happened
                if (book == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                    return;
                }
                addHateoasLinks(book);
                objectMapper.writeValue(resp.getOutputStream(), book);
                resp.setStatus(HttpServletResponse.SC_OK); // 200 OK
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BookHateoasDto dto = (BookHateoasDto) objectMapper.readValue(req.getInputStream(), BookHateoasDto.class);
        if (dto.getTitle() == null || dto.getAuthorId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            return;
        }

        BookHateoasDto created = bookService.save(dto);
        addHateoasLinks(created);

        String location = req.getRequestURL().append("/").append(created.getId()).toString();
        resp.setHeader("Location", location); // Location header for created resource
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), created);
        resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            return;
        }

        try {
            long id = Long.parseLong(path.substring(1));
            BookHateoasDto dto = (BookHateoasDto) objectMapper.readValue(req.getInputStream(), BookHateoasDto.class);
            BookHateoasDto updated = bookService.update(id, dto);
            if (updated == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                return;
            }
            addHateoasLinks(updated);
            objectMapper.writeValue(resp.getOutputStream(), updated);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK); // 200 OK
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            return;
        }

        try {
            long id = Long.parseLong(path.substring(1));
            boolean deleted = bookService.delete(id);
            if (!deleted) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        }
    }

    private void addHateoasLinks(BookHateoasDto dto) { // this method probably should be here in a real environment. but for demo purposes it is here to avoid additional complexity
        dto.addLink("self", "/books/" + dto.getId(), "get");
        dto.addLink("delete", "/books/" + dto.getId(), "delete");
        dto.addLink("author", "/authors/" + dto.getAuthorId(), "get");
    }

    private int parseIntOrDefault(String s, int defaultVal) { // this method probably should be here in a real environment. but for demo purposes it is here to avoid additional complexity
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
