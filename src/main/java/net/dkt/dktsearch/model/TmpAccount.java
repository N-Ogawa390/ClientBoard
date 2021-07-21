package net.dkt.dktsearch.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class TmpAccount {
	
	@Id
	@NotBlank(message = "※ユーザIDを入力してください")
	private String username;
	
	@NotBlank(message = "※パスワードを入力してください")
	@Size(message = "※パスワードは8文字以上で入力してください")
	private String password;
	
	@NotBlank(message = "※メールアドレスを入力してください")
	@Email(message = "※形式が不正です")
	private String email;
	
//	@NotBlank(message = "※サイトURLを入力してください")
	private String site;
	
	private String tmpURL;
	
	private LocalDateTime created;

}
