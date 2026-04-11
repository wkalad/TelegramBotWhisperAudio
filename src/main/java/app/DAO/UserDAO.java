package app.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveOrUpdateUser(long userId, String language, String model){
        int updated = jdbcTemplate.update(
                "UPDATE telegram_user SET language = ?, model = ? WHERE user_id = ?",
                language, model, userId
        );

        if (updated == 0){
            jdbcTemplate.update(
                    "INSERT INTO telegram_user (user_id, language, model) VALUES (?, ?, ?)",
                    userId, language, model
            );
        }
    }

    public Optional<String> findLanguageByUser(long userId){
        var list = jdbcTemplate.query(
                "SELECT language FROM telegram_user WHERE user_id = ?",
                (rs, rowNum) -> rs.getString("language"),
                userId
        );

        return list.stream().findFirst();
    }

    public Optional<String> findModelByUser(long userId){
        var list = jdbcTemplate.query(
                "SELECT model FROM telegram_user WHERE user_id = ?",
                (rs, rowNum) -> rs.getString("model"),
                userId
        );

        return list.stream().findFirst();
    }
}
