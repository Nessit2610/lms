package com.husc.lms.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBoxCreateRequest {
	private List<String> anotherAccounts;
	private String groupName;
	private String currentAccountUsername;
}
