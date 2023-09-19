package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<WalletDAO, Long>, JpaSpecificationExecutor<WalletDAO> {

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE W.USER_ID = ?1", nativeQuery = true)
    WalletDAO fetchByUserId(String userId);

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE W.WALLET_ADDRESS = ?1", nativeQuery = true)
    WalletDAO fetchByWalletAddress(String walletAddress);

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE LOWER(W.USER_EMAIL) = LOWER(?1) ORDER BY W.ID", nativeQuery = true)
    List<WalletDAO> listByUserEmail(String userEmail);

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE W.USER_EMAIL = ?1", nativeQuery = true)
    WalletDAO fetchByUserEmailCaseSensitive(String userEmail);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE W.USER_ID = ?1", nativeQuery = true)
    int countByUserId(String userId);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE W.WALLET_ADDRESS = ?1", nativeQuery = true)
    int countByWalletAddress(String walletAddress);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE LOWER(W.USER_EMAIL) = LOWER(?1) ORDER BY W.ID", nativeQuery = true)
    int countByUserEmail(String userEmail);

    @Query(value = "SELECT W1.USER_EMAIL as temp_user_email, W2.USER_EMAIL as permanent_user_email FROM WALLET_T W1 JOIN " +
            " (select wallet_t.user_email as user_email, count(1) as perm_count from wallet_t " +
            " where wallet_t.temp = 0 group by wallet_t.user_email) W2 " +
            " on LOWER(W1.user_email) = LOWER(W2.user_email) AND W1.TEMP = 1", nativeQuery = true)
    List<Object[]> listDuplicatedEmails();

    //  select wt.* from wallet_t wt where wt.create_date < (sysdate -?1) and wt.temp = 1
    default List<WalletDAO> fetchOldTemporaryWallets(int days) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);

        return findAll((root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(criteriaBuilder.lessThan(root.get("createDate"), cal.getTime()),
                    criteriaBuilder.equal(root.get("temporary"), true));
        });

    }

    @Override
    default void delete(WalletDAO walletDAO) {
        delete(walletDAO);
    }
}
