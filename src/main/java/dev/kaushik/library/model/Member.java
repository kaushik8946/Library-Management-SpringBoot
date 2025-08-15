package dev.kaushik.library.model;

import java.time.LocalDateTime;

import dev.kaushik.library.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
	private String name;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Email should be valid")
	private String email;

	@NotNull(message = "Phone number cannot be null")
	@Positive(message = "Phone number must be a positive number")
	private long phoneNumber;

	@NotNull(message = "Gender cannot be null")
	private Gender gender;

	@NotBlank(message = "Address cannot be blank")
	@Size(max = 255, message = "Address cannot exceed 255 characters")
	private String address;

	@PastOrPresent(message = "creation date must be past or present")
	private LocalDateTime createdAt;
	
	@NotNull(message = "'created by' name can not be null")
	@NotBlank(message = "'created by' name can not be blank")
	private String createdBy;

	private LocalDateTime updatedAt;
	private String updatedBy;
}