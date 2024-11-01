package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.mysql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.LendingMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
class LendingRepoCustomImpl implements LendingRepository {
    // get the underlying JPA Entity Manager via spring thru constructor dependency
    // injection
    private final EntityManager em;
    private final MySQLLendingRepository mySQLLendingRepository;


    @Override
    public Optional<Lending> findByLendingNumber(String lendingNumber) {
        return mySQLLendingRepository.findByLendingNumber(lendingNumber).map(LendingMapper::toModel);
    }

    @Override
    public List<Lending> listByReaderNumberAndIsbn(String readerNumber, String isbn) {
        return mySQLLendingRepository.listByReaderNumberAndIsbn(readerNumber, isbn).stream().map(LendingMapper::toModel).toList();
    }

    @Override
    public int getCountFromCurrentYear() {
        return mySQLLendingRepository.getCountFromCurrentYear();
    }

    @Override
    public List<Lending> listOutstandingByReaderNumber(String readerNumber) {
        return mySQLLendingRepository.listOutstandingByReaderNumber(readerNumber).stream().map(LendingMapper::toModel).toList();
    }

    @Override
    public Double getAverageDuration() {
        return mySQLLendingRepository.getAverageDuration();
    }

    @Override
    public Double getAvgLendingDurationByIsbn(String isbn) {
        return mySQLLendingRepository.getAvgLendingDurationByIsbn(isbn);
    }

    @Override
    public List<Lending> getOverdue(Page page) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Lending> cq = cb.createQuery(Lending.class);
        final Root<Lending> root = cq.from(Lending.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();

        // Select overdue lendings where returnedDate is null and limitDate is before the current date
        where.add(cb.isNull(root.get("returnedDate")));
        where.add(cb.lessThan(root.get("limitDate"), LocalDate.now()));

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("limitDate"))); // Order by limitDate, oldest first

        final TypedQuery<Lending> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }

    public List<Lending> searchLendings(Page page, String readerNumber, String isbn, Boolean returned, LocalDate startDate, LocalDate endDate) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Lending> cq = cb.createQuery(Lending.class);
        final Root<Lending> lendingRoot = cq.from(Lending.class);
        final Join<Lending, Book> bookJoin = lendingRoot.join("book");
        final Join<Lending, ReaderDetails> readerDetailsJoin = lendingRoot.join("readerDetails");
        cq.select(lendingRoot);

        final List<Predicate> where = new ArrayList<>();

        if (StringUtils.hasText(readerNumber))
            where.add(cb.like(readerDetailsJoin.get("readerNumber").get("readerNumber"), readerNumber));
        if (StringUtils.hasText(isbn))
            where.add(cb.like(bookJoin.get("isbn").get("isbn"), isbn));
        if (returned != null) {
            if (returned) {
                where.add(cb.isNotNull(lendingRoot.get("returnedDate")));
            } else {
                where.add(cb.isNull(lendingRoot.get("returnedDate")));
            }
        }
        if (startDate != null)
            where.add(cb.greaterThanOrEqualTo(lendingRoot.get("startDate"), startDate));
        if (endDate != null)
            where.add(cb.lessThanOrEqualTo(lendingRoot.get("startDate"), endDate));

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(lendingRoot.get("lendingNumber")));

        final TypedQuery<Lending> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }

    @Override
    public Lending save(Lending lending) {
        return null;
    }

    @Override
    public void delete(Lending lending) {

    }

/*
    @Override
    public List<ReaderAverageDto> getAverageMonthlyPerReader(LocalDate startDate, LocalDate endDate) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        final Root<Lending> lendingRoot = cq.from(Lending.class);
        final Join<ReaderDetails, Lending> readerDetailsJoin = lendingRoot.join("readerDetails");
        final Join<ReaderDetails, User> userJoin = readerDetailsJoin.join("user");

        final List<Predicate> where = new ArrayList<>();

        Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, lendingRoot.get("startDate"));
        Expression<Integer> monthExpr = cb.function("MONTH", Integer.class, lendingRoot.get("startDate"));

        Expression<Double> lendingCount = cb.count(lendingRoot).as(Double.class);

        Expression<Double> diffInMonths = cb.function(
                "date_part", Double.class, cb.literal("month"),
                cb.literal(LocalDate.now())
        );

        Expression<Double> lendingsAveragePerMonth = cb.diff(lendingCount, diffInMonths);



        cq.multiselect(readerDetailsJoin, yearExpr, monthExpr, durationInMonths);

        Expression<Long> durationInMonths = cb.quot(diffInDays, 30L);

        if(startDate!=null)
            where.add(cb.greaterThanOrEqualTo(lendingRoot.get("startDate"), startDate));
        if(endDate!=null)
            where.add(cb.lessThanOrEqualTo(lendingRoot.get("startDate"), endDate));

        cq.where(where.toArray(new Predicate[0]));
        cq.groupBy(readerDetailsJoin.get("readerNumber"), yearExpr, monthExpr);
        cq.orderBy(cb.asc(lendingRoot.get("lendingNumber")));

        List<Tuple> results = em.createQuery(cq).getResultList();
        Map<Integer, Map<Integer, List<ReaderAverageDto>>> groupedResults = new HashMap<>();


        for (Tuple result : results) {
            ReaderDetails readerDetails = result.get(0, ReaderDetails.class);
            int yearValue = result.get(1, Integer.class);
            int monthValue = result.get(2, Integer.class);
            Double averageDurationValue = result.get(3, Double.class);
            ReaderAverageDto readerAverageDto = new ReaderAverageDto(genre, averageDurationValue);

            groupedResults
                    .computeIfAbsent(yearValue, k -> new HashMap<>())
                    .computeIfAbsent(monthValue, k -> new ArrayList<>())
                    .add(readerAverageDto);
        }

        List<ReaderLendingsAvgPerMonthDto> readerLendingsAvgPerMonthDtos = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, List<ReaderAverageDto>>> yearEntry : groupedResults.entrySet()) {
            int yearValue = yearEntry.getKey();
            for (Map.Entry<Integer, List<ReaderAverageDto>> monthEntry : yearEntry.getValue().entrySet()) {
                int monthValue = monthEntry.getKey();
                List<ReaderAverageDto> values = monthEntry.getValue();
                readerLendingsAvgPerMonthDtos.add(new GenreLendingsPerMonthDTO(yearValue, monthValue, values));
            }
        }


        return null;
    }*/
}