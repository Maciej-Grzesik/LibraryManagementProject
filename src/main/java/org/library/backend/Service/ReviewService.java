package org.library.backend.Service;

import org.library.backend.Controller.DTO.ReviewDto.CreateReviewDto;
import org.library.backend.Controller.DTO.ReviewDto.CreateReviewResponseDto;
import org.library.backend.Controller.DTO.ReviewDto.GetReviewDto;
import org.library.backend.Infrastructure.Entity.ReviewEntity;
import org.library.backend.Infrastructure.Repository.BookRepository;
import org.library.backend.Infrastructure.Repository.ReviewRepository;
import org.library.backend.Infrastructure.Repository.UserRepository;
import org.library.backend.Service.exceptions.NotFound.BookNotFoundException;
import org.library.backend.Service.exceptions.NotFound.ReviewNotFoundException;
import org.library.backend.Service.exceptions.NotFound.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ReviewService class provides services related to book reviews
 */
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all reviews DTOs by book ID
     *
     * @param id the ID of the book
     * @return the list of GetReviewDto representing all reviews of the book
     */
    public List<GetReviewDto> getReviewsByBookId(long id) {
        var reviews = reviewRepository.findAllByBookId(id);
        return reviews.stream().map((review) -> new GetReviewDto(review.getRating(), review.getComment(), review.getDate())).collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews DTOs by user ID
     *
     * @param id the ID of the user
     * @return the list of GetReviewDto representing all reviews by the user
     */
    public List<GetReviewDto> getReviewsByUserId(long id) {
        var reviews = reviewRepository.findAllByUserId(id);
        return reviews.stream().map((review) -> new GetReviewDto(review.getRating(), review.getComment(), review.getDate())).collect(Collectors.toList());
    }

    /**
     * Creates a new review
     *
     * @param reviewDto the CreateReviewDto containing information about the review
     * @return the CreateReviewResponseDto representing the newly created review
     * @throws BookNotFoundException if the book with the specified ID is not found
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public CreateReviewResponseDto createReview(CreateReviewDto reviewDto) {
        var bookEntity = bookRepository.findById(reviewDto.getBookId()).orElseThrow(() -> new BookNotFoundException(reviewDto.getBookId()));
        var userEntity = userRepository.findById(reviewDto.getUserId()).orElseThrow(() -> new UserNotFoundException(reviewDto.getUserId()));
        var reviewEntity = new ReviewEntity();

        reviewEntity.setBook(bookEntity);
        reviewEntity.setUser(userEntity);
        reviewEntity.setComment(reviewDto.getComment());
        reviewEntity.setRating(reviewDto.getRating());
        reviewEntity.setDate(reviewDto.getDate());

        var newReview = reviewRepository.save(reviewEntity);
        return new CreateReviewResponseDto(newReview.getRating(), newReview.getComment(), newReview.getDate());
    }

    /**
     * Deletes a review by ID
     *
     * @param id the ID of the review to delete
     * @throws ReviewNotFoundException if the review with the specified ID is not found
     */
    public void deleteReview(long id) {
        if(!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException(id);
        }
        reviewRepository.deleteById(id);
    }
}
