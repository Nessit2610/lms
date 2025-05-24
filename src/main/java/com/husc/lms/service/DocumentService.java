package com.husc.lms.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.configuration.LimitedInputStream;
import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.dto.request.DocumentRequest;
import com.husc.lms.dto.response.DocumentResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Document;
import com.husc.lms.entity.Major;
import com.husc.lms.enums.DocumentStatus;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.FeeStatus;
import com.husc.lms.enums.SortEnum;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.DocumentMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.DocumentRepository;
import com.husc.lms.repository.MajorRepository;

@Service
public class DocumentService {
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired 
	private MajorRepository majorRepository;
	
	@Autowired
	private DocumentMapper documentMapper;
	
	public DocumentResponse createDocument(DocumentRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		String statuss = switch (request.getStatus()) {
        case "PRIVATE" -> DocumentStatus.PRIVATE.name();
        case "PUBLIC" -> DocumentStatus.PUBLIC.name();
        default -> throw new AppException(ErrorCode.STATUS_NOT_ALLOWED);
		};
		
		Major major = majorRepository.findById(request.getMajorId()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		
		
		MultipartFile file = request.getFile();
		String type = request.getType();
		if (file != null && !file.isEmpty() && type != null) {
			String extension = fileExtension.apply(file.getOriginalFilename());
			validateFileExtension(type, extension);
		}
		Document document = Document.builder()
				.account(account)
				.title(request.getTitle())
				.description(request.getDescription())
				.status(statuss)
				.major(major)
				.createdAt(new Date())
				.build();
		document = documentRepository.save(document);
		String id = document.getId();
		uploadFile(id, request.getFile(), request.getType());
		DocumentResponse documentResponse = documentMapper.toDocumentResponse(document);
		Object object = accountService.getAccountDetails(account.getId());
		documentResponse.setObject(object);
		return documentResponse;
	}
	
	public boolean deleteDocument(String id) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Document document = documentRepository.findByIdAndAccount(id,account);
		if(document != null) {
			documentRepository.deleteById(id);
			deletePhysicalFile(document.getPath());
			return true;
		}
		return false;
	}
	
