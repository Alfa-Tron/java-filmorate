package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
class ReviewRepositoryImpl implements ReviewRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Review> REVIEW_MAPPER = (rs, rowNum) -> Review.builder()
            .id(rs.getLong("REVIEW_ID"))
            .content(rs.getString("CONTENT"))
            .positive(rs.getBoolean("IS_POSITIVE"))
            .useful(rs.getLong("USEFUL"))
            .filmId(rs.getInt("FILM_ID"))
            .userId(rs.getInt("USER_ID"))
            .build();

    @Override
    public Long addReview(Review review) {
        String sql = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, FILM_ID, USER_ID) "
                + "VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, new String[]{"REVIEW_ID"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getPositive());
            statement.setLong(3, review.getFilmId());
            statement.setLong(4, review.getUserId());
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKeyAs(Long.class));
    }

    @Override
    @Transactional
    public Review updateReview(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT=?, IS_POSITIVE=? WHERE REVIEW_ID=?";
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getPositive());
            statement.setLong(3, review.getId());
            return statement;
        });
        return Objects.requireNonNull(getReviewById(review.getId()));
    }

    @Override
    public void deleteReview(Long reviewId) {
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID=?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        String sql = "SELECT " +
                "REVIEWS.*, " +
                "sum( " +
                "case LIKEREVIEWS.IS_POSITIVE " +
                "when TRUE then 1 " +
                "WHEN FALSE THEN -1 " +
                "else 0 END " +
                ") AS USEFUL " +
                "FROM REVIEWS " +
                "LEFT JOIN LIKEREVIEWS ON LIKEREVIEWS.REVIEW_ID = REVIEWS.REVIEW_ID " +
                "WHERE REVIEWS.REVIEW_ID = ? " +
                "GROUP BY REVIEWS.REVIEW_ID";
        return jdbcTemplate.queryForObject(sql, REVIEW_MAPPER, reviewId);
    }

    @Override
    public Collection<Review> getAllReviews(Long count) {
        String sql = "SELECT " +
                "REVIEWS.*, " +
                "sum( " +
                "case LIKEREVIEWS.IS_POSITIVE " +
                "when TRUE then 1 " +
                "WHEN FALSE THEN -1 " +
                "else 0 END " +
                ") AS USEFUL " +
                "FROM REVIEWS " +
                "LEFT JOIN LIKEREVIEWS ON LIKEREVIEWS.REVIEW_ID = REVIEWS.REVIEW_ID " +
                "GROUP BY REVIEWS.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, REVIEW_MAPPER, count);
    }

    @Override
    public Collection<Review> getAllReviews(Integer filmId, Long count) {
        String sql = "SELECT " +
                "REVIEWS.*, " +
                "sum( " +
                "case LIKEREVIEWS.IS_POSITIVE " +
                "when TRUE then 1 " +
                "WHEN FALSE THEN -1 " +
                "else 0 END " +
                ") AS USEFUL " +
                "FROM REVIEWS " +
                "LEFT JOIN LIKEREVIEWS ON LIKEREVIEWS.REVIEW_ID = REVIEWS.REVIEW_ID " +
                "WHERE REVIEWS.FILM_ID = ? " +
                "GROUP BY REVIEWS.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, REVIEW_MAPPER, filmId, count);
    }

    @Override
    public void likeReview(Long reviewId, Integer userId) {
        String sql = "MERGE INTO LIKEREVIEWS (REVIEW_ID, USER_ID, IS_POSITIVE)"
                + "VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, true);
    }

    @Override
    public void dislikeReview(Long reviewId, Integer userId) {
        String sql = "MERGE INTO LIKEREVIEWS (REVIEW_ID, USER_ID, IS_POSITIVE)"
                + "VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, false);
    }

    @Override
    public void deleteLikeReview(Long reviewId, Integer userId) {
        String sql = "DELETE FROM LIKEREVIEWS WHERE REVIEW_ID=? AND USER_ID=? AND IS_POSITIVE = true";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteDislikeReview(Long reviewId, Integer userId) {
        String sql = "DELETE FROM LIKEREVIEWS WHERE REVIEW_ID=? AND USER_ID=? AND IS_POSITIVE = false";
        jdbcTemplate.update(sql, reviewId, userId);
    }
}