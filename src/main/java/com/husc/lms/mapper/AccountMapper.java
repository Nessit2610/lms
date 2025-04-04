package com.husc.lms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.entity.Account;


@Mapper(componentModel = "spring")
public interface AccountMapper{
	
	public Account toAccount(AccountRequest request);

	
	public AccountResponse toAccountResponse(Account user);
}
