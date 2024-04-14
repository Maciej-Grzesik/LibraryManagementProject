package org.library.backend.Service;

import org.library.backend.Controller.DTO.LoanDto.CreateLoanDto;
import org.library.backend.Controller.DTO.LoanDto.CreateLoanResponseDto;
import org.library.backend.Controller.DTO.LoanDto.GetLoanDto;
import org.library.backend.Infrastructure.Entity.LoanEntity;
import org.library.backend.Infrastructure.Repository.BookRepository;
import org.library.backend.Infrastructure.Repository.LoanRepository;
import org.library.backend.Infrastructure.Repository.UserRepository;
import org.library.backend.Service.exceptions.NotFound.BookNotFoundException;
import org.library.backend.Service.exceptions.NotFound.LoanNotFoundException;
import org.library.backend.Service.exceptions.NotFound.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LoanService class provides services related to loan management
 */
@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieves a loan DTO by ID
     *
     * @param id the ID of the loan
     * @return the GetLoanDto representing the loan
     * @throws LoanNotFoundException if the loan with the specified ID is not found
     */
    public GetLoanDto getLoanDto(long id) {
        var loan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
        return new GetLoanDto(loan.getBook(), loan.getUser(), loan.getLoanDate(), loan.getDueDate(), loan.getReturnDate());
    }

    /**
     * Retrieves all loans DTOs by user ID
     *
     * @param id the ID of the user
     * @return the list of GetLoanDto representing all loans of the user
     */
    public List<GetLoanDto> getAllLoansByUserId(long id) {
        var loans = loanRepository.findAllByUserId(id);
        return loans.stream().map((loan) -> new GetLoanDto(loan.getBook(), loan.getUser(), loan.getLoanDate(), loan.getDueDate(), loan.getReturnDate())).collect(Collectors.toList());
    }

    /**
     * Creates a new loan
     *
     * @param loanDto the CreateLoanDto containing information about the loan
     * @return the CreateLoanResponseDto representing the newly created loan
     * @throws BookNotFoundException if the book with the specified ID is not found
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public CreateLoanResponseDto createLoan(CreateLoanDto loanDto) {
        var loanEntity = new LoanEntity();
        var bookEntity = bookRepository.findById(loanDto.getBookId()).orElseThrow(() -> new BookNotFoundException(loanDto.getBookId()));
        var userEntity = userRepository.findById(loanDto.getUserId()).orElseThrow(() -> new UserNotFoundException(loanDto.getUserId()));
        loanEntity.setUser(userEntity);
        loanEntity.setBook(bookEntity);
        loanEntity.setLoanDate(loanDto.getLoanDate());
        loanEntity.setDueDate(loanDto.getDueDate());

        var newLoan = loanRepository.save(loanEntity);

        return new CreateLoanResponseDto(newLoan.getBook(), newLoan.getDueDate());
    }

    /**
     * Deletes a loan by ID
     *
     * @param id the ID of the loan to delete
     * @throws LoanNotFoundException if the loan with the specified ID is not found
     */
    public void deleteLoan(long id) {
        if (!loanRepository.existsById(id)){
            throw new LoanNotFoundException(id);
        }
        loanRepository.deleteById(id);
    }
}
