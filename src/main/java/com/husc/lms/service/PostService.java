package com.husc.lms.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.husc.lms.configuration.LimitedInputStream;
import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.dto.request.FileUploadRequest;
import com.husc.lms.dto.request.PostRequest;
import com.husc.lms.dto.response.PostResponse;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Post;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.PostMapper;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.PostRepository;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private PostFileService postFileService;
	
	@Autowired
	private PostMapper postMapper;
	
	public PostResponse createPost(PostRequest request) {
	    Group group = groupRepository.findById(request.getGroupId())
	            .orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

	    Post post = Post.builder()
	            .group(group)
	            .title(request.getTitle())
	            .text(request.getText())
	            .createdAt(new Date())
	            .build();

	    post = postRepository.save(post);

	    List<FileUploadRequest> uploads = request.getFileUploadRequests();
	    if (uploads != null && !uploads.isEmpty()) {
	        for (FileUploadRequest fileRequest : uploads) {
	            MultipartFile file = fileRequest.getFile();
	            String type = fileRequest.getType();

	            if (file == null || file.isEmpty() || type == null || type.trim().isEmpty()) {
	                
	                continue;
	            }

	            postFileService.creatPostFile(post, file, type);
	        }
	    }

	    return postMapper.toPostResponse(post);
	}

	
	public Boolean deletePost(String postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
		if(post != null) {
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
	
}
