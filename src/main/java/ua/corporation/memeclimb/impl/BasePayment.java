package ua.corporation.memeclimb.impl;

import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.programs.ComputeBudgetProgram;
import org.sol4k.Connection;
import org.sol4k.Keypair;
import org.sol4k.PublicKey;
import org.sol4k.Transaction;
import org.sol4k.instruction.CreateAssociatedTokenAccountInstruction;
import org.sol4k.instruction.Instruction;
import org.sol4k.instruction.SplTransferInstruction;
import org.sol4k.instruction.TransferInstruction;
import ua.corporation.memeclimb.entity.main.PaymentInformation;
import ua.corporation.memeclimb.mapper.TransactionMapper;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePayment implements PaymentOperation {
    private final Connection sol4kConnection;
    private final TransactionMapper transactionMapper;

    protected BasePayment(Connection sol4kConnection, TransactionMapper transactionMapper) {
        this.sol4kConnection = sol4kConnection;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public String sendPaymentTransaction(PaymentInformation paymentInformation) {
        Transaction transaction = createSol4kTransaction(paymentInformation);

        return sol4kConnection.sendTransaction(transaction);
    }

    private Transaction createSol4kTransaction(PaymentInformation paymentInformation) {
        Keypair payer = Keypair.fromSecretKey(paymentInformation.getPayerPrivateKey());

        String recentBlockhash = sol4kConnection.getLatestBlockhash();
        List<Instruction> accountInstructions = getPaymentInstructions(paymentInformation);

        Transaction transaction = new Transaction(recentBlockhash, accountInstructions, payer.getPublicKey());
        transaction.sign(payer);

        return transaction;
    }

    protected abstract List<Instruction> getPaymentInstructions(PaymentInformation paymentInformation);

    protected List<Instruction> getComputerBudgetInstructions(int unitLimit, int unitPrice) {
        List<Instruction> computerBudgetInstructions = new ArrayList<>();

        TransactionInstruction instructionUnitLimitSol4k = ComputeBudgetProgram.setComputeUnitLimit(unitLimit);
        Instruction instructionUnitLimit = transactionMapper.fromSolanaJ(instructionUnitLimitSol4k);

        TransactionInstruction instructionUnitPriceSol4k = ComputeBudgetProgram.setComputeUnitPrice(unitPrice);
        Instruction instructionUnitPrice = transactionMapper.fromSolanaJ(instructionUnitPriceSol4k);

        computerBudgetInstructions.add(instructionUnitLimit);
        computerBudgetInstructions.add(instructionUnitPrice);

        return computerBudgetInstructions;
    }

    protected Instruction createSol4kTransferInstruction(String publicKeyFrom, String publicKeyTo, long lamport) {
        PublicKey fromPublicKey = new PublicKey(publicKeyFrom);
        PublicKey toPublicKey = new PublicKey(publicKeyTo);

        return new TransferInstruction(fromPublicKey, toPublicKey, lamport);
    }

    protected Instruction createSol4kSPLTransferInstruction(String payer, String publicAssociatedAccountFrom,
                                                            String publicAssociatedAccountTo, long lamport) {
        PublicKey payerKey = new PublicKey(payer);
        PublicKey from = new PublicKey(publicAssociatedAccountFrom);
        PublicKey to = new PublicKey(publicAssociatedAccountTo);

        return new SplTransferInstruction(payerKey, from, to, lamport);
    }

    protected Instruction createAssociatedInstruction(PaymentInformation paymentInformation) {
        PublicKey payer = new PublicKey(paymentInformation.getPayerPublicKey());
        PublicKey associatedAddress = findAssociatedAddress(paymentInformation.getReceiverPublicKey(), paymentInformation.getMint());
        PublicKey owner = new PublicKey(paymentInformation.getReceiverPublicKey());
        PublicKey tokenAddress = new PublicKey(paymentInformation.getMint());

        return new CreateAssociatedTokenAccountInstruction(
                payer,
                associatedAddress,
                owner,
                tokenAddress
        );
    }

    private PublicKey findAssociatedAddress(String walletPublicKey, String tokenAddress) {
        PublicKey owner = new PublicKey(walletPublicKey);
        PublicKey tokenAddressKey = new PublicKey(tokenAddress);

        return PublicKey.findProgramDerivedAddress(owner, tokenAddressKey)
                .getPublicKey();
    }
}
