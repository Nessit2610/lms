package com.husc.lms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.FileUploadRequest;
import com.husc.lms.dto.request.PostRequest;
import com.husc.lms.dto.response.PostResponse;
import com.husc.lms.dto.update.PostUpdateRequest;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Post;
import com.husc.lms.entity.PostFile;
import com.husc.lms.entity.Teacher;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.PostMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.PostFileRepository;
import com.husc.lms.repository.PostRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private PostFileService postFileService;
	
	@Autowired
	private PostFileRepository postFileRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PostMapper postMapper;
	
	public PostResponse createPost(PostRequest request) {

	    var context = SecurityContextHolder.getContext();
	    String name = context.getAuthentication().getName();

	    Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name)
	            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
	    Teacher teacher = teacherRepository.findByAccountAndDeletedDateIsNull(account);

	    Group group = groupRepository.findById(request.getGroupId())
	            .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

	    List<FileUploadRequest> uploads = request.getFileUploadRequests();
	    if (uploads != null && !uploads.isEmpty()) {
	        for (FileUploadRequest fileRequest : uploads) {
	            if (!isValidFileUpload(fileRequest)) continue;
	            MultipartFile file = fileRequest.getFile();
	            String type = fileRequest.getType(); 
	            if(file != null  && !file.isEmpty() && type != null) {
	    			String extension = fileExtension.apply(file.getOriginalFilename());
	    			validateFileExtension(type, extension);
	    		}  
	        }   
	    }
	    
	    Post post = Post.builder()
	            .group(group)
	            .title(request.getTitle())
	            .text(request.getText())
	            .teacher(teacher)
	            .createdAt(new Date())
	            .build();
	  
        List<PostFile> postFiles = new ArrayList<>();
        for (FileUploadRequest fileRequest : uploads) {
            if (!isValidFileUpload(fileRequest)) continue;
            MultipartFile file = fileRequest.getFile();
            String type = fileRequest.getType();
            if(file != null  && !file.isEmpty() && type != null) {
    			String extension = fileExtension.apply(file.getOriginalFilename());
    			validateFileExtension(type, extension);
    		}
            PostFile pf = postFileService.creatPostFile(post, file, type);
            postFiles.add(pf);
        }
        if (!postFiles.isEmpty()) {
            post.setFiles(postFiles);
        }
        post = postRepository.save(post);
	    return postMapper.toPostResponse(post);
	}

	private final Function<String, String> fileExtension = filename ->
    Optional.ofNullable(filename)
            .filter(name -> name.contains("."))
            .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
            .orElse("");
	

    private void validateFileExtension(String type, String extension) {
	    Set<String> imageExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif");
	    Set<String> videoExtensions = Set.of(".mp4", ".avi", ".mov");
	    Set<String> fileExtensions = Set.of(".pdf", ".doc", ".docx", ".txt");

	    switch (type.toLowerCase()) {
	        case "image" -> {
	            if (!imageExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
	            }
	        }
	        case "video" -> {
	            if (!videoExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_VIDEO_TYPE);
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
    
	public PostResponse updatePost(PostUpdateRequest request) {
	    Post post = postRepository.findById(request.getPostId())
	            .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

	    if (request.getTitle() != null) {
	        post.setTitle(request.getTitle());
	    }

	    if (request.getText() != null) {
	        post.setText(request.getText());
	    }

	    List<FileUploadRequest> uploads = request.getFileUploadRequests();
	    List<PostFile> existingFiles = new ArrayList<>();
	    if (post.getFiles() != null) {
	        existingFiles.addAll(post.getFiles());
	    }
	    
        Set<String> keepFileIds = new HashSet<>();
        if (request.getOldFileIds() != null) {
            keepFileIds.addAll(request.getOldFileIds());
        }

        List<PostFile> filesToDelete = existingFiles.stream()
                .filter(file -> file.getId() == null || !keepFileIds.contains(file.getId()))
                .collect(Collectors.toList());

    	for (PostFile fileToDelete : filesToDelete) {
            postFileService.deleteFile(post, fileToDelete);
        }
	   
        if (uploads != null) {
            for (FileUploadRequest fileRequest : uploads) {
                if (!isValidFileUpload(fileRequest)) continue;

                MultipartFile file = fileRequest.getFile();
                if (file == null || file.isEmpty()) continue;

                String fileName = file.getOriginalFilename();
                if (fileName == null) continue;
                postFileService.creatPostFile(post, file, fileRequest.getType());
            }
        }

	    post = postRepository.save(post);
	    return postMapper.toPostResponse(post);
	}

	
	public Boolean deletePost(String postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
		if(post != null) {
			List<PostFile> postFiles = postFileRepository.findByPost(post);
			if(postFiles != null) {
				for(PostFile p : postFiles) {
					postFileRepository.delete(p);
					deletePhysicalFile(p.getFileUrl());
				}
			}
			postRepository.deleteById(postId);
			return true;
		}
		return false;
	}
	
	public Page<PostResponse> getAllPostInGroup(String groupId ,int pageNumber , int pageSize){
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
		Page<Post> posts = postRepository.findByGroup(group, pageable);
		return posts.map(postMapper::toPostResponse);
	}
	
	private boolean isValidFileUpload(FileUploadRequest req) {
	    return req != null
	            && req.getFile() != null
	            && !req.getFile().isEmpty()
	            && req.getType() != null
	            && !req.getType().trim().isEmpty();
	}
	
	private void deletePhysicalFile(String fileUrl) {
	    if (fileUrl == null || fileUrl.isEmpty()) return;

	    String[] parts = fileUrl.split("/");
	    if (parts.length < 4) return;

	    String folder = parts[3];      
	    String filename = parts[4];    

	    String baseDir = switch (folder) {
	        case "images" -> Constant.PHOTO_DIRECTORY;
	        case "videos" -> Constant.VIDEO_DIRECTORY;
	        case "files" -> Constant.FILE_DIRECTORY;
	        default -> throw new RuntimeException("Unsupported folder: " + folder);
	    };

	    try {
	        Path path = Paths.get(baseDir, filename);
	        Files.deleteIfExists(path);
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Không thể xóa file: " + filename, e);
	    }
	}

}
