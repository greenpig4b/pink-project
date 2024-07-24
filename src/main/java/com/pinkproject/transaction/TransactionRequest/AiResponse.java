package com.pinkproject.transaction.TransactionRequest;

import com.pinkproject.transaction.Transaction;
import com.pinkproject.transaction.enums.CategoryIn;
import com.pinkproject.transaction.enums.CategoryOut;
import com.pinkproject.transaction.enums.TransactionType;
import lombok.Data;

public class AiResponse {

    @Data
    public static class AiDTO {
        private TransactionType transactionType;
        private CategoryIn categoryIn;
        private CategoryOut categoryOut;
        private int amount;
        private String description;

        public AiDTO(Transaction transaction) {
            this.transactionType = transaction.getTransactionType();
            this.categoryIn = transaction.getCategoryIn();
            this.categoryOut = transaction.getCategoryOut();
            this.amount = transaction.getAmount();
            this.description = transaction.getDescription();
        }

        @Override
        public String toString() {
            return String.format("거래 유형: %s, 수입 카테고리: %s, 지출 카테고리: %s, 금액: %d, 설명: %s",
                    transactionType, categoryIn, categoryOut, amount, description);
        }
    }
}

