package com.example.product_service.service;

import com.example.product_service.dto.request.AddToProductRequest;
import com.example.product_service.dto.request.CreateAuthorRequest;
import com.example.product_service.dto.request.RemoveToProductRequest;
import com.example.product_service.dto.request.UpdateAuthorRequest;
import com.example.product_service.dto.response.AuthorResponse;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Author;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.AppException;
import com.example.product_service.exception.ErrorCode;
import com.example.product_service.mapper.AuthorMapper;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.AuthorRepository;
import com.example.product_service.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService {
    AuthorRepository authorRepository;
    AuthorMapper authorMapper;
    ProductRepository productRepository;
    ProductMapper productMapper;

    public Mono<AuthorResponse> createAuthor(Mono<CreateAuthorRequest> request) {
        return request
                .map(authorMapper::toAuthor)
                .flatMap(authorRepository::save)
                .map(authorMapper::toAuthorResponse);
    }

    public Mono<AuthorResponse> getAuthor(String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.AUTHOR_NOT_EXISTED)))
                .map(authorMapper::toAuthorResponse);
    }

    public Mono<AuthorResponse> updateAuthor(String id, UpdateAuthorRequest request) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.AUTHOR_NOT_EXISTED)))
                .flatMap(author -> {
                    authorMapper.updateAuthor(author, request);
                    return authorRepository.save(author)
                            .map(authorMapper::toAuthorResponse);
                });
    }

    public Mono<Author> addProductToAuthor(String id, AddToProductRequest request) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .flatMap(author -> productRepository.findAllById(request.getStringSet())
                        .collectList()
                        .flatMap(authors -> {
                            author.getProduct().addAll(authors);
                            return authorRepository.save(author);
                        })
                );
    }

    public Mono<Author> removeProductToAuthor(String id, RemoveToProductRequest request) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .flatMap(author -> productRepository.findAllById(request.getStringSet())
                        .collectList()
                        .flatMap(authors -> {
                            author.getProduct().removeAll(authors);
                            return authorRepository.save(author);
                        })
                );
    }

    public Mono<Void> deleteAuthor(String id) {
        return authorRepository.deleteById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.AUTHOR_NOT_EXISTED)));
    }

//    public Set<ProductResponse> handleProductResponse(Set<String> stringSet) {
//        Set<ProductResponse> productResponses = stringSet.stream()
//                .map(s -> {
//                    var product = productRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
//                    return productMapper.toProductResponse(product);
//                }).collect(Collectors.toSet());
//        return productResponses;
//    }
//
//    public Set<ProductResponse> handleProductEntity(Set<Product> products) {
//        Set<ProductResponse> productResponses = products
//                .stream()
//                .map(product -> {
//                    var product1 = productRepository.findById(product.getId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
//                    ProductResponse productResponse = productMapper.toProductResponse(product1);
//                    return productResponse;
//                }).collect(Collectors.toSet());
//        return productResponses;
//    }
}
