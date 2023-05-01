package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
class ReviewRepositoryImpl implements ReviewRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Review> REVIEW_MAPPER = (rs, rowNum) -> Review.builder()
            .reviewId(rs.getLong("REVIEW_ID"))
            .content(rs.getString("CONTENT"))
            .isPositive(rs.getBoolean("IS_POSITIVE"))
            .useful(rs.getLong("USEFUL"))
            .filmId(rs.getInt("FILM_ID"))
            .userId(rs.getInt("USER_ID"))
            .build();

    @Override
    public Long addReview(Review review) {
        String sql = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USEFUL, FILM_ID, USER_ID) "
                + "VALUES(?, ?, 0, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, new String[]{"REVIEW_ID"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.isPositive());
            statement.setLong(3, review.getFilmId());
            statement.setLong(4, review.getUserId());
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKeyAs(Long.class));
    }

    @Override
    public void updateReview(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT=?, IS_POSITIVE=?, USEFUL=?, FILM_ID=?, USER_ID=? WHERE REVIEW_ID=?";
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.isPositive());
            statement.setLong(3, review.getUseful());
            statement.setLong(4, review.getFilmId());
            statement.setLong(5, review.getUserId());
            statement.setLong(6, review.getReviewId());
            return statement;
        });
    }

    @Override
    public void deleteReview(Long reviewId) {
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID=?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public Review getReviewById(Long reviewId) {
        String sql = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USEFUL, FILM_ID, USER_ID FROM REVIEWS WHERE REVIEW_ID=?";
        return jdbcTemplate.queryForObject(sql, REVIEW_MAPPER, reviewId);
    }

    @Override
    public Collection<Review> getAllReviews(Long count) {
        String sql = "SELECT * FROM REVIEW ORDER BY USEFUL LIMIT ?";
        return jdbcTemplate.query(sql, REVIEW_MAPPER, count);
    }

    @Override
    public Collection<Review> getAllReviews(Integer filmId, Long count) {
        String sql = "SELECT * FROM REVIEW WHERE FILM_ID = ? ORDER BY USEFUL LIMIT ?";
        return jdbcTemplate.query(sql, REVIEW_MAPPER, filmId, count);
    }

    // TODO: 01.05.2023 Разобраться далее
    @Override
    public void likeReview(Long reviewId, Integer userId) {
        String sql = "";
    }

    @Override
    public void dislikeReview(Long reviewId, Integer userId) {

    }

    @Override
    public void deleteLikeReview(Long reviewId, Integer userId) {

    }

    @Override
    public void deleteDislikeReview(Long reviewId, Integer userId) {

    }
}