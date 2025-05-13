package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

	    Post post = Post.builder()
	            .group(group)
	            .title(request.getTitle())
	            .text(request.getText())
	            .teacher(teacher)
	            .createdAt(new Date())
	            .build();
	    post = postRepository.save(post);

	    List<FileUploadRequest> uploads = request.getFileUploadRequests();
	    if (uploads != null && !uploads.isEmpty()) {
	        List<PostFile> postFiles = new ArrayList<>();
	        for (FileUploadRequest fileRequest : uploads) {
	            if (!isValidFileUpload(fileRequest)) continue;
	            MultipartFile file = fileRequest.getFile();
	            String type = fileRequest.getType();
	            PostFile pf = postFileService.creatPostFile(post, file, type);
	            postFiles.add(pf);
	        }
	        if (!postFiles.isEmpty()) {
	            post.setFiles(postFiles);
	            post = postRepository.save(post); 
	        }
	    }
	    return postMapper.toPostResponse(post);
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

	    if (uploads != null) {
	
	        Set<String> updatedFileNames = uploads.stream()
	                .filter(this::isValidFileUpload)
	                .map(req -> req.getFile().getOriginalFilename())
	                .filter(Objects::nonNull)
	                .map(String::toLowerCase)
	                .collect(Collectors.toSet());


	        List<PostFile> filesToKeep = existingFiles.stream()
	                .filter(existing -> updatedFileNames.contains(existing.getFileName().toLowerCase()))
	                .collect(Collectors.toList());

	        List<PostFile> filesToDelete = existingFiles.stream()
	                .filter(existing -> !updatedFileNames.contains(existing.getFileName().toLowerCase()))
	                .collect(Collectors.toList());

	        for (PostFile fileToDelete : filesToDelete) {
	            postFileService.deleteFile(fileToDelete); 
	        }

	        for (FileUploadRequest fileRequest : uploads) {
	            if (!isValidFileUpload(fileRequest)) continue;

	            MultipartFile file = fileRequest.getFile();
	            String fileName = file.getOriginalFilename();

	            boolean isAlreadyIncluded = filesToKeep.stream().anyMatch(existing ->
	                    existing.getFileName().equalsIgnoreCase(fileName));

	            if (!isAlreadyIncluded) {
	                PostFile newFile = postFileService.creatPostFile(post, file, fileRequest.getType());
	                filesToKeep.add(newFile);
	            }
	        }
	        post.setFiles(filesToKeep);
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

}
