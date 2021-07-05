package net.dkt.dktsearch.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountFormTest {
	
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	public void testValidationSuccess() {
		
		AccountForm form = new AccountForm();
		form.setUsername("user");
		form.setEmail("test@email.com");
		form.setPassword("passwordpassword");
		
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(0);
	}
	
	@Test
	public void testValidationFailUsernameBlank() {
		
		AccountForm form = new AccountForm();
		form.setUsername("user");
		form.setPassword("passwordpassword");
		
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		
		for(ConstraintViolation<AccountForm> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailEmailBlank() {
		
		AccountForm form = new AccountForm();
		form.setEmail("test@email.com");
		form.setPassword("passwordpassword");
		
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		
		for(ConstraintViolation<AccountForm> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailPassWordBlank() {
		
		AccountForm form = new AccountForm();
		form.setUsername("user");
		form.setEmail("test@email.com");
		
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		
		for(ConstraintViolation<AccountForm> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailPassWordSize() {
		
		AccountForm form = new AccountForm();
		form.setUsername("user");
		form.setEmail("test@email.com");
		form.setPassword("pass");
		
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		
		for(ConstraintViolation<AccountForm> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}

}
