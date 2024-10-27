package pt.psoft.g1.psoftg1.readermanagement.infraestructure.repositories.mysql;

import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;

import org.springframework.data.domain.Pageable;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mysql")
public interface SpringDataReaderRepositoryImpl extends ReaderRepository, ReaderDetailsRepoCustom, CrudRepository<ReaderDetails, Long> {
    @Override
    @Query("SELECT r " +
            "FROM ReaderDetails r " +
            "WHERE r.readerNumber.readerNumber = :readerNumber")
    Optional<ReaderDetails> findByReaderNumber(@Param("readerNumber") @NotNull String readerNumber);

    @Override
    @Query("SELECT r " +
            "FROM ReaderDetails r " +
            "WHERE r.phoneNumber.phoneNumber = :phoneNumber")
    List<ReaderDetails> findByPhoneNumber(@Param("phoneNumber") @NotNull String phoneNumber);

    @Override
    @Query("SELECT r " +
            "FROM ReaderDetails r " +
            "JOIN User u ON r.reader.id = u.id " +
            "WHERE u.username = :username")
    Optional<ReaderDetails> findByUsername(@Param("username") @NotNull String username);

    @Override
    @Query("SELECT r " +
            "FROM ReaderDetails r " +
            "JOIN User u ON r.reader.id = u.id " +
            "WHERE u.id = :userId")
    Optional<ReaderDetails> findByUserId(@Param("userId") @NotNull Long userId);


    @Override
    @Query("SELECT COUNT (rd) " +
            "FROM ReaderDetails rd " +
            "JOIN User u ON rd.reader.id = u.id " +
            "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE)")
    int getCountFromCurrentYear();

    @Query("SELECT rd " +
            "FROM ReaderDetails rd " +
            "JOIN Lending l ON l.readerDetails.pk = rd.pk " +
            "GROUP BY rd " +
            "ORDER BY COUNT(l) DESC")
    Page<ReaderDetails> findTopReaders(Pageable pageable);

    @Override
    @Query("SELECT NEW pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO(rd, count(l)) " +
            "FROM ReaderDetails rd " +
            "JOIN Lending l ON l.readerDetails.pk = rd.pk " +
            "JOIN Book b ON b.pk = l.book.pk " +
            "JOIN Genre g ON g.pk = b.genre.pk " +
            "WHERE g.genre = :genre " +
            "AND l.startDate >= :startDate " +
            "AND l.startDate <= :endDate " +
            "GROUP BY rd.pk " +
            "ORDER BY COUNT(l.pk) DESC")
    Page<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate);
}

