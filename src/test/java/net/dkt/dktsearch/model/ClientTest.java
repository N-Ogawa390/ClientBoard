package net.dkt.dktsearch.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientTest {
	
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	public void testValidationSuccess() {
		
		Client client = new Client();
		client.setClientName("client");
		Set<ConstraintViolation<Client>> violations = validator.validate(client);
		
		assertThat(violations.size()).isEqualByComparingTo(0);
	}
	
	@Test
	public void testValidationFailClientNameBlank() {
		
		Client client = new Client();
		Set<ConstraintViolation<Client>> violations = validator.validate(client);
		
		assertThat(violations.size()).isEqualByComparingTo(1);
		
		for(ConstraintViolation<Client> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
}
