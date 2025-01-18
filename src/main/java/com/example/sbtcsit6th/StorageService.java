package com.example.sbtcsit6th;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class StorageService {

	private static final Path path = Paths.get("uploads");

	@PostConstruct
	public void setup() throws IOException {
		Files.createDirectories(path);
	}

	public String store(MultipartFile image) {
		System.out.println("image is null?");

		if (image == null || image.isEmpty())
			return null;

		System.out.println("Before fileName");

		String fileName = Instant.now().toEpochMilli() + "." + getExtension(image.getOriginalFilename());

		Path toSavePath = path.resolve(fileName);

		try {
			System.out.println("Before copying file");
			Files.copy(image.getInputStream(), toSavePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(fileName);
		return fileName;
	}

	private String getExtension(String originalFilename) {
		return "jpg";
	}

}
