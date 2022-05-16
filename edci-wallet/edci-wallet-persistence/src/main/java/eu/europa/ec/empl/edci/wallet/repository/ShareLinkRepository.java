package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLinkDAO, Long>, JpaSpecificationExecutor<ShareLinkDAO> {

    @Query(value = "SELECT COUNT(1) FROM SHARELINK_T S WHERE S.SHAREHASH = ?1", nativeQuery = true)
    public int countByShareURL(String shareURL);

    @Query(value = "SELECT S.* FROM SHARELINK_T S WHERE S.SHAREHASH = ?1", nativeQuery = true)
    public ShareLinkDAO fetchBySharedURL(String sharedURL);

    //  SELECT S.* FROM SHARELINK_T S WHERE S.EXPIRATIONDATE < sysdate
    default List<ShareLinkDAO> fetchOldTemporarySharelinks() {

        return findAll((root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(criteriaBuilder.lessThan(root.get("expirationDate"), new Date()));
        });

    }

}
