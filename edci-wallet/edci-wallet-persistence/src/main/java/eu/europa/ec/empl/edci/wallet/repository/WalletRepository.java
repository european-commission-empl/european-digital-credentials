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

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE W.USER_EMAIL = ?1", nativeQuery = true)
    WalletDAO fetchByUserEmail(String userEmail);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE W.USER_ID = ?1", nativeQuery = true)
    int countByUserId(String userId);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE W.USER_EMAIL = ?1", nativeQuery = true)
    int countByUserEmail(String userEmail);

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
        System.out.println("works");
        delete(walletDAO);
        System.out.println("after delete");
    }
}
