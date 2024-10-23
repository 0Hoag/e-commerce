package com.example.product_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.product_service.dto.FileUtils;
import com.example.product_service.dto.request.AddImageToProductRequest;
import com.example.product_service.dto.request.CreateProductRequest;
import com.example.product_service.dto.request.ProductUpdateRequest;
import com.example.product_service.dto.request.UpdateStockQuantityProductRequest;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Author;
import com.example.product_service.entity.Category;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.AppException;
import com.example.product_service.exception.ErrorCode;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.AuthorRepository;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductRepository;
import io.netty.util.internal.ObjectUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    AuthorRepository authorRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;

    public Mono<ProductResponse> createProduct(Mono<CreateProductRequest> request, FilePart imagePart) {
        return Mono.zip(request, uploadPhoto(imagePart))
                .flatMap(tuple -> {
                    Product product = productMapper.toProduct(tuple.getT1());
                    String imageUrl = (String) tuple.getT2().get("url");

                    product.setImage(Collections.singleton(imageUrl));

                    return Mono.zip(
                            Mono.just(product),
                            categoryRepository.findAllById(product.getCategoryIds()).collectList(),
                            authorRepository.findById(product.getAuthor())
                    );
                }).flatMap(tuple -> {
                    Product product = tuple.getT1();
                    List<Category> categories = tuple.getT2();
                    Author author = tuple.getT3();

                    if (categories.isEmpty()) {
                        return Mono.error(new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
                    }

                    if (author == null) {
                        return Mono.error(new AppException(ErrorCode.AUTHOR_NOT_EXISTED));
                    }

                    return productRepository.save(product);
                }).map(productMapper::toProductResponse);
    }

    public Mono<ProductResponse> updateImageToProduct(String id, AddImageToProductRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .flatMap(product -> {
                    product.getImage().addAll(request.getImage());
                    return productRepository.save(product);
                }).map(productMapper::toProductResponse);
    }


    public Mono<Map> uploadPhoto(FilePart filePart) {
        //create new file name
        String fileName = FileUtils.generateFileName("File", FileUtils.getExtension(filePart.filename()));

        //create file temp to system
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);

        return FileUtils.validateFile(filePart)
                .flatMap(isValid -> {
                    if (isValid) {
                        return filePart.transferTo(tempFile)
                                .then(Mono.fromCallable(() -> {
                                    try {
                                        Map uploadResult = cloudinaryConfig().uploader().upload(tempFile, ObjectUtils.emptyMap());
                                        return uploadResult;
                                    }catch (AppException e) {
                                        throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
                                    } finally {
                                        if (tempFile.exists()) {
                                            tempFile.delete();
                                        }
                                    }
                                }));
                    }else {
                        return Mono.error(new AppException(ErrorCode.UPLOAD_FILE_FAIL));
                    }
                });
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", "CLOUD_NAME");
        config.put("api_key", "API_KEY");
        config.put("api_secret", "API_SECRET");
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }

    public Mono<Void> deleteProduct(String id) {
        return productRepository.deleteById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
    }

    public Flux<ProductResponse> getAllProduct() {
        return productRepository.findAll()
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .map(productMapper::toProductResponse);
    }

    public Mono<ProductResponse> getProduct(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .map(product -> {
                    if (product.getStockQuantity() < 1) {
                        throw new AppException(ErrorCode.OUT_OF_STOCK_PRODUCT);
                    }
                    return productMapper.toProductResponse(product);
                });
    }

    public Mono<ProductResponse> updateProduct(String id, ProductUpdateRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .map(product -> {
                    productMapper.updateProduct(product, request);
                    return productMapper.toProductResponse(product);
                });
    }

    public Mono<ProductResponse> updateQuantityProduct(String id, UpdateStockQuantityProductRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                .flatMap(product -> {
                    productMapper.updateStockQuantityProduct(product, request);
                    return productRepository.save(product)
                            .map(productMapper::toProductResponse);
                });
    }
}
