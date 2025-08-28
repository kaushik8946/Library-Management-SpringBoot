package dev.kaushik.library.model;

import java.time.LocalDateTime;

import dev.kaushik.library.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"createdAt","createdBy","updatedAt","updatedBy"})
@ToString(exclude = {"createdAt","createdBy","updatedAt","updatedBy"})
public class Member {
	private Integer memberID;

	@NotBlank(message = "name cant be blank")
	@Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "name must contain only alphabets and spaces")
	private String name;

	@NotBlank(message = "email cannot be blank")
	@Email(message = "email should be valid")
	private String email;

	@Min(value = 1000000000L, message = "Phone number must have exactly 10 digits")
	@Max(value = 9999999999L, message = "Phone number must have exactly 10 digits")
	private long phoneNumber;

	@NotNull(message = "Gender cannot be null")
	private Gender gender;

	@NotBlank(message = "Address cannot be blank")
	@Size(max = 500, message = "Address cant be more than 500 characters")
	private String address;
	
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
}