package pt.psoft.g1.psoftg1.readermanagement.infraestructure.repositories.redis;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("redis")
public class RedisReaderDetailsRepository implements ReaderRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisReaderDetailsRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<ReaderDetails> findByReaderNumber(String readerNumber) {
        return Optional.empty();
    }

    @Override
    public List<ReaderDetails> findByPhoneNumber(String phoneNumber) {
        return List.of();
    }

    @Override
    public Optional<ReaderDetails> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<ReaderDetails> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public int getCountFromCurrentYear() {
        return 0;
    }

    @Override
    public ReaderDetails save(ReaderDetails readerDetails) {
        return null;
    }

    @Override
    public List<ReaderDetails> findAll() {
        return null;
    }

    @Override
    public Page<ReaderDetails> findTopReaders(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate) {
        return null;
    }

    @Override
    public void delete(ReaderDetails readerDetails) {

    }

    @Override
    public List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query) {
        return List.of();
    }
}
