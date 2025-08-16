package dev.kaushik.library.controller;

import dev.kaushik.library.model.IssueRecord;
import dev.kaushik.library.model.Member;
import dev.kaushik.library.service.IssueService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "http://localhost:3000")
public class IssueController {

	private final IssueService issueService;

	@Autowired
	public IssueController(IssueService issueService) {
		this.issueService = issueService;
	}

	@PostMapping("/issueBook")
	public ResponseEntity<Integer> issueBook(@RequestParam @Positive int bookId, @RequestParam @Positive int memberId,
			@RequestParam @NotNull String issuedBy) {
		Integer issueId = issueService.issueBook(bookId, memberId, issuedBy);
		return new ResponseEntity<>(issueId, HttpStatus.CREATED);
	}

	@PutMapping("/returnBook")
	public ResponseEntity<Integer> returnBook(@RequestParam @Positive int bookId,
			@RequestParam @NotNull String returnedBy) {
		Integer updatedCount = issueService.returnBook(bookId, returnedBy);
		return new ResponseEntity<>(updatedCount, HttpStatus.OK);
	}

	@GetMapping("/viewIssuedRecords")
	public ResponseEntity<List<IssueRecord>> viewIssuedRecords() {
		List<IssueRecord> issuedRecords = issueService.getAllIssuedRecords();
		return new ResponseEntity<>(issuedRecords, HttpStatus.OK);
	}

	@GetMapping("/reports/overdueBooks")
	public ResponseEntity<List<IssueRecord>> getOverdueBooks() {
		List<IssueRecord> overdueBooks = issueService.getOverdueBooks();
		return new ResponseEntity<>(overdueBooks, HttpStatus.OK);
	}

	@GetMapping("/reports/membersWithActiveBooks")
	public ResponseEntity<List<Member>> getMembersWithActiveBooks() {
		List<Member> members = issueService.getMembersWithActiveBooks();
		return new ResponseEntity<>(members, HttpStatus.OK);
	}
}