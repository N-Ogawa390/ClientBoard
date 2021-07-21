package net.dkt.dktsearch.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Entity
@Data
public class Account {
	
	@Id
	@Column(length = 80)
	@NotBlank(message="※ユーザ名を入力してください")
	private String username;
	
	@Column(length = 20)
	private String type;
	
	@NotBlank(message = "※メールアドレスを入力してください")
	@Email(message = "※正しい形式で入力してください")
	private String email;
	
	private String site;
	
	private Boolean active;
	
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	private List<Client> clients;
	

}
