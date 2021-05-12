package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<WalletDAO, Long> {

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE W.USER_ID = ?1", nativeQuery = true)
    WalletDAO fetchByUserId(String userId);

    @Query(value = "SELECT W.* FROM WALLET_T W WHERE W.USER_EMAIL = ?1", nativeQuery = true)
    WalletDAO fetchByUserEmail(String userEmail);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE W.USER_ID = ?1", nativeQuery = true)
    int countByUserId(String userId);

    @Query(value = "SELECT COUNT(1) FROM WALLET_T W WHERE W.USER_EMAIL = ?1", nativeQuery = true)
    int countByUserEmail(String userEmail);

    @Query(value = "select wt.* from wallet_t wt where wt.create_date < (sysdate -?1) and wt.temp = 1", nativeQuery = true)
    List<WalletDAO> fetchOldTemporaryWallets(int days);

    @Query(value = "select wt.* from wallet_t wt left join credential_t cr on cr.wallet_id = wt.id where wt.create_date < (sysdate -?1) and wt.temp = 1 and cr.cred_id is null", nativeQuery = true)
    List<WalletDAO> fetchOldTemporaryWalletsWithNoCredentials(int days);


}
