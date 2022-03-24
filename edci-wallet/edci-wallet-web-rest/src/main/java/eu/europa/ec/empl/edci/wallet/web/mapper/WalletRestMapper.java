package eu.europa.ec.empl.edci.wallet.web.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.web.model.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CredentialRestMapper.class)
public interface WalletRestMapper {

    public List<WalletDTO> toDTO(List<WalletCreateBulkElemView> walletCreateView);

    public WalletDTO toDTO(WalletCreateView walletCreateView);

    public WalletDTO toDTO(WalletModifyView walletModifyView);

    public WalletCreateResponseView toVO(WalletDTO walletDTO);

    public WalletResponseView toVOResponse(WalletDTO walletDTO);

    public WalletCreateBulkResponseElemView toVOBulkElem(WalletDTO walletDTO);

    default WalletCreateBulkResponseView toVOErrors(List<WalletDTO> walletDTO) {

        WalletCreateBulkResponseView resp = new WalletCreateBulkResponseView();

        for (WalletDTO dto : walletDTO) {
            resp.getErrors().add(toVOBulkElem(dto));
        }

        return resp;
    }

}
