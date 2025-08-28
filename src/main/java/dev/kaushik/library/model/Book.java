package dev.kaushik.library.model;

import java.time.LocalDateTime;

import dev.kaushik.library.model.enums.BookAvailability;
import dev.kaushik.library.model.enums.BookCategory;
import dev.kaushik.library.model.enums.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"createdAt","createdBy","updatedAt","updatedBy"})
public class Book {
	private Integer bookId;

	@NotBlank(message = "Title cannot be blank")
	@Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
	private String title;

	@NotBlank(message = "Author cannot be blank")
	@Size(min = 5, max = 100, message = "Author name must be between 5 and 100 characters")
	private String author;

	@NotNull(message = "Category cannot be null")
	private BookCategory category;

	@NotNull(message = "Status cannot be null")
	private BookStatus status;

	@NotNull(message = "Availability cannot be null")
	private BookAvailability availability;

	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
}