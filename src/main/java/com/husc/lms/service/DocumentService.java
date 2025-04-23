package com.husc.lms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.dto.response.DocumentResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Document;
import com.husc.lms.entity.Major;
import com.husc.lms.enums.DocumentStatus;
import com.husc.lms.enums.ErrorCode;
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
	
	public DocumentResponse createDocument(String title, String description, String status, String majorId, MultipartFile file, String type) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		
		String statuss = switch (status) {
        case "PRIVATE" -> DocumentStatus.PRIVATE.name();
        case "PUBLIC" -> DocumentStatus.PUBLIC.name();
        default -> throw new AppException(ErrorCode.CODE_ERROR);
		};
		
		Major major = majorRepository.findById(majorId).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		Document document = Document.builder()
				.account(account)
				.title(title)
				.description(description)
				.status(statuss)
				.major(major)
				.createdAt(new Date())
				.build();
		document = documentRepository.save(document);
		String id = document.getId();
		uploadFile(id, file, type);
		DocumentResponse documentResponse = documentMapper.toDocumentResponse(document);
		Object object = accountService.getAccountDetails(account.getId());
		documentResponse.setObject(object);
		return documentResponse;
	}
	
	public boolean deleteDocument(String id) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		Document document = documentRepository.findByIdAndAccount(id,account);
		if(document != null) {
			documentRepository.deleteById(id);
			return true;
		}
		return false;
	}
	
	public List<DocumentResponse> getAllDocument(){
		List<Document> documents = documentRepository.findAllByStatus(DocumentStatus.PUBLIC.name());
		List<DocumentResponse> documentResponses = new ArrayList<DocumentResponse>();
		for(Document d : documents) {
			DocumentResponse documentResponse = documentMapper.toDocumentResponse(d);
			Object object = accountService.getAccountDetails(d.getAccount().getId());
			documentResponse.setObject(object);
			documentResponses.add(documentResponse);
		}
		return documentResponses;
	}
	
	public List<DocumentResponse> getAllMyDocument(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		List<Document> documents = documentRepository.findByAccount(account);
		List<DocumentResponse> documentResponses = new ArrayList<DocumentResponse>();
		for(Document d : documents) {
			DocumentResponse documentResponse = documentMapper.toDocumentResponse(d);
			Object object = accountService.getAccountDetails(d.getAccount().getId());
			documentResponse.setObject(object);
			documentResponses.add(documentResponse);
		}
		return documentResponses;
	}
	
	
	public boolean setStatus(String id, String status) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
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
	        throw new RuntimeException("File is empty");
	    }

	    Document document = documentRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    String fileUrl = generalFileUploadFunction.apply(id, type.toLowerCase(), file);
	    document.setPath(fileUrl);
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
	        case "video" -> "videos";
	        case "file"-> "files";
	        default -> throw new RuntimeException("Unsupported file type: " + type);
	    };
	}

	private final TriFunction<String, String, MultipartFile, String> generalFileUploadFunction = (id, type, file) -> {
    String extension = fileExtension.apply(file.getOriginalFilename());
    String filename = id + extension;

    String folder = getFolderFromType(type);
    String baseDir = switch (folder) {
        case "images" -> Constant.PHOTO_DIRECTORY;
        case "videos" -> Constant.VIDEO_DIRECTORY;
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
}
