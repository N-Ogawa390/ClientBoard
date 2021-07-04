package net.dkt.dktsearch.model;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;

public class AccountFormTest {
	
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

}
