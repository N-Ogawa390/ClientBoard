package net.dkt.dktsearch.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AccountForm {
	
	private String type;	//client or administrator
	
	@NotBlank(message = "※ユーザ名を入力してください")
	private String username;
	
	@NotBlank(message = "※メールアドレスを入力してください")
	private String email;
	
	private String site;

	@NotBlank(message = "※パスワードを入力してください")
	@Size(min = 8, message = "※パスワードは8文字以上で入力してください")
	private String password;
	
	private Boolean active;
}