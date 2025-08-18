package dev.kaushik.library.model;

import java.time.LocalDateTime;

import dev.kaushik.library.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	private Integer memberID;

	@NotBlank(message = "Name cannot be blank")
	@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only alphabets and spaces")
	private String name;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Email should be valid")
	private String email;

	@NotNull(message = "Phone number cannot be null")
	@Min(value = 1000000000L, message = "Phone number must have exactly 10 digits")
	@Max(value = 9999999999L, message = "Phone number must have exactly 10 digits")
	private long phoneNumber;


	@NotNull(message = "Gender cannot be null")
	private Gender gender;

	@NotBlank(message = "Address cannot be blank")
	@Size(max = 255, message = "Address cannot exceed 255 characters")
	private String address;
	
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
}