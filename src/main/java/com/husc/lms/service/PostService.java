package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.request.FileUploadRequest;
import com.husc.lms.dto.request.PostRequest;
import com.husc.lms.dto.response.PostResponse;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Post;
import com.husc.lms.entity.PostFile;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.PostMapper;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.PostFileRepository;
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
	private PostFileRepository postFileRepository;
	
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
	    	List<PostFile> postFiles = new ArrayList<PostFile>();
	        for (FileUploadRequest fileRequest : uploads) {
	            MultipartFile file = fileRequest.getFile();
	            String type = fileRequest.getType();

	            if (file == null || file.isEmpty() || type == null || type.trim().isEmpty()) {
	                
	                continue;
	            }
	            PostFile pf = postFileService.creatPostFile(post, file, type);
	            postFiles.add(pf);
	        }
	        post.setFiles(postFiles);
	        postRepository.save(post);
	    }

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
	
}
