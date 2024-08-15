package ua.corporation.memeclimb.mapper;

import org.mapstruct.Mapper;
import org.p2p.solanaj.core.TransactionInstruction;
import org.sol4k.AccountMeta;
import org.sol4k.PublicKey;
import org.sol4k.instruction.BaseInstruction;
import org.sol4k.instruction.Instruction;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    default List<Instruction> fromListCustom(List<ua.corporation.memeclimb.entity.main.response.helpEntity.TransactionInstruction> transactionInstructions) {
        return transactionInstructions
                .stream()
                .map(this::createTransaction)
                .collect(Collectors.toList());
    }

    default Instruction fromCustom(ua.corporation.memeclimb.entity.main.response.helpEntity.TransactionInstruction transactionInstruction) {
        return createTransaction(transactionInstruction);
    }

    default Instruction fromSolanaJ(TransactionInstruction instruction) {
        PublicKey publicKey = new PublicKey(instruction.getProgramId().toString());
        List<AccountMeta> accountMetas = getAccountMetas(instruction);
        return new BaseInstruction(instruction.getData(), accountMetas, publicKey);
    }

    private Instruction createTransaction(ua.corporation.memeclimb.entity.main.response.helpEntity.TransactionInstruction transactionInstruction) {
        PublicKey publicKey = new PublicKey(transactionInstruction.getPubkey());
        List<AccountMeta> accountMetas = getAccountMetas(transactionInstruction);
        byte[] data = Base64.getDecoder().decode(transactionInstruction.getData());

        return new BaseInstruction(data, accountMetas, publicKey);
    }

    private List<AccountMeta> getAccountMetas(ua.corporation.memeclimb.entity.main.response.helpEntity.TransactionInstruction transactionInstruction) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        if (transactionInstruction.getAccounts() != null) {
            accountMetas =
                    transactionInstruction.getAccounts()
                            .stream()
                            .map(this::convertAccountMeta)
                            .collect(Collectors.toList());
        }

        return accountMetas;
    }

    private List<AccountMeta> getAccountMetas(TransactionInstruction transactionInstruction) {
        List<AccountMeta> accountMetas = new ArrayList<>();

        if (transactionInstruction.getKeys() != null) {
            accountMetas =
                    transactionInstruction.getKeys()
                            .stream()
                            .map(this::convertAccountMetaSolanaj)
                            .collect(Collectors.toList());
        }

        return accountMetas;
    }

    private AccountMeta convertAccountMeta(ua.corporation.memeclimb.entity.main.response.helpEntity.AccountMeta accountMeta) {
        PublicKey publicKey = new PublicKey(accountMeta.getPublicKey());

        return new AccountMeta(publicKey, accountMeta.isSigner(), accountMeta.isWritable());
    }

    private AccountMeta convertAccountMetaSolanaj(org.p2p.solanaj.core.AccountMeta accountMeta) {
        PublicKey publicKey = new PublicKey(accountMeta.getPublicKey().toString());

        return new AccountMeta(publicKey, accountMeta.isSigner(), accountMeta.isWritable());
    }

}
