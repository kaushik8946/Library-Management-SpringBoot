package dev.kaushik.library.model;

import java.time.LocalDateTime;

import dev.kaushik.library.model.enums.IssueStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueRecord {
	private Integer issueId; 
	private Integer bookId;
	private Integer memberId;
	
	@NotNull(message = "IssueStatus can not be null")
	private IssueStatus status;
	
	@PastOrPresent(message = "Issue date must be past or present, not future")
	private LocalDateTime issueDate;
	
	@NotBlank(message = "Issuer name can not be blank")
	private String issuedBy;
	
	@PastOrPresent(message = "return date must be past or present, not future")
	private LocalDateTime returnDate;
	
	private String returnedBy;
}
