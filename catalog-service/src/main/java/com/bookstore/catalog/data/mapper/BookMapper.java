package com.bookstore.catalog.data.mapper;

import com.bookstore.catalog.data.entity.Book;
import com.bookstore.catalog.dto.BookRequest;
import com.bookstore.catalog.dto.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true)
    Book toEntity(BookRequest bookRequest);

    BookResponse toResponse(Book book);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(BookRequest request, @MappingTarget Book book);
}
