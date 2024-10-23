package com.example.product_service.dto;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUtils {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    public static Mono<Boolean> validateFile(FilePart filePart) {
        return Mono.just(filePart)
                .flatMap(filePart1 -> Mono.zip(
                        validateFileSize(filePart1),
                        validateFileExtension(filePart1)
                ))
                .map(tuple -> tuple.getT1() && tuple.getT2());
    }

    public static Mono<Boolean> validateFileSize(FilePart filePart){
        return filePart.content()
                .reduce(0L, (acc, dataBuffer) -> acc + dataBuffer.readableByteCount())
                .map(size -> size <= MAX_FILE_SIZE);
    }

    public static Mono<Boolean> validateFileExtension(FilePart filePart) {
        String extension = getExtension(filePart.filename()).toLowerCase();
        return Mono.just(ALLOWED_EXTENSIONS.contains(extension));
    }

    public static String generateFileName(String prefix, String extension) {
        return prefix + "_" + UUID.randomUUID() + "." + extension;
    }

    public static String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .map(f -> {
                    int pos = f.lastIndexOf(".");
                    return pos > 0 ? f.substring(pos + 1) : "";
                })
                .orElse("");
    }
}