	public boolean deleteAllDocument(List<String> documentIds) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		for(String id : documentIds) {
			Document document = documentRepository.findByIdAndAccount(id,account);
			if(document == null) {
				continue;
			}
			if(document != null) {
				documentRepository.deleteById(id);
				deletePhysicalFile(document.getPath());
			}
		}
		return true;
	}
	
	private void deletePhysicalFile(String fileUrl) {
	    if (fileUrl == null || fileUrl.isEmpty()) return;

	    String[] parts = fileUrl.split("/");
	    if (parts.length < 4) return;

	    String folder = parts[3];      
	    String filename = parts[4];    

	    String baseDir = switch (folder) {
	        case "images" -> Constant.PHOTO_DIRECTORY;
	        case "files" -> Constant.FILE_DIRECTORY;
	        default -> throw new RuntimeException("Unsupported folder: " + folder);
	    };

	    try {
	        Path path = Paths.get(baseDir, filename);
	        Files.deleteIfExists(path);
	        System.out.println("Đã xóa file: " + path.toString());
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Không thể xóa file: " + filename, e);
	    }
	}
	
	public Page<DocumentResponse> getAllDocument(int page, int size, String sortBy) {
	    Pageable pageable;
	    switch (sortBy) {
	        case "NEWEST" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
	        case "OLDEST" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
	        default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
	    }

	    Page<Document> documents = documentRepository.findAllByStatus(DocumentStatus.PUBLIC.name(), pageable);
	    
	    return documents.map(d -> {
	        DocumentResponse documentResponse = documentMapper.toDocumentResponse(d);
	        Object object = accountService.getAccountDetails(d.getAccount().getId());
	        documentResponse.setObject(object);
	        return documentResponse;
	    });
	}

	
	public Page<DocumentResponse> searchDocument(String title ,String majorId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    
	    Major major = majorRepository.findById(majorId).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
	    
	    Page<Document> documents = documentRepository.findByTitleContainingIgnoreCaseAndStatusAndMajor(title, DocumentStatus.PUBLIC.name(), major, pageable);

	    return documents.map(d -> {
	        DocumentResponse documentResponse = documentMapper.toDocumentResponse(d);
	        Object object = accountService.getAccountDetails(d.getAccount().getId());
	        documentResponse.setObject(object);
	        return documentResponse;
	    });
	}
	
	public Page<DocumentResponse> findDocumentByMajor(String majorId, int page, int size, String sortBy) {
		Pageable pageable;
	    switch (sortBy) {
	        case "NEWEST" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
	        case "OLDEST" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
	        default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
	    }
		
		
		Major major = majorRepository.findById(majorId).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		
		Page<Document> documents = documentRepository
				.findByStatusAndMajor(DocumentStatus.PUBLIC.name(), major, pageable);
		
		return documents.map(d -> {
			DocumentResponse documentResponse = documentMapper.toDocumentResponse(d);
			Object object = accountService.getAccountDetails(d.getAccount().getId());
			documentResponse.setObject(object);
			return documentResponse;
		});
	}

	
	public Page<DocumentResponse> getAllMyDocument(int page, int size, String keyword, String sortBy) {
		Pageable pageable;
	    switch (sortBy) {
	        case "NEWEST" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
	        case "OLDEST" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
	        default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
	    }
	    var context = SecurityContextHolder.getContext();
	    String name = context.getAuthentication().getName();
	    Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow();

	    Page<Document> documents;
	    if (keyword == null || keyword.trim().isEmpty()) {
	        documents = documentRepository.findByAccount(account, pageable);
	    } else {
	        documents = documentRepository.findByAccountAndTitleContainingIgnoreCase(account, keyword, pageable);
	    }

	    return documents.map(d -> {
	        DocumentResponse documentResponse = documentMapper.toDocumentResponse(d);
	        Object object = accountService.getAccountDetails(d.getAccount().getId());
	        documentResponse.setObject(object);
	        return documentResponse;
	    });
	}

	
	
	public boolean setStatus(String id, String status) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Document document = documentRepository.findByIdAndAccount(id,account);
		String statuss = switch (status) {
        case "PRIVATE" -> DocumentStatus.PRIVATE.name();
        case "PUBLIC" -> DocumentStatus.PUBLIC.name();
        default -> throw new AppException(ErrorCode.CODE_ERROR);
		};
		if(document != null) {
			document.setStatus(statuss);
			documentRepository.save(document);
			return true;
		}
		return false;
	}
	
	public String uploadFile(String id, MultipartFile file, String type) {
	    if (file == null || file.isEmpty()) {
	        throw new AppException(ErrorCode.FILE_EMPTY);
	    }

	    Document document = documentRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    String fileUrl = generalFileUploadFunction.apply(id, type.toLowerCase(), file);
	    document.setPath(fileUrl);
	    document.setFileName(file.getOriginalFilename());
	    documentRepository.save(document);

	    return fileUrl;
	}


	private final Function<String, String> fileExtension = filename ->
    Optional.ofNullable(filename)
            .filter(name -> name.contains("."))
            .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
            .orElse("");

	private String getFolderFromType(String type) {
	    return switch (type.toLowerCase()) {
	        case "image" -> "images";
	        case "file"-> "files";
	        default -> throw new RuntimeException("Unsupported file type: " + type);
	    };
	}

	private final TriFunction<String, String, MultipartFile, String> generalFileUploadFunction = (id, type, file) -> {
    String extension = fileExtension.apply(file.getOriginalFilename());
    validateFileExtension(type, extension);

    String filename = id + extension;

    String folder = getFolderFromType(type);
    String baseDir = switch (folder) {
        case "images" -> Constant.PHOTO_DIRECTORY;
        case "files" -> Constant.FILE_DIRECTORY;
        default -> throw new RuntimeException("Invalid folder: " + folder);
    };

    
    try {
        Path storagePath = Paths.get(baseDir).toAbsolutePath().normalize();
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        Path destination = storagePath.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return "/lms/document/" + folder + "/" + filename;

    } catch (IOException e) {
        throw new RuntimeException("Could not save file: " + filename, e);
    	}
	};
	
	
	public ResponseEntity<byte[]> getFile(String filename) throws IOException {
        Path path = Paths.get(Constant.FILE_DIRECTORY + filename);
        byte[] data = Files.readAllBytes(path);

        String mimeType = Files.probeContentType(path); // Lấy content-type tự động

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(data);
    }
	
	private void validateFileExtension(String type, String extension) {
	    Set<String> imageExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif");
	    Set<String> fileExtensions = Set.of(".pdf", ".doc", ".docx", ".txt", ".pptx");

	    switch (type.toLowerCase()) {
	        case "image" -> {
	            if (!imageExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
	            }
	        }
	        case "file" -> {
	            if (!fileExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
	            }
	        }
	        default -> throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);
	    }
	}

}
