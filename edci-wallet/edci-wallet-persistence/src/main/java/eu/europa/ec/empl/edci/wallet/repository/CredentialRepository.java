package eu.europa.ec.empl.edci.wallet.repository;

import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialDAO, Long> {

    @Query(value = "SELECT C.* FROM CREDENTIAL_T C JOIN WALLET_T W ON W.ID = C.WALLET_ID WHERE W.USER_ID = ?1 AND C.FILE_NAME is not null", nativeQuery = true)
    public List<CredentialDAO> findJsonLdCredentialsByUserId(String userId);

    @Query(value = "SELECT C.* FROM CREDENTIAL_T C JOIN WALLET_T W ON W.ID = C.WALLET_ID WHERE W.USER_ID = ?1 AND C.type = ?2", nativeQuery = true)
    public List<CredentialDAO> findCredentialsByUserId(String userId, String type);

    @Query(value = "SELECT C.* FROM CREDENTIAL_T C JOIN WALLET_T W ON W.ID = C.WALLET_ID WHERE W.USER_ID = ?1 AND C.UUID IN ?2", nativeQuery = true)
    public List<CredentialDAO> findByUuids(String userId, Set<String> uuids);

    @Query(value = "SELECT C.* FROM CREDENTIAL_T C JOIN WALLET_T W ON W.ID = C.WALLET_ID WHERE W.USER_ID = ?1 AND C.UUID = ?2", nativeQuery = true)
    public CredentialDAO fetchByUUID(String userId, String UUID);

    @Query(value = "SELECT C.* FROM CREDENTIAL_T C JOIN WALLET_T W ON W.ID = C.WALLET_ID WHERE W.USER_ID = ?1 AND C.UUID = ?2 AND C.type = ?3", nativeQuery = true)
    public CredentialDAO fetchByUUID(String userId, String UUID, String type);

    @Query(value = "SELECT COUNT(1) FROM CREDENTIAL_T C JOIN WALLET_T W ON W.ID = C.WALLET_ID WHERE W.USER_ID = ?1 AND C.UUID = ?2", nativeQuery = true)
    public int countByUUID(String userId, String UUUID);

    @Query(value = "SELECT C.CRED_ID FROM CREDENTIAL_T C " +
            "LEFT JOIN AUX_MIGRATION_LOG M ON C.CRED_ID = M.CREDENTIAL_PK WHERE C.FILE_NAME is null AND C.CREDENTIAL_XML is not null " +
            "AND (M.ERROR_CODE is null OR M.ERROR_CODE not in ('CI-0006', 'CI-0007', 'CI-0008', 'CI-0009', 'CI-0010')) order by C.CRED_ID desc", nativeQuery = true)
    public List<Long> fetchCredentialsToMigrate();

}
