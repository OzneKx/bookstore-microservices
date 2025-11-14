package com.bookstore.order.client;

import com.bookstore.order.dto.CatalogBookResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@Component
public class CatalogClient {
    private final RestTemplate restTemplate;

    public CatalogClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CatalogBookResponse getBookById(Long id, String authHeader) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<CatalogBookResponse> response = restTemplate.exchange(
                    "http://catalog-service/books/{id}",
                    HttpMethod.GET,
                    entity,
                    CatalogBookResponse.class,
                    id
            );

            if (response.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found in catalog-service");
            }

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found in catalog-service");
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catalog-service error: " + e.getStatusCode());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with catalog-service", e);
        }
    }
}
